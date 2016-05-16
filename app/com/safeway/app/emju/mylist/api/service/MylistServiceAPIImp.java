package com.safeway.app.emju.mylist.api.service;

import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.api.util.TransformUtil;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.model.ShoppingVO;
import com.safeway.app.emju.mylist.service.ShoppingListService;

public class MylistServiceAPIImp implements MylistServiceAPI {

	private ShoppingListService shoppingListService;
	
	@Inject
	public MylistServiceAPIImp(ShoppingListService shoppingListService) {
		this.shoppingListService = shoppingListService;
	}

	@Override
	public ShoppingVO getShoppingList(ClientRequestInfo request, String details, String timestamp)
			throws MobileException {
		
		ShoppingVO shoppingVo = null;
		List<ShoppingListVO> shoppingLists = null;
		
		ShoppingListVO shoppingListVo = TransformUtil.getShoppingListVO(request);
		
		shoppingListVo.getHeaderVO().setDetails(details);
        shoppingListVo.getHeaderVO().setTimestamp(timestamp);
		
        try {
        	shoppingLists = shoppingListService.getShoppingList(shoppingListVo);
        } catch (ApplicationException e) {
        	
			throw new MobileException(e);
		}
        
		String clientTimezone = shoppingListVo.getHeaderVO().getTimeZone();
        String sysTimestamp = DateHelper.getISODate(new Date(), clientTimezone);
        
		shoppingVo = new ShoppingVO();
        shoppingVo.setShoppingLists(shoppingLists);
        shoppingVo.setLastDeltaTS(sysTimestamp);
        
		return shoppingVo;
	}
}
