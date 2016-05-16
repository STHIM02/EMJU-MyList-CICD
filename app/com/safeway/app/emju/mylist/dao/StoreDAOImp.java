package com.safeway.app.emju.mylist.dao;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.customerlookup.dao.CustomerLookupDAO;
import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.entity.Store;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.model.PreferredStore;

public class StoreDAOImp implements StoreDAO {
	
	private CustomerLookupDAO customerLookupDAO;
	private StoreCache storeCache;
	
	@Inject
	public StoreDAOImp(CustomerLookupDAO customerLookupDAO, StoreCache storeCache) {
		
		this.customerLookupDAO = customerLookupDAO;
		this.storeCache = storeCache;
	}

	@Override
	public PreferredStore findStoreInfo(Integer storeId, Long householdID, 
			String registeredZipCode) throws Exception {
		
		Integer userStoreId = null;

        // If valid StoreID is not passed, find StoreID info user HHID.
        if (null != storeId) {
            userStoreId = storeId;
        } else {
            userStoreId = findPrimaryStore(householdID);
        }

        return findStoreDetails(userStoreId, registeredZipCode);
	}
	
	private Integer findPrimaryStore(final Long householdID) throws Exception {
		
		Integer primaryStoreID = null;

        if (null != householdID) {
        	
        	primaryStoreID = customerLookupDAO.findStoreByHousehold(householdID);
        }
        
        return primaryStoreID;
	}
	
	private PreferredStore findStoreDetails(final Integer storeId, final String registeredZipCode)
		throws Exception{
		
		PreferredStore preferredStore = new PreferredStore();
		preferredStore.setStoreId(0);
		preferredStore.setPriceZone("");
		preferredStore.setPostalCode("");
		preferredStore.setRegionId(0);
		preferredStore.setPostalBanners(null);
		preferredStore.setTimeZone(Constants.DEFAULT_TIMEZONE);
		
		Store store = storeCache.getStoreDetailsById(storeId);
		preferredStore.setStoreId(store.getStoreId());
		preferredStore.setPriceZone(store.getPriceZoneId().toString());
		preferredStore.setPostalCode(store.getZipCode());
		preferredStore.setRegionId(store.getRegionId());
		preferredStore.setTimeZone(store.getTimeZoneNm());
		preferredStore.setPriceZone(store.getPriceZoneId().toString());
		
		return preferredStore;
		
	}

}
