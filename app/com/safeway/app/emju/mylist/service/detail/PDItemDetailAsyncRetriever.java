package com.safeway.app.emju.mylist.service.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.dao.PDAllocationDAO;
import com.safeway.app.emju.allocation.entity.PDCustomOffer;
import com.safeway.app.emju.allocation.entity.PDDefaultOffer;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.F.Promise;

public class PDItemDetailAsyncRetriever extends AbstractItemDetailAsyncRetriever<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PDItemDetailAsyncRetriever.class);
	
	private OfferDetailCache offerCache;
	private PDAllocationDAO pdAllocationDAO;
	
	@Inject
	public PDItemDetailAsyncRetriever(final OfferDetailCache offerCache, final PDAllocationDAO pdAllocationDAO) {
		
		this.offerCache = offerCache;
		this.pdAllocationDAO = pdAllocationDAO;
	}

	@Override
	public Promise<Map<Long, OfferDetail>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		return Promise.promise(() -> this.getOfferDetails(itemMap, shoppingListVO));
	}
	
	private Map<Long, OfferDetail> getOfferDetails(Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO)
			throws ApplicationException {
		
		LOGGER.info("PDDetailsProvider setOfferDetails >>");
		Map<Long, OfferDetail> offerDetailMap = new HashMap<Long, OfferDetail>();
		List<Long> pdOfferIds = new ArrayList<Long>();
		List<Long> validOfferIds = new ArrayList<Long>();
		
		Integer storeId = shoppingListVO.getHeaderVO().getPreferredStore().getStoreId();
		Integer regionId = shoppingListVO.getHeaderVO().getPreferredStore().getRegionId();
		String hhId = shoppingListVO.getHeaderVO().getSwyhouseholdid();
		
		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {

			pdOfferIds.add(Long.parseLong(entry.getKey()));
		}
		
		Map<Long, PDCustomOffer> pdCustomOffers = 
				pdAllocationDAO.findPDCustomAllocation(Long.valueOf(hhId), regionId);
		Map<Long, PDDefaultOffer> pdDefaultOffers = 
				pdAllocationDAO.findPDDefaultAllocation(storeId);
			
		for(Long offerId : pdOfferIds) {
			
			if(pdCustomOffers.get(offerId) != null || pdDefaultOffers.get(offerId) != null) {
				
				validOfferIds.add(offerId);
			}
		}
		LOGGER.debug("Before getting offer details from cache");
		Long[] validOfferIdsArray = validOfferIds.toArray(new Long[validOfferIds.size()]);
		LOGGER.debug("Valid ids= " + validOfferIds);
		offerDetailMap = offerCache.getOfferDetailsByIds(validOfferIdsArray);
		offerDetailMap.putAll(offerDetailMap);
		return offerDetailMap;
	}

}
