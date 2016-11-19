package com.safeway.app.emju.mylist.service.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.safeway.app.emju.allocation.dao.PurchasedItemDAO;
import com.safeway.app.emju.allocation.entity.PurchasedItem;
import com.safeway.app.emju.cache.entity.RetailScanOffer;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mylist.comparator.OfferComparator;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

@Singleton
public class UPCDetailsProvider implements ItemDetailsProvider<RetailScanOffer> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UPCDetailsProvider.class);
	
	private PurchasedItemDAO purchasedItemDAO;
	
	@Inject
	public UPCDetailsProvider(PurchasedItemDAO purchasedItemDAO) {
		
		this.purchasedItemDAO = purchasedItemDAO;
	}

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers) {
		
		LOGGER.debug("ShoppingListServiceImp --> setStandardItemDetails");
		LOGGER.debug("Standard Items found: " + itemMap.keySet());
		
		if(ValidationHelper.isNonEmpty(matchedOffers)) {
			LOGGER.debug("matched offers found: " + matchedOffers.keySet());
		}
		
		Long hhid = Long.valueOf(shoppingListVO.getHeaderVO().getSwyhouseholdid());
		Map<String, ShoppingListItemVO> stndItemsMap = new HashMap<String, ShoppingListItemVO>();
		Map<String, List<AllocatedOffer>> offers = null;
		Integer ttl = null;

		ShoppingListItemVO shoppingListItemVO = null;
		ShoppingListItem productItem = null;
		
		Map<Long, PurchasedItem> purchaseMap = null;
		
		try {
			
			purchaseMap = purchasedItemDAO.findItemsByHousehold(hhid);
			
			
		} catch (Exception e) {
			
			LOGGER.error("An exceotion ocurrend while trying to get purchase iten information");
		}
		
		purchaseMap = ValidationHelper.isNonEmpty(purchaseMap) ? purchaseMap : new HashMap<Long, PurchasedItem>();

		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {

			productItem = entry.getValue();
			
			if(purchaseMap.get(Long.valueOf(entry.getKey())) == null ||
					ValidationHelper.isEmpty(productItem.getItemTitle())) {
				
				if(productItem.getTtl() == null || productItem.getTtl() == 0) {
					
					ttl = DataHelper.getTTLsetup(new Date(), 0, 1);
					productItem.setTtl(ttl);
					shoppingListVO.getUpdateUPCItem().add(productItem);
				}
				continue;
			}
			
			shoppingListItemVO = new ShoppingListItemVO();
			
			offers = null;
			if (matchedOffers != null) {
				offers = matchedOffers.get(entry.getKey());
			}
			String offersinf = offers != null ? offers.entrySet().toString() : null;
			LOGGER.debug("For upc :" + entry.getKey() + " there are offers: " + offersinf);
			stndItemsMap.put(entry.getKey(),
					setStandardDetails(productItem, offers, shoppingListItemVO, shoppingListVO));
		}

		return stndItemsMap.values();
	}
	
	private ShoppingListItemVO setStandardDetails(final ShoppingListItem standardItem,
			final Map<String, List<AllocatedOffer>> matchedOffers, final ShoppingListItemVO shoppingListItemVO,
			final ShoppingListVO shoppingListVO) {

		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getTimeZone();
		shoppingListItemVO.setId(standardItem.getClipId());
		shoppingListItemVO.setItemType(standardItem.getItemTypeCd());

		if (Constants.YES.equalsIgnoreCase(details)) {
			
			shoppingListItemVO.setDescription("");
			shoppingListItemVO.setQuantity("");
			
			if (null != standardItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(standardItem.getClipTs(), clientTimezone));
			}
			if (null != standardItem.getLastUpdTs()) {
				shoppingListItemVO
						.setLastUpdatedDate(DateHelper.getISODate(standardItem.getLastUpdTs(), clientTimezone));

			}

			shoppingListItemVO.setDescription(standardItem.getItemDesc());
			shoppingListItemVO.setQuantity(standardItem.getItemQuantity());
			shoppingListItemVO.setTitle(standardItem.getItemTitle());
			shoppingListItemVO.setReferenceId(standardItem.getItemId());
			if (null != standardItem.getCheckedId()) {
				shoppingListItemVO.setChecked(standardItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}

			if (null != standardItem.getCategoryId()) {
				shoppingListItemVO.setCategoryId(standardItem.getCategoryId().toString());
			}

			List<AllocatedOffer> relatedOffers = new ArrayList<AllocatedOffer>();
			List<AllocatedOffer> offers = null;

			if (matchedOffers != null && !matchedOffers.isEmpty()) {
				for (Map.Entry<String, List<AllocatedOffer>> entry : matchedOffers.entrySet()) {

					offers = entry.getValue();
					relatedOffers.addAll(offers);
				}
				Collections.sort(relatedOffers, new OfferComparator());
				shoppingListItemVO.setRelatedOffers(relatedOffers.toArray(new AllocatedOffer[relatedOffers.size()]));
			}
		}

		return shoppingListItemVO;
	}

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<Long, RetailScanOffer> itemDetailMap,
			Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO) throws ApplicationException {
		
		Map<String, ShoppingListItemVO> upcItemsMap = new HashMap<String, ShoppingListItemVO>();
		Map<Long, PurchasedItem> purchaseMap = null;
		ShoppingListItemVO shoppingListItemVO = null;
		ShoppingListItem productItem = null;
		Integer ttl = null;
		
		Long hhid = Long.valueOf(shoppingListVO.getHeaderVO().getSwyhouseholdid());
		
		try {
			
			purchaseMap = purchasedItemDAO.findItemsByHousehold(hhid);
			
			
		} catch (Exception e) {
			
			LOGGER.error("An exceotion ocurrend while trying to get purchase iten information");
		}
		
		purchaseMap = ValidationHelper.isNonEmpty(purchaseMap) ? purchaseMap : new HashMap<Long, PurchasedItem>();
		
		for(Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {
			
			productItem = entry.getValue();
			
			if(purchaseMap.get(Long.valueOf(entry.getKey())) == null ||
					itemDetailMap.get(Long.valueOf(entry.getKey())) == null) {
				
				if(productItem.getTtl() == null || productItem.getTtl() == 0) {
					
					ttl = DataHelper.getTTLsetup(new Date(), 0, 1);
					productItem.setTtl(ttl);
					shoppingListVO.getUpdateUPCItem().add(productItem);
				}
				continue;
			}
			
			shoppingListItemVO = new ShoppingListItemVO();

			upcItemsMap.put(entry.getKey(), populateUPCItem(productItem, shoppingListItemVO));
		}

		return upcItemsMap.values();
	}
	
	private ShoppingListItemVO populateUPCItem(ShoppingListItem upcItem, ShoppingListItemVO shoppingListItemVO) {
		
		shoppingListItemVO.setId(upcItem.getClipId());
		shoppingListItemVO.setItemType(upcItem.getItemTypeCd());
		
		return shoppingListItemVO;
	}

}
