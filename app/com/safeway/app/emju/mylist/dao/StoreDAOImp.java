package com.safeway.app.emju.mylist.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.safeway.app.emju.allocation.customerlookup.dao.CustomerLookupDAO;
import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.entity.Store;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.model.PreferredStore;

@Singleton
public class StoreDAOImp implements StoreDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreDAOImp.class);
	
	private CustomerLookupDAO customerLookupDAO;
	private StoreCache storeCache;
	
	@Inject
	public StoreDAOImp(CustomerLookupDAO customerLookupDAO, StoreCache storeCache) {
		
		this.customerLookupDAO = customerLookupDAO;
		this.storeCache = storeCache;
	}

	@Override
	public PreferredStore findStoreInfo(Integer storeId, Long householdID, 
			String registeredZipCode) throws ApplicationException {
		
		PreferredStore result = null;
		Integer userStoreId = null;

		try{
	        // If valid StoreID is not passed, find StoreID info user HHID.
	        if (null != storeId) {
	            userStoreId = storeId;
	        } else {
	            userStoreId = findPrimaryStore(householdID);
	        }
		} catch(Exception e) {
			LOGGER.error("Error on findPrimaryStore", e);
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, 
					"invalid or null storeId", null);
		}
		
		if(userStoreId == null) {
			LOGGER.error("Error on findStoreInfo, userStoreId is null");
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, 
					"invalid or null storeId", null);
		}
		
		try{
			
			result = findStoreDetails(userStoreId, registeredZipCode);
		} catch(Exception e) {
			
			LOGGER.error("Error on findStoreDetails", e);
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, 
					"invalid or null storeId", null);
		}

        return result;
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
