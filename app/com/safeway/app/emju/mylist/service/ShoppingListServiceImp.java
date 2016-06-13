package com.safeway.app.emju.mylist.service;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

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
import com.safeway.app.emju.mail.domain.EmailItemDetail;
import com.safeway.app.emju.mail.domain.EmailItemGroup;
import com.safeway.app.emju.mail.service.EmailBroker;
import com.safeway.app.emju.mail.util.EmailType;
import com.safeway.app.emju.mylist.comparator.ShoppingListEmailComparator;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.constant.Constants.ItemTypeCode;
import com.safeway.app.emju.mylist.dao.ShoppingListDAO;
import com.safeway.app.emju.mylist.dao.StoreDAO;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
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

public class ShoppingListServiceImp implements ShoppingListService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListServiceImp.class);

	private static final String J4U_IMAGE_URL;
	private static final String YCS_IMAGE_URL;
	private static final String YCS_IMAGE_EXT;
	// the savings suffix
	private static Map<String, String> savingsSuffix;
	// types
	private static Map<String, String> types;
	// usage limits
	private static Map<String, String> usageLimits;

	static {
		Configuration config = Play.application().configuration();
		J4U_IMAGE_URL = config.getString("j4u.offer.image.url");
		YCS_IMAGE_URL = config.getString("j4u.ycs.image.url");
		YCS_IMAGE_EXT = config.getString("j4u.ycs.image.ext");

		savingsSuffix = new HashMap<String, String>();
		savingsSuffix.put("MF", "");
		savingsSuffix.put("SC", "");
		savingsSuffix.put("PD", "Your Price ");
		savingsSuffix.put("YCS", "Club Price* $");

		types = new HashMap<String, String>();
		types.put("PD", "Personalized Price");
		types.put("SC", "Store Coupon");
		types.put("MF", "MFG Coupon");
		types.put("DM", "Deal Match**");
		types.put("YCS", "Club Price*");

		usageLimits = new HashMap<String, String>();
		usageLimits.put("O", "One-time");
		usageLimits.put("U", "Unlimited");
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

			if (!filterAllItems) {
				if (ValidationHelper.isNonEmpty(ycsStoreId)) {
					ycsStoreId = validateStoreInRequest(ycsStoreId);
				} else {
					ycsStoreId = shoppingListVO.getHeaderVO().getPreferredStore().getStoreId().toString();
				}
			}
			LOGGER.info("Store ID used for Filtering YCS items after: " + ycsStoreId);

			// Initialize map with all the item types
			for (ItemTypeCode itemType : ItemTypeCode.values()) {

				shoppingListItemsMap.put(itemType.toString(), new HashMap<String, ShoppingListItem>());
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
					hasItemIdFilter, versionValues, fromTime, deletedItems, Integer.valueOf(ycsStoreId));
			
			long endTime = System.currentTimeMillis();
			
			if((endTime - startTime) > 1000) {
				
				LOGGER.error("processShoppingList method took " + (endTime - startTime) + " milliseconds "
						+ "to process " + shoppingListItems.size() + " shopping list items");
			}

			List<ShoppingListItemVO> newSLItemVoSet = new ArrayList<ShoppingListItemVO>();

			setItemDetails(shoppingListItemsMap, shoppingListVO, newSLItemVoSet, details);

			shoppingListVO.setId("1");
			shoppingListVO.setTitle(GenericConstants.DEFAULT_SHOPPING_LIST);
			shoppingListVO.setDescription(Constants.EMPTY_STRING);
			shoppingListVO.setItems(newSLItemVoSet);
			shoppingListVO.setDeletedItems(deletedItems);

			returnShoppingListVOList.add(shoppingListVO);

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

				EmailDispatcher dispatcher = new EmailDispatcher(shoppingList.getItems(), 
						mailListVO, headerVO, slNotification, ycsStoreId);
				Executor executor = Executors.newSingleThreadExecutor();
				executor.execute(dispatcher);
				
			} else {
				LOGGER.warn("No record found");
				throw new ApplicationException(FaultCodeBase.EMLS_NO_LIST_FOUND, null, null);
			}

		} else {
			LOGGER.warn("No record found");
			throw new ApplicationException(FaultCodeBase.EMLS_NO_LIST_FOUND, null, null);
		}

	}

	private void setPreferredStoreInfo(HeaderVO headerVO, String selectedStoreId) {

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

		} catch (Exception e) {
			LOGGER.error("Caught Exception" + e.getMessage());
			LOGGER.error("preferredStore: null");
		}

		LOGGER.debug("setPreferredStoreInfo() finished.");
	}

	private void processShoppingList(List<ShoppingListItem> shoppingListItems,
			Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap, List<Long> redeemedOfferList,
			List<String> itemIdsList, boolean hasItemIdFilter, boolean[] versionValues, Date fromTime,
			List<ShoppingListItemVO> deletedItems, Integer storeId) {

		boolean canBeProcess = false;
		String itemTypeCd = null;
		String shoppingListItemId = null;
		String clipId = null;
		String itemId = null;
		String mapKey = null;
		Integer itemStoreId = null;
		Map<String, ShoppingListItem> itemMap = null;

		if (ValidationHelper.isNonEmpty(shoppingListItems)) {

			for (ShoppingListItem shoppingListItem : shoppingListItems) {

				canBeProcess = false;
				itemTypeCd = shoppingListItem.getItemTypeCd();
				itemTypeCd = itemTypeCd.equals("MF") || itemTypeCd.equals("SC")
						? Constants.ItemTypeCode.COUPON_ITEM.toString() : itemTypeCd;
				LOGGER.debug("The item type being processed is " + itemTypeCd);
				itemId = shoppingListItem.getItemId();
				shoppingListItemId = shoppingListItem.getItemRefId();
				clipId = shoppingListItem.getClipId();
				itemStoreId = shoppingListItem.getStoreId();
				itemMap = shoppingListItemsMap.get(itemTypeCd);
				LOGGER.debug("The map is " + itemMap);

				if (fromTime == null || fromTime.before(shoppingListItem.getLastUpdTs())) {
					// For CC, PD
					if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.COUPON_ITEM.toString())
							|| itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.PERSONAL_DEAL_ITEM.toString())) {

						mapKey = itemId;
						if (ValidationHelper.isEmpty(redeemedOfferList)
								|| !redeemedOfferList.contains(Long.valueOf(itemId))) {
							canBeProcess = true;
						}

						// For FF
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.MANUAL_ITEM.toString())) {

						mapKey = shoppingListItemId;
						canBeProcess = true;

						// For UPC
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())) {

						mapKey = itemId;
						canBeProcess = true;

						// For YCS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {

						if (itemStoreId.intValue() == storeId.intValue()) {

							mapKey = itemId;
							canBeProcess = true;
						}

						// For MCS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.MOBILE_SPECIAL_ITEM.toString())) {

						if (versionValues[2]) {

							if (shoppingListItem.getItemEndDate().after(new Timestamp(System.currentTimeMillis()))
									&& itemStoreId.intValue() == storeId.intValue()) {

								mapKey = shoppingListItemId;
								canBeProcess = true;
							}
						}
						// For WS
					} else if (itemTypeCd.equalsIgnoreCase(Constants.ItemTypeCode.WEEKLY_SPECIAL_ITEM.toString())) {

						if (versionValues[0]) {

							if (shoppingListItem.getItemEndDate().after(new Timestamp(System.currentTimeMillis()))
									&& itemStoreId.intValue() == storeId.intValue()) {

								mapKey = shoppingListItemId;
								canBeProcess = true;
							}
						}
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

		// String itemType = null;
		Map<String, ShoppingListItem> itemMap = null;
		Map<String, Promise<Map<Long, Object>>> offerDetails = new HashMap<String, Promise<Map<Long, Object>>>();

		List<String> itemIteration = getOrderByItemNumbers(shoppingListItemsMap);

		// for(ItemTypeCode itemTypes : ItemTypeCode.values()) {
		for (String itemType : itemIteration) {
			LOGGER.debug("Item type to set details: " + itemType);
			// itemType = itemTypes.toString(); //entry.getKey();
			itemMap = shoppingListItemsMap.get(itemType); // entry.getValue();
			if (ValidationHelper.isNonEmpty(itemMap)) {
				if (itemType.equals(ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())) {

					LOGGER.info("UPC Items COUNT:: " + itemMap.size());
					Map<Long, ShoppingListItem> upcItemMap = new HashMap<Long, ShoppingListItem>();

					for (Entry<String, ShoppingListItem> upcEntry : itemMap.entrySet()) {

						upcItemMap.put(Long.parseLong(upcEntry.getKey()), upcEntry.getValue());
					}

					Map<String, Map<String, List<AllocatedOffer>>> matchedOffers = null;
					if (ValidationHelper.isNonEmpty(details) && details.equals(Constants.YES)) {
						matchedOffers = matchOfferService.getRelatedOffers(upcItemMap, shoppingListVO.getHeaderVO());
					}

					if (ValidationHelper.isNonEmpty(matchedOffers)) {
						cleanMatchedOffers(matchedOffers, shoppingListItemsMap);
					}

					newSLItemVoSet.addAll(
							itemDetaislService.setItemDetails(itemType, itemMap, shoppingListVO, matchedOffers));

				} else if (itemType.equals(ItemTypeCode.COUPON_ITEM.toString())
						|| itemType.equals(ItemTypeCode.PERSONAL_DEAL_ITEM.toString())
						|| itemType.equals(ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {

					offerDetails.put(itemType, itemDetaislService.getAsyncDetails(itemType, itemMap, shoppingListVO));

				} else {

					newSLItemVoSet.addAll(itemDetaislService.setItemDetails(itemType, itemMap, shoppingListVO, null));
				}
			}
		}

		for (Entry<String, Promise<Map<Long, Object>>> entry : offerDetails.entrySet()) {

			LOGGER.debug("Retrieving async details for type: " + entry.getKey());
			Map<Long, Object> itemDetail = itemDetaislService.getDetailsPromiseResul(entry.getKey(), entry.getValue());
			LOGGER.debug("Retrived offer detail from async of size " + itemDetail.size());
			if (ValidationHelper.isNonEmpty(itemDetail)) {
				itemMap = shoppingListItemsMap.get(entry.getKey());
				newSLItemVoSet
						.addAll(itemDetaislService.getItemDetails(entry.getKey(), itemDetail, itemMap, shoppingListVO));
			}
		}

	}

	private List<String> getOrderByItemNumbers(Map<String, Map<String, ShoppingListItem>> shoppingListItemsMap) {

		List<String> result = new ArrayList<String>();

		Map<String, Integer> itemByNumbers = new HashMap<String, Integer>();

		for (Entry<String, Map<String, ShoppingListItem>> entry : shoppingListItemsMap.entrySet()) {

			LOGGER.debug("Ordering items: " + entry.getKey() + " size: " + entry.getValue().size());
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
							if (itemsMap.containsKey(iter.next().getOfferId())) {
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

	private String getStoreAdress(String storeId) throws ApplicationException {

		String address = null;

		if (ValidationHelper.isNumber(storeId)) {

			Store storeEntity = storeCache.getStoreDetailsById(Integer.parseInt(storeId));
			address = storeEntity.getAddress1();
		}

		return address;
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

	private EmailItemGroup[] orderShoppingListForEmail(final List<ShoppingListItemVO> itemsVOS,
			final MailListVO criteria) {

		LOGGER.info("start orderShoppingListForEmail");
		ArrayList<EmailItemGroup> shoppingList = new ArrayList<EmailItemGroup>();
		String presentCategory = "";
		String categoryNm = null;
		List<EmailItemDetail> items = null;
		EmailItemGroup shoppingListItemGroup = null;
		int numberOfResults = 0;
		int numberOfCategories = 0;

		// for sorting by category name, title
		List<ShoppingListItemVO> itemVOSList = new ArrayList<ShoppingListItemVO>();
		itemVOSList.addAll(itemsVOS);
		Collections.sort(itemVOSList, new ShoppingListEmailComparator());
		ShoppingListGroup[] shoppingListGroupList = criteria.getGroups();

		for (ShoppingListGroup shoppingListGroup : shoppingListGroupList) {

			List<String> itemIds = shoppingListGroup.getItemIds();
			for (String itemId : itemIds) {

				for (ShoppingListItemVO currentItem : itemVOSList) {

					if (itemId.equals(currentItem.getId())) {

						LOGGER.debug("Item retrieved for mail to be processed: " + currentItem.getReferenceId());
						categoryNm = shoppingListGroup.getGroupName();
						if (ValidationHelper.isEmpty(categoryNm)) {
							categoryNm = Constants.MY_ADDED_ITEMS;
						}

						if (!presentCategory.equals(categoryNm)) {
							numberOfCategories++;
							if (!(numberOfResults == 0)) {
								shoppingList.add(shoppingListItemGroup);
							}
							shoppingListItemGroup = new EmailItemGroup();
							shoppingListItemGroup.setGroupName(categoryNm);
							presentCategory = categoryNm;
							items = new ArrayList<EmailItemDetail>();
							shoppingListItemGroup.setItems(items);
						}

						EmailItemDetail shoppingListItemDetail = new EmailItemDetail();
						shoppingListItemDetail.setCategoryName(currentItem.getCategoryName());

						DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
						DateFormat fromGetFormatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);

						Date startDate = null;
						Date endDate = null;
						String parsedStart = null;
						String parsedEnd = null;

						try {
							if (null != currentItem.getStartDate()) {
								startDate = fromGetFormatter.parse(currentItem.getStartDate());
								parsedStart = formatter.format(startDate);
							}
							shoppingListItemDetail.setEffective(parsedStart);
							if (null != currentItem.getEndDate()) {
								endDate = fromGetFormatter.parse(currentItem.getEndDate());
								parsedEnd = formatter.format(endDate);
							}
							shoppingListItemDetail.setExpiration(parsedEnd);
						} catch (ParseException e) {
							LOGGER.error("Error Parsing Dates- Start Date: " + currentItem.getStartDate()
									+ ":: End Date: " + currentItem.getEndDate());
						}

						if (null != currentItem.getImage()) {
							StringBuffer imageUrl = new StringBuffer();

							if (Constants.J4U.contains(currentItem.getItemType())) {
								imageUrl.append(J4U_IMAGE_URL);
							}
							imageUrl.append(currentItem.getImage());
							shoppingListItemDetail.setImage(imageUrl.toString());
						}

						String itemTitle = currentItem.getTitle() == null ? null : currentItem.getTitle();
						LOGGER.debug("Item title: " + itemTitle);
						shoppingListItemDetail.setName(itemTitle);
						LOGGER.debug("Item quantity " + currentItem.getQuantity());
						String itemDescription = currentItem.getDescription() == null ? null
								: parseDescription(currentItem);
						LOGGER.debug("Item description: " + itemDescription);
						shoppingListItemDetail.setDescription(itemDescription);
						shoppingListItemDetail.setSavings(currentItem.getSavingsValue());
						shoppingListItemDetail.setType(currentItem.getSavingsType());
						shoppingListItemDetail.setUsageLimit(currentItem.getUsage());

						LOGGER.debug("Item Type: " + currentItem.getItemType());

						if (currentItem.getItemType()
								.equalsIgnoreCase(Constants.ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
								|| currentItem.getItemType()
										.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {

							LOGGER.debug("Image Type: " + currentItem.getImage());
							// Validate the image url is not exist then it
							// default to ycs url
							if (null == currentItem.getImage()) {
								StringBuffer imageUrl = new StringBuffer();

								String imageURL = YCS_IMAGE_URL + currentItem.getReferenceId() + YCS_IMAGE_EXT;
								LOGGER.debug("YCS Image Path: " + imageURL);
								if (canImageLoad(imageURL)) {
									imageUrl.append(imageURL);
								}

								LOGGER.debug("YCS Set Image Path: " + imageUrl.toString());
								shoppingListItemDetail.setImage(imageUrl.toString());
							}

							if (currentItem.getItemType()
									.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {
								double promoPrice = 0;

								if (currentItem.getSavingsValue() != null) {
									// promoPrice = ((BigDecimal)
									// Long.valueOf(currentItem.getSavingsValue())).doubleValue();
									promoPrice = Double.parseDouble(currentItem.getSavingsValue());
								}
								String priceMethodType = currentItem.getSavingsCode();
								String priceMethodSubType = currentItem.getSavingsSubCode();
								DecimalFormat decFormat = new DecimalFormat("0.00");
								if (priceMethodType != null
										&& priceMethodType.equalsIgnoreCase(Constants.PRICE_METHOD_TYPE_BOGO)) {
									if (priceMethodSubType != null && priceMethodSubType
											.equalsIgnoreCase(Constants.PRICE_METHOD_SUB_TYPE_B1G1)) {
										shoppingListItemDetail.setSavings(Constants.B1G1_SAVINGS_DESC);
									}
								} else if (priceMethodType != null
										&& priceMethodType.equalsIgnoreCase(Constants.PRICE_METHOD_TYPE_MB)) {
									if (priceMethodSubType != null && priceMethodSubType
											.equalsIgnoreCase(Constants.PRICE_METHOD_SUB_TYPE_MB2)) {
										shoppingListItemDetail.setSavings(savingsSuffix.get("YCS") + " "
												+ decFormat.format(promoPrice) + "<br>" + Constants.MB2_SAVINGS_DESC);
									}
								} else {
									shoppingListItemDetail
											.setSavings(savingsSuffix.get("YCS") + decFormat.format(promoPrice));
								}

								shoppingListItemDetail
										.setType(types.get(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString()));
								shoppingListItemDetail.setUsageLimit(usageLimits.get("U"));
							}
						} else if (Constants.J4U_ADD.contains(currentItem.getItemType())) {

							shoppingListItemDetail.setUsageLimit(usageLimits.get(currentItem.getUsage()));
							shoppingListItemDetail.setType(types.get(currentItem.getItemType()));
							shoppingListItemDetail.setSavings(
									savingsSuffix.get(currentItem.getItemType()) + currentItem.getSavingsValue());
						}

						items.add(shoppingListItemDetail);
						numberOfResults++;
						itemVOSList.remove(currentItem);
						break;
					}
				}
			}
		}

		if (numberOfCategories != shoppingList.size()) {
			shoppingList.add(shoppingListItemGroup);
		}
		LOGGER.info("End orderShoppingListForEmail");
		return shoppingList.toArray(new EmailItemGroup[shoppingList.size()]);

	}

	private boolean canImageLoad(final String imageURL) {

		Image image = null;
		boolean isImageExist = true;
		LOGGER.debug("start method canImageLoad(). . .");
		try {
			URL url = new URL(imageURL);
			LOGGER.debug("URL begin. . .");
			image = ImageIO.read(url);
			LOGGER.debug("image. . .");
			if (image == null) {
				isImageExist = false;
			}

		} catch (IOException e) {
			LOGGER.debug("No Images Exists. . .");
			isImageExist = false;
		}
		return isImageExist;
	}

	private String parseDescription(final ShoppingListItemVO currentItem) {

		StringBuffer sb = new StringBuffer();

		LOGGER.debug("The type of item " + currentItem.getId() + " is: " + currentItem.getItemType());

		try {
			sb.append(currentItem.getDescription());
			if (Constants.J4U_ADD.contains(currentItem.getItemType())
					&& ValidationHelper.isNonEmpty(currentItem.getSummary())) {

				sb = new StringBuffer();
				sb.append(currentItem.getSummary().trim());
				sb.append(", ");
				sb.append(currentItem.getDescription());

			}
			if ((currentItem.getItemType().equals(Constants.ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
					|| currentItem.getItemType().equals(Constants.ItemTypeCode.MANUAL_ITEM.toString()))
					&& ValidationHelper.isNonEmpty(currentItem.getQuantity())) {
				sb.append(" - ");
				sb.append(currentItem.getQuantity());
			}
		} catch (Exception ex) {
			return currentItem.getDescription();
		}
		return sb.toString();
	}
	
	private class EmailDispatcher implements Runnable {
		
		List<ShoppingListItemVO> items;
		MailListVO mailListVO;
		HeaderVO headerVO;
		EmailInformation slNotification;
		String ycsStoreId;
		
		public EmailDispatcher(List<ShoppingListItemVO> items, MailListVO mailListVO, HeaderVO headerVO,
				EmailInformation slNotification, String ycsStoreId) {
			
			this.items = items;
			this.mailListVO = mailListVO;
			this.headerVO = headerVO;
			this.slNotification = slNotification;
			this.ycsStoreId = ycsStoreId;
		}
		
		@Override
		public void run() {

			try{
				slNotification.setGroups(orderShoppingListForEmail(items, mailListVO));
				slNotification.setBannerId(headerVO.getBannner());
				slNotification.setStoreAddress(getStoreAdress(ycsStoreId));
				LOGGER.info("before dispatchShoppingList");
				emailBroker.sendEmail(slNotification, EmailType.SHOPPING_LIST);
			}catch(ApplicationException e) {
				LOGGER.error("An exception happened when trying to send the email: " + e);
			}
		}
	}

}
