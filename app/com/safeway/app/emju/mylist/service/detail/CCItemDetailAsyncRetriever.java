package com.safeway.app.emju.mylist.service.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.dao.CCAllocationDAO;
import com.safeway.app.emju.allocation.entity.CCAllocatedOffer;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;

public class CCItemDetailAsyncRetriever extends AbstractItemDetailAsyncRetriever<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CCItemDetailAsyncRetriever.class);
	private static final String STORE_ID_PREFIX = "S";
	
	private OfferDetailCache offerCache;
	private CCAllocationDAO ccAllocationDAO;
	
	@Inject
	public CCItemDetailAsyncRetriever(final OfferDetailCache offerCache, final CCAllocationDAO ccAllocationDAO) {
		
		this.ccAllocationDAO = ccAllocationDAO;
		this.offerCache = offerCache;
	}

	@Override
	public Promise<Map<Long, OfferDetail>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		return Promise.promise(() -> this.getOfferDetails(itemMap, shoppingListVO));
	}
	
	@Override
	public Promise<Map<Long, OfferDetail>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, ExecutionContext threadCtx) throws ApplicationException {
		
		ExecutionContext exeCtx = threadCtx != null ? threadCtx : Akka.system().dispatchers().defaultGlobalDispatcher();
		
		Promise<Map<Long, OfferDetail>> promise = F.Promise.promise((Function0<Map<Long, OfferDetail>>) () -> {
            return this.getOfferDetails(itemMap, shoppingListVO);
        } , exeCtx);
		
		return promise;
	}
	
	private Map<Long, OfferDetail> getOfferDetails(Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO)
			throws ApplicationException {
		
		LOGGER.info("CCDetailsProvider getOfferDetails >>");
		Map<Long, OfferDetail> offerDetailMap = new HashMap<Long, OfferDetail>();
		List<Long> validOfferIds = new ArrayList<Long>();
		List<Long> lookupOfferIds = new ArrayList<Long>();
		
		String postalCode = shoppingListVO.getHeaderVO().getPreferredStore().getPostalCode();
		
		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {
			LOGGER.debug("lookupOfferIds ==> " + entry.getKey());
			lookupOfferIds.add(Long.parseLong(entry.getKey()));
		}
		
		LOGGER.info("CCDetailsProvider before finding ccAllocations>>" + lookupOfferIds.size());
		String storeIdAsPostalCd = convertStoreIdAsPostalCd(Integer.parseInt(shoppingListVO.getHeaderVO().getParamStoreId()));
		LOGGER.debug("storeIdAsPostalCd = " + storeIdAsPostalCd);
		Map<Long, CCAllocatedOffer> allocatedOffersPostalOnly = 
				ccAllocationDAO.findCCAllocation(postalCode);
		Map<Long, CCAllocatedOffer> allocatedOffersStoreOnly = new HashMap<Long, CCAllocatedOffer>();
		allocatedOffersStoreOnly = ccAllocationDAO.findCCAllocation(storeIdAsPostalCd);
		
		LOGGER.debug("Content of allocatedOffersPostalOnly...");
		allocatedOffersPostalOnly.forEach((k,v)->LOGGER.debug("Key : " + k + " Value : " + v));
		
		LOGGER.debug("Content of allocatedOffersStoreOnly...");
		allocatedOffersStoreOnly.forEach((k,v)->LOGGER.debug("Key : " + k + " Value : " + v));
		
		Map<Long, CCAllocatedOffer> allocatedOffers = new HashMap<Long, CCAllocatedOffer>();
		allocatedOffers.putAll(allocatedOffersPostalOnly);
		allocatedOffers.putAll(allocatedOffersStoreOnly);
		
		LOGGER.info("CCDetailsProvider after finding ccAllocations>>" + allocatedOffers.size());
		
		for(Entry<Long, CCAllocatedOffer> entry : allocatedOffers.entrySet()) {
				
			if(lookupOfferIds.contains(entry.getKey())) {
				validOfferIds.add(entry.getKey());
			}
		}
			
			
		LOGGER.debug("Before getting offer details from cache");
		Long[] validOfferIdsArray = validOfferIds.toArray(new Long[validOfferIds.size()]);
		LOGGER.debug("Valid ids= " + validOfferIds);
		offerDetailMap = offerCache.getOfferDetailsByIds(validOfferIdsArray);
		LOGGER.debug("OfferDetailMap size being returned: " + offerDetailMap.size());
		return offerDetailMap;
	}
	
	private static String convertStoreIdAsPostalCd(Integer storeId) {
		String result = STORE_ID_PREFIX;
		
		if(0 >= storeId && 10 > storeId) {
			result = result + "000";
		} else if(10 >= storeId && 1000 > storeId) {
			result = result + "00";
		} else if(100 >= storeId && 1000 > storeId) {
			result = result + "0";
		} 
		
		result = result + storeId.toString();
		return result;
	}

}
