package com.safeway.app.emju.mylist.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.safeway.app.emju.cache.entity.OfferDetail;

@JsonIgnoreProperties({"offerInfo"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllocatedOffer {
	
	private Long offerId;
	private String offerPgm;
	private String offerProgramTypeCd;
	private String offerProvider;
	private String offerStatus;
	private Timestamp offerTs;
	private String clipStatus = "U";
	private Long clipId;
	private Map<String, String[]> hierarchies;
	private final AllocatedOfferDetail offerDetail = new AllocatedOfferDetail();
	private OfferDetail offerInfo;
    private String extlOfferId;
    private String purchaseInd;
    private String categoryName;
    private String titleDescription;
    private Long purchaseHistOfferId;
    private int purchaseHistRank;
    private int hhPurchaseCategoryRank;
    private int defaultCategoryRank;
    private Integer hhOfferRank;
    private String offerCategoryTypeCd;
    private String priceMethodCd;
    private int shoppingListCategoryId;
    private String imageId;
    private Date offerStartDt;
    private String offerEndDt;
    private String usageType;
    private String priceType;
    private String priceSubType;
    private String vndrBannerCd;
    private String titleDsc1;
    private String titleDsc2;
    private String prodDsc1;
    private String prodDsc2;
    private String priceTitle1;
    private String priceValue1;
    private String savingsValue;
    private String disclaimer;
    private Long displayRank;
    private boolean defaultAllocation;
    private String listStatus = "U";
    
	public String getListStatus() {
		return listStatus;
	}
	public void setListStatus(String listStatus) {
		this.listStatus = listStatus;
	}
	public Long getOfferId() {
		return offerId;
	}
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	public String getOfferPgm() {
		return offerPgm;
	}
	public void setOfferPgm(String offerPgm) {
		this.offerPgm = offerPgm;
	}
	public String getOfferProgramTypeCd() {
		return offerProgramTypeCd;
	}
	public void setOfferProgramTypeCd(String offerProgramTypeCd) {
		this.offerProgramTypeCd = offerProgramTypeCd;
	}
	public String getOfferProvider() {
		return offerProvider;
	}
	public void setOfferProvider(String offerProvider) {
		this.offerProvider = offerProvider;
	}
	public String getOfferStatus() {
		return offerStatus;
	}
	public void setOfferStatus(String offerStatus) {
		this.offerStatus = offerStatus;
	}
	public Timestamp getOfferTs() {
		return offerTs;
	}
	public void setOfferTs(Timestamp offerTs) {
		this.offerTs = offerTs;
	}
	public String getClipStatus() {
		return clipStatus;
	}
	public void setClipStatus(String clipStatus) {
		this.clipStatus = clipStatus;
	}
	public Long getClipId() {
		return clipId;
	}
	public void setClipId(Long clipId) {
		if (clipId == 0) {
            this.clipId = null;
        }
        this.clipId = clipId;
	}
	public Map<String, String[]> getHierarchies() {
		return hierarchies;
	}
	public void setHierarchies(Map<String, String[]> hierarchies) {
		this.hierarchies = hierarchies;
	}

	public AllocatedOfferDetail getOfferDetail() {
		return offerDetail;
	}
	public OfferDetail getOfferInfo() {
		return offerInfo;
	}
	public void setOfferInfo(OfferDetail offerInfo) {
		this.offerInfo = offerInfo;
	}
	public String getExtlOfferId() {
		return extlOfferId;
	}
	public void setExtlOfferId(String extlOfferId) {
		this.extlOfferId = extlOfferId;
	}
	public String getPurchaseInd() {
		return purchaseInd;
	}
	public void setPurchaseInd(String purchaseInd) {
		this.purchaseInd = purchaseInd;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getTitleDescription() {
		return titleDescription;
	}
	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}
	public Long getPurchaseHistOfferId() {
		return purchaseHistOfferId;
	}
	public void setPurchaseHistOfferId(Long purchaseHistOfferId) {
		this.purchaseHistOfferId = purchaseHistOfferId;
	}
	public int getPurchaseHistRank() {
		return purchaseHistRank;
	}
	public void setPurchaseHistRank(int purchaseHistRank) {
		this.purchaseHistRank = purchaseHistRank;
	}
	public int getHhPurchaseCategoryRank() {
		return hhPurchaseCategoryRank;
	}
	public void setHhPurchaseCategoryRank(int hhPurchaseCategoryRank) {
		this.hhPurchaseCategoryRank = hhPurchaseCategoryRank;
	}
	public int getDefaultCategoryRank() {
		return defaultCategoryRank;
	}
	public void setDefaultCategoryRank(int defaultCategoryRank) {
		this.defaultCategoryRank = defaultCategoryRank;
	}
	public Integer getHhOfferRank() {
		return hhOfferRank;
	}
	public void setHhOfferRank(Integer hhOfferRank) {
		this.hhOfferRank = hhOfferRank;
	}
	public String getOfferCategoryTypeCd() {
		return offerCategoryTypeCd;
	}
	public void setOfferCategoryTypeCd(String offerCategoryTypeCd) {
		this.offerCategoryTypeCd = offerCategoryTypeCd;
	}
	public String getPriceMethodCd() {
		return priceMethodCd;
	}
	public void setPriceMethodCd(String priceMethodCd) {
		this.priceMethodCd = priceMethodCd;
	}
	public int getShoppingListCategoryId() {
		return shoppingListCategoryId;
	}
	public void setShoppingListCategoryId(int shoppingListCategoryId) {
		this.shoppingListCategoryId = shoppingListCategoryId;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public Date getOfferStartDt() {
		return offerStartDt;
	}
	public void setOfferStartDt(Date offerStartDt) {
		this.offerStartDt = offerStartDt;
	}
	public String getOfferEndDt() {
		return offerEndDt;
	}
	public void setOfferEndDt(String offerEndDt) {
		this.offerEndDt = offerEndDt;
	}
	public String getUsageType() {
		return usageType;
	}
	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public String getPriceSubType() {
		return priceSubType;
	}
	public void setPriceSubType(String priceSubType) {
		this.priceSubType = priceSubType;
	}
	public String getVndrBannerCd() {
		return vndrBannerCd;
	}
	public void setVndrBannerCd(String vndrBannerCd) {
		this.vndrBannerCd = vndrBannerCd;
	}
	public String getTitleDsc1() {
		return titleDsc1;
	}
	public void setTitleDsc1(String titleDsc1) {
		this.titleDsc1 = titleDsc1;
	}
	public String getTitleDsc2() {
		return titleDsc2;
	}
	public void setTitleDsc2(String titleDsc2) {
		this.titleDsc2 = titleDsc2;
	}
	public String getProdDsc1() {
		return prodDsc1;
	}
	public void setProdDsc1(String prodDsc1) {
		this.prodDsc1 = prodDsc1;
	}
	public String getProdDsc2() {
		return prodDsc2;
	}
	public void setProdDsc2(String prodDsc2) {
		this.prodDsc2 = prodDsc2;
	}
	public String getPriceTitle1() {
		return priceTitle1;
	}
	public void setPriceTitle1(String priceTitle1) {
		this.priceTitle1 = priceTitle1;
	}
	public String getPriceValue1() {
		return priceValue1;
	}
	public void setPriceValue1(String priceValue1) {
		this.priceValue1 = priceValue1;
	}
	public String getSavingsValue() {
		return savingsValue;
	}
	public void setSavingsValue(String savingsValue) {
		this.savingsValue = savingsValue;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public Long getDisplayRank() {
		return displayRank;
	}
	public void setDisplayRank(Long displayRank) {
		this.displayRank = displayRank;
	}
	public boolean isDefaultAllocation() {
		return defaultAllocation;
	}
	public void setDefaultAllocation(boolean defaultAllocation) {
		this.defaultAllocation = defaultAllocation;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (offerId == null ? 0 : offerId.hashCode());
        return result;
    }
	
	@Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AllocatedOffer other = (AllocatedOffer) obj;
        if (offerId == null) {
            if (other.offerId != null) {
                return false;
            }
        }
        else if (!offerId.equals(other.offerId)) {
            return false;
        }
        return true;
    }
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AllocatedOffer [offerId=");
        builder.append(offerId);
        builder.append(", offerPgm=");
        builder.append(offerPgm);
        builder.append(", offerProgramTypeCd=");
        builder.append(offerProgramTypeCd);
        builder.append(", offerProvider=");
        builder.append(offerProvider);
        builder.append(", offerStatus=");
        builder.append(offerStatus);
        builder.append(", offerTs=");
        builder.append(offerTs);
        builder.append(", clipStatus=");
        builder.append(clipStatus);
        builder.append(", clipId=");
        builder.append(clipId);
        builder.append(", hierarchies=");
        builder.append(hierarchies);
        builder.append(", offerDetail=");
        builder.append(offerDetail);
        builder.append(", extlOfferId=");
        builder.append(extlOfferId);
        builder.append(", purchaseInd=");
        builder.append(purchaseInd);
        builder.append(", categoryName=");
        builder.append(categoryName);
        builder.append(", titleDescription=");
        builder.append(titleDescription);
        builder.append(", purchaseHistOfferId=");
        builder.append(purchaseHistOfferId);
        builder.append(", purchaseHistRank=");
        builder.append(purchaseHistRank);
        builder.append(", hhPurchaseCategoryRank=");
        builder.append(hhPurchaseCategoryRank);
        builder.append(", defaultCategoryRank=");
        builder.append(defaultCategoryRank);
        builder.append(", hhOfferRank=");
        builder.append(hhOfferRank);
        builder.append(", offerCategoryTypeCd=");
        builder.append(offerCategoryTypeCd);
        builder.append(", priceMethodCd=");
        builder.append(priceMethodCd);
        builder.append(", shoppingListCategoryId=");
        builder.append(shoppingListCategoryId);
        builder.append(", imageId=");
        builder.append(imageId);
        builder.append(", offerStartDt=");
        builder.append(offerStartDt);
        builder.append(", offerEndDt=");
        builder.append(offerEndDt);
        builder.append(", usageType=");
        builder.append(usageType);
        builder.append(", priceType=");
        builder.append(priceType);
        builder.append(", priceSubType=");
        builder.append(priceSubType);
        builder.append(", vndrBannerCd=");
        builder.append(vndrBannerCd);
        builder.append(", titleDsc1=");
        builder.append(titleDsc1);
        builder.append(", titleDsc2=");
        builder.append(titleDsc2);
        builder.append(", prodDsc1=");
        builder.append(prodDsc1);
        builder.append(", prodDsc2=");
        builder.append(prodDsc2);
        builder.append(", priceTitle1=");
        builder.append(priceTitle1);
        builder.append(", priceValue1=");
        builder.append(priceValue1);
        builder.append(", savingsValue=");
        builder.append(savingsValue);
        builder.append(", disclaimer=");
        builder.append(disclaimer);
        builder.append(", displayRank=");
        builder.append(displayRank);
        builder.append(", defaultAllocation=");
        builder.append(defaultAllocation);
        builder.append("]");
        return builder.toString();
    }

}
