/* **************************************************************************
 * Copyright 2015 Albertsons Safeway.
 *
 * This document/file contains proprietary data that is the property of
 * Albertsons Safeway.  Information contained herein may not be used,
 * copied or disclosed in whole or in part except as permitted by a
 * written agreement signed by an officer of Albertsons Safeway.
 *
 * Unauthorized use, copying or other reproduction of this document/file
 * is prohibited by law.
 *
 ***************************************************************************/

package com.safeway.app.emju.mylist.purchasehistory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.safeway.app.emju.allocation.cliptracking.entity.MyListItemStatus;
import com.safeway.app.emju.allocation.cliptracking.model.OfferClipStatus;
import com.safeway.app.emju.allocation.cliptracking.service.OfferStatusService;
import com.safeway.app.emju.allocation.dao.CCAllocationDAO;
import com.safeway.app.emju.allocation.dao.PDAllocationDAO;
import com.safeway.app.emju.allocation.dao.PurchasedItemDAO;
import com.safeway.app.emju.allocation.entity.CCAllocatedOffer;
import com.safeway.app.emju.allocation.entity.PDCustomOffer;
import com.safeway.app.emju.allocation.entity.PurchasedItem;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ItemType;
import com.safeway.app.emju.allocation.helper.OfferConstants.OfferProgram;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationRequest;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationType;
import com.safeway.app.emju.allocation.partner.service.PartnerAllocationService;
import com.safeway.app.emju.allocation.pricing.dao.ClubPriceDAO;
import com.safeway.app.emju.allocation.pricing.dao.OfferStorePriceDAO;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.allocation.requestidentification.model.ClientRequest;
import com.safeway.app.emju.cache.CacheAccessException;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.RetailScanCache;
import com.safeway.app.emju.cache.dao.RetailScanOfferDAO;
import com.safeway.app.emju.cache.entity.OfferDetail;
import com.safeway.app.emju.cache.entity.RetailScanOffer;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.helper.DataHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.model.AllocatedOffer;
import com.safeway.app.emju.mylist.purchasehistory.comparators.AlphaComparator;
import com.safeway.app.emju.mylist.purchasehistory.comparators.CategoryComparator;
import com.safeway.app.emju.mylist.purchasehistory.comparators.FrequencyComparator;
import com.safeway.app.emju.mylist.purchasehistory.comparators.OfferCountComparator;
import com.safeway.app.emju.mylist.purchasehistory.comparators.PurchaseRecencyComparator;
import com.safeway.app.emju.mylist.purchasehistory.model.OfferHierarchy;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffer;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;
import com.safeway.app.emju.util.ListItemReference;

import akka.dispatch.MessageDispatcher;
import play.libs.Akka;
import play.libs.F.Promise;

/**
 *
 * @author sshar64
 *
 */
