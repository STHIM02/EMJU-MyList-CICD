package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Singleton;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

@Singleton
public class CCDetailsProvider extends OFRDetailsProvider<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CCDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {
		return null;
	}
	
	public Collection<ShoppingListItemVO> getItemDetails(Map<Long, OfferDetail> offerDetailMap, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO)
					throws ApplicationException {
		
		LOGGER.debug("Returned offer detail key set= " + offerDetailMap.keySet());
		LOGGER.debug("After getting offer details from cache " + offerDetailMap);
		
		Map<String, ShoppingListItemVO> offerItemsMap = new HashMap<String, ShoppingListItemVO>();
		
		try {
			String clientTimeZone = shoppingListVO.getHeaderVO().getPreferredStore().getTimeZone();
			Long clientDate = DateHelper.getClientCurrDateInDBLocaleMS(clientTimeZone);
			Date currClientDate = new Date(clientDate);
			
			for (Entry<Long, OfferDetail> entry : offerDetailMap.entrySet()) {
	
				Long offerId = entry.getKey();
				OfferDetail offerDetail = entry.getValue();
				if(currClientDate.after(offerDetail.getOfferEffectiveEndDt())) {
					continue;
				}
				
				offerItemsMap.put(offerId.toString(),
						getOfferItemDefinitions(offerDetail, itemMap.get(Long.toString(offerId)), shoppingListVO));
			}
		} catch (Exception e) {
			LOGGER.error("Exception-->CCDetailsProvider>>setOfferDetails-->  "
					+ e.getMessage());
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, null, null);
		}
		LOGGER.info("CCDetailsProvider setOfferDetails <<");
		return offerItemsMap.values();
	}
	
	

}
