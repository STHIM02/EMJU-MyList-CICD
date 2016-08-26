package com.safeway.app.emju.mylist.service.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.cache.WeeklyAddCache;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.model.WeeklyAddVO;

import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;

public class WSItemDetailAsyncRetriever extends AbstractItemDetailAsyncRetriever<WeeklyAddVO> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WSItemDetailAsyncRetriever.class);
	private WeeklyAddCache weeklyAddCache;
	
	@Inject
	public WSItemDetailAsyncRetriever(WeeklyAddCache weeklyAddCache) {
		
		this.weeklyAddCache = weeklyAddCache;
	}
	
	@Override
	public Promise<Map<Long, WeeklyAddVO>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		return Promise.promise(() -> this.getItemDetails(itemMap, shoppingListVO));
	}
	
	@Override
	public Promise<Map<Long, WeeklyAddVO>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, ExecutionContext threadCtx) throws ApplicationException {
		
		ExecutionContext exeCtx = threadCtx != null ? threadCtx : Akka.system().dispatchers().defaultGlobalDispatcher();
		
		Promise<Map<Long, WeeklyAddVO>> promise = F.Promise.promise((Function0<Map<Long, WeeklyAddVO>>) () -> {
            return this.getItemDetails(itemMap, shoppingListVO);
        } , exeCtx);
		
		return promise;
	}
	
	private Map<Long, WeeklyAddVO> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		LOGGER.debug("WS Items found: " + itemMap.keySet());
		Map<Long, WeeklyAddVO> weeklyAdds = new HashMap<Long, WeeklyAddVO>();
		List<String> offerIds = new ArrayList<String>();
		
		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {

			offerIds.add(entry.getKey());
		}
		
		Map<String, WeeklyAddVO> wsOffersByOfferId = weeklyAddCache.getWeeklyAddByOfferId(offerIds);
		WeeklyAddVO entity = null;
		
		for(String offerId : offerIds) {
			
			entity = wsOffersByOfferId.get(offerId);
			if(entity != null) {
				weeklyAdds.put(Long.valueOf(offerId), entity);
			}
		}
		
		return weeklyAdds;
		
	}

}
