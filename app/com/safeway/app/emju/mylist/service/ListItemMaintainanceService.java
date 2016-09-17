package com.safeway.app.emju.mylist.service;

import java.util.List;

import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.dao.ShoppingListDAO;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;

public class ListItemMaintainanceService implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListItemMaintainanceService.class);
	
	List<ShoppingListItem> shoppingList;
	ShoppingListDAO shoppingListDAO;
	
	public ListItemMaintainanceService(List<ShoppingListItem> shoppingList, ShoppingListDAO shoppingListDAO) {
		
		this.shoppingList = shoppingList;
		this.shoppingListDAO = shoppingListDAO;
	}

	@Override
	public void run() {

		try {
			
			shoppingListDAO.insertShoppingListItems(shoppingList);
			
		} catch(ApplicationException e) {
			LOGGER.error("An exception happened when trying to send the email: " + e);
		}

	}

}
