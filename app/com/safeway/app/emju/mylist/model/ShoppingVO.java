package com.safeway.app.emju.mylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingVO {
	
	private List<ShoppingListVO> shoppingLists;	
	private String lastDeltaTS;	
	private List<ErrorVO> errors;
	
	public List<ShoppingListVO> getShoppingLists() {
		return shoppingLists;
	}
	public void setShoppingLists(List<ShoppingListVO> shoppingLists) {
		this.shoppingLists = shoppingLists;
	}
	public String getLastDeltaTS() {
		return lastDeltaTS;
	}
	public void setLastDeltaTS(String lastDeltaTS) {
		this.lastDeltaTS = lastDeltaTS;
	}
	public List<ErrorVO> getErrors() {
		return errors;
	}
	public void setErrors(List<ErrorVO> errors) {
		this.errors = errors;
	}

}
