/* **************************************************************************
 * Copyright 2015 Albertsons Safeway.
 *
 * This document/file contains proprietary data that is the property of
 * Albertsons Safeway.  Information contained herein may not be used,
 * copied or disclosed in whole or in part except as permitted by a
 * written agreement signed by an officer of Albertsons Safeway.
 *
 * Unauthorized use, copying or other reproduction of this document/file
 * is prohibited by law.
 *
 ***************************************************************************/

package com.safeway.app.emju.mylist.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * 
 * @author sshar64
 *
 */
@JsonIgnoreProperties({ "startDt", "endDt", "rank", "regularPrice", "offerPrice", "previouslyPurchaseInd",
    "purchaseCount", "purchaseHistOfferId", "purchaseHistRank", "hhPurchaseCategoryRank", "defaultCategoryRank",
    "hhOfferRank", "clipTs", "defaultAllocation", "priceMethod", "primaryCategoryId" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllocatedOfferDetail {

    private String titleDsc1;
    private String titleDsc2;
    private String titleDsc3;
    private String prodDsc1;
    private String prodDsc2;
    private String priceTitle1;
    private String priceValue1;
    private String priceTitle2;
    private String priceTitle2Type;
    private String priceValue2;
    private String priceType;
    private String priceSubType;
    private String offerStartDt;
    private String offerStartDate;
    private String offerEndDt;
    private String offerEndDate;
    private String savingsValue;
    private String savingsPct;
    private String brandName;
    private String categoryName; // YCS

    private Long displayRank;
    private Long purchaseRank;
    private Long arrivalRank;
    private Long expiryRank;
    private Long categoryRank;
    private Long frequencyRank;
    private Long savingsRank;

    // Sorting or Filtering
    private String priceMethod;
    private Integer primaryCategoryId;
    private Date startDt;
    private Date endDt;
    private Long rank = Long.MAX_VALUE;
    private String regularPrice;
    private String offerPrice;
    private Integer previouslyPurchaseInd;
    private Integer purchaseCount;
    private Long purchaseHistOfferId;
    private Long purchaseHistRank;
    private Long hhPurchaseCategoryRank;
    private Long defaultCategoryRank;
    private Long hhOfferRank;
    private Date clipTs;
    private boolean defaultAllocation;

    /**
     * @return the titleDsc1
     */
    public String getTitleDsc1() {
        return titleDsc1;
    }

    /**
     * @param titleDsc1
     *            the titleDsc1 to set
     */
    public void setTitleDsc1(final String titleDsc1) {
        this.titleDsc1 = titleDsc1;
    }

    /**
     * @return the titleDsc2
     */
    public String getTitleDsc2() {
        return titleDsc2;
    }

    /**
     * @param titleDsc2
     *            the titleDsc2 to set
     */
    public void setTitleDsc2(final String titleDsc2) {
        this.titleDsc2 = titleDsc2;
    }

    /**
     * @return the titleDsc3
     */
    public String getTitleDsc3() {
        return titleDsc3;
    }

    /**
     * @param titleDsc3
     *            the titleDsc3 to set
     */
    public void setTitleDsc3(final String titleDsc3) {
        this.titleDsc3 = titleDsc3;
    }

    /**
     * @return the prodDsc1
     */
    public String getProdDsc1() {
        return prodDsc1;
    }

    /**
     * @param prodDsc1
     *            the prodDsc1 to set
     */
    public void setProdDsc1(final String prodDsc1) {
        this.prodDsc1 = prodDsc1;
    }

    /**
     * @return the prodDsc2
     */
    public String getProdDsc2() {
        return prodDsc2;
    }

    /**
     * @param prodDsc2
     *            the prodDsc2 to set
     */
    public void setProdDsc2(final String prodDsc2) {
        this.prodDsc2 = prodDsc2;
    }

    /**
     * @return the priceTitle1
     */
    public String getPriceTitle1() {
        return priceTitle1;
    }

    /**
     * @param priceTitle1
     *            the priceTitle1 to set
     */
    public void setPriceTitle1(final String priceTitle1) {
        this.priceTitle1 = priceTitle1;
    }

    /**
     * @return the priceValue1
     */
    public String getPriceValue1() {
        return priceValue1;
    }

    /**
     * @param priceValue1
     *            the priceValue1 to set
     */
    public void setPriceValue1(final String priceValue1) {
        this.priceValue1 = priceValue1;
    }

    /**
     * @return the priceTitle2
     */
    public String getPriceTitle2() {
        return priceTitle2;
    }

    /**
     * @param priceTitle2
     *            the priceTitle2 to set
     */
    public void setPriceTitle2(final String priceTitle2) {
        this.priceTitle2 = priceTitle2;
    }

    /**
     * @return the priceTitle2Type
     */
    public String getPriceTitle2Type() {
        return priceTitle2Type;
    }

    /**
     * @param priceTitle2Type
     *            the priceTitle2Type to set
     */
    public void setPriceTitle2Type(final String priceTitle2Type) {
        this.priceTitle2Type = priceTitle2Type;
    }

    /**
     * @return the priceValue2
     */
    public String getPriceValue2() {
        return priceValue2;
    }

    /**
     * @param priceValue2
     *            the priceValue2 to set
     */
    public void setPriceValue2(final String priceValue2) {
        this.priceValue2 = priceValue2;
    }

    /**
     * @return the priceType
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * @param priceType
     *            the priceType to set
     */
    public void setPriceType(final String priceType) {
        this.priceType = priceType;
    }

    /**
     * @return the priceSubType
     */
    public String getPriceSubType() {
        return priceSubType;
    }

    /**
     * @param priceSubType
     *            the priceSubType to set
     */
    public void setPriceSubType(final String priceSubType) {
        this.priceSubType = priceSubType;
    }

    /**
     * @return the offerStartDt
     */
    public String getOfferStartDt() {
        return offerStartDt;
    }

    /**
     * @param offerStartDt
     *            the offerStartDt to set
     */
    public void setOfferStartDt(final String offerStartDt) {
        this.offerStartDt = offerStartDt;
    }

    /**
     * @return the offerStartDate
     */
    public String getOfferStartDate() {
        return offerStartDate;
    }

    /**
     * @param offerStartDate
     *            the offerStartDate to set
     */
    public void setOfferStartDate(final String offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    /**
     * @return the offerEndDt
     */
    public String getOfferEndDt() {
        return offerEndDt;
    }

    /**
     * @param offerEndDt
     *            the offerEndDt to set
     */
    public void setOfferEndDt(final String offerEndDt) {
        this.offerEndDt = offerEndDt;
    }

    /**
     * @return the offerEndDate
     */
    public String getOfferEndDate() {
        return offerEndDate;
    }

    /**
     * @param offerEndDate
     *            the offerEndDate to set
     */
    public void setOfferEndDate(final String offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    /**
     * @return the savingsValue
     */
    public String getSavingsValue() {
        return savingsValue;
    }

    /**
     * @param savingsValue
     *            the savingsValue to set
     */
    public void setSavingsValue(final String savingsValue) {
        this.savingsValue = savingsValue;
    }

    /**
     * @return the savingsPct
     */
    public String getSavingsPct() {
        return savingsPct;
    }

    /**
     * @param savingsPct
     *            the savingsPct to set
     */
    public void setSavingsPct(final String savingsPct) {
        this.savingsPct = savingsPct;
    }

    /**
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName
     *            the brandName to set
     */
    public void setBrandName(final String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName
     *            the categoryName to set
     */
    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the displayRank
     */
    public Long getDisplayRank() {
        return displayRank;
    }

    /**
     * @param displayRank
     *            the displayRank to set
     */
    public void setDisplayRank(final Long displayRank) {
        this.displayRank = displayRank;
    }

    /**
     * @return the purchaseRank
     */
    public Long getPurchaseRank() {
        return purchaseRank;
    }

    /**
     * @param purchaseRank
     *            the purchaseRank to set
     */
    public void setPurchaseRank(final Long purchaseRank) {
        this.purchaseRank = purchaseRank;
    }

    /**
     * @return the arrivalRank
     */
    public Long getArrivalRank() {
        return arrivalRank;
    }

    /**
     * @param arrivalRank
     *            the arrivalRank to set
     */
    public void setArrivalRank(final Long arrivalRank) {
        this.arrivalRank = arrivalRank;
    }

    /**
     * @return the expiryRank
     */
    public Long getExpiryRank() {
        return expiryRank;
    }

    /**
     * @param expiryRank
     *            the expiryRank to set
     */
    public void setExpiryRank(final Long expiryRank) {
        this.expiryRank = expiryRank;
    }

    /**
     * @return the categoryRank
     */
    public Long getCategoryRank() {
        return categoryRank;
    }

    /**
     * @param categoryRank
     *            the categoryRank to set
     */
    public void setCategoryRank(final Long categoryRank) {
        this.categoryRank = categoryRank;
    }

    /**
     * @return the frequencyRank
     */
    public Long getFrequencyRank() {
        return frequencyRank;
    }

    /**
     * @param frequencyRank
     *            the frequencyRank to set
     */
    public void setFrequencyRank(final Long frequencyRank) {
        this.frequencyRank = frequencyRank;
    }

    /**
     * @return the savingsRank
     */
    public Long getSavingsRank() {
        return savingsRank;
    }

    /**
     * @param savingsRank
     *            the savingsRank to set
     */
    public void setSavingsRank(final Long savingsRank) {
        this.savingsRank = savingsRank;
    }

    /**
     * @return the priceMethod
     */
    public String getPriceMethod() {
        return priceMethod;
    }

    /**
     * @param priceMethod
     *            the priceMethod to set
     */
    public void setPriceMethod(final String priceMethod) {
        this.priceMethod = priceMethod;
    }

    /**
     * @return the primaryCategoryId
     */
    public Integer getPrimaryCategoryId() {
        return primaryCategoryId;
    }

    /**
     * @param primaryCategoryId
     *            the primaryCategoryId to set
     */
    public void setPrimaryCategoryId(final Integer primaryCategoryId) {
        this.primaryCategoryId = primaryCategoryId;
    }

    /**
     * @return the startDt
     */
    public Date getStartDt() {
        return startDt;
    }

    /**
     * @param startDt
     *            the startDt to set
     */
    public void setStartDt(final Date startDt) {
        this.startDt = startDt;
    }

    /**
     * @return the endDt
     */
    public Date getEndDt() {
        return endDt;
    }

    /**
     * @param endDt
     *            the endDt to set
     */
    public void setEndDt(final Date endDt) {
        this.endDt = endDt;
    }

    /**
     * @return the rank
     */
    public Long getRank() {
        return rank;
    }

    /**
     * @param rank
     *            the rank to set
     */
    public void setRank(final Long rank) {
        this.rank = rank;
    }

    /**
     * @return the regularPrice
     */
    public String getRegularPrice() {
        return regularPrice;
    }

    /**
     * @param regularPrice
     *            the regularPrice to set
     */
    public void setRegularPrice(final String regularPrice) {
        this.regularPrice = regularPrice;
    }

    /**
     * @return the offerPrice
     */
    public String getOfferPrice() {
        return offerPrice;
    }

    /**
     * @param offerPrice
     *            the offerPrice to set
     */
    public void setOfferPrice(final String offerPrice) {
        this.offerPrice = offerPrice;
    }

    /**
     * @return the previouslyPurchaseInd
     */
    public Integer getPreviouslyPurchaseInd() {
        return previouslyPurchaseInd;
    }

    /**
     * @param previouslyPurchaseInd
     *            the previouslyPurchaseInd to set
     */
    public void setPreviouslyPurchaseInd(final Integer previouslyPurchaseInd) {
        this.previouslyPurchaseInd = previouslyPurchaseInd;
    }

    /**
     * @return the purchaseCount
     */
    public Integer getPurchaseCount() {
        return purchaseCount;
    }

    /**
     * @param purchaseCount
     *            the purchaseCount to set
     */
    public void setPurchaseCount(final Integer purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    /**
     * @return the purchaseHistOfferId
     */
    public Long getPurchaseHistOfferId() {
        return purchaseHistOfferId;
    }

    /**
     * @param purchaseHistOfferId
     *            the purchaseHistOfferId to set
     */
    public void setPurchaseHistOfferId(final Long purchaseHistOfferId) {
        this.purchaseHistOfferId = purchaseHistOfferId;
    }

    /**
     * @return the purchaseHistRank
     */
    public Long getPurchaseHistRank() {
        return purchaseHistRank;
    }

    /**
     * @param purchaseHistRank
     *            the purchaseHistRank to set
     */
    public void setPurchaseHistRank(final Long purchaseHistRank) {
        this.purchaseHistRank = purchaseHistRank;
    }

    /**
     * @return the hhPurchaseCategoryRank
     */
    public Long getHhPurchaseCategoryRank() {
        return hhPurchaseCategoryRank;
    }

    /**
     * @param hhPurchaseCategoryRank
     *            the hhPurchaseCategoryRank to set
     */
    public void setHhPurchaseCategoryRank(final Long hhPurchaseCategoryRank) {
        this.hhPurchaseCategoryRank = hhPurchaseCategoryRank;
    }

    /**
     * @return the defaultCategoryRank
     */
    public Long getDefaultCategoryRank() {
        return defaultCategoryRank;
    }

    /**
     * @param defaultCategoryRank
     *            the defaultCategoryRank to set
     */
    public void setDefaultCategoryRank(final Long defaultCategoryRank) {
        this.defaultCategoryRank = defaultCategoryRank;
    }

    /**
     * @return the hhOfferRank
     */
    public Long getHhOfferRank() {
        return hhOfferRank;
    }

    /**
     * @param hhOfferRank
     *            the hhOfferRank to set
     */
    public void setHhOfferRank(final Long hhOfferRank) {
        this.hhOfferRank = hhOfferRank;
    }

    /**
     * @return the clipTs
     */
    public Date getClipTs() {
        return clipTs;
    }

    /**
     * @param clipTs
     *            the clipTs to set
     */
    public void setClipTs(final Date clipTs) {
        this.clipTs = clipTs;
    }

    /**
     * @return the defaultAllocation
     */
    public boolean isDefaultAllocation() {
        return defaultAllocation;
    }

    /**
     * @param defaultAllocation
     *            the defaultAllocation to set
     */
    public void setDefaultAllocation(final boolean defaultAllocation) {
        this.defaultAllocation = defaultAllocation;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AllocatedOfferDetail [titleDsc1=");
        builder.append(titleDsc1);
        builder.append(", titleDsc2=");
        builder.append(titleDsc2);
        builder.append(", titleDsc3=");
        builder.append(titleDsc3);
        builder.append(", prodDsc1=");
        builder.append(prodDsc1);
        builder.append(", prodDsc2=");
        builder.append(prodDsc2);
        builder.append(", priceTitle1=");
        builder.append(priceTitle1);
        builder.append(", priceValue1=");
        builder.append(priceValue1);
        builder.append(", priceTitle2=");
        builder.append(priceTitle2);
        builder.append(", priceTitle2Type=");
        builder.append(priceTitle2Type);
        builder.append(", priceValue2=");
        builder.append(priceValue2);
        builder.append(", priceType=");
        builder.append(priceType);
        builder.append(", priceSubType=");
        builder.append(priceSubType);
        builder.append(", offerStartDt=");
        builder.append(offerStartDt);
        builder.append(", offerEndDt=");
        builder.append(offerEndDt);
        builder.append(", savingsValue=");
        builder.append(savingsValue);
        builder.append(", savingsPct=");
        builder.append(savingsPct);
        builder.append(", brandName=");
        builder.append(brandName);
        builder.append(", categoryName=");
        builder.append(categoryName);
        builder.append(", displayRank=");
        builder.append(displayRank);
        builder.append(", purchaseRank=");
        builder.append(purchaseRank);
        builder.append(", arrivalRank=");
        builder.append(arrivalRank);
        builder.append(", expiryRank=");
        builder.append(expiryRank);
        builder.append(", categoryRank=");
        builder.append(categoryRank);
        builder.append(", frequencyRank=");
        builder.append(frequencyRank);
        builder.append(", savingsRank=");
        builder.append(savingsRank);
        builder.append("]");
        return builder.toString();
    }

}
