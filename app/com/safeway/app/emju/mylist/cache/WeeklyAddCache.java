package com.safeway.app.emju.mylist.cache;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.cache.CacheAccessException;
import com.safeway.app.emju.mylist.model.WeeklyAddVO;

@ImplementedBy(WeeklyAddCacheImp.class)
public interface WeeklyAddCache {
	
	// Cache Key prefix
	static String WEEKLYADD_CACHE_KEY_PREFIX = "WEEKLYADD:";

	public Map<String, WeeklyAddVO> getWeeklyAddByOfferId(final List<String> offerIds) throws CacheAccessException;
}
