package com.staryea.essql;


import org.elasticsearch.search.SearchHit;

/**
 * 
 * @author wenb
 * 
 */
public interface HitEntry<T> {

	/**
	 * entry sh to T
	 * 
	 * @param sh
	 * @return
	 */
	T mapper( SearchHit sh );
}
