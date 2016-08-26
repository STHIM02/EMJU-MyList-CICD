package com.safeway.app.emju.mylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
	private String EndDate;
	private String ProductCode;
	private String BarCode;
	private List<WSCategoryVO> Categories;
	private List<WSCustomPropertyVO> CustomProperties;
	
	public String getEventId() {
		return EventId;
	}
	public void setEventId(String eventId) {
		EventId = eventId;
	}
	public String getOfferId() {
		return OfferId;
	}
	public void setOfferId(String offerId) {
		OfferId = offerId;
	}
	public String getItemType() {
		return ItemType;
	}
	public void setItemType(String itemType) {
		ItemType = itemType;
	}
	public String getVersionId() {
		return VersionId;
	}
	public void setVersionId(String versionId) {
		VersionId = versionId;
	}
	public String getPageVariantId() {
		return PageVariantId;
	}
	public void setPageVariantId(String pageVariantId) {
		PageVariantId = pageVariantId;
	}
	public String getStoreEventId() {
		return StoreEventId;
	}
	public void setStoreEventId(String storeEventId) {
		StoreEventId = storeEventId;
	}
	public String getOfferType() {
		return OfferType;
	}
	public void setOfferType(String offerType) {
		OfferType = offerType;
	}
	public String getPositionNumber() {
		return PositionNumber;
	}
	public void setPositionNumber(String positionNumber) {
		PositionNumber = positionNumber;
	}
	public String getDefaultImage() {
		return DefaultImage;
	}
	public void setDefaultImage(String defaultImage) {
		DefaultImage = defaultImage;
	}
	public String getHeadLine() {
		return HeadLine;
	}
	public void setHeadLine(String headLine) {
		HeadLine = headLine;
	}
	public String getBodyCopy() {
		return BodyCopy;
	}
	public void setBodyCopy(String bodyCopy) {
		BodyCopy = bodyCopy;
	}
	public String getSalePrice() {
		return SalePrice;
	}
	public void setSalePrice(String salePrice) {
		SalePrice = salePrice;
	}
	public String getRegularPrice() {
		return RegularPrice;
	}
	public void setRegularPrice(String regularPrice) {
		RegularPrice = regularPrice;
	}
	public String getWasNowPrice() {
		return WasNowPrice;
	}
	public void setWasNowPrice(String wasNowPrice) {
		WasNowPrice = wasNowPrice;
	}
	public String getUOM() {
		return UOM;
	}
	public void setUOM(String uOM) {
		UOM = uOM;
	}
	public String getPriceCopy() {
		return PriceCopy;
	}
	public void setPriceCopy(String priceCopy) {
		PriceCopy = priceCopy;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	public String getProductCode() {
		return ProductCode;
	}
	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}
	public String getBarCode() {
		return BarCode;
	}
	public void setBarCode(String barCode) {
		BarCode = barCode;
	}
	public List<WSCategoryVO> getCategories() {
		return Categories;
	}
	public void setCategories(List<WSCategoryVO> categories) {
		Categories = categories;
	}
	public List<WSCustomPropertyVO> getCustomProperties() {
		return CustomProperties;
	}
	public void setCustomProperties(List<WSCustomPropertyVO> customProperties) {
		CustomProperties = customProperties;
	}
}
