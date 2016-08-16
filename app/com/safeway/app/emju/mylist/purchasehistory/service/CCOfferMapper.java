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
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.PurchaseIndicator;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.model.AllocatedOffer;


/**
 * @author sshar64
 *
 */
public class CCOfferMapper {

	private static final List<String> INVALID_STATUS_TYPES = Arrays.asList(new String[] { "L", "l", "O", "o", "I", "i",
	        "D", "d", "AD", "ad" });
	
    private static final String HOUSEHOLD_TARGETED_OFFERS = "08";
	private final static Logger LOGGER = LoggerFactory.getLogger(CCOfferMapper.class);
	

	
    /**
     * 
     * @param offerDetail
     * @param catalinaAllocationOffers
     * @param offerInfoMap
     * @return
     */
	public AllocatedOffer mapOffer(OfferDetail offerDetail, 
			List<Long> catalinaAllocationOffers,
			OfferClipStatus offerClipStatus, String timezone)  {
		
		boolean catalinaOffersAvailable = !catalinaAllocationOffers.isEmpty();
        
		AllocatedOffer offer = null;
        Long currentOfferId = offerDetail.getOfferId();
        String offerStatus = offerDetail.getOfferStatusTypeId();
        boolean offerInvalid = INVALID_STATUS_TYPES.contains(offerStatus);
        String clipStatus = offerClipStatus.getClipStatus();
        
        Date displayStartDt = offerDetail.getDisplayEffectiveStartDt();
		Date displayEndDt = offerDetail.getDisplayEffectiveEndDt();
		Date offerEndDt = offerDetail.getOfferEffectiveEndDt();
        

		displayStartDt = DataHelper.getDateWithNoTime(displayStartDt);
		displayEndDt = DataHelper.getDateWithMaxedTime(displayEndDt);
		offerEndDt = DataHelper.getDateWithMaxedTime(offerEndDt);
		
		Long clientDBCurrDtInMS =DataHelper.getCurrTsInTzAsDBTzMs(timezone);
		Date clientDBCurrDt = new Date(clientDBCurrDtInMS);
		
        boolean isAcceptableOffer = true;

        String offerSubPgmTypeCd = offerDetail.getOfferSubProgram() == null ? "" : offerDetail.getOfferSubProgram();
        
        if (offerInvalid && ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
            isAcceptableOffer = false;
        }
        else if (!offerInvalid && ClipStatus.UNCLIPPED.equalsIgnoreCase(clipStatus)) {
            if (clientDBCurrDt.before(displayStartDt)
                || clientDBCurrDt.after(displayEndDt)
                || (offerSubPgmTypeCd.equalsIgnoreCase(HOUSEHOLD_TARGETED_OFFERS) && (!catalinaOffersAvailable || !catalinaAllocationOffers
                    .contains(currentOfferId)))) {
                isAcceptableOffer = false;
            }
        }
/*        
		boolean offerValid = OfferStatus.ACTIVE.equals(offerStatus);
		
		boolean isNotOfferDateValid = clientDBCurrDt.before(displayStartDt) || clientDBCurrDt.after(displayEndDt);
		

        
    	LOGGER.debug(">>>> offerStatus = " + offerStatus);
		LOGGER.debug(">>>> offerInvalid = " + offerInvalid);
		LOGGER.debug(">>>> offerValid = " + offerValid);

		LOGGER.debug(">>>> isNotOfferDateValid = " + isNotOfferDateValid);
		LOGGER.debug(">>>> clientDBCurrDt = " + clientDBCurrDt);
		LOGGER.debug(">>>> offer displayStartDt = " + displayStartDt);
		LOGGER.debug(">>>> offer displayEndDt = " + displayEndDt);
		
        
        if (null == offerSubPgmTypeCd) {
        	offerSubPgmTypeCd ="";
        }
        
        
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
		
		boolean isNotPartnerOfferValid = offerSubPgmTypeCd.equalsIgnoreCase(HOUSEHOLD_TARGETED_OFFERS) && (!catalinaOffersAvailable || !catalinaAllocationOffers
                .contains(currentOfferId));
		LOGGER.debug(">>>> isNotPartnerOfferValid = " + isNotPartnerOfferValid);
		
		if (isNotPartnerOfferValid) {
			isAcceptableOffer = false;
		}
		
		LOGGER.debug(">>>> isAcceptableOffer after = " + isAcceptableOffer);
        
        if(!isAcceptableOffer) {
        	return null;
        }
*/
        
        if (isAcceptableOffer) {
	        offer = new AllocatedOffer();
	        offer.setOfferId(currentOfferId);
	        offer.setOfferPgm(offerDetail.getOfferProgramCd());
	        offer.setOfferTs(new Timestamp(offerDetail.getLastUpdateTs().getTime()));
	        offer.setOfferProgramTypeCd(offerSubPgmTypeCd);
	        offer.setShoppingListCategoryId(offerDetail.getPrimaryCategoryId());
	        offer.setPriceMethodCd(DataHelper.replaceIfNull(offerDetail.getPriceMethodCd()));
	        offer.setExtlOfferId(offerDetail.getExternalOfferId());
	        offer.setClipStatus(clipStatus);
	        offer.setClipId(offerClipStatus.getOfferId());
	        offer.setPurchaseInd(PurchaseIndicator.PURCHASE_IND_BOUGHT); 
	        offer.getOfferDetail().setStartDt(displayStartDt);
	        offer.getOfferDetail().setEndDt(offerEndDt);
        }

        return offer;
    }
}
