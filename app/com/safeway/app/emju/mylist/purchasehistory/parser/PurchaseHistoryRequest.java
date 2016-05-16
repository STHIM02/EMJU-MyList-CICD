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

import com.safeway.app.emju.allocation.requestidentification.model.ClientRequest;
import com.safeway.app.emju.mylist.model.PreferredStore;

/**
 * 
 * @author sshar64
 * PurchaseHistoryRequest
 * 
 */
public class PurchaseHistoryRequest extends ClientRequest{
	
	private Integer ycsStoreId;
	private String sortType;
	
	//Nothing but either storeId or sStoreId (higher precedence)
	private PreferredStore preferredStore;
	  
	public Integer getYcsStoreId() {
		return ycsStoreId;
	}
	public void setYcsStoreId(Integer ycsStoreId) {
		this.ycsStoreId = ycsStoreId;
	}
	
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public PreferredStore getPreferredStore() {
		return preferredStore;
	}
	public void setPreferredStore(PreferredStore preferredStore) {
		this.preferredStore = preferredStore;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseHistoryCriteria [ycsStoreId=");
		builder.append(ycsStoreId);
		builder.append(", sortType=");
		builder.append(sortType);
		builder.append(", preferredStore=");
		builder.append(preferredStore);
		builder.append("]");
		return builder.toString();
	}
	



}
