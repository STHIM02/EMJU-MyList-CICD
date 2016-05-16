package com.safeway.app.emju.mylist.service;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.mylist.model.HeaderVO;
import com.safeway.app.emju.mylist.model.MailListVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

@ImplementedBy(ShoppingListServiceImp.class)
public interface ShoppingListService {
	
	FaultCodeBase findTimeZoneFromPostalCode(String postalCode,
			HeaderVO headerVO);
	
	List<ShoppingListVO> getShoppingList(ShoppingListVO shoppingListVO)
			throws ApplicationException;
	
	Integer getShoppingListCount(ShoppingListVO shoppingListVO, String listName)
		throws ApplicationException;
	
	void sendShoppingListMail(MailListVO mailListVO, HeaderVO headerVO)
			throws ApplicationException;

}
