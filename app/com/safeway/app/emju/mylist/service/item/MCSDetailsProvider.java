package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.google.inject.Singleton;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

@Singleton
public class MCSDetailsProvider implements ItemDetailsProvider<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MCSDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {
		
		LOGGER.debug("MCS Items found: " + itemMap.keySet());
		Map<String, ShoppingListItemVO> csItemsMap = new HashMap<String, ShoppingListItemVO>();
		for (Map.Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {
			ShoppingListItemVO shoppingListItemVO = new ShoppingListItemVO();
			csItemsMap.put(entry.getKey(),
					setMCSDetails(itemMap.get(entry.getKey()), shoppingListItemVO, shoppingListVO));
		}

		return csItemsMap.values();
	}
	
	private ShoppingListItemVO setMCSDetails(final ShoppingListItem csItem, final ShoppingListItemVO shoppingListItemVO,
			final ShoppingListVO shoppingListVO) {

		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getTimeZone();
		shoppingListItemVO.setId(csItem.getClipId());
		shoppingListItemVO.setItemType(csItem.getItemTypeCd());
		if (Constants.YES.equalsIgnoreCase(details)) {

			if (null != csItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(csItem.getClipTs(), clientTimezone));
			}
			if (null != csItem.getLastUpdTs()) {
				shoppingListItemVO.setLastUpdatedDate(DateHelper.getISODate(csItem.getLastUpdTs(), clientTimezone));
			}
			if (null != csItem.getCheckedId()) {
				shoppingListItemVO.setChecked(csItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}
			if (null != csItem.getCategoryId()) {
				shoppingListItemVO.setCategoryId(csItem.getCategoryId().toString());
			}

			if (null != csItem.getItemId()) {
				shoppingListItemVO.setReferenceId(String.valueOf(csItem.getItemId()));
			}

			if (null != csItem.getStoreId()) {
				shoppingListItemVO.setStoreId(csItem.getStoreId().toString());
			}
			if (null != csItem.getItemStartDate()) {
				shoppingListItemVO.setStartDate(DateHelper.getISODate(csItem.getItemStartDate(), clientTimezone));
			}
			if (null != csItem.getItemEndDate()) {
				shoppingListItemVO.setEndDate(DateHelper.getISODate(csItem.getItemEndDate(), clientTimezone));
			}
			if (null != csItem.getItemDesc()) {
				shoppingListItemVO.setDescription(csItem.getItemDesc());
			}
			if (null != csItem.getItemTitle()) {
				shoppingListItemVO.setTitle(csItem.getItemTitle());
			}
			if (null != csItem.getItemPriceValue()) {
				shoppingListItemVO.setPriceValue(csItem.getItemPriceValue());
			}
			if (null != csItem.getItemPromoPrice()) {
				shoppingListItemVO.setPromoPrice(csItem.getItemPromoPrice());
			}
		}

		return shoppingListItemVO;
	}

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<Long, OfferDetail> offerDetailMap,
			Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO) throws ApplicationException {
		
		return null;
	}

}
