package com.utility.widget.ass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Recycler<K, V> {

	protected Map<K, V> mUsing = new HashMap<K, V>();
	protected Stack<V> mFree = new Stack<V>();
	
	public Set<K> getUsingKeySet() {
		return new HashSet<K>(mUsing.keySet());
	}
	
	public boolean isUsing(K key) {
		return mUsing.containsKey(key);
	}
	
	public V getFree() {
		if (mFree.empty()) {
			return null;
		} else {
			return mFree.pop();
		}
	}
	
	public V getUsing(K key) {
		return mUsing.get(key);
	}
	
	public void putUsing(K key, V value) {
		mUsing.put(key, value);
	}
	
	public void changeFree(K key) {
		V value = mUsing.remove(key);
		
		if (null != value) {
			mFree.push(value);
		}
	}
	
	public void recycleUsing() {
		mFree.addAll(mUsing.values());
		mUsing.clear();
	}
	
	public void clear() {
		mUsing.clear();
		mFree.clear();
	}
	
}
