package com.safeway.app.emju.mylist.helper;

import com.safeway.app.emju.helper.ValidationHelper;

public class DetailUtil {
	
	public static String cleanExtraChars(String input, String initChar) {
		
		String result = null;
		
		if(ValidationHelper.isNonEmpty(input)) {
			String[] descArray = input.split(initChar);
			if (descArray.length > 1) {
				result = descArray[0];
			} else {
				result = input;
			}
		}
		
		return result;
	}

}
