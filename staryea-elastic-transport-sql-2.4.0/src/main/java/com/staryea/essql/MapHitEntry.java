package com.staryea.essql;


import java.util.Map;
import org.elasticsearch.search.SearchHit;

/**
 * 
 * @author wenb
 * 
 */
@SuppressWarnings("rawtypes")
public class MapHitEntry implements HitEntry<Map> {

	@Override
	public Map mapper( SearchHit sh ) {
		return sh.getSource();
	}
}
