package com.safeway.app.emju.mylist.service.item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.allocation.helper.OfferConstants.OfferProgram;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;

public abstract class OFRDetailsProvider<T> implements ItemDetailsProvider<T> {
	
	@SuppressWarnings({"unused"})
	private static final Logger LOGGER = LoggerFactory.getLogger(OFRDetailsProvider.class);

	@Override
	public abstract Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException;
	
	protected ShoppingListItemVO getOfferItemDefinitions(OfferDetail offerDetail, ShoppingListItem shoppingListItem,
			ShoppingListVO shoppingListVO) throws Exception {

		ShoppingListItemVO shoppingListItemVO = new ShoppingListItemVO();
		String details = shoppingListVO.getHeaderVO().getDetails();
		String clientTimezone = shoppingListVO.getHeaderVO().getPreferredStore().getTimeZone();

		shoppingListItemVO.setReferenceId(offerDetail.getOfferId().toString());

		shoppingListItemVO.setItemType(shoppingListItem.getItemTypeCd());
		shoppingListItemVO.setId(shoppingListItem.getClipId());

		if (Constants.YES.equalsIgnoreCase(details)) {
			
			shoppingListItemVO.setOfferDetail(offerDetail);
			shoppingListItemVO.setAddedDate("");
			shoppingListItemVO.setStartDate("");
			shoppingListItemVO.setEndDate("");
			shoppingListItemVO.setUsage("");
			shoppingListItemVO.setImage("");
			shoppingListItemVO.setCategoryId("");
			shoppingListItemVO.setDescription("");
			shoppingListItemVO.setSavingsValue("");
			shoppingListItemVO.setSavingsType("");
			shoppingListItemVO.setTitle("");
			shoppingListItemVO.setSummary("");
			shoppingListItemVO.setLastUpdatedDate("");

			if (null != shoppingListItem.getClipTs()) {
				shoppingListItemVO.setAddedDate(DateHelper.getISODate(shoppingListItem.getClipTs(), clientTimezone));
			}
			if (null != shoppingListItem.getLastUpdTs()) {
				shoppingListItemVO
						.setLastUpdatedDate(DateHelper.getISODate(shoppingListItem.getLastUpdTs(), clientTimezone));
			}
			if (null != offerDetail.getOfferEffectiveStartDt()) {
				// ClientTimezone should be used to set the actual start date
				shoppingListItemVO.setStartDate(
						DateHelper.getEffectiveISODate(offerDetail.getOfferEffectiveStartDt(), clientTimezone));

			}

			if (null != offerDetail.getOfferEffectiveEndDt()) {
				// ClientTimezone should be used to set the actual End date
				shoppingListItemVO.setEndDate(
						DateHelper.getEffectiveISODate(offerDetail.getOfferEffectiveEndDt(), clientTimezone));
			}

			shoppingListItemVO.setUsage(offerDetail.getUsageTypeCd());

			shoppingListItemVO.setImage(offerDetail.getProductImgId());

			if (null != shoppingListItem.getCheckedId()) {
				shoppingListItemVO
						.setChecked(shoppingListItem.getCheckedId().equalsIgnoreCase(Constants.YES));
			}

			if (null != offerDetail.getPrimaryCategoryId()) {
				shoppingListItemVO.setCategoryId(offerDetail.getPrimaryCategoryId().toString());
			}

			// MF and BT offer
			String itemType = shoppingListItemVO.getItemType();

			String offerProgramTypeCd = offerDetail.getOfferSubProgram();

			if (itemType != null && offerProgramTypeCd != null && itemType.trim().equalsIgnoreCase(OfferProgram.MF)
					&& offerProgramTypeCd.equals("07")) {

				shoppingListItemVO.setItemSubType(offerProgramTypeCd);
			}

			// savingsType
			if (null != offerDetail.getPriceMethodCd()) {
				shoppingListItemVO.setSavingsType(offerDetail.getPriceMethodCd());
			}

			StringBuffer displayName = new StringBuffer();

			if (null != offerDetail.getProdDsc1()) {
				shoppingListItemVO.setDescription(offerDetail.getProdDsc1());
			}

			// For DM offers if prodDsc2 is present that will also be
			// displayed
			if (null != offerDetail.getProdDsc2()) {
				shoppingListItemVO
						.setDescription(shoppingListItemVO.getDescription() + " " + offerDetail.getProdDsc2());
			}

			if (null != offerDetail.getTitleDsc2()) {
				displayName.append(offerDetail.getTitleDsc2());
			}
			// For DM offers if titleDsc3 is present that will also be
			// displayed
			if (null != offerDetail.getTitleDsc3()) {
				displayName.append(",");
				displayName.append(offerDetail.getTitleDsc3());
			}

			if (shoppingListItemVO.getItemType().equalsIgnoreCase(OfferProgram.PD)) {
				if (null != offerDetail.getPriceValue1()) {
					// For the DM and PD offers price value will be set as
					// the savings value
					shoppingListItemVO.setSavingsValue(offerDetail.getPriceValue1());
				}
			}

			if (!shoppingListItemVO.getItemType().equalsIgnoreCase(OfferProgram.PD)) {
				if (null != offerDetail.getSavingValue()) {
					// For NON-DM/PD offers
					shoppingListItemVO.setSavingsValue(offerDetail.getSavingValue());
				}
			}

			if (null != offerDetail.getTitleDsc1()) {
				shoppingListItemVO.setTitle(offerDetail.getTitleDsc1());
			}
			shoppingListItemVO.setSummary(displayName.toString());

			if (null != offerDetail.getDisclaimer()) {
				shoppingListItemVO.setDisclaimer(offerDetail.getDisclaimer());
			}
		}

		return shoppingListItemVO;
	}

}
