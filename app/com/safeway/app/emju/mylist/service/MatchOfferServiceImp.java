package com.safeway.app.emju.mylist.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.google.inject.Inject;
import com.safeway.app.emju.allocation.cliptracking.model.OfferClipStatus;
import com.safeway.app.emju.allocation.cliptracking.service.OfferStatusService;
import com.safeway.app.emju.allocation.dao.CCAllocationDAO;
import com.safeway.app.emju.allocation.dao.PDAllocationDAO;
import com.safeway.app.emju.allocation.dao.PurchasedItemDAO;
import com.safeway.app.emju.allocation.entity.CCAllocatedOffer;
import com.safeway.app.emju.allocation.entity.PDCustomOffer;
import com.safeway.app.emju.allocation.entity.PDDefaultOffer;
import com.safeway.app.emju.allocation.entity.PurchasedItem;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferClassifiers;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferHierarchyType;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferProgram;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferSubProgram;
import com.safeway.app.emju.allocation.helper.OfferConstants.PurchaseIndicator;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationRequest;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationType;
import com.safeway.app.emju.allocation.partner.service.PartnerAllocationService;
import com.safeway.app.emju.allocation.pricing.dao.ClubPriceDAO;
import com.safeway.app.emju.allocation.pricing.dao.OfferStorePriceDAO;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.RetailScanCache;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.cache.entity.RetailScanOffer;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.helper.PDPricing;
import com.safeway.app.emju.mylist.helper.PDPricingHelper;
import com.safeway.app.emju.mylist.helper.Utils;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.model.HeaderVO;
import com.safeway.app.emju.mylist.model.PreferredStore;

import play.Configuration;
import play.Play;
import play.libs.F.Promise;

public class MatchOfferServiceImp implements MatchOfferSevice {

	private PurchasedItemDAO purchasedItemDAO;
	private ClubPriceDAO clubPriceDao;
	private OfferStatusService offerStatusService;
	private RetailScanCache retailScanCache;
	private PDAllocationDAO pdAllocationDAO;
	private CCAllocationDAO ccAllocationDAO;
	private OfferDetailCache offerCache;
	private OfferStorePriceDAO pricingDAO;
	private PartnerAllocationService partnerAllocationService;
	
