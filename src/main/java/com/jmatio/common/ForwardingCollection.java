/*
 * Code licensed under new-style BSD (see LICENSE).
 * All code up to tags/original: Copyright (c) 2006, Wojciech Gradkowski
 * All code after tags/original: Copyright (c) 2015, DiffPlug
 */
package com.jmatio.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Collection which forwards all calls to a delegate collection,
 * except for toArray(), which are overridden to reflect any changes
 * in the iterator() method.
 * 
 * Used to implement {@link DeterministicKeyMap}.
 */
class ForwardingCollection<T> implements Collection<T> {
	protected final Collection<T> delegate;

	protected ForwardingCollection(Collection<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Iterator<T> iterator() {
		return delegate.iterator();
	}

	@Override
	public Object[] toArray() {
		Object[] result = new Object[this.size()];
		return toArray(result);
	}

	@Override
	public <R> R[] toArray(R[] a) {
		ArrayList<T> result = new ArrayList<T>(size());
		Iterator<T> iter = iterator();
		while (iter.hasNext()) {
			result.add(iter.next());
		}
		return result.toArray(a);
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
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean add(T e) {
		return delegate.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return delegate.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}

	@Override
	public void clear() {
		delegate.clear();
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
