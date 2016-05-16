package com.safeway.app.emju.mylist.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.safeway.app.emju.allocation.requestidentification.model.ProfileIdentifiers.ClientApiKey;
import com.safeway.app.emju.authentication.annotation.TokenValidator;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.constant.Constants.ProfileDataRestrictions;
import com.safeway.app.emju.mylist.dao.StoreDAO;
import com.safeway.app.emju.mylist.exception.ExceptionUtil;
import com.safeway.app.emju.mylist.helper.DateHelper;
import com.safeway.app.emju.mylist.model.HeaderVO;
import com.safeway.app.emju.mylist.model.ListCountVO;
import com.safeway.app.emju.mylist.model.MailListVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.model.ShoppingVO;
import com.safeway.app.emju.mylist.service.ShoppingListService;
import com.safeway.app.emju.util.GenericConstants;

import play.Configuration;
import play.Play;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.Cookie;
import play.mvc.Http.Cookies;
import play.mvc.Result;

public class ShoppingListController extends Controller {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListController.class);
	
	private static Map<String, String> headerKey;
	private static String RUNTIME_MODE;

    static {
        headerKey = new HashMap<String, String>();
        headerKey.put("CUST_GUID_SWY", Constants.SWY_CUST_GUID);
        headerKey.put("CUST_ZIP_SWY", Constants.POSTAL_CODE);
        headerKey.put("CUST_HHID_SWY", Constants.SWY_HOUSE_HOLD_ID);
        headerKey.put("CUST_CCNR_SWY", Constants.SWY_COREMA_CLUB_CARD);
        headerKey.put("CUST_PZONE_SWY", Constants.SWY_CC_PRICE_ZONE);
        headerKey.put("CUST_STORE_SWY", Constants.SWY_CUST_STORE_ID);

        headerKey.put("CUST_GUID_CCA", Constants.SWY_CUST_GUID_CCA);
        headerKey.put("CUST_ZIP_CCA", Constants.POSTAL_CODE_CCA);
        headerKey.put("CUST_HHID_CCA", Constants.SWY_HOUSE_HOLD_ID_CCA);
        headerKey.put("CUST_CCNR_CCA", Constants.SWY_COREMA_CLUB_CARD_CCA);
        headerKey.put("CUST_PZONE_CCA", Constants.SWY_CC_PRICE_ZONE_CCA);
        headerKey.put("CUST_STORE_CCA", Constants.SWY_CUST_STORE_ID_CCA);
        
        Configuration config = Play.application().configuration();
        RUNTIME_MODE = config.getString("emju.app.mylist.mode");
    }
    
    private static Map<String, boolean[]> versionMap;

    static {
        versionMap = new HashMap<String, boolean[]>();
        versionMap.put("1.0", Constants.filter_1_0);
        versionMap.put("1.1", Constants.filter_1_1);
        versionMap.put("1.2", Constants.filter_1_2);
        versionMap.put("1.3", Constants.filter_1_3);
        versionMap.put("1.4", Constants.filter_1_4);
        versionMap.put("1.5", Constants.filter_1_5);
    }
    
    private ShoppingListService shoppingListService;
    
    @Inject
    public ShoppingListController(ShoppingListService shoppingListService, StoreDAO storeDAO) {
    	this.shoppingListService = shoppingListService;
    }
	
    @TokenValidator(userProfile = true)
	public Promise<Result> getShoppingList(String details, String storeId, String timestamp) {
		
		Promise<Result> result = null;
		LOGGER.info("Inside getShoppingList");
		result = Promise.promise((Function0<Result>) () -> {
			
			LOGGER.debug("Query parameters passed: ");
			LOGGER.debug("details: " + details);
			LOGGER.debug("storeId: " + storeId);
			LOGGER.debug("timestamp: " + timestamp);
			ShoppingVO shoppingVo = null;
	        ShoppingListVO shoppingListVo = new ShoppingListVO();
	        List<ShoppingListVO> shoppingLists = null;
	        validateHeader(shoppingListVo, true, storeId);
	        shoppingListVo.getHeaderVO().setDetails(details);
	        shoppingListVo.getHeaderVO().setTimestamp(timestamp);
	        shoppingLists = shoppingListService.getShoppingList(shoppingListVo);
	        
	        String clientTimezone = shoppingListVo.getHeaderVO().getTimeZone();
            String sysTimestamp = DateHelper.getISODate(new Date(), clientTimezone);
            
	        shoppingVo = new ShoppingVO();
            shoppingVo.setShoppingLists(shoppingLists);
            shoppingVo.setLastDeltaTS(sysTimestamp);
            
            LOGGER.info("getShoppingList <<");
            return ok(Json.toJson(shoppingVo));
		});
		
		result = result.recover((F.Function<Throwable, Result>) (final Throwable t) -> {
        	LOGGER.error("getShoppingList() - Recovery:"
        	+ "Recovery Message:" + t.getMessage() 
        	+ "Recovery Cause:" + t.getCause());
            t.printStackTrace();
            return status(401, Json.toJson(ExceptionUtil.getErrorResponse(t)));
        });
		
		return result;
	}
	
    @TokenValidator(userProfile = true)
	public Promise<Result> getShoppingListCount(String storeId) {
		
		Promise<Result> result = null;
		
		result = Promise.promise((Function0<Result>) () -> {
			LOGGER.info("getShoppingListCount >>");
			Integer iSLItemcount = 0;
	        ShoppingListVO shoppingListVo = new ShoppingListVO();
	        validateHeader(shoppingListVo, true, storeId);
	        iSLItemcount = shoppingListService.getShoppingListCount(shoppingListVo, null);
	        ListCountVO countVO = new ListCountVO();
	        countVO.setNoShoppingListItems(iSLItemcount);
	        LOGGER.info("getShoppingListCount <<");
	        return ok(Json.toJson(countVO));
		});
		
		result = result.recover((F.Function<Throwable, Result>) (final Throwable t) -> {
        	LOGGER.error("getShoppingListCount() - Recovery:"
        	+ "Recovery Message:" + t.getMessage() 
        	+ "Recovery Cause:" + t.getCause());
            t.printStackTrace();
            return status(401, Json.toJson(ExceptionUtil.getErrorResponse(t)));
        });
		
		return result;
	}
	
    @TokenValidator(userProfile = true)
	public Promise<Result> emailShoppingList(String storeId) {
		
		Promise<Result> result = null;
		
		result = Promise.promise((Function0<Result>) () -> {
			LOGGER.info("emailShoppingList >>");
			ShoppingListVO shoppingListVo = new ShoppingListVO();
	        validateHeader(shoppingListVo, true, storeId);
	        MailListVO mailListVO = new ObjectMapper().treeToValue(request().body().asJson(), MailListVO.class);
	        shoppingListService.sendShoppingListMail(mailListVO, shoppingListVo.getHeaderVO());
			return ok();
		});
		
		result = result.recover((F.Function<Throwable, Result>) (final Throwable t) -> {
        	LOGGER.error("emailShoppingList() - Recovery:"
        	+ "Recovery Message:" + t.getMessage() 
        	+ "Recovery Cause:" + t.getCause());
            t.printStackTrace();
            return status(401, Json.toJson(ExceptionUtil.getErrorResponse(t)));
        });
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void validateHeader(final ShoppingListVO shoppingListVO,
	        boolean sessionTokenRequired, String selectedStoreId) throws ApplicationException {
		LOGGER.info("Start header validation >>");
		FaultCodeBase error = null;
        HeaderVO headerVO = new HeaderVO();
        String postalCode = null;
        String postalBanner = null;
        String custGuid = null;
        String sessionId = null;
        Map<String, String[]> headermap = null;
        Map<String, String[]> profileMap = null;
        
        try {
        	
        	headermap = request().headers();
        	if(RUNTIME_MODE.equals(Constants.DEV_MODE)) {
        		LOGGER.debug("Dev Mode is on");
        		profileMap = headermap;
        	} else {
        		LOGGER.debug("No dev Mode is on");
        		profileMap = (Map<String,String[]>) Context.current().args.get(GenericConstants.ARGS_USER_PROFILE);
        		LOGGER.debug("profileMap is: " + profileMap);
        	}
        	
        	Cookies cookieMap = request().cookies();
        	String ccaOrswy = getHeaderKey(headermap.get(Constants.APP_KEY), headermap.get(Constants.AGENT_ID));
        	LOGGER.info("ccaOrswy is " + ccaOrswy);
        	String banner = null;
            String apiVersion = null;

            if (checkForNullAndEmptyHeaders(profileMap
                    .get(headerKey.get("CUST_GUID" + ccaOrswy)))) {
                error = FaultCodeBase.EMLS_INVALID_CUST_ID;
            }
            else {
                custGuid = profileMap.get(headerKey.get("CUST_GUID" + ccaOrswy))[0];
                LOGGER.info("Customer GUID of the user " + custGuid);
            }
            
            if (error == null && checkForNullAndEmptyHeaders(profileMap
                    .get(headerKey.get("CUST_HHID" + ccaOrswy)))) {
                error = FaultCodeBase.EMLS_INVALID_HOUSEHOLD_ID;
            }
            else if (checkForNullAndEmptyHeaders(profileMap
                    .get(headerKey.get("CUST_CCNR" + ccaOrswy)))) {
                error = FaultCodeBase.EMLS_INVALID_CLUBCARD_ID;
            }
            else if (checkForNullAndEmptyHeaders(profileMap
                    .get(headerKey.get("CUST_ZIP" + ccaOrswy)))) {
                error = FaultCodeBase.EMLS_INVALID_POSTAL_CODE;
            }
            else if (checkForNullAndEmptyHeaders(headermap
                    .get(Constants.APP_KEY))) {
                error = FaultCodeBase.EMLS_INVALID_API_KEY;
            }
            else if (checkForNullAndEmptyHeaders(headermap
                    .get(Constants.APP_VERSION))) {
                error = FaultCodeBase.EMLS_INVALID_API_VERSION;
            }
            else if (checkForNullAndEmptyHeaders(headermap
                    .get(Constants.SWY_BANNER))) {
                error = FaultCodeBase.EMLS_INVALID_BANNER;
            }
            
            if (error == null) {
                banner = headermap.get(Constants.SWY_BANNER)[0];                
                //banner can be null and validateBanner can handle null
                if (!validateBanner(banner)) {
                    error = FaultCodeBase.EMLS_INVALID_BANNER;
                    LOGGER.error("INVALID BANNER: " + banner);
                }
                apiVersion = headermap.get(Constants.APP_VERSION)[0];
                LOGGER.debug("api Version is: " + apiVersion);
                //apiVersion can be null but validateVersion can handle null 
                if (!validateVersion(apiVersion)) {
                    error = FaultCodeBase.EMLS_INVALID_API_VERSION;
                    LOGGER.error("INVALID API VERSION: " + apiVersion);
                }
            }
            
            if (error == null) {
                postalCode = profileMap.get(headerKey.get("CUST_ZIP" + ccaOrswy))[0];
                //postalcode can be null
                if (postalCode!=null) {
                	postalCode = ValidationHelper.isNumber(postalCode) ? postalCode
                        .substring(0, 5) : postalCode.substring(0, 6).toUpperCase();
                }
                LOGGER.info("Formatted Postal Code : " + postalCode);
                LOGGER.info("Service call to find the timezone >> ");
                error = shoppingListService.findTimeZoneFromPostalCode(
                        postalCode, headerVO);
                LOGGER.info("Service call to find the timezone << ");
            }
            
            if (error == null && sessionTokenRequired) {
                if (cookieMap != null) {
                    // get sessionToken to process Catalina
                    if (cookieMap.get(Constants.USER_SESSION_TOKEN) != null) {
                        sessionId = cookieMap.get(Constants.USER_SESSION_TOKEN).value();
                    }
                }
                //Try from header
                if(ValidationHelper.isEmpty(sessionId)) {
                	
                	if(!checkForNullAndEmptyHeaders(headermap.get(Constants.HEADER_SESSION_TOKEN))) {
                		sessionId = headermap.get(Constants.HEADER_SESSION_TOKEN)[0];
                	}
                }
                if (ValidationHelper.isEmpty(sessionId)) {
                    error = FaultCodeBase.EMLS_INVALID_USER_SESSION_TOKEN;
                }
            }
            if (error != null) {
                LOGGER.warn("Error " + error + " found while validating the header for the user :" + custGuid);
                LOGGER.warn("OSSO Header Injected values: " + headermap);
                LOGGER.warn("Cookie values: " + cookieMap);
                throw new ApplicationException(error, null, null);
            }
            
            headerVO.setSwycustguid(custGuid);
            headerVO.setSwyhouseholdid(profileMap.get(headerKey.get("CUST_HHID" + ccaOrswy))[0]);
            headerVO.setSwycoremaclubcard(profileMap.get(headerKey.get("CUST_CCNR" + ccaOrswy))[0]);
            headerVO.setPostalcode(postalCode);
            headerVO.setPostalBanner(postalBanner);
            String[] priceccZone = profileMap.get(headerKey.get("CUST_PZONE" + ccaOrswy));
            headerVO.setSwyccpricezone(priceccZone != null ? priceccZone[0] : null);
            headerVO.setAppKey(parseHeaderForApiKey(headermap.get(Constants.APP_KEY)[0]));
            headerVO.setAppVersion(headermap.get(Constants.APP_VERSION)[0]);
            headerVO.setVersionValues(versionMap.get(headerVO.getAppVersion().trim()));
            headerVO.setParamStoreId(selectedStoreId);
            headerVO.setBannner(banner);
            headerVO.setLoggedUserId(
                    ValidationHelper.trimForMaxLength(headerVO.getSwycustguid(), Constants.MAX_LENGTH));
            // Check the cookies for SWY_LOGONID if found set to LoggedUserId
            if (checkForNullAndEmptyHeaders(headermap.get(Constants.LOGIN_ID))) {
                LOGGER.info("UID not available in header.Look in Cookie start>>");
                if (cookieMap != null) {
                    Cookie loginIdCookie = cookieMap.get(Constants.SWY_LOGONID);
                    if (!isEmptyCookie(loginIdCookie)) {
                        headerVO.setLoggedUserId(
                                ValidationHelper.trimForMaxLength(
                                        loginIdCookie.value(), Constants.MAX_LENGTH));
                        LOGGER.info("UID Found in Cookie : " + headerVO.getLoggedUserId());
                    }
                }
            }
            else {
                headerVO.setLoggedUserId(
                        ValidationHelper.trimForMaxLength(
                                headermap.get(Constants.LOGIN_ID)[0], Constants.MAX_LENGTH));
                LOGGER.info("UID found in Header: " + headerVO.getLoggedUserId());
            }
            
            LOGGER.info(headerVO.toString());
            if (ValidationHelper.isNonEmpty(sessionId)) {
          	  //set the unique token as a combination of osso token and hhid.
          	  LOGGER.debug("Token is " + sessionId);
          	  LOGGER.debug("HHid is " + headerVO.getSwyhouseholdid());
          	  String md5Token = getSessionkey(sessionId + headerVO.getSwyhouseholdid());
          	  headerVO.setSessionToken(md5Token);
            }
            
        } catch (ApplicationException ae) {
            LOGGER.error(ae.getMessage());
            LOGGER.error("Client sent following header values: " + headermap);
            if (ae.getFaultCode().getCode() != null && ae.getFaultCode().getDescription() != null) {
                throw ae;
            }
        } catch (Exception ex) {
        	LOGGER.error(ex.getMessage(), ex);
        }
        
        shoppingListVO.setHeaderVO(headerVO);
        LOGGER.info("Header Validation Success ");
        
	}
	
	private String getHeaderKey(final String[] apiKey, final String[] agentid) {

        String appendString = "_SWY";

        if (!checkForNullAndEmptyHeaders(apiKey) &&
                !checkForNullAndEmptyHeaders(agentid)) {
            if (apiKey[0].equalsIgnoreCase("svct")) {
                appendString = "_CCA";

            }
        }

        return appendString;
    }
	
	private static boolean checkForNullAndEmptyHeaders(String[] headers) {
		boolean isHeaderInValid = false;
		if (!ValidationHelper.isNonEmptyArray(headers) || ValidationHelper.isEmpty(headers[0])) {
			isHeaderInValid = true;
		}
		return isHeaderInValid;
	}
	
	private boolean validateBanner(final String bannerName) {
        boolean validBanner = false;
        LOGGER.info("Banner Name: " + bannerName);
        if (bannerName!=null) {
	        for (Constants.Banner banner : Constants.Banner.values()) {
	        	LOGGER.debug("Available Banner Name: " + banner.getBannerName());
	            if (banner.getBannerName().equalsIgnoreCase(bannerName)) {
	                validBanner = true;
	                break;
	            }
	        }
        }
        return validBanner;
    }
	
	private boolean validateVersion(final String version) {
        boolean validVersion = false;
        LOGGER.info("Version : " + version);
        if (null != version && Constants.VERSIONS.contains(version.trim())) {
            validVersion = true;
        }
        return validVersion;
    }
	
	private String parseHeaderForApiKey(final String inValue) {
    	
        StringBuilder value = new StringBuilder(inValue);
        if (value.indexOf(Constants.CHAR_PERIOD) != value.lastIndexOf(Constants.CHAR_PERIOD)) {
            value.setLength(0);
            value.append(ClientApiKey.EMMD_APP);
        }
        else if (value.length() > ProfileDataRestrictions.SWY_API_KEY_MAX_LENGTH) {
            value.delete(
            		ProfileDataRestrictions.SWY_API_KEY_MAX_LENGTH - 1, value.length()).append(Constants.CHAR_UNDERSCORE);
        }
        return value.toString();
    }
	
	private static boolean isEmptyCookie(Cookie cookie) {
		return cookie == null || cookie.value().trim().length() == 0;
	}
	
	private static String getSessionkey(String token) {
        LOGGER.info("ProcessUtil:getSessionkey(): token = " + token);
        String sessionKey = null;
        try {

            MessageDigest m = MessageDigest.getInstance("MD5");            
            m.update(token.getBytes("UTF-8"), 0, token.length());
            sessionKey = new BigInteger(1, m.digest()).toString(16).toString();            

        } 
        catch (Exception e) {
            LOGGER.error("Error while processing the session token  ", e);
        }
        
        LOGGER.info("ProcessUtil:getSessionkey(): Session Key = " + sessionKey);
        return sessionKey;
    }

}
