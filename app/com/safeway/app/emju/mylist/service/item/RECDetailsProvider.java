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
import com.safeway.app.emju.mylist.helper.DetailUtil;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

@Singleton
public class RECDetailsProvider implements ItemDetailsProvider<OfferDetail> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RECDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {
		
		LOGGER.debug("REC Items found: " + itemMap.keySet());
		Map<String, ShoppingListItemVO> recipeItemsMap = new HashMap<String, ShoppingListItemVO>();
		for (Map.Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {
			ShoppingListItemVO shoppingListItemVO = new ShoppingListItemVO();
			recipeItemsMap.put(entry.getKey(),
					setRecipeDetails(itemMap.get(entry.getKey()), shoppingListItemVO, shoppingListVO));
		}

		return recipeItemsMap.values();
	}
	
	private ShoppingListItemVO setRecipeDetails(final ShoppingListItem recipelItem,
			final ShoppingListItemVO shoppingListItemVO, final ShoppingListVO shoppingListVO) {
		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getTimeZone();
		shoppingListItemVO.setId(recipelItem.getClipId());
		shoppingListItemVO.setItemType(recipelItem.getItemTypeCd());

		if (Constants.YES.equalsIgnoreCase(details)) {
			
			shoppingListItemVO.setDescription("");
            shoppingListItemVO.setQuantity("");

			if (null != recipelItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(recipelItem.getClipTs(), clientTimezone));
			}
			if (null != recipelItem.getLastUpdTs()) {
				// Fix for Production Issue LastUpdatedDate
				shoppingListItemVO.setLastUpdatedDate(DateHelper.getISODate(recipelItem.getLastUpdTs(), clientTimezone));

			}

			shoppingListItemVO.setDescription(DetailUtil.cleanExtraChars(recipelItem.getItemDesc(), "%3F"));
			shoppingListItemVO.setQuantity(recipelItem.getItemQuantity());
			if (null != recipelItem.getCheckedId()) {
				shoppingListItemVO.setChecked(recipelItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}

			if (null != recipelItem.getCategoryId()) {
				shoppingListItemVO.setCategoryId(recipelItem.getCategoryId().toString());
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
