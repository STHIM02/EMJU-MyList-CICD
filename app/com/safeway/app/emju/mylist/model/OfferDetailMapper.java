package com.safeway.app.emju.mylist.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mylist.constant.OfferConstants.OfferDetail;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferDetailMapper {
	
	private Map<String, Object> detail;
	
	public OfferDetailMapper() {
        super();
    }
	
	public OfferDetailMapper(final Map<String, Object> detail) {
        this.detail = detail;
    }
	
	public void setPriceMethodType(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_METHOD_TYPE, value);
        }
    }
	
	public void setPriceMethodSubType(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_METHOD_SUB_TYPE, value);
        }
    }
	
	public void setTitleDsc1(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.TITLE_DSC_1, value);
        }
    }
	
	public void setTitleDsc2(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.TITLE_DSC_2, value);
        }
    }
	
	public void setProdDsc1(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PROD_DSC_1, value);
        }
    }
	
	public void setProdDsc2(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PROD_DSC_2, value);
        }
    }
	
	public void setPriceTitle1(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_TITLE_1, value);
        }
    }
	
	public void setPriceValue1(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_VALUE_1, value);
        }
    }
	
	public void setPriceTitle2(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_TITLE_2, value);
        }
    }
	
	public void setPriceTitle2Type(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_TITLE_2_TYPE, value);
        }
    }
	
	public void setPriceValue2(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PRICE_VALUE_2, value);
        }
    }
	
	public void setOfferStartDt(final Date dateValue) {
        if (dateValue != null) {
            detail.put(OfferDetail.OFFER_START_DT, DataHelper.getJSONDate(dateValue));
        }
    }
	
	public void setOfferEndDt(final Date dateValue) {
        if (dateValue != null) {
            detail.put(OfferDetail.OFFER_END_DT, DataHelper.getJSONDate(dateValue));
        }
    }
	
	public void setPurchaseCountValue(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.PURCHASE_COUNT_VALUE, value);
        }
    }
	
	public String getPurchaseCountValue() {
        String value = null;
        value = (String) detail.get(OfferDetail.PURCHASE_COUNT_VALUE);
        return value != null ? value.trim() : "0";
    }
	
	public void setSavingsValue(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.SAVINGS_VALUE, value);
        }
    }
	
	public String getSavingsValue() {
        String value = null;
        value = (String) detail.get(OfferDetail.SAVINGS_VALUE);
        return value != null ? value.trim() : "0";
    }
	
	public void setSavingsPct(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.SAVINGS_PCT, value);
        }
    }
	
	public void setDisplayRank(final int value) {
        detail.put(OfferDetail.DISPLAY_RANK, value);
    }
	
	public void setCategoryRank(final int value) {
        detail.put(OfferDetail.CATEGORY_RANK, value);
    }
	
	public void setPurchaseRank(final int value) {
        detail.put(OfferDetail.PURCHASE_RANK, value);
    }
	
	public void setArrivalRank(final int value) {
        detail.put(OfferDetail.ARRIVAL_RANK, value);
    }
	
	public void setExpiryRank(final int value) {
        detail.put(OfferDetail.EXPIRY_RANK, value);
    }
	
	public void setCategoryNm(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.CATEGORY_NM, value);
        }
    }
	
	public void setCompetitorPriceAmount(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.COMPETITOR_PRICE_AMT, value);
        }
    }
	
	public void setOfferPrice(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.OFFER_PRICE, value);
        }
    }
	
	public String getOfferPrice() {
		
		return (String)detail.get(OfferDetail.OFFER_PRICE);
	}
	
	public void setCompetitorName(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.COMPETITOR_NM, value);
        }
    }
	
	public void setRegularRetailPriceAmount(final String strValue) {
        String value = DataHelper.replaceIfNull(strValue);
        if (ValidationHelper.isNonEmpty(value)) {
            detail.put(OfferDetail.REGULAR_RETAIL_PRICE_AMT, value);
        }
    }
	
	public void setOfferRankNbr(final int value) {
        detail.put(OfferDetail.RANK_NBR, value);

    }
	
	public void setHouseholdOfferRankNbr(final long value) {
        detail.put(OfferDetail.RANK_NBR, value);

    }
	
	public void setDefaultPriceZoneOfferRankNbr(final int value) {
        detail.put(OfferDetail.RANK_NBR, value);

    }
	
	public void setPreviouslyPurchaseInd(final String value) {
        detail.put(OfferDetail.PREVIOUSLY_PURCHASED_IND, value);
    }
	
	public Map<String, Object> getDetail() {
        return detail;
    }
	
	public void setDetail(final Map<String, Object> detail) {
        this.detail = detail;
    }
	
	public void setRedeemDate(final Date dateValue) {
        if (dateValue != null) {
            detail.put(OfferDetail.REDEEM_TS, DataHelper.getJSONDate(dateValue));
        }
    }
	
	public void setRedeemCount(final int value) {
        detail.put(OfferDetail.REDEEM_COUNT, value);
    }
	
	public void setStoreId(final int value) {
        detail.put(OfferDetail.STORE_ID, value);
    }
	
	public void setStoreName(final String value) {
        detail.put(OfferDetail.STORE_NAME, value);
    }

}
