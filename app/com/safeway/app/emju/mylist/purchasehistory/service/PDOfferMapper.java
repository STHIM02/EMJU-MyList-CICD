/**
 * 
 */
package com.safeway.app.emju.mylist.purchasehistory.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.safeway.app.emju.allocation.cliptracking.model.OfferClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.PurchaseIndicator;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.allocation.pricing.helper.PDPricingHelper;
import com.safeway.app.emju.allocation.pricing.model.PDPricing;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.model.AllocatedOffer;

/**
 * @author sshar64
 *
 */
public class PDOfferMapper {

	
	 private final static Logger LOGGER = LoggerFactory.getLogger(PDOfferMapper.class);
	

	private static final List<String> INVALID_STATUS_TYPES = Arrays
			.asList(new String[] { "L", "l", "O", "o", "I", "i", "D", "d", "AD", "ad", "E", "e" });

	/**
	 * 
	 * @param offerDetail
	 * @param offerStorePrice
	 * @param offerInfoMap
	 * @return
	 */
	public AllocatedOffer mapOffer(OfferDetail offerDetail, 
							OfferStorePrice offerStorePrice,
							OfferClipStatus offerClipStatus, String timeZone) {
		
		LOGGER.debug(">>>> PDOfferMapper >>>>> " );

		AllocatedOffer offer = null;
		// get current DB current Date
		Long clientDBCurrDtInMS = DataHelper.getCurrTsInTzAsDBTzMs(timeZone);
	    Date clientDBCurrDt = new Date(clientDBCurrDtInMS);
	    
	    // clip status for this offer Id
		String clipStatus = offerClipStatus.getClipStatus();

		Date displayStartDt = offerDetail.getDisplayEffectiveStartDt();
		Date displayEndDt = offerDetail.getDisplayEffectiveEndDt();
		Date offerEndDt = offerDetail.getOfferEffectiveEndDt();
		
		
		displayStartDt = DataHelper.getDateWithNoTime(displayStartDt);
		displayEndDt = DataHelper.getDateWithMaxedTime(displayEndDt);
		offerEndDt = DataHelper.getDateWithMaxedTime(offerEndDt);
		

		boolean isAcceptableOffer = true;
		
		LOGGER.debug(">>>> Offer id = " + offerDetail.getOfferId());
		
		String offerStatus = offerDetail.getOfferStatusTypeId();
		boolean offerInvalid =  INVALID_STATUS_TYPES.contains(offerStatus);
		
		
		if (clientDBCurrDt.before(displayStartDt) || clientDBCurrDt.after(offerEndDt)) {
			
			isAcceptableOffer = false;
			
		} else if (offerInvalid && ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
			
			isAcceptableOffer = false;
		}

		if (isAcceptableOffer) {
			offer = new AllocatedOffer();		
			offer.setOfferId(offerDetail.getOfferId());
			offer.setOfferPgm(offerDetail.getOfferProgramCd());
			offer.setOfferTs(new Timestamp(offerDetail.getLastUpdateTs().getTime()));
			offer.setShoppingListCategoryId(offerDetail.getPrimaryCategoryId());
			offer.setClipStatus(clipStatus);
			offer.setClipId(offerClipStatus.getOfferId());
			offer.setPurchaseInd(PurchaseIndicator.PURCHASE_IND_BOUGHT);
			offer.getOfferDetail().setStartDt(displayStartDt);
			offer.getOfferDetail().setEndDt(offerEndDt);
			Double regularPrice = null;
			if (null != offerStorePrice) {
				regularPrice = offerStorePrice.getRegularPrice();
			}
			PDPricing pDPricing = PDPricingHelper.analyzePricing(offerDetail.getOfferPrice(), regularPrice);
			offer.getOfferDetail().setPriceTitle2(pDPricing.getPriceTitle());
			offer.getOfferDetail().setPriceTitle2Type(pDPricing.getPriceTitleType());
			offer.getOfferDetail().setPriceValue2(pDPricing.getPriceValue());
		}

		
		return offer;
	
	}

}
