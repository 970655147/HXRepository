package com.hx.repository.utils;

import com.alibaba.fastjson.JSON;
import com.hx.log.file.FileUtils;
import com.hx.log.util.Constants;
import com.hx.log.util.Tools;
import com.hx.repository.consts.SqlConstants;
import com.hx.repository.domain.BaseEntity;
import com.hx.repository.model.ClassInfo;
import com.hx.repository.model.FieldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hx.repository.consts.SqlConstants.COLUMN_ID;
import static com.hx.repository.utils.TypeCastUtils.*;

/**
 * SqliteUtils
 *
 * @author Jerry.X.He <970655147@qq.com>
 * @version 1.0
 * @date 2021-01-24 16:13
 */
public final class MysqlUtils {

    /** 默认的缩进 */
    public static int IDENT = 4;
    /** 默认的缩进的数量 */
    public static int IDENT_TIMES_INITIAL = 0;

    // disable constructor
    private MysqlUtils() {
        Tools.assert0("can't instantiate !");
    }

    /**
     * 获取给定的 clazz 的建表语句
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:14
     */
    public static <T> List<String> generateTableSchema(Class<T> clazz, String filePath) {
        try {
            List<String> lines = new ArrayList<>();
            if (FileUtils.exists(filePath)) {
                lines = Tools.getContentWithList(filePath);
            }

            generateSchemaAndSave(lines, clazz,
                                  MysqlUtils::generateCreateTableSignature,
                                  MysqlUtils::generateCreateTableSchema);

            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 获取给定的 clazz 的建表语句
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:50
     */
    public static <T> String generateCreateTableSchema(Class<T> clazz, Map<String, String> key2Line) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        List<FieldInfo> allFieldList = classInfo.allFieldInfo();

        StringBuilder sb = new StringBuilder();
        String createTableSignature = generateCreateTableSignature(clazz);
        int identTimes = IDENT_TIMES_INITIAL;
        sb.append(ident(identTimes)).append(String.format("%s ( \n", createTableSignature));
        for (FieldInfo fieldInfo : allFieldList) {
            String columnName = fieldInfo.getColumnName();
            String dataType = dataType(fieldInfo, key2Line);
            String notNull = notNull(fieldInfo);
            String defaultValue = defaultValue(fieldInfo);

            String fieldDeclareTemplate = "%s %s %s %s,\n";
            String fieldDeclare = String.format(fieldDeclareTemplate, columnName, dataType, notNull, defaultValue);
            sb.append(ident(identTimes + 1)).append(fieldDeclare);
        }

        if (BaseEntity.class.isAssignableFrom(clazz)) {
            sb.append(ident(identTimes + 1)).append(String.format("PRIMARY KEY (%s),\n", COLUMN_ID));
        }
        Tools.removeLastSep(sb, ",\n");
        sb.append("\n");
        sb.append(ident(identTimes)).append(");\n");
        return sb.toString();
    }

    /**
     * 生成给定的方法, 并且保存给定的方法到 lines
     *
     * @param lines lines
     * @param clazz clazz
     * @return java.util.List<java.lang.String>
     * @author Jerry.X.He
     * @date 2021-01-21 17:01
     */
    public static void generateSchemaAndSave(
            List<String> lines, Class clazz,
            Function<Class, String> methodLocatorFunc,
            BiFunction<Class, Map<String, String>, String> methodGeneratorFunc) {
        String methodLocator = methodLocatorFunc.apply(clazz);
        int methodStart = locateMethodStart(lines, methodLocator, "(");
        int methodEnd = locateMethodEnd(lines, methodStart, "(", ")");

        // 采集 key -> line
        Map<String, String> key2Line = new HashMap<>();
        if (methodStart >= 0) {
            for (int i = methodStart; i < methodEnd; i++) {
                String line = lines.get(i);
                if (line.contains("DEFAULT")) {
                    String[] splits = line.trim().split("\\s+");
                    key2Line.put(splits[0], line);
                }
            }
        }

        String methodCode = methodGeneratorFunc.apply(clazz, key2Line);
        saveMethodToLines(lines, methodCode, methodStart, methodEnd);
    }

    /**
     * 生成创表语句的 声明
     *
     * @param clazz clazz
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:52
     */
    public static String generateCreateTableSignature(Class clazz) {
        ClassInfo classInfo = ClassInfoUtils.getClassInfo(clazz);
        String tableName = classInfo.getTableName();
        return String.format("CREATE TABLE %s ", tableName);
    }

    // ----------------------------------------- 其他公用 -----------------------------------------

    /**
     * 生成缩进的字符串
     *
     * @param times times
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-22 13:48
     */
    public static String ident(int times) {
        int identWithSpace = times * IDENT;
        StringBuilder result = new StringBuilder(identWithSpace);
        for (int i = 0; i < identWithSpace; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * 获取给定的字段的 数据类型
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:24
     */
    public static String dataType(FieldInfo fieldInfo, Map<String, String> key2Line) {
        Field field = fieldInfo.getField();
        Class fieldType = field.getType();
        String columnName = fieldInfo.getColumnName();
        if (key2Line.containsKey(columnName)) {
            return parseDataType(key2Line.get(columnName));
        }

        if (ClassUtils.isBooleanClass(fieldType)) {
            return "TINYINT(1)";
        } else if (ClassUtils.isIntegerClass(fieldType)) {
            return "INT";
        } else if (ClassUtils.isLongClass(fieldType)) {
            return "BIGINT";
        } else if (ClassUtils.isBigIntegerClass(fieldType)) {
            return "BIGINT";
        } else if (ClassUtils.isFloatClass(fieldType)) {
            return "FLOAT(20, 2)";
        } else if (ClassUtils.isBigDecimalClass(fieldType)) {
            return "NUMERIC(20, 2)";
        } else if (ClassUtils.isStringClass(fieldType)) {
            int maxLength = fieldInfo.getMaxLength();
            return String.format("VARCHAR(%s)", maxLength);
        }
        throw new RuntimeException(" unknown field :  " + JSON.toJSONString(fieldInfo));
    }

    /**
     * 获取给定的 类型声明中的 字段的类型
     *
     * @param fieldDeclare fieldDeclare
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 17:01
     */
    public static String parseDataType(String fieldDeclare) {
        String[] splits = fieldDeclare.trim().split("\\s+");
        return splits[1];
    }

    /**
     * 获取给定的字段的 是否可以为空
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:40
     */
    public static String notNull(FieldInfo fieldInfo) {
        if (fieldInfo.isNullable()) {
            return Constants.EMPTY_STR;
        }
        return "NOT NULL";
    }

    /**
     * 获取给定的字段的 默认值
     *
     * @param fieldInfo fieldInfo
     * @return java.lang.String
     * @author Jerry.X.He
     * @date 2021-01-24 16:41
     */
    public static String defaultValue(FieldInfo fieldInfo) {
        Object defaultValue = fieldInfo.getDefaultValue();
        Field field = fieldInfo.getField();
        Class fieldType = field.getType();
        if (!fieldInfo.isNullable() && (defaultValue == null)) {
            return "";
        }
        if (defaultValue == null) {
            return "DEFAULT NULL";
        }

        if (ClassUtils.isBooleanClass(fieldType)) {
            return "DEFAULT " + String.valueOf(defaultValue).toUpperCase();
        } else if (ClassUtils.isIntegerClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isLongClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isBigIntegerClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isFloatClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isBigDecimalClass(fieldType)) {
            return "DEFAULT " + defaultValue;
        } else if (ClassUtils.isStringClass(fieldType)) {
            return "DEFAULT '" + defaultValue + "'";
        }
        throw new RuntimeException(" unknown field :  " + JSON.toJSONString(fieldInfo));
    }

    public static String assembleInsertSql(String tableName, Map<String, Object> entity, boolean addCommonFields) {
        String insertSqlTemplate = " insert into %s (%s) values (%s); ";
        List<String> fieldNames = new ArrayList<>(), fieldValues = new ArrayList<>();
        for (String fieldName : entity.keySet()) {
            Object originalFieldValue = entity.get(fieldName);
            String fieldValue = resolveFieldValue(entity, fieldName, originalFieldValue);
            fieldNames.add(String.format("`%s`", fieldName));
            fieldValues.add(transferFieldValueIfNecessary(fieldValue));
        }
        if (addCommonFields) {
            Long currentTs = System.currentTimeMillis();
            addFixedFieldNames(fieldNames, currentTs, true);
            addFixedFieldValues(fieldValues, currentTs, true);
        }

        String sql = String.format(insertSqlTemplate, tableName,
                StringUtils.join(fieldNames, ", "),
                StringUtils.join(fieldValues, ", ")
        );
        return sql;
    }

    public static String assembleInsertSql(String tableName, Map<String, Object> entity) {
        return assembleInsertSql(tableName, entity, true);
    }

    public static String assembleBatchInsertSql(String tableName, List<Map<String, Object>> entityList, boolean addCommonFields) {
        String insertSqlTemplate = " insert into %s (%s) values %s; ";
        List<String> insertFieldNames = new ArrayList<>(), outerFieldValues = new ArrayList<>();
        Set<String> fieldNames = new LinkedHashSet<>();
        Long currentTs = System.currentTimeMillis();

        for (Map<String, Object> entity : entityList) {
            fieldNames.addAll(entity.keySet());
        }
        for (String fieldName : fieldNames) {
            insertFieldNames.add(String.format("`%s`", fieldName));
        }
        if (addCommonFields) {
            addFixedFieldNames(insertFieldNames, currentTs, true);
        }

        for (Map<String, Object> entity : entityList) {
            List<String> fieldValues = new ArrayList<>();
            for (String fieldName : fieldNames) {
                Object originalFieldValue = entity.get(fieldName);
                String fieldValue = resolveFieldValue(entity, fieldName, originalFieldValue);
                fieldValues.add(transferFieldValueIfNecessary(fieldValue));
            }

            if (addCommonFields) {
                addFixedFieldValues(fieldValues, currentTs, true);
            }
            outerFieldValues.add(String.format("(%s)", StringUtils.join(fieldValues, ", ")));
        }

        String sql = String.format(insertSqlTemplate, tableName,
                StringUtils.join(insertFieldNames, ", "),
                StringUtils.join(outerFieldValues, ", ")
        );
        return sql;
    }

    public static String assembleBatchInsertSql(String tableName, List<Map<String, Object>> entityList) {
        return assembleBatchInsertSql(tableName, entityList, true);
    }

    public static String assembleUpdateSql(String tableName, String idFieldName, Map<String, Object> entity, boolean addCommonFields) {
        String updateSqlTemplate = " update %s set %s %s; ";
        List<String> fieldNames = new ArrayList<>(), fieldValues = new ArrayList<>();
        for (String fieldName : entity.keySet()) {
            Object originalFieldValue = entity.get(fieldName);
            String fieldValue = resolveFieldValue(entity, fieldName, originalFieldValue);
            fieldNames.add(String.format("`%s`", fieldName));
            fieldValues.add(transferFieldValueIfNecessary(fieldValue));
        }
        if (addCommonFields) {
            Long currentTs = System.currentTimeMillis();
            addFixedFieldNames(fieldNames, currentTs, false);
            addFixedFieldValues(fieldValues, currentTs, false);
        }

        List<String> setClauseList = new ArrayList<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            setClauseList.add(String.format(" %s = %s ", fieldNames.get(i), fieldValues.get(i)));
        }
        String setClause = StringUtils.join(setClauseList, ", ");

        String idValue = String.valueOf(entity.get(idFieldName));
        String whereCond = String.format(" where %s = %s ", idFieldName, transferFieldValueIfNecessary(idValue));

        String sql = String.format(updateSqlTemplate, tableName, setClause, whereCond);
        return sql;
    }

    public static String assembleUpdateSql(String tableName, String idFieldName, Map<String, Object> entity) {
        return assembleUpdateSql(tableName, idFieldName, entity, true);
    }

    public static List<String> assembleBatchSaveSql(JdbcTemplate jdbcTemplate, String tableName, String idFieldName,
                                                    List<Map<String, Object>> entityList, boolean addCommonFields) {
        List<String> idList = entityList.stream().map(ele -> String.valueOf(ele.get(idFieldName))).collect(Collectors.toList());
        List<String> existsIdList = selectExistsById(jdbcTemplate, tableName, idFieldName, idList);
        Map<String, Map<String, Object>> toInsertById = new LinkedHashMap<>(), toUpdateById = new LinkedHashMap<>();
        for (Map<String, Object> entity : entityList) {
            String idValue = String.valueOf(entity.get(idFieldName));

            Map<String, Map<String, Object>> entityByIdTmp = toInsertById;
            if (existsIdList.contains(idValue)) {
                entityByIdTmp = toUpdateById;
            }
            entityByIdTmp.put(idValue, entity);
        }

        List<String> result = new ArrayList<>();
        String insertSql = SqlConstants.SQL_DUMMY_SQL;
        List<Map<String, Object>> toInsertList = new ArrayList<>(toInsertById.values());
        if (!CollectionUtils.isEmpty(toInsertList)) {
            insertSql = assembleBatchInsertSql(tableName, toInsertList, addCommonFields);
        }
        result.add(insertSql);

        List<Map<String, Object>> toUpdateList = new ArrayList<>(toUpdateById.values());
        for (Map<String, Object> toUpdate : toUpdateList) {
            String updateSql = assembleUpdateSql(tableName, idFieldName, toUpdate, addCommonFields);
            result.add(updateSql);
        }
        return result;
    }

    public static List<String> assembleBatchSaveSql(JdbcTemplate jdbcTemplate, String tableName, String idFieldName,
                                                    List<Map<String, Object>> entityList) {
        return assembleBatchSaveSql(jdbcTemplate, tableName, idFieldName, entityList, true);
    }

    public static List<String> selectExistsById(JdbcTemplate jdbcTemplate, String tableName, String idFieldName, List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }

        String querySqlTemplate = " select %s as id from %s %s; ";
        String idInSnippet = StringUtils.join(idList.stream().map(MysqlUtils::transferFieldValueIfNecessary).collect(Collectors.toList()), ", ");
        String whereCond = String.format(" where %s in (%s) ", idFieldName, idInSnippet);
        String querySql = String.format(querySqlTemplate, idFieldName, tableName, whereCond);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql);
        return list.stream().map(ele -> String.valueOf(ele.get("id"))).collect(Collectors.toList());
    }

