package com.safeway.app.emju.mylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListCountVO {

	private Integer noShoppingListItems;

	public Integer getNoShoppingListItems() {
		return noShoppingListItems;
	}

	public void setNoShoppingListItems(Integer noShoppingListItems) {
		this.noShoppingListItems = noShoppingListItems;
	}
	
}
