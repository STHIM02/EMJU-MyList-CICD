package com.safeway.app.emju.mylist.service;

import java.util.Map;

import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.F.Promise;

public interface ItemDetailAsyncRetriever<T> {
	
	public Promise<Map<Long, T>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException;
	
	public Map<Long, T>  getDetailsPromiseResul(Promise<Map<Long, T>> promiseItemDetail) 
    		throws ApplicationException;

}
