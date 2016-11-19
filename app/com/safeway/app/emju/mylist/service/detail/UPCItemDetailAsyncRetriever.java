package com.safeway.app.emju.mylist.service.detail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.safeway.app.emju.cache.RetailScanCache;
import com.safeway.app.emju.cache.entity.RetailScanOffer;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;

public class UPCItemDetailAsyncRetriever extends AbstractItemDetailAsyncRetriever<RetailScanOffer> {
	
	private RetailScanCache retailScanCache;
	
	@Inject
	public UPCItemDetailAsyncRetriever(RetailScanCache retailScanCache) {
		
		this.retailScanCache = retailScanCache;
	}

	@Override
	public Promise<Map<Long, RetailScanOffer>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<Map<Long, RetailScanOffer>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, ExecutionContext threadCtx) throws ApplicationException {

		ExecutionContext exeCtx = threadCtx != null ? threadCtx : Akka.system().dispatchers().defaultGlobalDispatcher();
		
		Promise<Map<Long, RetailScanOffer>> promise = F.Promise.promise((Function0<Map<Long, RetailScanOffer>>) () -> {
            return this.getItemDetails(itemMap);
        } , exeCtx);
		
		return promise;
	}
	
	private Map<Long, RetailScanOffer> getItemDetails(Map<String, ShoppingListItem> itemMap) 
			throws ApplicationException {
		
		List<Long> scanCodes = new ArrayList<Long>();
		
		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {

			scanCodes.add(Long.valueOf(entry.getKey()));
		}
		
		Map<Long, RetailScanOffer> retailScanOfferList = retailScanCache
				.getRetailScanOfferDetailByScanCds(scanCodes.toArray(new Long[scanCodes.size()]));
		
		return retailScanOfferList;
		
	}

}
