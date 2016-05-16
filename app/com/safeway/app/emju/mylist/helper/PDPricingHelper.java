package com.safeway.app.emju.mylist.helper;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.helper.DataHelper;

public class PDPricingHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PDPricingHelper.class);
	public static final String PRICE_TITLE_2_TYPE_SFWY = "S";
    public static final String TITLE_REGULAR_PRICE = "Our Regular Price";
    
    public PDPricing calculatePricing(final String offerPrice1, final String regRetailPriceAmt) {
    	
    	Double anOfferPrice = DataHelper.parseDouble(offerPrice1);
    	Double aRegPrice = DataHelper.parseDouble(regRetailPriceAmt);
    	
    	if (LOGGER.isDebugEnabled()) {
            LOGGER.info("calculatePricing - anOfferPrice: " + anOfferPrice);
            LOGGER.info(".calculatePricing - aRegPrice: " + aRegPrice);
        }

        return this.calculatePricing(anOfferPrice, aRegPrice);
    }
    
    public PDPricing calculatePricing(final Double offerPrice1, final Double regRetailPriceAmt) {

        PDPricing pricing = new PDPricing();

        // Price diff between regular and Offer
        Double regOfferPriceDiff = new Double("0");
        if (offerPrice1 != null && regRetailPriceAmt != null) {
            regOfferPriceDiff = regRetailPriceAmt - offerPrice1;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info(".calculatePricing - dRegOfferPriceDiff: " + regOfferPriceDiff.floatValue());
        }

        if (regOfferPriceDiff.floatValue() >= 0.1) {
            pricing.setPriceTitleType(PRICE_TITLE_2_TYPE_SFWY);
            pricing.setPriceTitle(TITLE_REGULAR_PRICE);
            pricing.setPriceValue(String.valueOf(regRetailPriceAmt));
        }
        else {
            pricing.setPriceTitleType(null);
            pricing.setPriceTitle(" ");
            pricing.setPriceValue(null);
        }

        return pricing;
    }

}
