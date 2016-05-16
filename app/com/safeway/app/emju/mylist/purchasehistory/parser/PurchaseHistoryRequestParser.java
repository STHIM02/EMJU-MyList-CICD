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

package com.safeway.app.emju.mylist.purchasehistory.parser;

import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.purchasehistory.controller.PurchaseHistoryController;

import play.mvc.Http.Request;

/**
 * 
 * 
 * @author sshar64
 * PurchaseHistoryRequestParser
 *
 */
public class PurchaseHistoryRequestParser {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryRequestParser.class);

	/**
	 * 
	 * 
	 * @param request
	 * @param clientRequest
	 * @param criteria
	 */
	public void parseForPurchaseHistoryFilters(final Request request, final PurchaseHistoryRequest clientRequest) {

		String sortType = request.getQueryString("sort");

		if (ValidationHelper.isEmpty(sortType)) {
			sortType = "category";
		}

		Integer ycsStoreId = (clientRequest.getStoreId() != null) ? clientRequest.getStoreId() : 0;

		String sstoreId = request.getQueryString("sstoreId");
		
		LOGGER.debug(">>PurchaseHistoryRequestParser >> sstoreId" + sstoreId);
		

		if (sstoreId != null) {
			ycsStoreId = ValidationHelper.isNumber(sstoreId) ? Integer.parseInt(sstoreId) : 0;
			// overriding the storeId with sstoreId
			clientRequest.setStoreId(Integer.parseInt(sstoreId));
		}

		clientRequest.setYcsStoreId(ycsStoreId);

	}

}
