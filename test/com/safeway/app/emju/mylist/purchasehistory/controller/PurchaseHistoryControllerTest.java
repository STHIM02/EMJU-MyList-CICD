/* **************************************************************************
 * Copyright 2015 Albertsons Safeway.
 *
 * This document/file contains proprietary data that is the property of
 * Albertsons Safeway.  Information contained herein may not be used,
 * copied or disclosed in whole or in part except as permitted by a
 * written agreement signed by an officer of Albertsons Safeway.
 *
 * Unauthorized use, copying or other reproduction of this document/file
 * is prohibited by law.
 *
 ***************************************************************************/

package com.safeway.app.emju.mylist.purchasehistory.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.datastax.driver.core.exceptions.DriverException;
import com.safeway.app.emju.allocation.customerlookup.dao.CustomerLookupDAO;
import com.safeway.app.emju.allocation.exception.ErrorDescriptor;
import com.safeway.app.emju.allocation.exception.FaultCode;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.allocation.requestidentification.model.ProfileIdentifiers;
import com.safeway.app.emju.allocation.requestidentification.parser.ConsumerRequestParser;
import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.entity.Store;
import com.safeway.app.emju.dao.connector.CassandraConnector;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.mylist.purchasehistory.model.OfferHierarchy;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffer;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;
import com.safeway.app.emju.mylist.purchasehistory.service.PurchaseHistoryService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.Cookie;
import play.mvc.Http.Cookies;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

