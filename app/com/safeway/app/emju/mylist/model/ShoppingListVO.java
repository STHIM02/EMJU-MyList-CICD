package com.safeway.app.emju.mylist.model;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"headerVO"})
public class ShoppingListVO {

	private String id;
	private String title;
	private String description;
	private List<ShoppingListItemVO> items;
	private List<ShoppingListItemVO> deletedItems;
	private HeaderVO headerVO;
	private HierarchyVO hierarchies;
	private List<ErrorVO> errors;
	private String[] itemIds;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ShoppingListItemVO> getItems() {
		return items;
	}
	public void setItems(List<ShoppingListItemVO> items) {
		this.items = items;
	}
	public List<ShoppingListItemVO> getDeletedItems() {
		return deletedItems;
	}
	public void setDeletedItems(List<ShoppingListItemVO> deletedItems) {
		this.deletedItems = deletedItems;
	}
	public HeaderVO getHeaderVO() {
		return headerVO;
	}
	public void setHeaderVO(HeaderVO headerVO) {
		this.headerVO = headerVO;
	}
	public HierarchyVO getHierarchies() {
		return hierarchies;
	}
	public void setHierarchies(HierarchyVO hierarchies) {
		this.hierarchies = hierarchies;
	}
	public List<ErrorVO> getErrors() {
		return errors;
	}
	public void setErrors(List<ErrorVO> errors) {
		this.errors = errors;
	}
	public String[] getItemIds() {
		return itemIds;
	}
	public void setItemIds(String[] itemIds) {
		
		if (itemIds != null) {
			this.itemIds = Arrays.copyOf(itemIds, itemIds.length);
		}
	}	
}
