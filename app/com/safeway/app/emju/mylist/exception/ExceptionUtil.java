package com.safeway.app.emju.mylist.exception;

import java.util.ArrayList;
import java.util.List;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.mylist.model.ErrorVO;
import com.safeway.app.emju.mylist.model.ShoppingVO;

public final class ExceptionUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);
	
	 public static ShoppingVO getErrorResponse(final Throwable t) {
		 
		 ShoppingVO responseShoppingListVO = new ShoppingVO();
		 List<ErrorVO> errorCodeList = null;
		 
		 if (t instanceof ApplicationException) {
			 
			 errorCodeList = getErrorResponse((ApplicationException) t);
			 
		 } else {
			 
			 LOGGER.error("Exception :" + t);
			 errorCodeList = new ArrayList<ErrorVO>();
			 errorCodeList.add(new ErrorVO(FaultCodeBase.EMLS_UNABLE_TO_PROCESS.getCode(), 
					 FaultCodeBase.EMLS_UNABLE_TO_PROCESS.getDescription()));
		 }
		 
		 responseShoppingListVO.setErrors(errorCodeList);
		 return responseShoppingListVO;
		 
	 }
	 
	 public static List<ErrorVO> getErrorResponse(ApplicationException e) {
		 
		 List<ErrorVO> errorCodeList = new ArrayList<ErrorVO>();
		 
		 if (e.getFaultCode().getCode() != null && e.getFaultCode().getDescription() != null) {
	            errorCodeList.add(new ErrorVO(e.getFaultCode().getCode(), e.getFaultCode().getDescription()));            
	            LOGGER.warn("ApplicationException");
	            LOGGER.error(e.getMessage());
	            LOGGER.debug(e.getMessage(), e);
		 } else {
	            errorCodeList.add(new ErrorVO(FaultCodeBase.EMLS_UNABLE_TO_PROCESS.getCode(),
	            		FaultCodeBase.EMLS_UNABLE_TO_PROCESS.getDescription()));
		 }
		 
		 return errorCodeList;
	 }

}
