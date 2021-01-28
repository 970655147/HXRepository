package com.hx.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 键值对
 *
 * @author ziv
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValuePair<K, V> implements Serializable {

    private static final long serialVersionUID = 100L;

    private K key;

    private V value;

}