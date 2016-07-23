package com.safeway.app.emju.mylist.api.service;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.model.ShoppingVO;

@ImplementedBy(MylistServiceAPIImp.class)
public interface MylistServiceAPI {

	ShoppingVO getShoppingList(final ClientRequestInfo request, String details, String timestamp) throws MobileException;
	
	Map<Long, OfferStorePrice> findOfferPrices(Integer storeId, List<Long> offerIds) throws MobileException;
}
