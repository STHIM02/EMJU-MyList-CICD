package com.safeway.app.emju.mylist.service.detail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.pricing.dao.ClubPriceDAO;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;

import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;

public class YCSItemDetailAsyncRetriever extends AbstractItemDetailAsyncRetriever<ClubPrice> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YCSItemDetailAsyncRetriever.class);
	
	private ClubPriceDAO clubPriceDao;
	
	@Inject
	public YCSItemDetailAsyncRetriever(ClubPriceDAO clubPriceDao){
		this.clubPriceDao = clubPriceDao;
	}

	@Override
	public Promise<Map<Long, ClubPrice>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		return Promise.promise(() -> this.getItemDetails(itemMap, shoppingListVO));
		
	}
	
	@Override
	public Promise<Map<Long, ClubPrice>> getAsyncDetails(String itemType, Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO, ExecutionContext threadCtx) throws ApplicationException {
		
		ExecutionContext exeCtx = threadCtx != null ? threadCtx : Akka.system().dispatchers().defaultGlobalDispatcher();
		
		Promise<Map<Long, ClubPrice>> promise = F.Promise.promise((Function0<Map<Long, ClubPrice>>) () -> {
            return this.getItemDetails(itemMap, shoppingListVO);
        } , exeCtx);
		
		return promise;
	}
	
	private Map<Long, ClubPrice> getItemDetails(Map<String, ShoppingListItem> itemMap,
			ShoppingListVO shoppingListVO) throws ApplicationException {
		
		LOGGER.debug("YCS Items found: " + itemMap.keySet());
		Map<Long, ClubPrice> ycsPrices = new HashMap<Long, ClubPrice>();
		List<Long> scanCodes = new ArrayList<Long>();
		List<String> itemIds = new ArrayList<String>();
		String timeZone = shoppingListVO.getHeaderVO().getPreferredStore().getTimeZone();
		Integer storeId = shoppingListVO.getHeaderVO().getPreferredStore().getStoreId();

		for (Entry<String, ShoppingListItem> entry : itemMap.entrySet()) {

			scanCodes.add(Long.valueOf(entry.getKey()));
		}

		Map<Long, ClubPrice> ycsPricesByStoreId = clubPriceDao.findItemPrices(timeZone, storeId);
		ClubPrice entity = null;
		
		for(Long scanCode : scanCodes) {
			
			entity = ycsPricesByStoreId.get(scanCode);
			if(entity != null) {
				ycsPrices.put(scanCode, entity);
			} else {
				itemIds.add(scanCode.toString());
			}
		}
		
		fillUpdatableItems(shoppingListVO, itemMap, itemIds);
		
		return ycsPrices;
	}
	
	private void fillUpdatableItems(ShoppingListVO shoppingListVO, Map<String, ShoppingListItem> itemMap,
			List<String> itemIds) {
		
		ShoppingListItem shoppingListItem = null;
		Integer ttl = null;
		
		for(String itemId : itemIds) {
			
			shoppingListItem = itemMap.get(itemId);
			if(shoppingListItem.getTtl() == null || shoppingListItem.getTtl() == 0){
				
				ttl = DataHelper.getTTLsetup(new Date(), 0, 14);
				shoppingListItem.setTtl(ttl);
				shoppingListVO.getUpdateYCSItem().add(shoppingListItem);
			}
		}
	}

}
