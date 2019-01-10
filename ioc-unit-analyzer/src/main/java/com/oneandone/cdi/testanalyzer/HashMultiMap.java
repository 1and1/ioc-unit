package com.oneandone.cdi.testanalyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author aschoerk
 */
class HashMultiMap<K, V> {

    HashMap<K, Set<V>> content = new HashMap<>();

    public int size() {
        return content.size();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public boolean containsKey(final Object key) {
        return content.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        for (Set<V> valueset : content.values()) {
            if (valueset.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public Set<V> get(K key) {
        return content.get(key);
    }

    public V put(final K key, final V value) {
        if (content.get(key) == null) {
            content.put(key, new HashSet<>());
        }
        content.get(key).add(value);
        return value;
    }

    public Set<V> put(final K key, final Set<V> values) {
        content.put(key, values);
        return values;
    }

    public Set<V> remove(final Object key) {
        Set<V> values = content.get(key);
        return values;
    }

    public void remove(final Object key, V value) {
        Set<V> values = content.get(key);
        if (values != null) {
            values.remove(value);
        }
    }

    public void clear() {
        content.clear();
    }

    public Set<K> keySet() {
        return content.keySet();
    }

    public Collection<Set<V>> values() {
        return content.values();
    }

    public Set<Map.Entry<K, Set<V>>> entries() {
        return content.entrySet();
    }

    public Set<V> getValues(final K c) {
        Set<V> result = content.get(c);
        if (result == null) {
            return Collections.EMPTY_SET;
        } else {
            return result;
        }
    }

    public HashMultiMap<K, V> clone() {
        HashMultiMap<K, V> result = new HashMultiMap<>();
        for (K k: keySet()) {
            for (V e: get(k)) {
                result.put(k,e);
            }
        }
        return result;
    }
}