    public static String generateQuerySql(String tableName, String whereCond) {
        String querySql = String.format(" select * from %s ", tableName);
        if (StringUtils.isNotBlank(whereCond)) {
            querySql = String.format(" %s where %s ", querySql, whereCond);
        }
        return querySql;
    }

    public static String generateDeleteSql(String tableName, String whereCond) {
        String querySql = String.format(" delete from %s ", tableName);
        if (StringUtils.isNotBlank(whereCond)) {
            querySql = String.format(" %s where %s ", querySql, whereCond);
        }
        return querySql;
    }

    public static void executeSql(JdbcTemplate jdbcTemplate, String sql) {
        try {
            jdbcTemplate.execute(sql);
            System.out.println(String.format(" [execSql] 执行 sql : %s", sql));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format(" [updateSql] 执行 sql : %s, 出现异常, 详情请检查日志 ", sql));
        }
    }

    public static int updateSql(JdbcTemplate jdbcTemplate, String sql) {
        try {
            int updatedCount = jdbcTemplate.update(sql);
            System.out.println(String.format(" [updateSql] 执行 sql : %s, 合计更新了 %s 条数据 ", sql, updatedCount));
            return updatedCount;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(String.format(" [updateSql] 执行 sql : %s, 出现异常, 详情请检查日志 ", sql));
            return -1;
        }
    }

    public static String resolveFieldValue(Map<String, Object> entity, String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            return null;
        }

        if (fieldValue instanceof Date) {
            return DateFormatUtils.format((Date) fieldValue, "yyyy-MM-dd HH:mm:ss");
        }
        if (fieldValue instanceof LocalDateTime) {
            LocalDateTime dateTime = ((LocalDateTime) fieldValue);
            return String.format("%s-%s-%s %s:%s:%s",
                    String.format("%04d", dateTime.getYear()),
                    String.format("%02d", dateTime.getMonthValue()),
                    String.format("%02d", dateTime.getDayOfMonth()),
                    String.format("%02d", dateTime.getHour()),
                    String.format("%02d", dateTime.getMinute()),
                    String.format("%02d", dateTime.getSecond())
            );
        }

        return String.valueOf(fieldValue);
    }

    public static void addFixedFieldNames(List<String> fieldNames, Long currentTs, boolean addCatm) {
        if (addCatm) {
            fieldNames.add(SqlConstants.COLUMN_CATM);
        }
        fieldNames.add(SqlConstants.COLUMN_UPTM);
    }

    public static void addFixedFieldValues(List<String> fieldValues, Long currentTs, boolean addCatm) {
        if (addCatm) {
            fieldValues.add(transferFieldValueIfNecessary(String.valueOf(currentTs)));
        }
        fieldValues.add(transferFieldValueIfNecessary(String.valueOf(currentTs)));
    }

    public static String transferFieldValueIfNecessary(String fieldValue) {
        if (fieldValue == null) {
            return "NULL";
        }

        if (fieldValue.contains("\"")) {
            fieldValue = fieldValue.replace("\"", "\\\"");
        }
        return String.format("\"%s\"", fieldValue);
    }

    public static String transferSingleQuoteFieldValueIfNecessary(String fieldValue) {
        if (fieldValue == null) {
            return "NULL";
        }

        if (fieldValue.contains("'")) {
            fieldValue = fieldValue.replace("'", "\\'");
        }
        return String.format("'%s'", fieldValue);
    }

    public static int countInserted(String sql) {
        if (sql.contains(SqlConstants.SQL_DUMMY_SQL)) {
            return 0;
        }
        return StringUtils.countMatches(sql, "), (") + 1;
    }

    public static void fillOrTrimToFieldNames(Map<String, Object> entity, List<String> fieldNames, String defaultValue) {
        List<String> field2Remove = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            String fieldName = entry.getKey();
            if (!fieldNames.contains(fieldName)) {
                field2Remove.add(fieldName);
            }
        }
        for (String fieldName : field2Remove) {
            entity.remove(fieldName);
        }

        for (String fieldName : fieldNames) {
            if (!entity.containsKey(fieldName)) {
                entity.put(fieldName, defaultValue);
            }
        }
    }

    public static void fillOrTrimToFieldNames(Map<String, Object> entity, List<String> fieldNames) {
        fillOrTrimToFieldNames(entity, fieldNames, "");
    }

    public static String wrapSqlIn(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }

        return String.format("\"%s\"", StringUtils.join(list, "\", \""));
    }


}