/**
 * 
 * 
 * @author sshar64
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseHistoryControllerTest {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryControllerTest.class);

   // private static FakeApplication fakeApp;
	
    private static CassandraConnector cassandraConn;
    
    private static FakeApplication fakeApp;
    
    @Mock private StoreCache storeCache;

    @Mock private CustomerLookupDAO customerLookupDAO;

    @Mock
    private Http.Request request;
    
    @Mock
    private PurchaseHistoryService  service;

    private ConsumerRequestParser requestParser;
    
    private PurchaseHistoryController controller;


//    @BeforeClass
    public static void start() {
    	
    	Config additionalConfig = ConfigFactory.parseFile(new File("test/purchase-test.conf"));
    	Configuration additionalConfigurations = new Configuration(additionalConfig);
    	
    	fakeApp = Helpers.fakeApplication(additionalConfigurations.asMap());
        Helpers.start(fakeApp);
        LOGGER.debug("Initializing FaultCodes with PLAY system before mocking internal Play objects"
            + FaultCode.INVALID_CUSTOMER_GUID);
    }

//    @AfterClass
    public static void stop() {
        Helpers.stop(fakeApp);
    }

//    @Before
    public void setup() {
        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 5L;
        play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
        Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
        Http.Context.current.set(context);
       
        Cookies cookies = mock(Cookies.class);
        Cookie mockCookie = mock(Cookie.class);
        
        when(request.getHeader("swycustguid")).thenReturn("123-12345-1234");
        when(request.getHeader("swyhouseholdid")).thenReturn("100001");
        when(request.getHeader("swycoremaclubcard")).thenReturn("200002");
        when(request.getHeader("postalcode")).thenReturn("91205");
        when(request.cookies()).thenReturn(cookies);
       
        when(cookies.get(any(String.class))).thenReturn(mockCookie);
        when(cookies.get("swyConsumerDirectoryPro").value()).thenReturn("_sessionToken");
        
        
     
     
        when(request.getQueryString(ProfileIdentifiers.URLParams.STORE_ID)).thenReturn("1234");
        
   
        requestParser = new ConsumerRequestParser(customerLookupDAO,storeCache);
        //requestParser = new ConsumerRequestParser(null, null);
        controller = new PurchaseHistoryController(requestParser, service);
    }

    

    	
    	
    @Test
    public void testFindPurchaseItemsAndOffersForSuccess() throws Exception {
//    	Store preferedStore = createStore();
//        //storeId
//        when(request.getQueryString(ProfileIdentifiers.URLParams.STORE_ID)).thenReturn("1");
//        when(storeCache.getStoreDetailsById(1)).thenReturn(preferedStore);
//
//    	PurchasedItemOffers purchasedItemOffers = preparePurchasedItemOffers();
//    	
//        when(service.findPurchaseItemsAndOffers(any(PurchaseHistoryRequest.class))).thenReturn(purchasedItemOffers);
//
//        Promise<Result> promise = controller.findPurchaseItemsAndOffers();
//        assertNotNull("Promise should not be null", promise);
//
//        Result result = promise.get(2000);
//        assertNotNull("Result should not be null", result);
//        assertEquals("Http Status should be 200", 200, result.status());
//        assertEquals("JSON should be same", Json.toJson(purchasedItemOffers).toString(), Helpers.contentAsString(result));
    }

    
//    @Test
    public void testFindPurchaseItemsAndOffersForFailure() throws Exception {

        OfferServiceException e = new OfferServiceException(
            FaultCodeBase.DB_SELECT_FAILURE, "Simulated Error", new DriverException("Error"));
        
        when(service.findPurchaseItemsAndOffers(any(PurchaseHistoryRequest.class))).thenThrow(e);

        Promise<Result> promise = controller.findPurchaseItemsAndOffers();
        assertNotNull("Promise should not be null", promise);

        Result result = promise.get(2000);
        assertNotNull("Result should not be null", result);
        assertEquals("Http Status should be 401", 401, result.status());

        Map<String, ErrorDescriptor[]> errorMap = new HashMap<String, ErrorDescriptor[]>(1);
        ErrorDescriptor error = new ErrorDescriptor(
            FaultCodeBase.SYSTEM_FAILURE.getCode(),
            FaultCodeBase.SYSTEM_FAILURE.getDescription() + " - '" + FaultCodeBase.DB_SELECT_FAILURE.getCode() + "'.");
        errorMap.put("errors", new ErrorDescriptor[] { error });
        assertEquals("JSON response should be same", Json.toJson(errorMap).toString(), Helpers.contentAsString(result));
    }

    
//    @Test
    public void testFindPurchaseItemsAndOffersForMissingHeaders() throws Exception {

        when(request.getHeader("swycustguid")).thenReturn(null);

        Promise<Result> promise = controller.findPurchaseItemsAndOffers();
        assertNotNull("Promise should not be null", promise);

        Result result = promise.get(2000);
        assertNotNull("Result should not be null", result);
        assertEquals("Http Status should be 401", 401, result.status());

        Map<String, ErrorDescriptor[]> errorMap = new HashMap<String, ErrorDescriptor[]>(1);
        ErrorDescriptor error = new ErrorDescriptor(
            FaultCodeBase.SYSTEM_FAILURE.getCode(),
            FaultCodeBase.SYSTEM_FAILURE.getDescription() + " - '" + FaultCode.INVALID_CUSTOMER_GUID.getCode() + "'.");
        errorMap.put("errors", new ErrorDescriptor[] { error });
        assertEquals("JSON response should be same", Json.toJson(errorMap).toString(), Helpers.contentAsString(result));
    }
    
    
    private PurchasedItemOffers preparePurchasedItemOffers() {
    	
    	PurchasedItemOffers purchasedItemOffers = new PurchasedItemOffers();
    	
    	List<PurchasedItemOffer> purchasedItemOfferList = new ArrayList<PurchasedItemOffer>();

		Map<Integer, OfferHierarchy> categoryMap = new HashMap<Integer, OfferHierarchy>(25);
    	
    	for(int i=0;i<10;i++) {
    		
			PurchasedItemOffer purchasedItemOffer = new PurchasedItemOffer();

			purchasedItemOffer.setUpcId(new Long(i));
			purchasedItemOffer.setPurchaseCount(new Long(i+1));
			purchasedItemOffer.setTitleDsc1("Desc"+i);
			purchasedItemOffer.setProdDsc1("ProdDesc"+i);
			purchasedItemOffer.setCategoryId(new Long(i+1));
			purchasedItemOffer.setCategoryName("Category"+i);
			purchasedItemOffer.setLastPurchasedTs(new Date());
			purchasedItemOffer.setItemType("UPC");
			
			purchasedItemOfferList.add(purchasedItemOffer);

			// Setup Category Hierarchy

			OfferHierarchy category = new OfferHierarchy();
			
			category.setCode(purchasedItemOffer.getCategoryId().toString());
			category.setName(purchasedItemOffer.getCategoryName());
			category.setCount(1L);
			
			categoryMap.put(purchasedItemOffer.getCategoryId().intValue(), category);
			
    	}

    	// convert list to array and set in PurchasedItemOffers object
    	purchasedItemOffers.setItems(
    			purchasedItemOfferList.toArray(new PurchasedItemOffer[purchasedItemOfferList.size()]));

    	Collection<OfferHierarchy> categories = categoryMap.values();
    	// convert collection to array and set in PurchasedItemOffers object
    	purchasedItemOffers.setCategories(
    			categories.toArray(new OfferHierarchy[categories.size()]));
    	
    	return purchasedItemOffers;
    	
    }
    
    private Store createStore() {
        Store preferedStore = new Store();
        preferedStore.setAddress1("ADDRESS 1");
        preferedStore.setAddress2("ADDRESS 2");
        preferedStore.setBannerNm("SWY");
        preferedStore.setCaptureDt(new Date());
        preferedStore.setCity("CITY");
        preferedStore.setCountry("COUNTRY");
        preferedStore.setDivisionNm("DIVISION");
        preferedStore.setZipCode("00000");
        preferedStore.setPriceZoneId(1);
        preferedStore.setRegionId(1);
        preferedStore.setRogCd("ROG");
        preferedStore.setState("STATE");
        preferedStore.setStoreId(1);
        preferedStore.setTimeZoneNm("America/Los_Angeles");
        return preferedStore;
    }
}

