package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mylist.constant.Constants;

@JsonIgnoreProperties({"offerDetail","clubPrice"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingListItemVO {
	
	private String id;
	private String itemType;
	private String referenceId;
	private String categoryId;
	private String quantity;
	private String description;
	private String storeId ;
	private Boolean checked;
	private String title;
	private String addedDate;
	private String endDate;
	private String startDate;
	private String savingsValue;
	private String savingsType;
	private String usage;
	private String image;
	private String summary;
	private String  lastUpdatedDate;
	private String priceValue;
	private String promoPrice;
	private AllocatedOffer[] relatedOffers;
	private String code;
	private String msg;
	private Integer categoryIdByName;
	private String disclaimer;
	private String categoryName;
	private String updateDate;
	private Boolean deleted;
	private Boolean created;
	private Boolean updated;
	private String itemSubType;
	private String savingsCode;
	private String savingsSubCode;
	private OfferDetail offerDetail;
	private ClubPrice clubPrice;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = ValidationHelper.trimLengthInBytes(description, Constants.DESCR_MAX_SIZE);;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = ValidationHelper.trimLengthInBytes(title, Constants.TITLE_MAX_SIZE);
	}
	public String getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getSavingsValue() {
		return savingsValue;
	}
	public void setSavingsValue(String savingsValue) {
		this.savingsValue = savingsValue;
	}
	public String getSavingsType() {
		return savingsType;
	}
	public void setSavingsType(String savingsType) {
		this.savingsType = savingsType;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	public String getPriceValue() {
		return priceValue;
	}
	public void setPriceValue(String priceValue) {
		this.priceValue = priceValue;
	}
	public String getPromoPrice() {
		return promoPrice;
	}
	public void setPromoPrice(String promoPrice) {
		this.promoPrice = promoPrice;
	}
	public AllocatedOffer[] getRelatedOffers() {
		return relatedOffers;
	}
	public void setRelatedOffers(AllocatedOffer[] relatedOffers) {
		this.relatedOffers = relatedOffers;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getCategoryIdByName() {
		return categoryIdByName;
	}
	public void setCategoryIdByName(Integer categoryIdByName) {
		this.categoryIdByName = categoryIdByName;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public Boolean getCreated() {
		return created;
	}
	public void setCreated(Boolean created) {
		this.created = created;
	}
	public Boolean getUpdated() {
		return updated;
	}
	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}
	public String getItemSubType() {
		return itemSubType;
	}
	public void setItemSubType(String itemSubType) {
		this.itemSubType = itemSubType;
	}
	public String getSavingsCode() {
		return savingsCode;
	}
	public void setSavingsCode(String savingsCode) {
		this.savingsCode = savingsCode;
	}
	public String getSavingsSubCode() {
		return savingsSubCode;
	}
	public void setSavingsSubCode(String savingsSubCode) {
		this.savingsSubCode = savingsSubCode;
	}
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}
	public void setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
	}
	public ClubPrice getClubPrice() {
		return clubPrice;
	}
	public void setClubPrice(ClubPrice clubPrice) {
		this.clubPrice = clubPrice;
	}

}
