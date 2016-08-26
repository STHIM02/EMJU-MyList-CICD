package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WSCustomPropertyVO {

	private String PropertyName;
	private String PropertyValue;
	private String CustomPropertyId;
	private String OfferId;
	
	public String getPropertyName() {
		return PropertyName;
	}
	public void setPropertyName(String propertyName) {
		PropertyName = propertyName;
	}
	public String getPropertyValue() {
		return PropertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		PropertyValue = propertyValue;
	}
	public String getCustomPropertyId() {
		return CustomPropertyId;
	}
	public void setCustomPropertyId(String customPropertyId) {
		CustomPropertyId = customPropertyId;
	}
	public String getOfferId() {
		return OfferId;
	}
	public void setOfferId(String offerId) {
		OfferId = offerId;
	}
}
