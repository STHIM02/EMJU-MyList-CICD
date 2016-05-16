/* **************************************************************************
 * Copyright 2014 Safeway, Inc.
 *
 * This document/file contains proprietary data that is the property of
 * Safeway, Inc.  Information contained herein may not be used,
 * copied or disclosed in whole or in part except as permitted by a
 * written agreement signed by an officer of Safeway.
 *
 * Unauthorized use, copying or other reproduction of this document/file
 * is prohibited by law.
 *
 ***************************************************************************/

package com.safeway.app.emju.mylist.purchasehistory.model.json;


import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.safeway.app.emju.mylist.model.AllocatedOffer;

/**
 * 
 * @author sshar64
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties({ "purchaseCount", "lastPurchasedTs" })
public class PurchasedItemOffer {


    private Long upcId;
    private Long listClipId;
    private String itemType;
    private String clipStatus;
    private Long purchaseCount;
    private String titleDsc1;
    private String prodDsc1;
    private Long categoryRank;
    private Long purchaseRank;
    private Long purchaseRecencyRank;
    private Long offerRank;
    private Long alphaRank;
    private Long categoryId;
    private String categoryName;
    private Date lastPurchasedTs;
    private AllocatedOffer[] relatedOffers;

    /**
     * Returns the upcId.
     * 
     * @return Long
     */
    public Long getUpcId() {
        return upcId;
    }

    /**
     * @param upcId
     *            The upcId to set.
     */
    public void setUpcId(final Long upcId) {
        this.upcId = upcId;
    }

    /**
     * Returns the listClipId.
     * 
     * @return Long
     */
    public Long getListClipId() {
        return listClipId;
    }

    /**
     * @param listClipId
     *            The listClipId to set.
     */
    public void setListClipId(final Long listClipId) {
        this.listClipId = listClipId;
    }

    /**
     * Returns the itemType.
     * 
     * @return String
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType
     *            The itemType to set.
     */
    public void setItemType(final String itemType) {
        this.itemType = itemType;
    }

    /**
     * Returns the clipStatus.
     * 
     * @return String
     */
    public String getClipStatus() {
        return clipStatus;
    }

    /**
     * @param clipStatus
     *            The clipStatus to set.
     */
    public void setClipStatus(final String clipStatus) {
        this.clipStatus = clipStatus;
    }

    /**
     * Returns the purchaseCount.
     * 
     * @return Long
     */
    public Long getPurchaseCount() {
        return purchaseCount;
    }

    /**
     * @param purchaseCount
     *            The purchaseCount to set.
     */
    public void setPurchaseCount(final Long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    /**
     * Returns the titleDsc1.
     * 
     * @return String
     */
    public String getTitleDsc1() {
        return titleDsc1;
    }

    /**
     * @param titleDsc1
     *            The titleDsc1 to set.
     */
    public void setTitleDsc1(final String titleDsc1) {
        this.titleDsc1 = titleDsc1;
    }

    /**
     * Returns the prodDsc1.
     * 
     * @return String
     */
    public String getProdDsc1() {
        return prodDsc1;
    }

    /**
     * @param prodDsc1
     *            The prodDsc1 to set.
     */
    public void setProdDsc1(final String prodDsc1) {
        this.prodDsc1 = prodDsc1;
    }

    /**
     * Returns the categoryRank.
     * 
     * @return Long
     */
    public Long getCategoryRank() {
        return categoryRank;
    }

    /**
     * @param categoryRank
     *            The categoryRank to set.
     */
    public void setCategoryRank(final Long categoryRank) {
        this.categoryRank = categoryRank;
    }

    /**
     * Returns the purchaseRank.
     * 
     * @return Long
     */
    public Long getPurchaseRank() {
        return purchaseRank;
    }

    /**
     * @param purchaseRank
     *            The purchaseRank to set.
     */
    public void setPurchaseRank(final Long purchaseRank) {
        this.purchaseRank = purchaseRank;
    }
    
    /**
     * Returns the purchaseRecencyRank.
     * 
     * @return Long
     */
    public Long getPurchaseRecencyRank() {
        return purchaseRecencyRank;
    }

    /**
     * @param purchaseRecencyRank
     *            The purchaseRecencyRank to set.
     */
    public void setPurchaseRecencyRank(final Long purchaseRecencyRank) {
        this.purchaseRecencyRank = purchaseRecencyRank;
    }

    /**
     * Returns the offerRank.
     * 
     * @return Long
     */
    public Long getOfferRank() {
        return offerRank;
    }

    /**
     * @param offerRank
     *            The offerRank to set.
     */
    public void setOfferRank(final Long offerRank) {
        this.offerRank = offerRank;
    }

    /**
     * Returns the alphaRank.
     * 
     * @return Long
     */
    public Long getAlphaRank() {
        return alphaRank;
    }

    /**
     * @param alphaRank
     *            The alphaRank to set.
     */
    public void setAlphaRank(final Long alphaRank) {
        this.alphaRank = alphaRank;
    }

    /**
     * Returns the categoryId.
     * 
     * @return Long
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId
     *            The categoryId to set.
     */
    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the categoryName.
     * 
     * @return String
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }
    
    /**
     * Returns the lastPurchasedTs.
     * 
     * @return Date
     */
    public Date getLastPurchasedTs() {
        return lastPurchasedTs;
    }

    /**
     * @param lastPurchasedTs
     *            The lastPurchasedTs to set.
     */
    public void setLastPurchasedTs(final Date lastPurchasedTs) {
        this.lastPurchasedTs = lastPurchasedTs;
    }

    /**
     * Returns the related offers.
     * 
     * @return AllocatedOffer[]
     */
    public AllocatedOffer[] getRelatedOffers() {
        return relatedOffers;
    }

    /**
     * @param relatedOffers
     *            The offers to set.
     */
     public void setRelatedOffers(final AllocatedOffer[] relatedOffers) {
        this.relatedOffers = relatedOffers;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PurchasedItemOffer [upcId=");
        builder.append(upcId);
        builder.append(", listClipId=");
        builder.append(listClipId);
        builder.append(", itemType=");
        builder.append(itemType);
        builder.append(", clipStatus=");
        builder.append(clipStatus);
        builder.append(", purchaseCount=");
        builder.append(purchaseCount);
        builder.append(", titleDsc1=");
        builder.append(titleDsc1);
        builder.append(", prodDsc1=");
        builder.append(prodDsc1);
        builder.append(", categoryRank=");
        builder.append(categoryRank);
        builder.append(", purchaseRank=");
        builder.append(purchaseRank);
        builder.append(", offerRank=");
        builder.append(offerRank);
        builder.append(", alphaRank=");
        builder.append(alphaRank);
        builder.append(", categoryId=");
        builder.append(categoryId);
        builder.append(", categoryName=");
        builder.append(categoryName);
        builder.append(", relatedOffers=");
       // builder.append(Arrays.toString(relatedOffers));
        builder.append("]");
        return builder.toString();
    }

}