	Configuration config = Play.application().configuration();

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchOfferServiceImp.class);
	
	@Inject
	public MatchOfferServiceImp(PurchasedItemDAO purchasedItemDAO, ClubPriceDAO clubPriceDao,
			OfferStatusService offerStatusService, RetailScanCache retailScanCache, 
			PDAllocationDAO pdAllocationDAO, CCAllocationDAO ccAllocationDAO, OfferDetailCache offerCache, 
			OfferStorePriceDAO pricingDAO, PartnerAllocationService partnerAllocationService) {

		this.purchasedItemDAO = purchasedItemDAO;
		this.clubPriceDao = clubPriceDao;
		this.offerStatusService = offerStatusService;
		this.retailScanCache = retailScanCache;
		this.pdAllocationDAO = pdAllocationDAO;
		this.ccAllocationDAO = ccAllocationDAO;
		this.offerCache = offerCache;
		this.pricingDAO = pricingDAO;
		this.partnerAllocationService = partnerAllocationService;
	}

	@Override
	@SuppressWarnings("all")
	public Map<String, Map<String, List<AllocatedOffer>>> getRelatedOffers(final Map<Long, ShoppingListItem> itemsMap,
			final HeaderVO headerVO) throws ApplicationException {

		Map<String, Map<String, List<AllocatedOffer>>> result = null;

		if (ValidationHelper.isEmpty(itemsMap)) {
			return result;
		}

		Map<Long, RetailScanOffer> retailScanOfferList;
		List<Long> upcList = null;
		Map<Long, AllocatedOffer> ycsOffers = null;
		Map<Long, AllocatedOffer> pdOffers = null;
		Map<Long, AllocatedOffer> ccOffers = null;
		List<Long> listUPCsToMap = null;
		Map<Long, List<Long>> offerUPCMap = new HashMap<Long, List<Long>>();
		List<Long> relatedOfferIds = new ArrayList<Long>();
		Map<Long, OfferClipStatus> offerClipStatus;

		boolean versionValues[] = headerVO.getVersionValues();
		boolean filterAllItems = versionValues[1];

		Long householdId = Long.valueOf(headerVO.getSwyhouseholdid());

		Integer storeId = null;
		Integer preferStoreID = null;
		Integer paramStoreID = null;
		PreferredStore preferStore = headerVO.getPreferredStore();
		String timeZone = preferStore.getTimeZone();
		Integer regionId = preferStore.getRegionId();
		String zipCode = preferStore.getPostalCode();
		Long clientDBCurrDtInMS = DataHelper.getCurrTsInTzAsDBTzMs(timeZone);
		Date currentClientDt = new Date(clientDBCurrDtInMS);

		if (headerVO.getParamStoreId() != null) {
			paramStoreID = Integer.valueOf(headerVO.getParamStoreId());
		}

		if (preferStore != null) {
			preferStoreID = preferStore.getStoreId();
		}

		if (filterAllItems) {
			storeId = preferStoreID;
		} else {
			storeId = (paramStoreID == null) ? preferStoreID : paramStoreID;
		}

		upcList = new ArrayList<Long>();

		for (Map.Entry<Long, ShoppingListItem> entry : itemsMap.entrySet()) {
			upcList.add(entry.getKey());
		}

		try {
			
			Promise<List<Long>> partnerAllocationPromise = getPartnerAllocationOffers(headerVO);
			
			// get YCS offer related to upc list
			ycsOffers = getYCSOffers(upcList, timeZone, householdId, storeId);

			if (ycsOffers != null && ycsOffers.size() > 0) {
				for (Map.Entry<Long, AllocatedOffer> entry : ycsOffers.entrySet()) {
					listUPCsToMap = new ArrayList<Long>();
					listUPCsToMap.add(entry.getKey());
					offerUPCMap.put(entry.getKey(), listUPCsToMap);
				}
			}

			long upcId = 0;
			retailScanOfferList = retailScanCache
					.getRetailScanOfferDetailByScanCds(upcList.toArray(new Long[upcList.size()]));
			RetailScanOffer retailScanOffer = null;

			List<Long> redeemedOfferIdList = offerStatusService.findRedeemedOffersForRemoval(householdId);

			// Fill up PHPL item's title and desc. Get all related offers from
			// all
			// PHPL items added to list
			for (Map.Entry<Long, RetailScanOffer> entry : retailScanOfferList.entrySet()) {

				upcId = entry.getKey();
				ShoppingListItem upcItem = itemsMap.get(upcId);
				retailScanOffer = entry.getValue();
				upcItem.setItemTitle(retailScanOffer.getItemDesc());
				upcItem.setItemDesc(retailScanOffer.getPackageSizeDsc());

				if(ValidationHelper.isNonEmpty(retailScanOffer.getOfferIdList())) {
					for (Long offerId : retailScanOffer.getOfferIdList()) {
	
						LOGGER.debug("getRelatedOffers. retailScanOffer --> " + offerId);
						if (redeemedOfferIdList.contains(offerId)) {
							LOGGER.debug("getRelatedOffers. redeemedOffer --> " + offerId);
							continue;
						}
	
						relatedOfferIds.add(offerId);
					}
				}
			}

			offerClipStatus = offerStatusService.findOfferClipStatus(headerVO.getSwycustguid(), householdId,
					relatedOfferIds, null);

			String clipStatus = null;

			// Process and map all valid offers
			for (Map.Entry<Long, RetailScanOffer> entry : retailScanOfferList.entrySet()) {

				upcId = entry.getKey();
				retailScanOffer = entry.getValue();

				if(ValidationHelper.isNonEmpty(retailScanOffer.getOfferIdList())) {
					for (Long offerId : retailScanOffer.getOfferIdList()) {
	
						LOGGER.debug("getRelatedOffers. statusOffer checking --> " + offerId);
						OfferClipStatus status = offerClipStatus.get(offerId);
						clipStatus = status != null ? status.getClipStatus() : ClipStatus.UNCLIPPED;
	
						if (!ClipStatus.UNCLIPPED.equals(clipStatus)) {
							
							LOGGER.debug("getRelatedOffers. statusOffer clipped --> " + offerId);
							relatedOfferIds.remove(offerId);
							continue;
						}
	
						listUPCsToMap = offerUPCMap.get(offerId);
	
						if (listUPCsToMap == null) {
							listUPCsToMap = new ArrayList<Long>();
							listUPCsToMap.add(upcId);
							offerUPCMap.put(offerId, listUPCsToMap);
						} else {
							if (!listUPCsToMap.contains(upcId)) {
								listUPCsToMap.add(upcId);
							}
						}
					}
				}
			}

			// get PD offer related to upc list
			pdOffers = getPDOffers(householdId, regionId, relatedOfferIds, currentClientDt, storeId);
			// get CC offer related to upc list
			LOGGER.debug("MatchOfferServiceImp before retrieving partner allocation");
			List<Long> partnerAllocation = null;
			try {
				partnerAllocation = partnerAllocationPromise.get(750, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
	            LOGGER.error("Unable to retrieve Partner Allocation within 1 second", e);
	        }
			LOGGER.debug("MatchOfferServiceImp before retrieving cc allocation");
			ccOffers = getCCOffers(currentClientDt, zipCode, storeId, relatedOfferIds, partnerAllocation);
			LOGGER.debug("MatchOfferServiceImp after retrieving cc allocation");

			result = generateUPCOffersMap(itemsMap, result, ycsOffers, offerUPCMap, "YCS");
			result = generateUPCOffersMap(itemsMap, result, pdOffers, offerUPCMap, "PD");
			result = generateUPCOffersMap(itemsMap, result, ccOffers, offerUPCMap, "CC");
		} catch (ApplicationException e) {
			
			LOGGER.error("Exception on MatchOfferServiceImp.getRelatedOffers" + e);
			throw e;
		}
		return result;
	}

	private Map<Long, AllocatedOffer> getYCSOffers(List<Long> upcList, String timeZone,
			Long householdId, Integer storeId) throws ApplicationException {

		LOGGER.debug("CommonRepositoryBackupImpl --> getYCSOffers");

		Map<Long, PurchasedItem> purchasedItems = new HashMap<Long, PurchasedItem>();
		Map<Long, ClubPrice> ycsPrices = new HashMap<Long, ClubPrice>();

		Map<Long, AllocatedOffer> offerMap = new HashMap<Long, AllocatedOffer>();
		List<Long> retailScanCds = new ArrayList<Long>();

		purchasedItems
					.putAll(purchasedItemDAO.findItemsByHHIdAndScanCode(householdId, upcList));

		loadPurchaseItems(purchasedItems, offerMap, retailScanCds, storeId);

		if(ValidationHelper.isNonEmpty(retailScanCds)) {
			ycsPrices.putAll(clubPriceDao.findItemPrices(timeZone, storeId, retailScanCds));
	
			List<Long> removableItems = new ArrayList<Long>();
			loadPricingData(ycsPrices, offerMap, removableItems, timeZone);
			offerMap.keySet().removeAll(removableItems);
		}

		return offerMap;

	}

	public Map<Long, AllocatedOffer> getPDOffers(Long householdId, Integer regionId, List<Long> offerList,
			Date currentClientDt, Integer storeId) throws ApplicationException {

		Map<Long, AllocatedOffer> offerMap = new HashMap<Long, AllocatedOffer>();

		Map<Long, PDCustomOffer> customOffers = pdAllocationDAO.findPDCustomAllocation(householdId, regionId,
				offerList);

		List<Long> validPDOfferIds = new ArrayList<Long>();
		validPDOfferIds.addAll(customOffers.keySet());

		offerList.removeAll(validPDOfferIds);

		Map<Long, OfferDetail> offerDetailMap = null;
		offerDetailMap = offerCache.getOfferDetailsByIds(validPDOfferIds.toArray(new Long[validPDOfferIds.size()]));

		validPDOfferIds.clear();
		buildPDOffers(offerMap, offerDetailMap, validPDOfferIds, currentClientDt);

		Map<Long, OfferStorePrice> pricingMap = pricingDAO.findOfferPrices(storeId, validPDOfferIds);
		loadAllocationWithPricingData(offerMap, customOffers, null, pricingMap);

		return offerMap;

	}

	private Map<Long, AllocatedOffer> getCCOffers(Date currentDt, String zipCode, Integer storeId,
			List<Long> offerList, List<Long> partnerAllocation) throws ApplicationException {

		LOGGER.debug("MatchOfferServiceImp --> getCCOffers");
		Map<Long, AllocatedOffer> offerMap = new HashMap<Long, AllocatedOffer>();

		Map<Long, OfferDetail> offerDetailMap = new HashMap<Long, OfferDetail>();
		LOGGER.debug("MatchOfferServiceImp --> getCCOffers. Retrieving allocated Offers");
		
		String storeIdAsPostalCd = "";
		if(null != storeId) 
			storeIdAsPostalCd = Utils.convertStoreIdAsPostalCd(storeId);
		LOGGER.debug("storeIdAsPostalCd = " + storeIdAsPostalCd);
		Map<Long, CCAllocatedOffer> allocatedOffersPostalOnly = 
				ccAllocationDAO.findCCAllocation(zipCode, offerList);
		Map<Long, CCAllocatedOffer> allocatedOffersStoreOnly = new HashMap<Long, CCAllocatedOffer>();
		if(!"".equalsIgnoreCase(storeIdAsPostalCd))
			allocatedOffersStoreOnly = ccAllocationDAO.findCCAllocation(storeIdAsPostalCd, offerList);
		
		Map<Long, CCAllocatedOffer> ccOffers = new HashMap<Long, CCAllocatedOffer>();
		ccOffers.putAll(allocatedOffersPostalOnly);
		ccOffers.putAll(allocatedOffersStoreOnly);
		
		//Map<Long, CCAllocatedOffer> ccOffers = ccAllocationDAO.findCCAllocation(zipCode, offerList);

		List<Long> validOfferIds = new ArrayList<Long>();
		validOfferIds.addAll(ccOffers.keySet());
		LOGGER.debug("MatchOfferServiceImp --> getCCOffers. Valid allocated offers");
		
		LOGGER.debug("MatchOfferServiceImp --> getCCOffers. retrieving offer details");
		offerDetailMap.putAll(offerCache.getOfferDetailsByIds(validOfferIds.toArray(new Long[validOfferIds.size()])));
		
		LOGGER.debug("MatchOfferServiceImp --> getCCOffers. building CC Offers");
		buildCCOffers(offerMap, offerDetailMap, currentDt, partnerAllocation);

		return offerMap;

	}

	private Map<String, Map<String, List<AllocatedOffer>>> generateUPCOffersMap(Map<Long, ShoppingListItem> itemsMap,
			Map<String, Map<String, List<AllocatedOffer>>> result, Map<Long, AllocatedOffer> offerMap,
			Map<Long, List<Long>> offerUPCMap, String offerType) {

		LOGGER.debug("MatchOfferServiceImp --> generateUPCOffersMap");
		if (result == null)
			result = new HashMap<String, Map<String, List<AllocatedOffer>>>();

		Long offerId = null;
		List<Long> upcList = null;
		Map<String, List<AllocatedOffer>> typeOfferMap = null;
		List<AllocatedOffer> relatedOffers = null;
		AllocatedOffer offer = null;
		ShoppingListItem upcItem = null;
		if (offerMap != null && offerMap.size() > 0) {
			for (Map.Entry<Long, AllocatedOffer> entry : offerMap.entrySet()) {
				offerId = entry.getKey();
				upcList = offerUPCMap.get(offerId);
				offer = entry.getValue();
				for (Long upcId : upcList) {
					upcItem = itemsMap.get(upcId);
					// inspect offer start date before adding it
					if (upcItem.getClipTs().after(offer.getOfferStartDt())) {
						continue;
					}
					typeOfferMap = result.get(upcId.toString());
					if (typeOfferMap == null) {
						typeOfferMap = new HashMap<String, List<AllocatedOffer>>();
						relatedOffers = new ArrayList<AllocatedOffer>();
						relatedOffers.add(offer);
						typeOfferMap.put(offerType, relatedOffers);
						result.put(upcId.toString(), typeOfferMap);
					} else {
						relatedOffers = typeOfferMap.get(offerType);
						if (relatedOffers == null) {
							relatedOffers = new ArrayList<AllocatedOffer>();
							relatedOffers.add(offer);
							typeOfferMap.put(offerType, relatedOffers);
						} else {
							relatedOffers.add(offer);
						}
					}
				}
			}
		}

		return result;
	}

	private void loadPurchaseItems(final Map<Long, PurchasedItem> purchasedItems,
			final Map<Long, AllocatedOffer> offers, final List<Long> retailScanCds, final Integer storeId) {

		purchasedItems.values().forEach((final PurchasedItem item) -> {
			AllocatedOffer offer = new AllocatedOffer();
			Long retailScanCd = item.getRetailScanCd();
			offers.put(retailScanCd, offer);
			retailScanCds.add(retailScanCd);
			offer.setOfferId(retailScanCd);
			offer.setOfferPgm(OfferProgram.YCS);
			offer.getOfferDetail().setPurchaseCount(item.getPurchaseCnt());
		});
	}

	private void loadPricingData(final Map<Long, ClubPrice> prices, final Map<Long, AllocatedOffer> offers,
			final List<Long> removableItems, final String timeZone) {

		offers.entrySet().forEach((final Map.Entry<Long, AllocatedOffer> entry) -> {
			Long retailScanCd = entry.getKey();
			AllocatedOffer offer = entry.getValue();
			ClubPrice pricing = prices.get(retailScanCd);
			if (pricing == null) {
				removableItems.add(retailScanCd);
			} else {
				populateItemPricing(offer, pricing, timeZone);
			}
		});
	}

	private void populateItemPricing(final AllocatedOffer offer, final ClubPrice pricing, final String timeZone) {

		Map<String, String[]> hierarchies = new HashMap<String, String[]>();
		Integer categoryId = pricing.getCategoryId();
		if (categoryId != null) {
			hierarchies.put(OfferHierarchyType.CATEGORIES, new String[] { String.valueOf(categoryId) });
			offer.setHierarchies(hierarchies);
		}
		
		offer.getOfferDetail().setTitleDsc1(pricing.getItemDsc());
		offer.getOfferDetail().setProdDsc1(pricing.getPackageSizeDsc());

		Date actualStartDt = pricing.getStartDt();
		Date clientStartDt = DataHelper.getDateInClientTimezone(timeZone, actualStartDt);
		offer.setOfferStartDt(clientStartDt);
		offer.getOfferDetail().setOfferStartDt(DataHelper.getJSONDate(clientStartDt));

		Date actualEndDt = pricing.getEndDt();
		Date clientEndDt = DataHelper.getDateInClientTimezone(timeZone, actualEndDt);
		offer.getOfferDetail().setOfferEndDt(DataHelper.getJSONDate(clientEndDt));

		String priceMethod = pricing.getPriceMethod();
		String priceMethodSubType = pricing.getPriceMethodSubType();

		if (OfferClassifiers.PRICE_METHOD_TYPE_BOGO.equalsIgnoreCase(priceMethod)
				&& OfferClassifiers.PRICE_METHOD_SUB_TYPE_B1G1.equalsIgnoreCase(priceMethodSubType)) {
			offer.getOfferDetail().setPriceType(OfferClassifiers.PRICE_METHOD_TYPE_BOGO);
			offer.getOfferDetail().setPriceSubType(OfferClassifiers.PRICE_METHOD_SUB_TYPE_B1G1);
		} else if (OfferClassifiers.PRICE_METHOD_TYPE_MB.equalsIgnoreCase(priceMethod)
				&& OfferClassifiers.PRICE_METHOD_SUB_TYPE_MB2.equalsIgnoreCase(priceMethodSubType)) {
			offer.getOfferDetail().setPriceType(OfferClassifiers.PRICE_METHOD_TYPE_MB);
			offer.getOfferDetail().setPriceSubType(OfferClassifiers.PRICE_METHOD_SUB_TYPE_MB2);
		}

		double priceValue1 = pricing.getPromotionPrice();
		double priceValue2 = pricing.getRetailPrice();
		double savings = pricing.getSavings();
		int savingsPct = (int) ((savings / priceValue2) * 100);
		offer.getOfferDetail().setSavingsValue(String.valueOf(savings));
		offer.getOfferDetail().setPriceValue1(String.valueOf(priceValue1));
		offer.getOfferDetail().setPriceValue2(String.valueOf(priceValue2));
		offer.getOfferDetail().setSavingsPct(String.valueOf(savingsPct));

	}

	private void buildPDOffers(final Map<Long, AllocatedOffer> offerMap, final Map<Long, OfferDetail> offerDetails,
			final List<Long> validOfferIds, final Date currentDt) {

		Long offerId = null;
		String offerStatus = null;
		OfferDetail offerDetail = null;
		Date displayStartDate = null;
		Date displayEndDate = null;
		AllocatedOffer offer = null;

		for (Entry<Long, OfferDetail> entry : offerDetails.entrySet()) {

			offerId = entry.getKey();
			offerDetail = entry.getValue();
			offerStatus = offerDetail.getOfferStatusTypeId();
			displayStartDate = offerDetail.getDisplayEffectiveStartDt();
			displayEndDate = offerDetail.getDisplayEffectiveEndDt();
			
			if(!(offerStatus.equalsIgnoreCase(OfferStatus.ACTIVE) 
					|| offerStatus.equalsIgnoreCase(OfferStatus.DEACTIVATED))) {
				
				continue;
			}

			if (offerStatus.equals(OfferStatus.ACTIVE)) {

				if (displayEndDate.before(currentDt) || displayStartDate.after(currentDt)) {
					continue;
				}
			}

			if (offerStatus.equals(OfferStatus.DEACTIVATED)) {
				continue;
			}

			validOfferIds.add(offerId);
			offer = new AllocatedOffer();
			offerMap.put(offerId, offer);
			offer.setOfferInfo(offerDetail);
			offer.setOfferId(offerId);
			offer.setOfferPgm(offerDetail.getOfferProgramCd());
			offer.setClipStatus(ClipStatus.UNCLIPPED);
			offer.setOfferStartDt(displayStartDate);
			setOfferCategory(offer, offerDetail);

			offer.setOfferTs(new Timestamp(offerDetail.getLastUpdateTs().getTime()));
			offer.getOfferDetail().setOfferPrice(DataHelper.getDoubleAsString(offerDetail.getOfferPrice()));
			
		}
	}

	private void buildCCOffers(final Map<Long, AllocatedOffer> offerMap, final Map<Long, OfferDetail> offerDetails,
			final Date currentDt, final List<Long> partnerAllocation) {

		Long offerId = null;
		OfferDetail offerDetail = null;
		String offerStatus = null;
		Date displayStartDate = null;
		Date displayEndDate = null;
		AllocatedOffer offer = null;

		for (Entry<Long, OfferDetail> entry : offerDetails.entrySet()) {

			offerId = entry.getKey();
			offerDetail = entry.getValue();
			offerStatus = offerDetail.getOfferStatusTypeId();
			displayStartDate = offerDetail.getDisplayEffectiveStartDt();
			displayEndDate = offerDetail.getDisplayEffectiveEndDt();

			if (offerStatus.equals(OfferStatus.ACTIVE)) {

				if (displayEndDate.before(currentDt) || displayStartDate.after(currentDt)) {
					continue;
				}
				
			} else {
				
				continue;
			}
			
			if (OfferSubProgram.HTO_PROGRAM_TYPE_CODE.equals(offerDetail.getOfferSubProgram())
					&& (ValidationHelper.isEmpty(partnerAllocation) || !partnerAllocation.contains(offerId))) {
				
					continue;
			}

			offer = new AllocatedOffer();
			offer.setOfferId(offerId);
			
			offer.setOfferInfo(offerDetail);
			offer.setOfferStartDt(displayStartDate);
			offer.setClipStatus(ClipStatus.UNCLIPPED);
			offer.setPurchaseInd("B");
			setOfferCategory(offer, offerDetail);
			offer.setOfferPgm(offerDetail.getOfferProgramCd());

			offer.setOfferTs(new Timestamp(offerDetail.getLastUpdateTs().getTime()));
			offer.getOfferDetail().setOfferPrice(DataHelper.getDoubleAsString(offerDetail.getOfferPrice()));

			offer.getOfferDetail().setOfferStartDt(displayStartDate.toString());
			
			offer.getOfferDetail().setOfferEndDt(displayEndDate.toString());
			

			offer.setOfferProgramTypeCd(offerDetail.getOfferSubProgram());
			offer.setShoppingListCategoryId(offerDetail.getPrimaryCategoryId());
			offer.setPriceMethodCd(offerDetail.getPriceMethodCd());
			offer.setExtlOfferId(offerDetail.getExternalOfferId());
			offerMap.put(offerId, offer);

		}

	}

	private void setOfferCategory(AllocatedOffer offer, OfferDetail offerDetail) {

		Map<String, String[]> hierarchies = new HashMap<String, String[]>(2);
		List<String> hierarchyList = new ArrayList<String>(2);
		String[] hierarchyValues = null;
		if (ValidationHelper.isNonEmpty(offerDetail.getCategories())) {

			for (Entry<Integer, String> entry : offerDetail.getCategories().entrySet()) {

				hierarchyList.add(entry.getKey().toString());
			}
		}

		if (ValidationHelper.isNonEmpty(hierarchyList)) {
			hierarchyValues = hierarchyList.toArray(new String[hierarchyList.size()]);
		} else {
			hierarchyValues = new String[0];
		}
		hierarchies.put("categories", hierarchyValues);

		offer.setHierarchies(hierarchies);

	}

	@SuppressWarnings("unused")
	private void loadAllocationWithPricingData(final Map<Long, AllocatedOffer> offerMap,
			final Map<Long, PDCustomOffer> customOffers, final Map<Long, PDDefaultOffer> defaultOffers,
			final Map<Long, OfferStorePrice> pricingMap) {

		Long offerId = null;
		PDCustomOffer customOffer = null;
		PDDefaultOffer defaultOffer = null;
		Integer previouslyPurchasedInd = null;
		Double regRetailPriceAmt = null;

		OfferStorePrice pricing = null;
		for (AllocatedOffer offer : offerMap.values()) {
			offerId = offer.getOfferId();
			customOffer = customOffers.get(offerId);
			if (customOffer != null) {
				previouslyPurchasedInd = customOffer.getPreviouslyPurchased();
				offer.setPurchaseInd(PurchaseIndicator.getPurchaseIndicator(previouslyPurchasedInd));
				offer.getOfferDetail().setPreviouslyPurchaseInd(previouslyPurchasedInd);
				offer.getOfferDetail().setHhOfferRank(customOffer.getRank());
			}

			pricing = pricingMap.get(offerId);
			if (pricing != null) {
				
				regRetailPriceAmt = pricing.getRegularPrice();
				PDPricingHelper aPDPricingHelper = new PDPricingHelper();
				PDPricing aPDPricing = aPDPricingHelper.calculatePricing(Double.parseDouble(offer.getOfferDetail().getOfferPrice()),
						regRetailPriceAmt);

				
				offer.getOfferDetail().setPriceTitle2(aPDPricing.getPriceTitle());
				offer.getOfferDetail().setPriceTitle2Type(aPDPricing.getPriceTitleType());
				offer.getOfferDetail().setPriceValue2(aPDPricing.getPriceValue());
			}
		}
	}
	
	private Promise<List<Long>> getPartnerAllocationOffers(final  HeaderVO headerVO)
			throws OfferServiceException {

		Boolean isRiqEnabled = Boolean.parseBoolean(config.getString("riq.enabled"));
		
		PartnerAllocationRequest partnerAllocationRequest = new PartnerAllocationRequest();
		partnerAllocationRequest.setAppId(headerVO.getAppKey());
		LOGGER.debug("Swycoremaclubcard passed to partner allocation service is " + headerVO.getSwycoremaclubcard());
		partnerAllocationRequest.setCardNbr(Long.parseLong(headerVO.getSwycoremaclubcard()));
		LOGGER.debug("Swyhouseholdid passed to partner allocation service is " + headerVO.getSwyhouseholdid());
		partnerAllocationRequest.setHouseholdId(Long.parseLong(headerVO.getSwyhouseholdid()));
		LOGGER.debug("SessionToken passed to partner allocation service is " + headerVO.getSessionToken());
		partnerAllocationRequest.setHouseholdSessionId(headerVO.getSessionToken()); 
		Promise<List<Long>> partnerAllocationList = 
				partnerAllocationService.getAsyncAllocations(PartnerAllocationType.HOUSEHOLD_TARGETED_OFFER, 
						partnerAllocationRequest, isRiqEnabled);
        
        return partnerAllocationList;

	}

}
