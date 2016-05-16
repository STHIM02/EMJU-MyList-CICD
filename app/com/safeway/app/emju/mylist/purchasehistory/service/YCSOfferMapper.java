/**
 * 
 */
package com.safeway.app.emju.mylist.purchasehistory.service;

import java.util.Date;
import java.util.Map;

import com.safeway.app.emju.allocation.cliptracking.entity.MyListItemStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ItemType;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferClassifiers;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferProgram;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.AllocatedOfferDetail;
import com.safeway.app.emju.util.ListItemReference;

/**
 * @author sshar64
 *
 */
public class YCSOfferMapper {

	/**
	 * 
	 * 
	 * @param clubPrice
	 * @param timezone
	 * @param ycsUPCListItemStatus
	 * @return
	 */
	public AllocatedOffer mapOffer(ClubPrice clubPrice, String timezone,
			Map<String, MyListItemStatus> ycsUPCListItemStatus) {

		AllocatedOffer offer = new AllocatedOffer();
	

		// retail scan cd will be used as offerId for YCS offer
		Long offerId = clubPrice.getRetailScanCd();

		offer.setOfferId(offerId);

		offer.setOfferPgm(OfferProgram.YCS);

		// Question:
		// how to map this field with ClubPrice??
		// offer.setOfferTs(offerDetail.getLastUpdateTs()); FROM
		// OfferDetail.LastUpdateTs?
		// Arun will check with UI

		offer.getOfferDetail().setCategoryName(clubPrice.getCategoryNm());
		offer.getOfferDetail().setTitleDsc1(clubPrice.getItemDsc());
		offer.getOfferDetail().setProdDsc1(clubPrice.getPackageSizeDsc());

		Date effectiveStartDt = clubPrice.getStartDt();

		if (effectiveStartDt != null) {
			Date clientDt = DataHelper.getDateInClientTimezone(timezone, effectiveStartDt);
			offer.getOfferDetail().setOfferStartDt( DataHelper.getJSONDate(clientDt));
			offer.getOfferDetail().setStartDt(clientDt);
			offer.getOfferDetail().setOfferStartDate(DataHelper.getISODate(effectiveStartDt));		
		}

		Date effectiveEndDt = clubPrice.getEndDt();

		if (effectiveEndDt != null) {
			Date clientDt = DataHelper.getDateInClientTimezone(timezone, effectiveEndDt);
			offer.getOfferDetail().setOfferEndDt( DataHelper.getJSONDate(clientDt));
			offer.getOfferDetail().setEndDt(clientDt);
			offer.getOfferDetail().setOfferEndDate( DataHelper.getISODate(effectiveEndDt));
		}

		// Change for MB2 Offer (Must buy 2)
		String priceMethodType = clubPrice.getPriceMethod();
		String priceMethodSubType = clubPrice.getPriceMethodSubType();

		if (OfferClassifiers.PRICE_METHOD_TYPE_BOGO.equalsIgnoreCase(priceMethodType)) {
			if (OfferClassifiers.PRICE_METHOD_SUB_TYPE_B1G1.equalsIgnoreCase(priceMethodSubType)) {
				offer.getOfferDetail().setPriceType(OfferClassifiers.PRICE_METHOD_TYPE_BOGO);
				offer.getOfferDetail().setPriceSubType(OfferClassifiers.PRICE_METHOD_SUB_TYPE_B1G1);
			}
		} else if (OfferClassifiers.PRICE_METHOD_TYPE_MB.equalsIgnoreCase(priceMethodType)) {// Muff Byte : To get one offer , buy 2 items.
			if (OfferClassifiers.PRICE_METHOD_SUB_TYPE_MB2.equalsIgnoreCase(priceMethodSubType)) {
				offer.getOfferDetail().setPriceType( OfferClassifiers.PRICE_METHOD_TYPE_MB);
				offer.getOfferDetail().setPriceSubType(OfferClassifiers.PRICE_METHOD_SUB_TYPE_MB2);
			}
		}

		offer.getOfferDetail().setPriceValue1(String.valueOf(clubPrice.getPromotionPrice()));



		Double regularPrice = clubPrice.getRetailPrice();
		Double savingsAmt = clubPrice.getSavings();

		offer.getOfferDetail().setPriceValue2(String.valueOf(regularPrice));
		offer.getOfferDetail().setSavingsValue(String.valueOf(savingsAmt));

		int savingsPct = 0;

		if (regularPrice != null && savingsAmt != null) {
			savingsPct = (int) (savingsAmt / regularPrice * 100);
			offer.getOfferDetail().setSavingsPct(String.valueOf(savingsPct));
		}

		// set YCS offer clip status
		String ycsRefId = new ListItemReference(ItemType.YCS, String.valueOf(clubPrice.getRetailScanCd()),
				clubPrice.getStoreId()).getItemRefId();

		if (ycsUPCListItemStatus.containsKey(ycsRefId)) {
			MyListItemStatus ycsListItemStatus = ycsUPCListItemStatus.get(ycsRefId);
			offer.setClipStatus(getClipStatus(ycsListItemStatus));
		}
		
				return offer;
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	private String getClipStatus(MyListItemStatus item) {
		if (item.getDeleteTs() != null) {
			return ClipStatus.UNCLIPPED;
		} else {
			return ClipStatus.ADDED_TO_LIST;
		}
	}

}
