package com.safeway.app.emju.mylist.api.service;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;

@ImplementedBy(MobilePurchaseHistoryServiceImpl.class)
public interface MobilePurchaseHistoryService {
	
	PurchasedItemOffers findPurchaseItemsAndOffers(final ClientRequestInfo request) throws MobileException;

}
