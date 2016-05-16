package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class WSDetailsProvider implements ItemDetailsProvider<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WSDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {
		
		LOGGER.debug("WS Items found: " + itemMap.keySet());
		Map<String, ShoppingListItemVO> wsItemsMap = new HashMap<String, ShoppingListItemVO>();
		for (Entry<String, ShoppingListItem> enty : itemMap.entrySet()) {
			ShoppingListItemVO shoppingListItemVO = new ShoppingListItemVO();
			wsItemsMap.put(enty.getKey(),
					setWSDetails(itemMap.get(enty.getKey()), shoppingListItemVO, shoppingListVO));
		}

		return wsItemsMap.values();
	}
	
	private ShoppingListItemVO setWSDetails(final ShoppingListItem wsItem, final ShoppingListItemVO shoppingListItemVO,
			final ShoppingListVO shoppingListVO) {

		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getPreferredStore().getTimeZone();
		shoppingListItemVO.setId(wsItem.getClipId());
		shoppingListItemVO.setReferenceId(wsItem.getItemId());
		shoppingListItemVO.setItemType(wsItem.getItemTypeCd());

		if (Constants.YES.equalsIgnoreCase(details)) {
			
			shoppingListItemVO.setDescription("");
            shoppingListItemVO.setCategoryId("");
            shoppingListItemVO.setStoreId("");
            shoppingListItemVO.setTitle("");
            
			if (null != wsItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(wsItem.getClipTs(), clientTimezone));
			}
			if (null != wsItem.getLastUpdTs()) {
				// Production issue fix
				shoppingListItemVO.setLastUpdatedDate(DateHelper.getISODate(wsItem.getLastUpdTs(), clientTimezone));
			}
			if (null != wsItem.getCheckedId()) {
				shoppingListItemVO.setChecked(wsItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}
			if (null != wsItem.getCategoryId()) {
				shoppingListItemVO.setCategoryId(wsItem.getCategoryId().toString());
			}

			if (null != wsItem.getStoreId()) {
				shoppingListItemVO.setStoreId(wsItem.getStoreId().toString());
			}

			if (null != wsItem.getItemEndDate()) {
				shoppingListItemVO.setEndDate(DateHelper.getISODate(wsItem.getItemEndDate(), clientTimezone));
			}
			if (null != wsItem.getItemDesc()) {
				shoppingListItemVO.setDescription(wsItem.getItemDesc());
			}
			if (null != wsItem.getItemTitle()) {
				shoppingListItemVO.setTitle(wsItem.getItemTitle());
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
