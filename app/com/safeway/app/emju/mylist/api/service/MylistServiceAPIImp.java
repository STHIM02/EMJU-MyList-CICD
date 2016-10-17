package com.safeway.app.emju.mylist.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.allocation.pricing.dao.OfferStorePriceDAO;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.api.util.TransformUtil;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.model.ShoppingVO;
import com.safeway.app.emju.mylist.service.ShoppingListService;

public class MylistServiceAPIImp implements MylistServiceAPI {

	private static final Logger LOGGER = LoggerFactory.getLogger(MylistServiceAPIImp.class);
	private ShoppingListService shoppingListService;
	private OfferStorePriceDAO ospDAO;
	
	@Inject
	public MylistServiceAPIImp(ShoppingListService shoppingListService, OfferStorePriceDAO ospDAO) {
		this.shoppingListService = shoppingListService;
		this.ospDAO = ospDAO;
	}

	@Override
	public ShoppingVO getShoppingList(ClientRequestInfo request, String details, String timestamp)
			throws MobileException {
		
		LOGGER.debug("Inside getShoppingList");
		ShoppingVO shoppingVo = null;
		List<ShoppingListVO> shoppingLists = null;
		
		try {
			ShoppingListVO shoppingListVo = TransformUtil.getShoppingListVO(request);
			
			shoppingListVo.getHeaderVO().setDetails(details);
	        shoppingListVo.getHeaderVO().setTimestamp(timestamp);		
        	shoppingLists = shoppingListService.getShoppingList(shoppingListVo);
        	
    		String clientTimezone = shoppingListVo.getHeaderVO().getTimeZone();
            String sysTimestamp = DateHelper.getISODate(new Date(), clientTimezone);
            
    		shoppingVo = new ShoppingVO();
            shoppingVo.setShoppingLists(shoppingLists);
            shoppingVo.setLastDeltaTS(sysTimestamp);
            
        } catch (ApplicationException e) {
        	LOGGER.error("Error when invoking shoppingListService.getShoppingList", e);
			throw new MobileException(e);
		}
        
		return shoppingVo;
	}

	@Override
	public Map<Long, OfferStorePrice> findOfferPrices(Integer storeId, List<Long> offerIds) throws MobileException {
		
		LOGGER.debug("Inside findOfferPrices");
		
		try{
			
			return ospDAO.findOfferPrices(storeId, offerIds);
			
		} catch(OfferServiceException e) {
			
			LOGGER.error("Error when invoking ospDAO.findOfferPrices", e);
			throw new MobileException(e);
		}
	}
}
