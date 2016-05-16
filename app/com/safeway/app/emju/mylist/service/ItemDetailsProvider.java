package com.safeway.app.emju.mylist.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

public interface ItemDetailsProvider<T> {
	
	public Collection<ShoppingListItemVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, Map<String, Map<String, List<AllocatedOffer>>> matchedOffers)
					throws ApplicationException;
	
	public Collection<ShoppingListItemVO> getItemDetails(Map<Long, T> offerDetailMap, 
			Map<String, ShoppingListItem> itemMap, ShoppingListVO shoppingListVO)
					throws ApplicationException;
}
