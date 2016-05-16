/* **************************************************************************
 * Copyright 2016 Albertsons Safeway.
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

package com.safeway.app.emju.mylist.api.service;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mobile.exception.MobileException;
import com.safeway.app.emju.mobile.model.ClientRequestInfo;
import com.safeway.app.emju.mylist.api.util.TransformUtil;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;
import com.safeway.app.emju.mylist.purchasehistory.service.PurchaseHistoryService;

/* ***************************************************************************
 * NAME         : GenericAllocationServiceAPIImpl.java
 *
 * SYSTEM       : emju-gallery
 *
 * AUTHOR       : Arun Hariharan
 *
 * REVISION HISTORY
 *
 * Revision 0.0.0.0 Jan 14, 2016 ahani00
 * Initial creation for emju-gallery
 *
 ***************************************************************************/

/**
 * @author ahani00
 *
 */
public class MobilePurchaseHistoryServiceImpl implements MobilePurchaseHistoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobilePurchaseHistoryServiceImpl.class);

	private PurchaseHistoryService purchaseHistoryService;

	@Inject
	public MobilePurchaseHistoryServiceImpl(PurchaseHistoryService purchaseHistoryService){
		this.purchaseHistoryService = purchaseHistoryService;
	}

	@Override
	public PurchasedItemOffers findPurchaseItemsAndOffers(final ClientRequestInfo request) throws MobileException {
		LOGGER.info("Mobile - findPurchaseItemsAndOffers: Getting PurchasedItemOffers HHID:"+request.getHouseholdId());
		LOGGER.debug("Mobile - findPurchaseItemsAndOffers: Start Parsing ClientRequestInfo:"+request.toString());
		
		PurchaseHistoryRequest purchaseHistoryRequest = TransformUtil.getPurchaseHistoryRequest(request);
		
		LOGGER.debug("Mobile - findPurchaseItemsAndOffers: Start Connection with Gallery Service");
		
		try {
			return purchaseHistoryService.findPurchaseItemsAndOffers(purchaseHistoryRequest);//85
		} catch (OfferServiceException e) {
			e.printStackTrace();
			LOGGER.error("Mobile - findPurchaseItemsAndOffers - ClientRequestInfo:"+request.toString() + "Error Msg:"+e.getMessage());
			throw new MobileException(e);
		}

	}
}

