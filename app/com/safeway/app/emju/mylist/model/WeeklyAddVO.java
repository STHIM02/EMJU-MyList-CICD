package com.safeway.app.emju.mylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeeklyAddVO {

	private String EventId;
	private String OfferId;
	private String ItemType;
	private String VersionId;
	private String PageVariantId;
	private String StoreEventId;
	private String OfferType;
	private String PositionNumber;
	private String DefaultImage;
	private String HeadLine;
	private String BodyCopy;
	private String SalePrice;
	private String RegularPrice;
	private String WasNowPrice;
	private String UOM;
	private String PriceCopy;
	private String SaveAmount;
	private String CallOutCopy;
	private String LimitText;
	private String LegalText;
	private String StartDate;
	private String EndDate;
	private String ProductCode;
	private String BarCode;
	private List<WSCategoryVO> Categories;
	private List<WSCustomPropertyVO> CustomProperties;
	
	@JsonProperty("EventId")
	public String getEventId() {
		return EventId;
	}
	public void setEventId(String eventId) {
		EventId = eventId;
	}
	@JsonProperty("OfferId")
	public String getOfferId() {
		return OfferId;
	}
	public void setOfferId(String offerId) {
		OfferId = offerId;
	}
	@JsonProperty("ItemType")
	public String getItemType() {
		return ItemType;
	}
	public void setItemType(String itemType) {
		ItemType = itemType;
	}
	@JsonProperty("VersionId")
	public String getVersionId() {
		return VersionId;
	}
	public void setVersionId(String versionId) {
		VersionId = versionId;
	}
	@JsonProperty("PageVariantId")
	public String getPageVariantId() {
		return PageVariantId;
	}
	public void setPageVariantId(String pageVariantId) {
		PageVariantId = pageVariantId;
	}
	@JsonProperty("StoreEventId")
	public String getStoreEventId() {
		return StoreEventId;
	}
	public void setStoreEventId(String storeEventId) {
		StoreEventId = storeEventId;
	}
	@JsonProperty("OfferType")
	public String getOfferType() {
		return OfferType;
	}
	public void setOfferType(String offerType) {
		OfferType = offerType;
	}
	@JsonProperty("PositionNumber")
	public String getPositionNumber() {
		return PositionNumber;
	}
	public void setPositionNumber(String positionNumber) {
		PositionNumber = positionNumber;
	}
	@JsonProperty("DefaultImage")
	public String getDefaultImage() {
		return DefaultImage;
	}
	public void setDefaultImage(String defaultImage) {
		DefaultImage = defaultImage;
	}
	@JsonProperty("HeadLine")
	public String getHeadLine() {
		return HeadLine;
	}
	public void setHeadLine(String headLine) {
		HeadLine = headLine;
	}
	@JsonProperty("BodyCopy")
	public String getBodyCopy() {
		return BodyCopy;
	}
	public void setBodyCopy(String bodyCopy) {
		BodyCopy = bodyCopy;
	}
	@JsonProperty("SalePrice")
	public String getSalePrice() {
		return SalePrice;
	}
	public void setSalePrice(String salePrice) {
		SalePrice = salePrice;
	}
	@JsonProperty("RegularPrice")
	public String getRegularPrice() {
		return RegularPrice;
	}
	public void setRegularPrice(String regularPrice) {
		RegularPrice = regularPrice;
	}
	@JsonProperty("WasNowPrice")
	public String getWasNowPrice() {
		return WasNowPrice;
	}
	public void setWasNowPrice(String wasNowPrice) {
		WasNowPrice = wasNowPrice;
	}
	@JsonProperty("UOM")
	public String getUOM() {
		return UOM;
	}
	public void setUOM(String uOM) {
		UOM = uOM;
	}
	@JsonProperty("PriceCopy")
	public String getPriceCopy() {
		return PriceCopy;
	}
	public void setPriceCopy(String priceCopy) {
		PriceCopy = priceCopy;
	}
	@JsonProperty("SaveAmount")
	public String getSaveAmount() {
		return SaveAmount;
	}
	public void setSaveAmount(String saveAmount) {
		SaveAmount = saveAmount;
	}
	@JsonProperty("CallOutCopy")
	public String getCallOutCopy() {
		return CallOutCopy;
	}
	public void setCallOutCopy(String callOutCopy) {
		CallOutCopy = callOutCopy;
	}
	@JsonProperty("LimitText")
	public String getLimitText() {
		return LimitText;
	}
	public void setLimitText(String limitText) {
		LimitText = limitText;
	}
	@JsonProperty("LegalText")
	public String getLegalText() {
		return LegalText;
	}
	public void setLegalText(String legalText) {
		LegalText = legalText;
	}
	@JsonProperty("StartDate")
	public String getStartDate() {
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	@JsonProperty("EndDate")
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	@JsonProperty("ProductCode")
	public String getProductCode() {
		return ProductCode;
	}
	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}
	@JsonProperty("BarCode")
	public String getBarCode() {
		return BarCode;
	}
	public void setBarCode(String barCode) {
		BarCode = barCode;
	}
	@JsonProperty("Categories")
	public List<WSCategoryVO> getCategories() {
		return Categories;
	}
	public void setCategories(List<WSCategoryVO> categories) {
		Categories = categories;
	}
	@JsonProperty("CustomProperties")
	public List<WSCustomPropertyVO> getCustomProperties() {
		return CustomProperties;
	}
	public void setCustomProperties(List<WSCustomPropertyVO> customProperties) {
		CustomProperties = customProperties;
	}
}
