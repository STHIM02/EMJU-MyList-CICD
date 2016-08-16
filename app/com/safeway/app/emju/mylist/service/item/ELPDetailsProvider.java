package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

@Singleton
public class ELPDetailsProvider implements ItemDetailsProvider<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ELPDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {

		LOGGER.debug("ELP Items found: " + itemMap.keySet());
		Map<String, ShoppingListItemVO> elpItemsMap = new HashMap<String, ShoppingListItemVO>();
		for (Map.Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {
			ShoppingListItemVO shoppingListItemVO = new ShoppingListItemVO();
			elpItemsMap.put(entry.getKey(),
					setELPDetails(itemMap.get(entry.getKey()), shoppingListItemVO, shoppingListVO));
		}

		return elpItemsMap.values();
	}
	
	private ShoppingListItemVO setELPDetails(final ShoppingListItem elpItem,
			final ShoppingListItemVO shoppingListItemVO, final ShoppingListVO shoppingListVO) {
		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getTimeZone();
		shoppingListItemVO.setId(elpItem.getClipId());
		shoppingListItemVO.setItemType(elpItem.getItemTypeCd());

		if (Constants.YES.equalsIgnoreCase(details)) {
			
			shoppingListItemVO.setDescription("");
            shoppingListItemVO.setQuantity("");

			if (null != elpItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(elpItem.getClipTs(), clientTimezone));
			}
			if (null != elpItem.getLastUpdTs()) {
				// Fix for Production Issue LastUpdatedDate
				shoppingListItemVO.setLastUpdatedDate(DateHelper.getISODate(elpItem.getLastUpdTs(), clientTimezone));

			}

			shoppingListItemVO.setDescription(elpItem.getItemDesc());
			shoppingListItemVO.setQuantity(elpItem.getItemQuantity());
			if (null != elpItem.getCheckedId()) {
				shoppingListItemVO.setChecked(elpItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}

			if (null != elpItem.getCategoryId()) {
				shoppingListItemVO.setCategoryId(elpItem.getCategoryId().toString());
			}
			
			if (null != elpItem.getItemTitle()) {
				shoppingListItemVO.setTitle(elpItem.getItemTitle());
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
