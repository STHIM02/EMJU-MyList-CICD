package com.safeway.app.emju.mylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HierarchyVO {

	private List<CategoryHierarchyVO> categories;

	public List<CategoryHierarchyVO> getCategories() {
		return categories;
	}
	public void setCategories(List<CategoryHierarchyVO> categories) {
		this.categories = categories;
	}
}
