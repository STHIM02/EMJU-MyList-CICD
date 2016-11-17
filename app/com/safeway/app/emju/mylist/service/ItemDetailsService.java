package com.safeway.app.emju.mylist.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.F.Promise;

@ImplementedBy(ItemDetailsServiceImp.class)
public interface ItemDetailsService {

	Collection<ShoppingListItemVO> setItemDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException;
	
	Promise<Map<Long, ?>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException;
	
	public Collection<ShoppingListItemVO> getItemDetails(String itemType, Map<Long, ?> itemDetailMap, 
			Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO) throws ApplicationException;
	
	public Map<Long, ?>  getDetailsPromiseResul(String itemType, Promise<Map<Long, ?>> promiseItemDetail) 
    		throws ApplicationException;
}