public class PurchaseHistoryService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryService.class);


    private PurchasedItemDAO purchasedItemDAO;
    private PartnerAllocationService partnerAllocationService;
    private ClubPriceDAO clubPriceDAO;
    private OfferStatusService offerStatusService;
    private PDAllocationDAO pdAllocationDAO;
    private OfferStorePriceDAO offerStorePriceDAO;
    private CCAllocationDAO cCAllocationDAO;
    private RetailScanCache retailScanCache;
    private OfferDetailCache offerDetailCache;

    private RetailScanOfferDAO retailScanOfferDAO;

    private PDOfferMapper pdOfferMapper;
    private CCOfferMapper ccOfferMapper;
    private YCSOfferMapper ycsOfferMapper;

    private static final String SORT_TYPE_FREQUENCY = "purchase";
    private static final String SORT_TYPE_OFFERS = "offers";
    private static final String SORT_TYPE_ALPHA = "az";
    private static final String SORT_TYPE_ALPHA_REVERSE = "za";
    private static final String SORT_TYPE_RECENCY = "purchaseRecency";

    String[] OFFER_TYPES = { OfferProgram.PD, OfferProgram.SC, OfferProgram.MF };

    @Inject
    public PurchaseHistoryService(final PurchasedItemDAO purchasedItemDAO
        , final ClubPriceDAO clubPriceDAO,
        final OfferStatusService offerStatusService, final PDAllocationDAO pdAllocationDAO,
        final OfferStorePriceDAO offerStorePriceDAO, final CCAllocationDAO cCAllocationDAO,
        final RetailScanCache retailScanCache, final OfferDetailCache offerDetailCache,
        final YCSOfferMapper ycsOfferMapper, final PDOfferMapper pdOfferMapper, final CCOfferMapper ccOfferMapper,
        final PartnerAllocationService partnerAllocationService, final RetailScanOfferDAO retailScanOfferDAO) {

        this.purchasedItemDAO = purchasedItemDAO;
        this.clubPriceDAO = clubPriceDAO;
        this.offerStatusService = offerStatusService;
        this.pdAllocationDAO = pdAllocationDAO;
        this.offerStorePriceDAO = offerStorePriceDAO;
        this.cCAllocationDAO = cCAllocationDAO;
        this.retailScanCache = retailScanCache;
        this.offerDetailCache = offerDetailCache;
        this.ycsOfferMapper = ycsOfferMapper;
        this.pdOfferMapper = pdOfferMapper;
        this.ccOfferMapper = ccOfferMapper;
        this.partnerAllocationService = partnerAllocationService;
        this.retailScanOfferDAO = retailScanOfferDAO;
    }

    /**
     *
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    public PurchasedItemOffers findPurchaseItemsAndOffers(final PurchaseHistoryRequest purchaseHistoryRequest) throws OfferServiceException {

        PurchasedItemOffers purchasedItemOffers = new PurchasedItemOffers();
        LOGGER.debug("@@PurchaseHistoryServiceImpl.findPurchaseItemsAndOffers@@ ");

        try {

            // Given an hhid, get all the PurchaseItems
            Map<Long, PurchasedItem> purchasedItemMap = purchasedItemDAO
                .findItemsByHousehold(purchaseHistoryRequest.getHouseholdId());
            
            
            
            //if nothing found then just return, dont proceed
            if (null ==purchasedItemMap || purchasedItemMap.isEmpty()) {
            	PurchasedItemOffer[] purchasedItemOfferArray = new PurchasedItemOffer[0];
            	OfferHierarchy[] offerHierarchyArray = new OfferHierarchy[0];
            	
                purchasedItemOffers.setItems(purchasedItemOfferArray);
                purchasedItemOffers.setCategories(offerHierarchyArray);
                return purchasedItemOffers;
            	
            }

            // get all the retailScanCds as a list
            List<Long> retailScanCds = Lists.newArrayList(purchasedItemMap.keySet());


            // given a list of UPCId's find all details including the list of
            // Related Offers.This will come from Cache.


            Map<Long, RetailScanOffer> retailScanOffersMap = retailScanCache.getRetailScanOfferDetailByScanCds(retailScanCds.toArray(new Long[retailScanCds.size()]));

            // Get all MyList status for both UPC and YCS items.
            Map<String, MyListItemStatus> ycsUPCListItemStatus = getYCSAndUPCMyListItemStatus(purchaseHistoryRequest);

            Map<Long, List<AllocatedOffer>> relatedOffers = populateRelatedOffers(purchaseHistoryRequest,
                retailScanOffersMap, ycsUPCListItemStatus);

            processPurchasedItemOffers(purchaseHistoryRequest, purchasedItemOffers, purchasedItemMap,
                retailScanOffersMap, relatedOffers, ycsUPCListItemStatus);


        } catch (OfferServiceException oe) {
        	LOGGER.error("findPurchaseItemsAndOffers(): Error in processing purchased items and offers.", oe);
            throw oe;
        } catch (CacheAccessException ce) {
        	LOGGER.error("findPurchaseItemsAndOffers(): Error in processing purchased items and offers.", ce);
            throw new OfferServiceException(ce);
        } catch (Exception e) {
        	LOGGER.error("findPurchaseItemsAndOffers(): Error in processing purchased items and offers.", e);
            throw new OfferServiceException(FaultCodeBase.UNEXPECTED_SYSTEM_FAILURE);
        }

        return purchasedItemOffers;

    }

    /**
     *
     *
     * @param purchaseHistoryRequest
     * @param purchasedItemOffers
     * @param purchasedItemMap
     * @param retailScanOffersMap
     * @param relatedOffersMap
     * @param ycsUPCListItemStatus
     */
    private void processPurchasedItemOffers(final PurchaseHistoryRequest purchaseHistoryRequest,
        final PurchasedItemOffers purchasedItemOffers, final Map<Long, PurchasedItem> purchasedItemMap,
        final Map<Long, RetailScanOffer> retailScanOffersMap, final Map<Long, List<AllocatedOffer>> relatedOffersMap,
        final Map<String, MyListItemStatus> ycsUPCListItemStatus) {

        LOGGER.debug("@@PurchaseHistoryServiceImpl.processPurchasedItemOffers@@ ");

        List<PurchasedItemOffer> purchasedItemOfferList = new ArrayList<PurchasedItemOffer>();

        Map<Integer, OfferHierarchy> categoryMap = new HashMap<Integer, OfferHierarchy>(25);
        
        Set keys = ycsUPCListItemStatus.keySet();
        
        LOGGER.debug("print all ycsUPCListItemStatus ");
        
        for (Iterator i = keys.iterator(); i.hasNext(); ){
        	String key = (String) i.next();
        	LOGGER.debug(key);;
        }
        		   

        for (Long retailScanId : retailScanOffersMap.keySet()) {

            PurchasedItem item = purchasedItemMap.get(retailScanId);
            RetailScanOffer retailScanOffer = retailScanOffersMap.get(retailScanId);
            List<AllocatedOffer> relatedOffers = relatedOffersMap.get(retailScanId);

            PurchasedItemOffer purchasedItemOffer = new PurchasedItemOffer();

            purchasedItemOffer.setUpcId(item.getRetailScanCd());
            purchasedItemOffer.setPurchaseCount(new Long(item.getPurchaseCnt()));
            purchasedItemOffer.setTitleDsc1(retailScanOffer.getItemDesc());
            purchasedItemOffer.setProdDsc1(retailScanOffer.getPackageSizeDsc());
            purchasedItemOffer.setCategoryId(new Long(retailScanOffer.getCategoryId()));
            purchasedItemOffer.setCategoryName(retailScanOffer.getCategoryNm());
            purchasedItemOffer.setLastPurchasedTs(item.getPurchaseDt());
            purchasedItemOffer.setItemType("UPC");
            purchasedItemOffer.setRelatedOffers(relatedOffers != null
                ? relatedOffers.toArray(new AllocatedOffer[relatedOffers.size()]):new AllocatedOffer[0]);


            // Set UPC clip status
            String itemRefId = new ListItemReference(ItemType.UPC, String.valueOf(retailScanId),
                purchaseHistoryRequest.getStoreId()).getItemRefId();
            
            /*LOGGER.debug("processPurchasedItemOffers >>>  ");
            LOGGER.debug("retailScanId >>>  "+ retailScanId);
            LOGGER.debug("purchaseHistoryRequest.getStoreId() >>>  "+ purchaseHistoryRequest.getStoreId());
            LOGGER.debug("itemRefId >>>  "+ itemRefId);
            
            
            */

            if (ycsUPCListItemStatus.containsKey(itemRefId)) {
                MyListItemStatus upcListItemStatus = ycsUPCListItemStatus.get(itemRefId);
                String upcClipStatus = getClipStatus(upcListItemStatus);
                LOGGER.debug("upcClipStatus >>>  "+ upcClipStatus);
                
                purchasedItemOffer.setClipStatus(upcClipStatus);
            } else {
            	purchasedItemOffer.setClipStatus(ClipStatus.UNCLIPPED);
            }

            purchasedItemOfferList.add(purchasedItemOffer);

            // Setup Category Hierarchy

            OfferHierarchy category = categoryMap.get(retailScanOffer.getCategoryId());

            if (category == null) {
                category = new OfferHierarchy();
                category.setCode(retailScanOffer.getCategoryId().toString());
                category.setName(retailScanOffer.getCategoryNm());
                category.setCount(1L);
                categoryMap.put(retailScanOffer.getCategoryId(), category);
            } else {
                category.setCount(category.getCount() + 1);
            }

        }


        // sort
        sortPurchasedItemOffers(purchaseHistoryRequest, purchasedItemOfferList);

        // convert list to array and set in PurchasedItemOffers object
        purchasedItemOffers.setItems(purchasedItemOfferList.toArray(new PurchasedItemOffer[purchasedItemOfferList.size()]));

        Collection<OfferHierarchy> categories = categoryMap.values();
        // convert collection to array and set in PurchasedItemOffers object
        purchasedItemOffers.setCategories(categories.toArray(new OfferHierarchy[categories.size()]));

    }

    /**
     *
     * Populate the related offers (YCS, PD, CC (MF+SC+ Catalina) for each
     * retail scan id.
     *
     * @param purchaseHistoryRequest
     * @param retailScanOffersMap
     * @return
     * @throws OfferServiceException
     * @throws CacheAccessException
     */
    private Map<Long, List<AllocatedOffer>> populateRelatedOffers(final ClientRequest purchaseHistoryRequest,
        final Map<Long, RetailScanOffer> retailScanOffersMap, final Map<String, MyListItemStatus> ycsUPCListItemStatus)
            throws OfferServiceException, CacheAccessException {

        LOGGER.debug("@@PurchaseHistoryServiceImpl.populateRelatedOffers@@ ");

        // get all the retailScanCds as a list
        List<Long> retailScanCds = Lists.newArrayList(retailScanOffersMap.keySet());

        // get all YCS items
        MessageDispatcher dispatcher = Akka.system().dispatchers().defaultGlobalDispatcher();
		Map<Long, ClubPrice> ycsOfferItems = clubPriceDAO.findItemPrices(dispatcher, purchaseHistoryRequest.getTimezone(),
	            purchaseHistoryRequest.getStoreId(), retailScanCds);
        // Get all redeemed Offers
        List<Long> redeemedOfferList = getRedeemedOffers(purchaseHistoryRequest);

        // Get all PD offers
        Map<Long, PDCustomOffer> pdCustomOfferMap = getPDOffers(purchaseHistoryRequest);
        LOGGER.debug(">>>  pdCustomOfferMap" + pdCustomOfferMap.size());

        // Get all PD Offer Store Prices
        Map<Long, OfferStorePrice> offerStorePriceMap = getPDOfferStorePrices(purchaseHistoryRequest, pdCustomOfferMap);

        // Get all CC offers
        Map<Long, CCAllocatedOffer> ccAllocatedOfferMap = getCCOffers(purchaseHistoryRequest);

        // Get all catalina allocation offers : TODO 11/13/2015
        //List<Long> catalinaAllocationOffers = getCatalinaAllocationOffers(purchaseHistoryRequest);
        
        List<Long> partnerAllocationList = new ArrayList<>();
        
        try {
        	Promise<List<Long>> partnerAllocationPromise = getPartnerAllocationOffers(purchaseHistoryRequest);
        	partnerAllocationList = partnerAllocationPromise.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
        	LOGGER.warn("Unable to retrieve Partner Allocation within 1 second", e);
        	partnerAllocationList = new ArrayList<>();
        }
 


        // Get all the clip/list status of the items from the list of Offers.
        // for YCS, check CLip status.offerStatusService.getMyList
        // offerInfoMap.put(offerId, new Object[] { status, itemId });
        Set<Long> offerIds = new HashSet<Long>();

        for (RetailScanOffer retailScanOffer : retailScanOffersMap.values()) {
        	
        	List<Long> offerIdList = retailScanOffer.getOfferIdList();
        	if (null != offerIdList) {
        	
        		offerIds.addAll(offerIdList);
        	}
        }

        // Get each offer clip status
        Map<Long, OfferClipStatus> offerClipListStatusMap = offerStatusService.findOfferClipStatus(
            purchaseHistoryRequest.getCustomerGUID(), purchaseHistoryRequest.getHouseholdId(),
            Lists.newArrayList(offerIds), OfferProgram.PD, OfferProgram.MF, OfferProgram.SC);
        

		

        return processRelatedOffers(purchaseHistoryRequest, retailScanOffersMap.values(), ycsOfferItems,
            redeemedOfferList, offerClipListStatusMap, pdCustomOfferMap, offerStorePriceMap, ccAllocatedOfferMap,
            partnerAllocationList, ycsUPCListItemStatus);

    }
    
	private Promise<List<Long>> getPartnerAllocationOffers(ClientRequest purchaseHistoryRequest)
			throws OfferServiceException {

		  
		PartnerAllocationRequest partnerAllocationRequest = new PartnerAllocationRequest();
		partnerAllocationRequest.setAppId(purchaseHistoryRequest.getAppId());
		LOGGER.debug("ClubCard passed to partner allocation service is " + purchaseHistoryRequest.getClubCard());
		partnerAllocationRequest.setCardNbr(purchaseHistoryRequest.getClubCard());
		LOGGER.debug("Swyhouseholdid passed to partner allocation service is " + purchaseHistoryRequest.getHouseholdId());
		partnerAllocationRequest.setHouseholdId(purchaseHistoryRequest.getHouseholdId());
		LOGGER.debug("SessionToken passed to partner allocation service is " + purchaseHistoryRequest.getSessionToken());
		partnerAllocationRequest.setHouseholdSessionId(purchaseHistoryRequest.getSessionToken()); 
		Promise<List<Long>> partnerAllocationList = 
				partnerAllocationService.getAsyncAllocations(PartnerAllocationType.HOUSEHOLD_TARGETED_OFFER, 
						partnerAllocationRequest);
		
        
        return partnerAllocationList;

	}

    /**
     * Main logic for processing the related offers for each purchased item
     * (retail_scan_id).
     *
     * @param purchaseHistoryRequest
     * @param retailScanOffers
     * @param ycsOfferItems
     * @param redeemedOfferList
     * @param offerClipListStatusMap
     * @param pdCustomOfferMap
     * @param offerStorePriceMap
     * @param ccAllocatedOfferMap
     * @param catalinaAllocationOffers
     * @param ycsUPCListItemStatus
     * @return
     * @throws CacheAccessException
     * @throws OfferServiceException
     */
    private Map<Long, List<AllocatedOffer>> processRelatedOffers(final ClientRequest purchaseHistoryRequest,
        final Collection<RetailScanOffer> retailScanOffers, final Map<Long, ClubPrice> ycsOfferItems,
        final List<Long> redeemedOfferList, final Map<Long, OfferClipStatus> offerClipListStatusMap,
        final Map<Long, PDCustomOffer> pdCustomOfferMap, final Map<Long, OfferStorePrice> offerStorePriceMap,
        final Map<Long, CCAllocatedOffer> ccAllocatedOfferMap, final List<Long> catalinaAllocationOffers,
        final Map<String, MyListItemStatus> ycsUPCListItemStatus) throws CacheAccessException, OfferServiceException {

        LOGGER.debug("@@PurchaseHistoryServiceImpl.processRelatedOffers@@ ");

        // Get the each OfferIdList from retailScanOffer and create an aggregated array and send to the cache for
        // fetching to avoid the performance delays.
        // If the offerId is in redeemedOfferIdList, discard
        Map<Long, OfferDetail> offerDetailsMap = getAllOfferDetailsMap(retailScanOffers, redeemedOfferList);

        // Map of Retail Scan Id as key and list of offers (YCS, CC, PD) as
        // value
        Map<Long, List<AllocatedOffer>> retailScanRelatedOffersMap = new HashMap<Long, List<AllocatedOffer>>();

        for (RetailScanOffer retailScanOffer : retailScanOffers) {

            List<AllocatedOffer> relatedOffers = new ArrayList<AllocatedOffer>();

            // process YCS Offer
            ClubPrice ycsOfferItem = ycsOfferItems.get(retailScanOffer.getRetailScanCd());

            if (ycsOfferItem != null) {
                relatedOffers.add(ycsOfferMapper.mapOffer(ycsOfferItem, purchaseHistoryRequest.getTimezone(),
                    ycsUPCListItemStatus));
            }

            List<Long> offerIdList = retailScanOffer.getOfferIdList();
            
            

            // for each related offer id filter and convert into AllocatedOffer
            // 1. check if the offerId in offerDetailCache, otherwise discard
            // 2. Determine the delete and clip my list item status for the
            // offerId
            // 3. Based on the program type, check if the offerId exist in their
            // respective map
            // 4. convert the offerDetail into AllocatedOffer based on each
            // program cd.
            
            if (null != offerIdList) {

            	String offerRefId = null;
	            for (Long offerId : offerIdList) {
	
	                // check if offer is in OfferCache
	                OfferDetail offerDetail = offerDetailsMap.get(offerId);
	
	                // if the offer is not in caches
	                if (offerDetail == null) {
	                    continue;
	                }
	
	                offerRefId = offerDetail.getOfferProgramCd()+"~"+offerId;
	                // Get OfferClipStatus for the offer
	                OfferClipStatus offerClipStatus = offerClipListStatusMap.get(offerRefId);
	
	
	                AllocatedOffer allocatedOffer = null;
	
	                String offerPgm = offerDetail.getOfferProgramCd();
	
	                boolean ccOffer = (OfferProgram.MF.equals(offerPgm) || OfferProgram.SC.equals(offerPgm));
	                boolean pdOffer = (OfferProgram.PD.equals(offerPgm));
	                
	               
	                
	                LOGGER.debug(">>> pdOffer >>  " + pdOffer);
	                LOGGER.debug(">>> pdCustomOfferMap.containsKey(offerId >>  " + pdCustomOfferMap.containsKey(offerId));
	
	                // if cc offer and the offerId is in ccAllocatedOfferMap
	                if (ccOffer && ccAllocatedOfferMap.containsKey(offerId)) {
	
	                    allocatedOffer = ccOfferMapper.mapOffer(offerDetail, catalinaAllocationOffers, offerClipStatus,
	                        purchaseHistoryRequest.getTimezone());
	
	                } else if (pdOffer && pdCustomOfferMap.containsKey(offerId)) {
	                	LOGGER.debug(">>> process PD offer with offer id = >>" + offerId);
	                    allocatedOffer = pdOfferMapper.mapOffer(offerDetail, offerStorePriceMap.get(offerId),
	                        offerClipStatus, purchaseHistoryRequest.getTimezone());
	                }
	
	                if (allocatedOffer != null) {
	                    relatedOffers.add(allocatedOffer);
	                }
	
	            } // End of related offers
            }
            
            
            

            retailScanRelatedOffersMap.put(retailScanOffer.getRetailScanCd(), relatedOffers);

        } // end of retail scan

        return retailScanRelatedOffersMap;

    }

	private Map<Long, OfferDetail> getAllOfferDetailsMap(final Collection<RetailScanOffer> retailScanOffers,
			final List<Long> redeemedOfferList) throws CacheAccessException {
		List<Long> combinedOfferIdList = new ArrayList<Long>();
        
        for (RetailScanOffer retailScanOffer : retailScanOffers) {
            List<Long> offerIdList = retailScanOffer.getOfferIdList();
            if (null != offerIdList) {
	            for (Long offerId : offerIdList) {
	                // check if offer is in redeemed list. ignore if this is true.
	            	
	                if (redeemedOfferList.contains(offerId)) {
	                    continue;
	                }
	            	combinedOfferIdList.add(offerId);
	            }
            }
        }
        
        Long[] combinedOfferIdArray = combinedOfferIdList.toArray(new Long[combinedOfferIdList.size()]);
        
        Map<Long, OfferDetail> offerDetailsMap = offerDetailCache.getOfferDetailsByIds(combinedOfferIdArray);
		return offerDetailsMap;
	}

    /**
     * Get list of shopping list item status for given customer GUID,
     * householdId and storeId.
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    private Map<String, MyListItemStatus> getYCSAndUPCMyListItemStatus(final ClientRequest purchaseHistoryRequest)
        throws OfferServiceException {

        return offerStatusService.findMyListItems(purchaseHistoryRequest.getCustomerGUID(),
            purchaseHistoryRequest.getHouseholdId(), purchaseHistoryRequest.getStoreId(), OfferProgram.YCS,
            ItemType.UPC);

    }

    /**
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    private List<Long> getRedeemedOffers(final ClientRequest purchaseHistoryRequest) throws OfferServiceException {
        return offerStatusService.findRedeemedOffersForRemoval(purchaseHistoryRequest.getHouseholdId());
    }

    /**
     * Get all PD offers for a given householdId and regionId.
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    private Map<Long, PDCustomOffer> getPDOffers(final ClientRequest purchaseHistoryRequest)
        throws OfferServiceException {
    	LOGGER.debug(" PH > getPDOffers > HHID =  " + purchaseHistoryRequest.getHouseholdId());
    	LOGGER.debug(" PH > getPDOffers Region id> " + purchaseHistoryRequest.getRegionId());
        return pdAllocationDAO.findPDCustomAllocation(purchaseHistoryRequest.getHouseholdId(),
            purchaseHistoryRequest.getRegionId());
    }

    /**
     *
     * Get <code>OfferStorePrice</code> for all the <code>PDCustomOffer</code>.
     *
     * @param purchaseHistoryRequest
     * @param pdOffers
     * @return
     * @throws OfferServiceException
     */
    private Map<Long, OfferStorePrice> getPDOfferStorePrices(final ClientRequest purchaseHistoryRequest,
        final Map<Long, PDCustomOffer> pdOffers) throws OfferServiceException {

        // get list of all PD offer id's
        List<Long> pdofferIdLists = Lists.newArrayList(pdOffers.keySet());
        // get price of PD offers
        return offerStorePriceDAO.findOfferPrices(purchaseHistoryRequest.getStoreId(), pdofferIdLists);
    }

    /**
     * Get all CC offers (SC + MF) for a given postal code.
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    private Map<Long, CCAllocatedOffer> getCCOffers(final ClientRequest purchaseHistoryRequest)
        throws OfferServiceException {
        // get all CC offers (SC + MF)
        return cCAllocationDAO.findCCAllocation(purchaseHistoryRequest.getPostalCode());
    }

    /**
     * Get list of catalina allocation offers.
     *
     * @param purchaseHistoryRequest
     * @return
     * @throws OfferServiceException
     */
    private List<Long> getCatalinaAllocationOffers(final ClientRequest purchaseHistoryRequest)
        throws OfferServiceException {

        PartnerAllocationRequest partnerAllocationRequest = new PartnerAllocationRequest();
        partnerAllocationRequest.setAppId(purchaseHistoryRequest.getAppId());
        partnerAllocationRequest.setCardNbr(purchaseHistoryRequest.getClubCard());
        partnerAllocationRequest.setHouseholdId(purchaseHistoryRequest.getHouseholdId());
        partnerAllocationRequest.setHouseholdSessionId(purchaseHistoryRequest.getSessionToken()); // confirm
        // ?
        Promise<List<Long>> partnerAllocationList =
            partnerAllocationService.getAsyncAllocations(
                PartnerAllocationType.HOUSEHOLD_TARGETED_OFFER, partnerAllocationRequest);

        List<Long> partnerOfferIds = partnerAllocationList.get(1, TimeUnit.SECONDS);

        return partnerOfferIds;

    }

    private boolean isActiveOffer(final String timezone, final OfferDetail offerDetail, final OfferClipStatus offerClipStatus) {

        // if the offer is inactive( offer clip Date is in between offer start
        // and end date and the offer is not clipped,
        Date clientCurrentDt = DataHelper.getDateInClientTimezone(timezone, new Date());
        Date displayEffectiveStartDt = DataHelper.getDateInClientTimezone(timezone,
            offerDetail.getDisplayEffectiveStartDt());
        Date displayEffectiveEndtDt = DataHelper.getDateInClientTimezone(timezone,
            offerDetail.getDisplayEffectiveEndDt());
        Date offerEffectiveStartDt = DataHelper.getDateInClientTimezone(timezone,
            offerDetail.getOfferEffectiveStartDt());
        Date offerEffectiveEndtDt = DataHelper.getDateInClientTimezone(timezone, offerDetail.getOfferEffectiveEndDt());

        if ((displayEffectiveStartDt.before(clientCurrentDt) && displayEffectiveEndtDt.after(clientCurrentDt))
            || (offerEffectiveStartDt.before(clientCurrentDt) && offerEffectiveEndtDt.after(clientCurrentDt))) {
            return true;
        }

        if (null != offerClipStatus.getCardClipTs()) {
            // If the offer is not clipped, what is the value of "CardClipTs"
            Date offerClipDt = DataHelper.getDateInClientTimezone(timezone, new Date(offerClipStatus.getCardClipTs()));
            return (offerClipDt.before(displayEffectiveEndtDt) || offerClipDt.before(offerEffectiveEndtDt));
        }

        return false;
    }

    /**
     * Get the clip status for my list item.
     *
     * @param item
     * @return
     */
    private String getClipStatus(final MyListItemStatus item) {
        if (item != null) {
            if (item.getDeleteTs() != null) {
                return ClipStatus.UNCLIPPED;
            } else {
                return ClipStatus.ADDED_TO_LIST;
            }
        }
        return null;
    }


    /**
     * Sort the <code>List<PurchasedItemOffer></code> list bassed on sort type.
     *
     * @param purchaseHistoryRequest
     * @param items
     */
    private void sortPurchasedItemOffers(final PurchaseHistoryRequest purchaseHistoryRequest, final List<PurchasedItemOffer> items) {

        LOGGER.debug("@@PurchaseHistoryServiceImpl.sortPurchasedItemOffers@@ ");
        // reorder the items based on the sorting wanted by UI
        String sortType = purchaseHistoryRequest.getSortType();

        if (SORT_TYPE_FREQUENCY.equalsIgnoreCase(sortType)) {
            // sort by purchase frequency last
            sortByOfferCount(items);
            sortByCategory(items);
            sortByAlpha(items);
            sortByPurchaseRecency(items);
            sortByPurchaseFrequency(items);

        } else if (SORT_TYPE_OFFERS.equalsIgnoreCase(sortType)) {
            // sort by offer count
            sortByCategory(items);
            sortByPurchaseFrequency(items);
            sortByPurchaseRecency(items);
            sortByAlpha(items);
            sortByOfferCount(items);

        } else if (SORT_TYPE_ALPHA.equalsIgnoreCase(sortType)) {
            // sort by ascending order
            sortByOfferCount(items);
            sortByCategory(items);
            sortByPurchaseFrequency(items);
            sortByPurchaseRecency(items);
            sortByAlpha(items);

        } else if (SORT_TYPE_ALPHA_REVERSE.equalsIgnoreCase(sortType)) {
            // sort by descending order
            sortByOfferCount(items);
            sortByCategory(items);
            sortByPurchaseFrequency(items);
            sortByPurchaseRecency(items);
            sortByAlpha(items);
            Collections.reverse(items);

        } else if (SORT_TYPE_RECENCY.equalsIgnoreCase(sortType)) {
            // sort by purchase recency last
            sortByOfferCount(items);
            sortByCategory(items);
            sortByAlpha(items);
            sortByPurchaseFrequency(items);
            sortByPurchaseRecency(items);

        } else {
            // sort by category last
            sortByOfferCount(items);
            sortByAlpha(items);
            sortByPurchaseFrequency(items);
            sortByPurchaseRecency(items);
            sortByCategory(items);
        }

    }

    private void sortByPurchaseFrequency(final List<PurchasedItemOffer> items) {
        Collections.sort(items, new FrequencyComparator());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPurchaseRank((long) i);
        }
    }

    private void sortByPurchaseRecency(final List<PurchasedItemOffer> items) {
        Collections.sort(items, new PurchaseRecencyComparator());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPurchaseRecencyRank((long) i);
        }
    }

    private void sortByOfferCount(final List<PurchasedItemOffer> items) {
        Collections.sort(items, new OfferCountComparator());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOfferRank((long) i);
        }
    }

    private void sortByCategory(final List<PurchasedItemOffer> items) {
        Collections.sort(items, new CategoryComparator());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setCategoryRank((long) i);
        }
    }

    private void sortByAlpha(final List<PurchasedItemOffer> items) {
        Collections.sort(items, new AlphaComparator());
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setAlphaRank((long) i);
        }
    }

}
