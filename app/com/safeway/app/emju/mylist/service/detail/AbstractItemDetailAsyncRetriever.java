package com.safeway.app.emju.mylist.service.detail;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.service.ItemDetailAsyncRetriever;

import play.libs.F.Promise;
import play.libs.F.PromiseTimeoutException;

public abstract class AbstractItemDetailAsyncRetriever<T> implements ItemDetailAsyncRetriever<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemDetailAsyncRetriever.class);
	private static long ITEM_CACHE_TIMEOUT = 2000;
	private static long ITEM_CACHE_TIMEOUT_INCREMENT = 1000;

	public Map<Long, T>  getDetailsPromiseResul(Promise<Map<Long, T>> promiseItemDetail) 
    		throws ApplicationException {
		
		Map<Long, T> offerDetails = null;
    	LOGGER.info("getDetailsPromiseResul...");
    	
		try {			
			offerDetails = getOfferDetailfromPromise(promiseItemDetail, ITEM_CACHE_TIMEOUT);						
		} catch(PromiseTimeoutException ptoe) {
			
			LOGGER.error("Timeout from OfferDetailPromise.get " + ptoe.getMessage());			
			// Re-try if timed out
			try {
				offerDetails = getOfferDetailfromPromise(promiseItemDetail, ITEM_CACHE_TIMEOUT + 
						ITEM_CACHE_TIMEOUT_INCREMENT);									
			} catch(PromiseTimeoutException ptoe1){
				LOGGER.error("Timeout again from OfferDetailPromise.get " + ptoe.getMessage());
				handleException(ptoe1);
			}
		}
		
		return offerDetails;
	}
	
	private Map<Long, T> getOfferDetailfromPromise(Promise<Map<Long, T>> promiseOfferDetail, long timeOut) 
    		throws PromiseTimeoutException {

		long startOfferDtlCache = System.currentTimeMillis();
		Map<Long, T> offerDetailMap = (Map<Long, T>) promiseOfferDetail.get(timeOut, TimeUnit.MILLISECONDS);
		long endOfferDtlCache = System.currentTimeMillis();
		LOGGER.debug("Time taken to retrieve OfferDetail cache :"+ (endOfferDtlCache - startOfferDtlCache));
		return offerDetailMap;
    }
	
    private void handleException(Exception e) throws ApplicationException {
		LOGGER.error("OfferServiceException: " + e.getMessage() + e.getStackTrace());
		StackTraceElement[] steArray = e.getStackTrace();    		
		for (int i=0; i< steArray.length; i++) {
			StackTraceElement ste = steArray[i];
			LOGGER.error(ste.getClassName() + "." + ste.getMethodName() + ste.getLineNumber());
		}
		e.printStackTrace();
		throw new ApplicationException(null, e.getMessage(), null);    	
    }
}
