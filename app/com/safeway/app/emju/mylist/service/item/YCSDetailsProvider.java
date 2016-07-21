package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.google.inject.Singleton;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

@Singleton
public class YCSDetailsProvider implements ItemDetailsProvider<ClubPrice> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YCSDetailsProvider.class);

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException {

		return null;
	}
	
	private ShoppingListItemVO populateYCSItem(ShoppingListItem newYourCardSpecialItem, ClubPrice clubPrice,
			ShoppingListVO shoppingListVO) {

		ShoppingListItemVO shoppingListItemVO = null;
		String value = null;
		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getPreferredStore().getTimeZone();

		if (newYourCardSpecialItem != null) {

			shoppingListItemVO = new ShoppingListItemVO();
			
			shoppingListItemVO.setReferenceId(clubPrice.getRetailScanCd().toString());
			shoppingListItemVO.setItemType(newYourCardSpecialItem.getItemTypeCd());
			shoppingListItemVO.setId(newYourCardSpecialItem.getClipId());
			
			if (Constants.YES.equalsIgnoreCase(details)) {
				
				shoppingListItemVO.setClubPrice(clubPrice);
				shoppingListItemVO.setAddedDate("");
				shoppingListItemVO.setEndDate("");
				shoppingListItemVO.setTitle("");
				shoppingListItemVO.setDescription("");
				shoppingListItemVO.setSavingsValue("");
				shoppingListItemVO.setStartDate("");
				shoppingListItemVO.setCategoryId("");
				shoppingListItemVO.setLastUpdatedDate("");

				if (null != newYourCardSpecialItem.getClipTs()) {
					shoppingListItemVO
							.setAddedDate(DateHelper.getISODate(newYourCardSpecialItem.getClipTs(), clientTimezone));
				}
				if (null != newYourCardSpecialItem.getLastUpdTs()) {
					shoppingListItemVO.setLastUpdatedDate(
							DateHelper.getISODate(newYourCardSpecialItem.getLastUpdTs(), clientTimezone));
				}

				if (null != clubPrice.getEndDt()) {
					// ClientTimezone should be used to set the actual start
					// date
					shoppingListItemVO.setEndDate(DateHelper.getEffectiveISODate(clubPrice.getEndDt(), clientTimezone));
				}
				shoppingListItemVO.setTitle(clubPrice.getItemDsc());
				shoppingListItemVO.setDescription(clubPrice.getPackageSizeDsc());
				shoppingListItemVO.setStoreId(clubPrice.getStoreId().toString());

				Double savingsValue = clubPrice.getPromotionPrice();
				value = savingsValue != null ? savingsValue.toString() : "";
				shoppingListItemVO.setSavingsValue(value);

				String priceType = clubPrice.getPriceMethod();
				String priceSubType = clubPrice.getPriceMethodSubType();

				StringBuffer savingsType = new StringBuffer();

				if (null != priceType) {
					shoppingListItemVO.setSavingsCode(priceType);
					savingsType.append(priceType);
					if (null != priceSubType) {
						shoppingListItemVO.setSavingsSubCode(priceSubType);
						savingsType.append(":");
						savingsType.append(priceSubType);
					}
				}

				shoppingListItemVO.setSavingsType(savingsType.toString());
				if (null != clubPrice.getStartDt()) {
					// ClientTimezone should be used to set the actual start
					// date
					shoppingListItemVO
							.setStartDate(DateHelper.getEffectiveISODate(clubPrice.getStartDt(), clientTimezone));
				}
				if (null != clubPrice.getCategoryId()) {
					shoppingListItemVO.setCategoryId(clubPrice.getCategoryId().toString());
				}

				if (null != newYourCardSpecialItem.getCheckedId()) {
					shoppingListItemVO.setChecked(
							newYourCardSpecialItem.getCheckedId().equalsIgnoreCase(Constants.YES));
				}
			}
		}
		return shoppingListItemVO;
	}

	@Override
	public Collection<ShoppingListItemVO> getItemDetails(Map<Long, ClubPrice> itemDetailMap,
			Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO) throws ApplicationException {
		
		Map<String, ShoppingListItemVO> ycsItemsMap = new HashMap<String, ShoppingListItemVO>();
		String upcId = null;

		for (Entry<Long, ClubPrice> entry : itemDetailMap.entrySet()) {

			upcId = entry.getKey().toString();
			ycsItemsMap.put(upcId, populateYCSItem(itemMap.get(upcId), entry.getValue(), shoppingListVO));
		}

		return ycsItemsMap.values();
	}

}
