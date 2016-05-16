package com.safeway.app.emju.mylist.purchasehistory.exception;

import java.util.HashMap;
import java.util.Map;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.allocation.exception.ErrorDescriptor;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;

public final class ExceptionUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);
	
    public static Map<String, ErrorDescriptor[]> getErrorResponse(final Throwable t) {
        Map<String, ErrorDescriptor[]> errorMap = null;
		 if (t instanceof ApplicationException) {
			 errorMap = getErrorResponse((ApplicationException) t);
			 
		 } else {
	            errorMap = new HashMap<String, ErrorDescriptor[]>();
	            ErrorDescriptor[] errors = new ErrorDescriptor[1];
	            errors[0] = new ErrorDescriptor(
	                FaultCodeBase.UNEXPECTED_SYSTEM_FAILURE.getCode(), FaultCodeBase.UNEXPECTED_SYSTEM_FAILURE
	                    .getDescription());
	            errorMap.put("errors", errors);
		 }

        return errorMap;
    }
    

	 
    public static Map<String, ErrorDescriptor[]> getErrorResponse(final ApplicationException appE) {
		 Map<String, ErrorDescriptor[]> errorMap = new HashMap<String, ErrorDescriptor[]>(1);
	        String errorCd = appE.getFaultCode().getCode();
	        String errorMsg = appE.getFaultCode().getDescription();
		 if (errorCd != null && errorMsg != null) {
		        ErrorDescriptor[] errors = new ErrorDescriptor[1];
		        errors[0] = new ErrorDescriptor(errorCd, errorMsg);
		        errorMap.put("errors", errors);
		 } else {
	            errorMap = new HashMap<String, ErrorDescriptor[]>(1);
	            ErrorDescriptor[] errors = new ErrorDescriptor[0];
	            errors[0] = new ErrorDescriptor(
	                FaultCodeBase.UNEXPECTED_SYSTEM_FAILURE.getCode(), FaultCodeBase.UNEXPECTED_SYSTEM_FAILURE
	                    .getDescription());
	            errorMap.put("errors", errors);
		 }
		 
		 return errorMap;
	 }

}
