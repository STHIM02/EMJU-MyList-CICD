package com.safeway.app.emju.mylist.dao;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

@ImplementedBy(ShoppingListDAOImp.class)
public interface ShoppingListDAO {
	
	public List<ShoppingListItem> getShoppingListItems(ShoppingListVO shoppingListVO) throws ApplicationException;
	
	public void insertShoppingListItems(List<ShoppingListItem> shoppingListItems) throws ApplicationException;

}
