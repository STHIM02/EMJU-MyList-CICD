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

package com.safeway.app.emju.mylist.helper;

public class Utils {
	private static final String STORE_ID_PREFIX = "S";
	
	public static String convertStoreIdAsPostalCd(Integer storeId) {
		String result = STORE_ID_PREFIX;
		
		if(0 >= storeId && 10 > storeId) {
			result = result + "000";
		} else if(10 >= storeId && 1000 > storeId) {
			result = result + "00";
		} else if(100 >= storeId && 1000 > storeId) {
			result = result + "0";
		} 
		
		result = result + storeId.toString();
		return result;
	}
}