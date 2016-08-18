/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** A Map which forwards all of its method calls to a delegate map.  Used to implement {@link DeterministicKeyMap}. */
class ForwardingMap<K, V> implements Map<K, V> {
	protected final Map<K, V> delegate;

	protected ForwardingMap(Map<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return delegate.get(key);
	}

	@Override
	public V put(K key, V value) {
		return delegate.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		delegate.putAll(m);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return delegate.entrySet();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return delegate.equals(other);
	}
}
