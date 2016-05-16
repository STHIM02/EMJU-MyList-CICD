package com.safeway.app.emju.mylist.service;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.HeaderVO;

@ImplementedBy(MatchOfferServiceImp.class)
public interface MatchOfferSevice {
	
	public Map<String, Map<String, List<AllocatedOffer>>> getRelatedOffers(Map<Long, ShoppingListItem> itemsMap, 
	        HeaderVO headerVO)
	        throws ApplicationException;

}
