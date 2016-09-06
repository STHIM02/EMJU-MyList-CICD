package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WSCategoryVO {

	private String CategoryNameType;
	private String CategoryName;
	private String CategoryType;
	
	@JsonProperty("CategoryNameType")
	public String getCategoryNameType() {
		return CategoryNameType;
	}
	public void setCategoryNameType(String categoryNameType) {
		CategoryNameType = categoryNameType;
	}
	@JsonProperty("CategoryName")
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	@JsonProperty("CategoryType")
	public String getCategoryType() {
		return CategoryType;
	}
	public void setCategoryType(String categoryType) {
		CategoryType = categoryType;
	}
}
