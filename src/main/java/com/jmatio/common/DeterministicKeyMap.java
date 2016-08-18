/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A map implementation which guarantees that all of its iterators
 * (keySet(), values(), and entrySet()) will be in the same order
 * as the keyOrder set which is passed in the constructor.
 * 
 * The keySet must contain all of the keys in the delegate, but it's
 * okay if the keySet contains more.
 * 
 * Useful in MLObject and MLStruct for ensuring that arrays have
 * their fields in the same order.
 */
public class DeterministicKeyMap<K, V> extends ForwardingMap<K, V> {
	private final Set<K> keyOrder;

	/**
	 * 
	 * @param keyOrder A set which must always contain all of the keys in delegate, and may contain more.
	 * @param delegate An underlying map.
	 */
	public DeterministicKeyMap(Set<K> keyOrder, Map<K, V> delegate) {
		super(delegate);
		this.keyOrder = keyOrder;
	}

	/** Returns the keyset of this map in the same order as keyOrder. */
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		return new DeterministicSet<K>(delegate.keySet(), (Function<K, K>) identity);
	}

	/** Returns the values of this map in the same order as keyOrder. */
	@Override
	public Collection<V> values() {
		return new DeterministicCollection<V>(delegate.values(), new Function<K, V>() {
			@Override
			public V apply(K input) {
				return delegate.get(input);
			}
		});
	}

	/** Returns the entries of this map in the same order as keyOrder. */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new DeterministicSet<Map.Entry<K, V>>(delegate.entrySet(), new Function<K, Map.Entry<K, V>>() {
			@Override
			public Map.Entry<K, V> apply(final K key) {
				return new Map.Entry<K, V>() {
					@Override
					public K getKey() {
						return key;
					}

					@Override
					public V getValue() {
						return delegate.get(key);
					}

					@Override
					public V setValue(V value) {
						return delegate.put(key, value);
					}
				};
			}
		});
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/** Java didn't find functional programming until Java 8. */
	static interface Function<T, R> {
		R apply(T input);
	}

	static final Function<Object, Object> identity = new Function<Object, Object>() {
		@Override
		public Object apply(Object input) {
			return input;
		}
	};

	/** DeterministicCollection which guarantees a Set delegate. */
	class DeterministicSet<T> extends DeterministicCollection<T> implements Set<T> {
		DeterministicSet(Set<T> delegate, Function<K, T> keyToValue) {
			super(delegate, keyToValue);
		}
	}

	/** A collection which iterates over the key set, transformed into values using the given function. */
	class DeterministicCollection<T> extends ForwardingCollection<T> {
		Function<K, T> keyToValue;

		DeterministicCollection(Collection<T> delegate, Function<K, T> keyToValue) {
			super(delegate);
			this.keyToValue = keyToValue;
		}

		@Override
		public Iterator<T> iterator() {
			final Iterator<K> iterByKey = keyOrder.iterator();
			return new AbstractIterator<T>() {
				@Override
				protected T computeNext() {
					while (iterByKey.hasNext()) {
						K nextKey = iterByKey.next();
						T value = keyToValue.apply(nextKey);
						if (delegate.contains(value)) {
							return value;
						}
					}
					return endOfData();
				}
			};
		}

		@Override
		public boolean equals(Object other) {
			return super.equals(other);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
}
