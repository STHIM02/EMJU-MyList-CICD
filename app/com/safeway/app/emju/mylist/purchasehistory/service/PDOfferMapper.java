/**
 * 
 */
package com.safeway.app.emju.mylist.purchasehistory.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.safeway.app.emju.allocation.cliptracking.model.OfferClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferStatus;
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
			.asList(new String[] { "L", "l", "O", "o", "I", "i", "D", "d", "AD", "ad" });

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
		boolean offerValid = OfferStatus.ACTIVE.equals(offerStatus);
		
		boolean isNotOfferDateValid = clientDBCurrDt.before(displayStartDt) || clientDBCurrDt.after(displayEndDt);
		
		LOGGER.debug(">>>> offerStatus = " + offerStatus);
		LOGGER.debug(">>>> offerInvalid = " + offerInvalid);
		LOGGER.debug(">>>> offerValid = " + offerValid);

		LOGGER.debug(">>>> isNotOfferDateValid = " + isNotOfferDateValid);
		LOGGER.debug(">>>> clientDBCurrDt = " + clientDBCurrDt);
		LOGGER.debug(">>>> offer displayStartDt = " + displayStartDt);
		LOGGER.debug(">>>> offer displayEndDt = " + displayEndDt);
		
		
		if (isNotOfferDateValid) {
			isAcceptableOffer = false;
		} else {
		
				//Clipped offer
				if(ClipStatus.ADDED_TO_CARD.equalsIgnoreCase(clipStatus)) {
					if(offerInvalid && isNotOfferDateValid) {//offer is clipped: Offer Status in  “L,D,E etc”:  end date of offer passed : not show Offer
						isAcceptableOffer = false;
					} 
					
					if (offerValid && isNotOfferDateValid) { //offer is clipped: Offer Status is A:  end date of offer passed 
						isAcceptableOffer = false;
					}
					
				}
				
				//Unclipped offer
				if(ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
					
					if(offerInvalid && !isNotOfferDateValid) {//offer is Unclipped: Offer Status in “L,D,E etc”: end date of offer not passed : don’t show Offer
						isAcceptableOffer = false;
					} 
					
					if (offerValid && isNotOfferDateValid) { //offer is Unclipped: Offer Status is A:  end date of offer passed : don’t show Offer 
							isAcceptableOffer = false;
					}
				}
		}
		
		LOGGER.debug(">>>> isAcceptableOffer = " + isAcceptableOffer);
		

		/*if (offerInvalid && ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
			isAcceptableOffer = false;
		} //else if (!offerInvalid && ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
			else if (!offerInvalid) {
				if (clientDBCurrDt.before(displayStartDt) || clientDBCurrDt.after(displayEndDt)) {
					isAcceptableOffer = false;
				}
		}*/
		
		if(!isAcceptableOffer) {
			return null;
		}

		AllocatedOffer offer = new AllocatedOffer();
		
		offer.setOfferId(offerDetail.getOfferId());
		offer.setOfferPgm(offerDetail.getOfferProgramCd());
		

		offer.setOfferTs(new Timestamp(offerDetail.getLastUpdateTs().getTime()));
		

		offer.setShoppingListCategoryId(offerDetail.getPrimaryCategoryId());
		
		
		offer.setClipStatus(clipStatus);
		offer.setClipId(offerClipStatus.getOfferId()); // Check with Efren . It should be a String but is Long now.
		//TODO: Change to ClipId. per Arun
		
		offer.setPurchaseInd(PurchaseIndicator.PURCHASE_IND_BOUGHT);
		
		// Allocated Offer Details
		
	
		offer.getOfferDetail().setStartDt(displayStartDt);
		offer.getOfferDetail().setEndDt(offerEndDt);
		
		Double regularPrice = null;
		
		if (null != offerStorePrice) {
			regularPrice = offerStorePrice.getRegularPrice();
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("currentOfferId " + offerDetail.getOfferId());
			LOGGER.info("dOfferPrice " + offerDetail.getOfferPrice());
			LOGGER.info("dRegPrice " + regularPrice);
		}
		
		
		
		PDPricing pDPricing = PDPricingHelper.analyzePricing(offerDetail.getOfferPrice(), regularPrice);
		
		offer.getOfferDetail().setPriceTitle2(pDPricing.getPriceTitle());
		offer.getOfferDetail().setPriceTitle2Type(pDPricing.getPriceTitleType());
		offer.getOfferDetail().setPriceValue2(pDPricing.getPriceValue());
		

		
	  return offer;
	
	}

}
