package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WSCustomPropertyVO {

	private String PropertyName;
	private String PropertyValue;
	private String CustomPropertyId;
	private String OfferId;
	
	@JsonProperty("PropertyName")
	public String getPropertyName() {
		return PropertyName;
	}
	public void setPropertyName(String propertyName) {
		PropertyName = propertyName;
	}
	@JsonProperty("PropertyValue")
	public String getPropertyValue() {
		return PropertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		PropertyValue = propertyValue;
	}
	@JsonProperty("CustomPropertyId")
	public String getCustomPropertyId() {
		return CustomPropertyId;
	}
	public void setCustomPropertyId(String customPropertyId) {
		CustomPropertyId = customPropertyId;
	}
	@JsonProperty("OfferId")
	public String getOfferId() {
		return OfferId;
	}
	public void setOfferId(String offerId) {
		OfferId = offerId;
	}
}
