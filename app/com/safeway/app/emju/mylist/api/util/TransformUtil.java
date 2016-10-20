package com.safeway.app.emju.mylist.api.util;

import java.util.HashMap;
import java.util.Map;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mobile.util.ClientInfoUtil;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mobile.exception.TransformException;
import com.safeway.app.emju.mylist.model.HeaderVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;

public class TransformUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransformUtil.class);
	private static Map<String, boolean[]> versionMap;

	static {
		versionMap = new HashMap<String, boolean[]>();
		versionMap.put("1.0", new boolean[] { false, true, false });
		versionMap.put("1.1", new boolean[] { true, true, true });
	}
	
	public static ShoppingListVO getShoppingListVO(ClientRequestInfo clientInfo) throws TransformException {

		LOGGER.debug("TransformUtil - getShoppingListVO : Creation of ShoppingListVO from ClientRequestInfo: "+clientInfo.toString());
		ShoppingListVO shoppingListVo = new ShoppingListVO();
		LOGGER.debug("TransformUtil - getShoppingListVO : Creation of HeaderVO from ClientRequestInfo: "+clientInfo.toString());
		HeaderVO headerVO = new HeaderVO();

		//Mandatory Attributes
		LOGGER.debug("TransformUtil - getShoppingListVO : Validation of Mandatory Attributes");
		ClientInfoUtil.validateCustomerGUID(clientInfo);
		ClientInfoUtil.validateValidHouseholdId(clientInfo);
		ClientInfoUtil.validateClubcard(clientInfo);
		ClientInfoUtil.validatePostalCode(clientInfo);
		ClientInfoUtil.validateSessionToken(clientInfo);
		
		LOGGER.debug("TransformUtil - getShoppingListVO : Asign ClientRequestInfo to ShoppingListVO");
		headerVO.setSwycustguid(clientInfo.getCustomerGUID());
		headerVO.setSwyhouseholdid(Long.toString(clientInfo.getHouseholdId()));
		headerVO.setSwycoremaclubcard(Long.toString(clientInfo.getClubCard()));
		headerVO.setPostalcode(clientInfo.getPostalCode());
		headerVO.setPostalBanner(clientInfo.getBanner());
		headerVO.setAppKey(clientInfo.getAppId());
		headerVO.setAppVersion(clientInfo.getAppVersion());
		headerVO.setVersionValues(versionMap.getOrDefault(headerVO.getAppVersion().trim(), 
				new boolean[] { true, true, true }));
		headerVO.setParamStoreId(Integer.toString(clientInfo.getStoreId()));
		headerVO.setBannner(clientInfo.getBanner());
		headerVO.setLoggedUserId(clientInfo.getAppUser());
		headerVO.setSessionToken(clientInfo.getSessionToken());
		headerVO.setSwyccpricezone(clientInfo.getPriceZone());

		shoppingListVo.setHeaderVO(headerVO);

		return shoppingListVo;
	}

	public static PurchaseHistoryRequest getPurchaseHistoryRequest(ClientRequestInfo request) throws TransformException {
		LOGGER.debug("TransformUtil - getPurchaseHistoryRequest : Creation of PurchaseHistoryRequest with ClientRequestInfo:"+request.toString());
		PurchaseHistoryRequest purchaseHistoryRequest = new PurchaseHistoryRequest();
		ClientInfoUtil.validateCustomerGUID(request);
		ClientInfoUtil.validateValidHouseholdId(request);
		ClientInfoUtil.validateClubcard(request);
		ClientInfoUtil.validatePostalCode(request);
		ClientInfoUtil.validateSessionToken(request);
		
		LOGGER.debug("TransformUtil - getPurchaseHistoryRequest : Asign ClientRequestInfo to PurchaseHistoryRequest");
		purchaseHistoryRequest.setAppId(request.getAppId());
		purchaseHistoryRequest.setAppUser(request.getAppUser());
		purchaseHistoryRequest.setAppVersion(request.getAppVersion());
		purchaseHistoryRequest.setBanner(request.getBanner());
		purchaseHistoryRequest.setClubCard(request.getClubCard());
		purchaseHistoryRequest.setPostalCode(request.getPostalCode());
		purchaseHistoryRequest.setSessionToken(request.getSessionToken());

		purchaseHistoryRequest.setRegionId(request.getRegionId());
		purchaseHistoryRequest.setStoreId(request.getStoreId());
		purchaseHistoryRequest.setCustomerGUID(request.getCustomerGUID());
		purchaseHistoryRequest.setHouseholdId(request.getHouseholdId());
		purchaseHistoryRequest.setTimezone(request.getTimeZone());

		
		return purchaseHistoryRequest;
	}
}
