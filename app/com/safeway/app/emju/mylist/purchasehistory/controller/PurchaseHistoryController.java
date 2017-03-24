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

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.safeway.app.emju.allocation.requestidentification.model.ProfileIdentifiers.CustomerProfileDataIdentifiers;
import com.safeway.app.emju.allocation.requestidentification.model.ProfileIdentifiers.URLParamsIdentifiers;
import com.safeway.app.emju.allocation.requestidentification.parser.RequestParser;
import com.safeway.app.emju.authentication.annotation.TokenValidator;
import com.safeway.app.emju.mylist.purchasehistory.exception.ExceptionUtil;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequestParser;
import com.safeway.app.emju.mylist.purchasehistory.service.PurchaseHistoryService;

import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * 
 * @author sshar64
 *
 */

public class PurchaseHistoryController extends Controller {

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryController.class);

    private static final long URL_REQUIRED_INFO = URLParamsIdentifiers.STORE_ID;

    private static final long REQUIRED_INFO = CustomerProfileDataIdentifiers.CUSTOMER_INFO | 
    															CustomerProfileDataIdentifiers.SESSION_TOKEN;
    
   
    

    private PurchaseHistoryService purchaseHistoryService;
    private RequestParser requestParser;
    private PurchaseHistoryRequestParser phRequestParser;

    @Inject
    public PurchaseHistoryController(@Named("CRP")final RequestParser requestParser, PurchaseHistoryService purchaseHistoryService) {
        this.purchaseHistoryService = purchaseHistoryService;
        this.requestParser = requestParser;
        this.phRequestParser = new PurchaseHistoryRequestParser();
    }


    @TokenValidator
    public Promise<Result> findPurchaseItemsAndOffers(){
    	
    	LOGGER.debug(">>> findPurchaseItemsAndOffers >>>>");
    	
    	Promise<Result> result = null;
        PurchaseHistoryRequest purchaseHistoryRequest = new PurchaseHistoryRequest();

        result = F.Promise.promise((Function0<Result>) () -> {
           
        	 requestParser.parseClientRequest(request(), purchaseHistoryRequest, REQUIRED_INFO, URL_REQUIRED_INFO); 
        	 
        	 LOGGER.debug("findPurchaseItemsAndOffers > getStoreId"+ purchaseHistoryRequest.getStoreId());
        	 LOGGER.debug("findPurchaseItemsAndOffers > getRegionId"+ purchaseHistoryRequest.getRegionId());
        	 
        	 phRequestParser.parseForPurchaseHistoryFilters(request(), purchaseHistoryRequest);
        	 
        	// LOGGER.debug("Selected storeId is = " + purchaseHistoryRequest.getPreferredStore().getStoreId()); 
        	 LOGGER.debug("YCS storeId is = " + purchaseHistoryRequest.getYcsStoreId()); 
        	 LOGGER.debug("store id after"+ purchaseHistoryRequest.getStoreId());
        	 

        	 
             
        	 PurchasedItemOffers purchasedItemOffers = purchaseHistoryService.findPurchaseItemsAndOffers(purchaseHistoryRequest);
             
             return ok(Json.toJson(purchasedItemOffers));
        });

        result = result.recover((F.Function<Throwable, Result>) (final Throwable t) -> {
        	LOGGER.error("findPurchaseItemsAndOffers() - Recovery:" + purchaseHistoryRequest.toString() 
        	+ "Recovery Message:" + t.getMessage() 
        	+ "Recovery Cause:" + t.getCause());
            t.printStackTrace();
            return status(401, Json.toJson(ExceptionUtil.getErrorResponse(t)));
        });

        return result;

    }

}

