package com.safeway.app.emju.mylist.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.cliptracking.service.OfferStatusService;
import com.safeway.app.emju.allocation.pricing.dao.ClubPriceDAO;
import com.safeway.app.emju.cache.MiscEntityCache;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.entity.Store;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mail.domain.EmailInformation;
import com.safeway.app.emju.mail.service.EmailBroker;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.constant.Constants.ItemTypeCode;
import com.safeway.app.emju.mylist.dao.ShoppingListDAO;
import com.safeway.app.emju.mylist.dao.StoreDAO;
import com.safeway.app.emju.mylist.email.EmailDispatcher;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.ExecutionContextHelper;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.CategoryHierarchyVO;
import com.safeway.app.emju.mylist.model.HeaderVO;
import com.safeway.app.emju.mylist.model.HierarchyVO;
import com.safeway.app.emju.mylist.model.MailListVO;
import com.safeway.app.emju.mylist.model.PreferredStore;
import com.safeway.app.emju.mylist.model.ShoppingListGroup;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.util.GenericConstants;

import play.Configuration;
import play.Play;
import play.libs.F.Promise;
import scala.concurrent.ExecutionContext;

public class ShoppingListServiceImp implements ShoppingListService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListServiceImp.class);

	private static final String J4U_IMAGE_URL;
	private static final String YCS_IMAGE_URL;
	private static final String YCS_IMAGE_EXT;
	private static final String WS_IMAGE_URL;
	private static final String WS_IMAGE_EXT;
	private static final int LOG_TIMEOUT;

	static {
		
		Configuration config = Play.application().configuration();
		J4U_IMAGE_URL = config.getString("j4u.offer.image.url");
		YCS_IMAGE_URL = config.getString("j4u.ycs.image.url");
		YCS_IMAGE_EXT = config.getString("j4u.ycs.image.ext");
		WS_IMAGE_URL = config.getString("j4u.ws.image.url");
		WS_IMAGE_EXT = config.getString("j4u.ws.image.ext");
		LOG_TIMEOUT = config.getInt("emju.app.mylist.log.timeout");
	}

	private StoreDAO storeDAO;
	private ShoppingListDAO shoppingListDAO;
	private OfferStatusService offerStatusService;
	private MatchOfferSevice matchOfferService;
	private ItemDetailsService itemDetaislService;
	private MiscEntityCache miscEntityCache;
	private StoreCache storeCache;
	private EmailBroker emailBroker;

	@Inject
	public ShoppingListServiceImp(ShoppingListDAO shoppingListDAO, OfferStatusService offerStatusService,
			MatchOfferSevice matchOfferService, ItemDetailsService itemDetailsService, OfferDetailCache offerCache,
			ClubPriceDAO clubPriceDao, MiscEntityCache miscEntityCache, StoreCache storeCache, EmailBroker emailBroker,
			StoreDAO storeDAO) {

		this.storeDAO = storeDAO;
		this.shoppingListDAO = shoppingListDAO;
		this.offerStatusService = offerStatusService;
		this.matchOfferService = matchOfferService;
		this.itemDetaislService = itemDetailsService;
		this.miscEntityCache = miscEntityCache;
		this.storeCache = storeCache;
		this.emailBroker = emailBroker;

	}

	@Override
	public FaultCodeBase findTimeZoneFromPostalCode(String postalCode, HeaderVO headerVO) {

		return null;
	}

	@Override
	public List<ShoppingListVO> getShoppingList(ShoppingListVO shoppingListVO) throws ApplicationException {

		LOGGER.info(">>> getShoppingList");
		
		List<ShoppingListVO> returnShoppingListVOList = new ArrayList<ShoppingListVO>();

		setPreferredStoreInfo(shoppingListVO.getHeaderVO(), shoppingListVO.getHeaderVO().getParamStoreId());

		Date fromTime = null;

		try {
			if (shoppingListVO.getHeaderVO().getTimestamp() != null) {
				DateFormat inputFormatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
				fromTime = inputFormatter.parse(shoppingListVO.getHeaderVO().getTimestamp());
			}
		} catch (ParseException pe) {
			LOGGER.error("Invalid Date format");
			throw new ApplicationException(FaultCodeBase.EMLS_INVALID_TIMESTAMP, null, null);
		}

		List<ShoppingListItemVO> deletedItems = fromTime == null ? null : new ArrayList<ShoppingListItemVO>();

		String[] itemIds = shoppingListVO.getItemIds();
		String ycsStoreId = shoppingListVO.getHeaderVO().getParamStoreId();
		String details = shoppingListVO.getHeaderVO().getDetails();

		boolean versionValues[] = shoppingListVO.getHeaderVO().getVersionValues();
		boolean filterAllItems = versionValues[1];

		boolean hasItemIdFilter = false;
		List<String> itemIdsList = null;

		try {

			Long houseHoldId = Long.parseLong(shoppingListVO.getHeaderVO().getSwyhouseholdid());
			if (itemIds != null) {
				hasItemIdFilter = true;
				itemIdsList = Arrays.asList(itemIds);
			}

			Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap = new HashMap<String, Map<String, ShoppingListItem>>();
			Map<String, Integer> logMap = new HashMap<String, Integer>();

			if (!filterAllItems) {
				if (ValidationHelper.isNonEmpty(ycsStoreId)) {
					ycsStoreId = validateStoreInRequest(ycsStoreId);
				} else {
					ycsStoreId = shoppingListVO.getHeaderVO().getPreferredStore().getStoreId().toString();
				}
			}
			
			if(!ValidationHelper.isNumber(ycsStoreId)) {
				LOGGER.error("Error on getShoppingList, ycsStoreId is not a number");
				throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, 
						"invalid or null storeId", null);
			}
			LOGGER.info("Store ID used for Filtering YCS items after: " + ycsStoreId);

			// Initialize map with all the item types
			for (ItemTypeCode itemType : ItemTypeCode.values()) {

				shoppingListItemsMap.put(itemType.toString(), new HashMap<String, ShoppingListItem>());
				logMap.put(itemType.toString(), 0);
			}

			LOGGER.debug("Retrieving redeemed offers");
			List<Long> redeemedOfferList = offerStatusService.findRedeemedOffersForRemoval(houseHoldId);
			LOGGER.debug("After retrieving redeemed offers");

			LOGGER.debug("Retrieving shopping list items");
			List<ShoppingListItem> shoppingListItems = shoppingListDAO.getShoppingListItems(shoppingListVO);
			LOGGER.debug("Number of items retrieved: " + shoppingListItems.size());

			if (ValidationHelper.isEmpty(shoppingListItems)) {

				LOGGER.warn(
						"Shopping List NOT exist for CUSTOMER_GUID: " + shoppingListVO.getHeaderVO().getSwycustguid());

			} else {

				LOGGER.info("Got shoppingListItemsMap");
			}

			long startTime = System.currentTimeMillis();
			
			processShoppingList(shoppingListItems, shoppingListItemsMap, redeemedOfferList, itemIdsList,
					hasItemIdFilter, versionValues, fromTime, deletedItems, Integer.valueOf(ycsStoreId), logMap);
			
			long endTime = System.currentTimeMillis();
			
			if((endTime - startTime) > LOG_TIMEOUT) {
				
				LOGGER.error("processShoppingList method took " + (endTime - startTime) + " milliseconds "
						+ "to process " + shoppingListItems.size() + " shopping list items");
				LOGGER.error("The distribution of item types are: ");
				for(Entry<String, Integer> entry : logMap.entrySet()) {
					LOGGER.error("Total of " + entry.getKey() + " retrived: " + entry.getValue() + 
							", total valid items: " + shoppingListItemsMap.get(entry.getKey()).size());
				}
			}

			List<ShoppingListItemVO> newSLItemVoSet = new ArrayList<ShoppingListItemVO>();

			setItemDetails(shoppingListItemsMap, shoppingListVO, newSLItemVoSet, details);

			shoppingListVO.setId("1");
			shoppingListVO.setTitle(GenericConstants.DEFAULT_SHOPPING_LIST);
			shoppingListVO.setDescription(Constants.EMPTY_STRING);
			shoppingListVO.setItems(newSLItemVoSet);
			shoppingListVO.setDeletedItems(deletedItems);

			returnShoppingListVOList.add(shoppingListVO);
			
			setUpdatableTTLItems(shoppingListVO);

			LOGGER.info("Final ShoppingListsVO Created: " + returnShoppingListVOList);
			if (shoppingListVO.getItemIds() == null) {
				returnShoppingListVOList = setCategoryDetails(returnShoppingListVOList);
			}
			LOGGER.info("<<< getShoppingList");

		} catch (ApplicationException ae) {
			LOGGER.error("getShoppingList ApplicationException : " + shoppingListVO.getHeaderVO());
			LOGGER.error(ae.getMessage(), ae);
			if (ae.getFaultCode().getCode() != null && ae.getFaultCode().getDescription() != null) {
				throw ae;
			} else {
				throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, null, null);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
		}
		return returnShoppingListVOList;
	}

	public Integer getShoppingListCount(ShoppingListVO shoppingListVO, String listName) throws ApplicationException {

		Integer totalCount = 0;
		String listlookup = listName == null ? GenericConstants.DEFAULT_SHOPPING_LIST : listName;

		try {

			List<ShoppingListVO> shoppingLists = getShoppingList(shoppingListVO);

			for (ShoppingListVO shoppingList : shoppingLists) {

				if (shoppingList.getTitle().equals(listlookup)) {

					totalCount = shoppingList.getItems().size();
					break;
				}
			}
		} catch (ApplicationException ae) {

			LOGGER.warn("getShoppingListCount ApplicationException : ");
			LOGGER.error(ae.getMessage(), ae);
			if (ae.getFaultCode().getCode() != null && ae.getFaultCode().getDescription() != null) {
				throw ae;
			}
		} catch (Exception e) {
			LOGGER.error("Error in getShoppingListCount: " + totalCount + " ::Exception: " + e);
			LOGGER.error(e.getMessage(), e);
			throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
		}

		LOGGER.info("Total Valid Items Count :" + totalCount);

		return totalCount;
	}

	public void sendShoppingListMail(MailListVO mailListVO, HeaderVO headerVO) throws ApplicationException {

		String listlookup = GenericConstants.DEFAULT_SHOPPING_LIST;
		String ycsStoreId = headerVO.getParamStoreId();
		boolean versionValues[] = headerVO.getVersionValues();
		boolean filterAllItems = versionValues[1];
		String[] toEmails = mailListVO.getToEmails();
		String[] itemIds = consolidateItemLists(mailListVO);
		// validating empty item ids and emails
		if (ValidationHelper.isNullOrEmptyArray(toEmails) || ValidationHelper.isNullOrEmptyArray(itemIds)) {

			LOGGER.error("Mandatory input (Email id and SLItemId) not provided");
			throw new ApplicationException(FaultCodeBase.EMLS_INVALID_EMAIL_INFO, null, null);
		}

		for (int count = 0; count < toEmails.length; count++) {
			String email = toEmails[count];
			if (!ValidationHelper.validateEmail(email)) {
				LOGGER.error("Invalid Email id");
				throw new ApplicationException(FaultCodeBase.EMLS_INVALID_EMAIL_ID, null, null);
			}
		}

		EmailInformation slNotification = new EmailInformation();
		slNotification.setToEmails(toEmails);

		// Enabling offer details
		headerVO.setDetails(Constants.YES);
		ShoppingListVO shoppingListVO = new ShoppingListVO();
		shoppingListVO.setHeaderVO(headerVO);

		// the item ids from input for filtration
		shoppingListVO.setItemIds(itemIds);

		/*
		 * Calling the service method to return all lists with details.
		 * Currently there will be one list with multiple items.
		 */
		List<ShoppingListVO> integratedShoppingList = getShoppingList(shoppingListVO);
		if (filterAllItems || ValidationHelper.isEmpty(ycsStoreId)) {
			ycsStoreId = shoppingListVO.getHeaderVO().getPreferredStore().getStoreId().toString();
		}

		ShoppingListVO shoppingList = null;
		if (ValidationHelper.isNonEmpty(integratedShoppingList)) {

			for (ShoppingListVO list : integratedShoppingList) {

				if (list.getTitle().equals(listlookup)) {

					shoppingList = list;
					break;
				}
			}

			if (ValidationHelper.isNonEmpty(shoppingList.getItems())) {

				sendEmail(shoppingList.getItems(), mailListVO, headerVO.getBannner(), 
						slNotification, ycsStoreId);
				
			} else {
				LOGGER.warn("No record found");
				throw new ApplicationException(FaultCodeBase.EMLS_NO_LIST_FOUND, null, null);
			}

		} else {
			LOGGER.warn("No record found");
			throw new ApplicationException(FaultCodeBase.EMLS_NO_LIST_FOUND, null, null);
		}

	}

	private void setUpdatableTTLItems(ShoppingListVO shoppingListVO) {
		
		LOGGER.debug("Inside setUpdatableTTLItems with " +  shoppingListVO);
		List<ShoppingListItem> updatebleItems = new ArrayList<ShoppingListItem>();
		
		if(ValidationHelper.isNonEmpty(shoppingListVO.getUpdateYCSItem())) {
			LOGGER.info("YCS items to process: " + shoppingListVO.getUpdateYCSItem().size());
			updatebleItems.addAll(shoppingListVO.getUpdateYCSItem());
		}
		if(ValidationHelper.isNonEmpty(shoppingListVO.getUpdateUPCItem())) {
			LOGGER.info("UPC items to process: " + shoppingListVO.getUpdateUPCItem().size());
			updatebleItems.addAll(shoppingListVO.getUpdateUPCItem());
		}
		if(ValidationHelper.isNonEmpty(updatebleItems)) {
			
			ListItemMaintainanceService maintainanceService = 
					new ListItemMaintainanceService(updatebleItems, shoppingListDAO);
			
			ExecutionContext executor = ExecutionContextHelper.getContext("play.akka.actor.ttl-context");
			executor.execute(maintainanceService);
		}
	}

	private void setPreferredStoreInfo(HeaderVO headerVO, String selectedStoreId) throws ApplicationException {

		LOGGER.debug("setPreferredStoreInfo() starts.");

		PreferredStore preferredStore = null;
		try {

			LOGGER.debug("store: " + selectedStoreId);
			LOGGER.debug("hhid: " + headerVO.getSwyhouseholdid());
			LOGGER.debug("postalCode: " + headerVO.getPostalcode());

			Integer storeId = null;
			if (ValidationHelper.isNonEmpty(selectedStoreId) && headerVO.getVersionValues()[1]) {

				storeId = Integer.valueOf(selectedStoreId);

			}
			preferredStore = storeDAO.findStoreInfo(storeId, Long.valueOf(headerVO.getSwyhouseholdid()),
					headerVO.getPostalcode());
			headerVO.setPreferredStore(preferredStore);
			LOGGER.debug("PreferredStore: " + headerVO.getPreferredStore());

		} catch (ApplicationException ae) {
			LOGGER.error("Caught Exception" + ae.getMessage(), ae);
			LOGGER.error("preferredStore: null");
			throw ae;
		} catch (Exception e) {
			LOGGER.error("Caught Exception" + e.getMessage(), e);
			LOGGER.error("Error on setPreferredStoreInfo");
				throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, 
						"invalid or null storeId", null);
		}

		LOGGER.debug("setPreferredStoreInfo() finished.");
	}

	private void processShoppingList(List<ShoppingListItem> shoppingListItems,
			Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap, List<Long> redeemedOfferList,
			List<String> itemIdsList, boolean hasItemIdFilter, boolean[] versionValues, Date fromTime,
			List<ShoppingListItemVO> deletedItems, Integer storeId, Map<String, Integer> logMap) {

		boolean canBeProcess = false;
		String itemTypeCd = null;
		String shoppingListItemId = null;
		String clipId = null;
		String itemId = null;
		String mapKey = null;
		Integer itemStoreId = null;
		Map<String, ShoppingListItem> itemMap = null;
		Timestamp currentTime;

		if (ValidationHelper.isNonEmpty(shoppingListItems)) {

			currentTime = new Timestamp(System.currentTimeMillis());
			
			for (ShoppingListItem shoppingListItem : shoppingListItems) {

				canBeProcess = false;
				itemTypeCd = shoppingListItem.getItemTypeCd();
				itemTypeCd = itemTypeCd.equals("MF") || itemTypeCd.equals("SC")
						? Constants.ItemTypeCode.COUPON_ITEM.toString() : itemTypeCd;
				itemId = shoppingListItem.getItemId();
				shoppingListItemId = shoppingListItem.getItemRefId();
				clipId = shoppingListItem.getClipId();
				
				if(clipId == null) {
					
					String error = "Service has found an item with null Clip Id. Item :" + shoppingListItem.toString();
					LOGGER.error(FaultCodeBase.EMLS_INVALID_CLIP_ID, error, new Exception(error), false);
					continue;
				}
				
				itemStoreId = shoppingListItem.getStoreId();
				itemMap = shoppingListItemsMap.get(itemTypeCd);
				
				logMap.put(itemTypeCd, logMap.get(itemTypeCd) + 1);

				if (fromTime == null || fromTime.before(shoppingListItem.getLastUpdTs())) {
					// For CC, PD
					if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.COUPON_ITEM.toString())
							|| itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.PERSONAL_DEAL_ITEM.toString())) {

						mapKey = itemId;
						if (ValidationHelper.isEmpty(redeemedOfferList)
								|| !redeemedOfferList.contains(Long.valueOf(itemId))) {
							canBeProcess = true;
						}
					// For YCS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {

						if (itemStoreId.intValue() == storeId.intValue()) {

							mapKey = itemId;
							canBeProcess = true;
						}

						// For MCS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.MOBILE_SPECIAL_ITEM.toString())) {

						if (versionValues[2]) {

							if (shoppingListItem.getItemEndDate().after(currentTime)
									&& itemStoreId.intValue() == storeId.intValue()) {

								mapKey = shoppingListItemId;
								canBeProcess = true;
							}
						}
						// For WS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.WEEKLY_SPECIAL_ITEM.toString())) {

						if (versionValues[0]) {

							if (shoppingListItem.getItemEndDate().after(currentTime)
									&& itemStoreId.intValue() == storeId.intValue()) {

								mapKey = itemId;
								canBeProcess = true;
							}
						}
					} else { //Includes FF, UPC, ELP and REC
						
						mapKey = itemId;
						canBeProcess = true;
					}
				}
				if (canBeProcess) {
					LOGGER.info("Item type " + itemTypeCd + " identifier: " + mapKey);
					if (shoppingListItem.getDeleteTs() == null) {
						if (hasItemIdFilter) {

							if (itemIdsList.contains(clipId)) {
								itemMap.put(mapKey, shoppingListItem);
							}
						} else {
							itemMap.put(mapKey, shoppingListItem);
						}
					} else if (deletedItems != null) {
						ShoppingListItemVO deletedItem = new ShoppingListItemVO();
						deletedItem.setId(shoppingListItemId);
						deletedItems.add(deletedItem);
					}
				}
			}
		}
	}

	private void setItemDetails(Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap,
			ShoppingListVO shoppingListVO, List<ShoppingListItemVO> newSLItemVoSet, String details)
					throws ApplicationException {

		Map<String, ShoppingListItem> itemMap = null;
		Map<String, Promise<Map<Long, ?>>> offerDetails = new HashMap<String, Promise<Map<Long, ?>>>();

		List<String> itemIteration = getOrderByItemNumbers(shoppingListItemsMap);

		for (String itemType : itemIteration) {
			
			LOGGER.debug("Item type to set details: " + itemType);
			itemMap = shoppingListItemsMap.get(itemType);
			Map<String, Map<String, List<AllocatedOffer>>> matchedOffers = null;
			
			if (ValidationHelper.isNonEmpty(itemMap)) {
				
				if (itemType.equals(ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
						&& details.equals(Constants.YES)) {
					
					Map<Long, ShoppingListItem> upcItemMap = new HashMap<Long, ShoppingListItem>();
					for (Entry<String, ShoppingListItem> upcEntry : itemMap.entrySet()) {

						upcItemMap.put(Long.parseLong(upcEntry.getKey()), upcEntry.getValue());
					}
						
					matchedOffers = matchOfferService.getRelatedOffers(upcItemMap, shoppingListVO.getHeaderVO());					
						
					if (ValidationHelper.isNonEmpty(matchedOffers)) {
						cleanMatchedOffers(matchedOffers, shoppingListItemsMap);
					}

				}

				if (itemType.equals(ItemTypeCode.COUPON_ITEM.toString())
						|| itemType.equals(ItemTypeCode.PERSONAL_DEAL_ITEM.toString())
						|| itemType.equals(ItemTypeCode.CLUB_SPECIAL_ITEM.toString())
						|| itemType.equals(ItemTypeCode.WEEKLY_SPECIAL_ITEM.toString())
						|| (itemType.equals(ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
								&& !details.equals(Constants.YES))) {

					offerDetails.put(itemType, itemDetaislService.getAsyncDetails(itemType, itemMap, shoppingListVO));

				} else {

					newSLItemVoSet.addAll(itemDetaislService.setItemDetails(itemType, itemMap, shoppingListVO, matchedOffers));
				}
			}
		}

		for (Entry<String, Promise<Map<Long, ?>>> entry : offerDetails.entrySet()) {

			LOGGER.debug("Retrieving async details for type: " + entry.getKey());
			Map<Long, ?> itemDetail = itemDetaislService.getDetailsPromiseResul(entry.getKey(), entry.getValue());
			LOGGER.debug("Retrived offer detail from async of size " + itemDetail.size());
			if (ValidationHelper.isNonEmpty(itemDetail)) {
				itemMap = shoppingListItemsMap.get(entry.getKey());
				newSLItemVoSet
						.addAll(itemDetaislService.setItemDetails(entry.getKey(), itemDetail, itemMap, shoppingListVO));
			}
		}

	}

	private List<String> getOrderByItemNumbers(Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap) {

		List<String> result = new ArrayList<String>();

		Map<String, Integer> itemByNumbers = new HashMap<String, Integer>();

		for (Entry<String, Map<String, ShoppingListItem>> entry : shoppingListItemsMap.entrySet()) {
			
			itemByNumbers.put(entry.getKey(), entry.getValue().size());
		}

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(itemByNumbers.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		for (Entry<String, Integer> entry : list) {

			result.add(entry.getKey());
		}

		return result;
	}

	private void cleanMatchedOffers(final Map<String, Map<String, List<AllocatedOffer>>> matchedOffers,
			final Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap) {

		Map<String, ShoppingListItem> itemsMap = null;
		Map<String, List<AllocatedOffer>> offers = null;
		List<AllocatedOffer> typedOffers = null;

		for (String itemTypeCode : Constants.REMOVABLE_MATCHED_OFFERS) {

			itemsMap = shoppingListItemsMap.get(itemTypeCode);
			if (ValidationHelper.isNonEmpty(itemsMap)) {

				Iterator<String> it = matchedOffers.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					offers = matchedOffers.get(key);
					typedOffers = offers.get(itemTypeCode);
					if (ValidationHelper.isNonEmpty(typedOffers)) {
						for (Iterator<AllocatedOffer> iter = typedOffers.listIterator(); iter.hasNext();) {
							if (itemsMap.containsKey(iter.next().getOfferId().toString())) {
								iter.remove();
							}
						}

						if (typedOffers.isEmpty()) {
							matchedOffers.get(key).remove(itemTypeCode);
						}
					}
				}

			}
		}
	}

	private List<ShoppingListVO> setCategoryDetails(final List<ShoppingListVO> shoppingLists) throws Exception {

		LOGGER.info("Start setCategoryDetails: ");
		List<ShoppingListVO> shoppingListsObj = shoppingLists;
		Map<String, CategoryHierarchyVO> categoryMap = new HashMap<String, CategoryHierarchyVO>();
		List<CategoryHierarchyVO> categories = null;
		CategoryHierarchyVO itemCategory = null;
		Integer categoryId = null;
		String categoryName = null;
		try {
			Map<Integer, String> category = miscEntityCache.getAllCategories();
			for (ShoppingListVO shoppingListVO : shoppingListsObj) {
				for (ShoppingListItemVO shoppingListitem : shoppingListVO.getItems()) {
					if (shoppingListitem.getCategoryId() != null && !shoppingListitem.getCategoryId().isEmpty()) {
						categoryId = Integer.parseInt(shoppingListitem.getCategoryId());
						categoryName = category.get(categoryId);
						if (categoryName != null) {
							if (categoryMap.containsKey(categoryName)) {
								itemCategory = categoryMap.get(categoryName);
								int count = itemCategory.getCount();
								itemCategory.setCount(count++);
							} else {
								itemCategory = new CategoryHierarchyVO();
								itemCategory.setId(categoryId);
								itemCategory.setName(categoryName);
								itemCategory.setCount(1);
								categoryMap.put(categoryName, itemCategory);
							}
						}
					}
				}
				categories = new ArrayList<CategoryHierarchyVO>(categoryMap.values());
				HierarchyVO hierarchyVO = new HierarchyVO();
				hierarchyVO.setCategories(categories);
				shoppingListVO.setHierarchies(hierarchyVO);
				LOGGER.info(" setCatogoryDetails: categories size :" + categories.size());
			}
		} catch (Exception e) {
			LOGGER.error("Error in setCategoryDetails - CategoryVO: " + itemCategory + " ::Exception: " + e);
		}
		LOGGER.info("End setCatogoryDetails: ");
		return shoppingListsObj;
	}

	private String validateStoreInRequest(String storeId) throws Exception {

		String store = null;

		if (ValidationHelper.isNumber(storeId)) {

			Store storeEntity = storeCache.getStoreDetailsById(Integer.parseInt(storeId));
			store = storeEntity.getStoreId().toString();
		}

		if (null == store) {

			return new String("0");
		}
		return store;
	}

	private String[] consolidateItemLists(final MailListVO mailListInput) {

		List<String> list = new ArrayList<String>();
		ShoppingListGroup[] shoppingListGroup = mailListInput.getGroups();
		for (ShoppingListGroup listGroup : shoppingListGroup) {
			List<String> sendList = listGroup.getItemIds();
			for (String ids : sendList) {
				if (list != null) {
					list.add(ids);
				}
			}
		}

		return list.toArray(new String[list.size()]);
	}
	
	private void sendEmail(List<ShoppingListItemVO> items, MailListVO mailListVO, String banner, 
			EmailInformation slNotification, String ycsStoreId) {
		
		EmailDispatcher dispatcher = new EmailDispatcher(emailBroker, storeCache, items, 
				mailListVO, banner, slNotification, ycsStoreId, J4U_IMAGE_URL, 
				YCS_IMAGE_URL, YCS_IMAGE_EXT, WS_IMAGE_URL, WS_IMAGE_EXT); 
		ExecutionContext executor = ExecutionContextHelper.getContext("play.akka.actor.email-context");
		executor.execute(dispatcher);
	}

}
