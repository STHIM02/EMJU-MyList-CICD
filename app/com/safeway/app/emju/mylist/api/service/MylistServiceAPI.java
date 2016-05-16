package com.safeway.app.emju.mylist.api.service;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.model.ShoppingVO;

@ImplementedBy(MylistServiceAPIImp.class)
public interface MylistServiceAPI {

	ShoppingVO getShoppingList(final ClientRequestInfo request, String details, String timestamp) throws MobileException;
}
