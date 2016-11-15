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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.allocation.cliptracking.entity.MyListItemStatus;
import com.safeway.app.emju.allocation.cliptracking.model.OfferClipStatus;
import com.safeway.app.emju.allocation.cliptracking.service.OfferStatusService;
import com.safeway.app.emju.allocation.customerlookup.dao.CustomerLookupDAO;
import com.safeway.app.emju.allocation.dao.CCAllocationDAO;
import com.safeway.app.emju.allocation.dao.PDAllocationDAO;
import com.safeway.app.emju.allocation.dao.PurchasedItemDAO;
import com.safeway.app.emju.allocation.entity.CCAllocatedOffer;
import com.safeway.app.emju.allocation.entity.PDCustomOffer;
import com.safeway.app.emju.allocation.entity.PurchasedItem;
import com.safeway.app.emju.allocation.exception.FaultCode;
import com.safeway.app.emju.allocation.exception.OfferServiceException;
import com.safeway.app.emju.allocation.helper.OfferConstants.ClipStatus;
import com.safeway.app.emju.allocation.helper.OfferConstants.ItemType;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationRequest;
import com.safeway.app.emju.allocation.partner.model.PartnerAllocationType;
import com.safeway.app.emju.allocation.partner.service.PartnerAllocationService;
import com.safeway.app.emju.allocation.pricing.dao.ClubPriceDAO;
import com.safeway.app.emju.allocation.pricing.dao.OfferStorePriceDAO;
import com.safeway.app.emju.allocation.pricing.entity.ClubPrice;
import com.safeway.app.emju.allocation.pricing.entity.OfferStorePrice;
import com.safeway.app.emju.cache.CacheAccessException;
import com.safeway.app.emju.cache.OfferDetailCache;
import com.safeway.app.emju.cache.RedisCacheManager;
import com.safeway.app.emju.cache.RetailScanCache;
import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.dao.RetailScanOfferDAO;
import com.safeway.app.emju.cache.entity.RetailScanOffer;
import com.safeway.app.emju.dao.connector.CassandraConnector;
import com.safeway.app.emju.mylist.purchasehistory.model.OfferHierarchy;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffer;
import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffers;
import com.safeway.app.emju.mylist.purchasehistory.parser.PurchaseHistoryRequest;
import com.safeway.app.emju.util.ListItemReference;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.test.FakeApplication;
import play.test.Helpers;

/**
 *
 * @author sshar64
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseHistoryServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryServiceTest.class);

    private static FakeApplication fakeApp;

    @Mock
    private PurchasedItemDAO purchasedItemDAO;
    @Mock
    private PartnerAllocationService partnerAllocationService;
    @Mock
    private ClubPriceDAO clubPriceDAO;
    @Mock
    private OfferStatusService offerStatusService;
    @Mock
    private PDAllocationDAO pdAllocationDAO;
    @Mock
    private OfferStorePriceDAO offerStorePriceDAO;
    @Mock
    private CCAllocationDAO cCAllocationDAO;
    @Mock
    private RetailScanCache retailScanCache;
    @Mock
    private OfferDetailCache offerDetailCache;

    private static CassandraConnector cassandraConn;

    private static RedisCacheManager redisCache;

    private RetailScanOfferDAO retailScanOfferDAO;



    @Mock private StoreCache storeCache;

    @Mock private CustomerLookupDAO customerLookupDAO;

    private PDOfferMapper pdOfferMapper;
    private CCOfferMapper ccOfferMapper;
    private YCSOfferMapper ycsOfferMapper;

    private PurchaseHistoryService service;

    private PurchaseHistoryRequest request;

//    @BeforeClass
    public static void start() {

        Config additionalConfig = ConfigFactory.parseFile(new File("test/purchase-test.conf"));
        Configuration additionalConfigurations = new Configuration(additionalConfig);

        fakeApp = Helpers.fakeApplication(additionalConfigurations.asMap());
        Helpers.start(fakeApp);
        LOGGER.debug("Initializing FaultCodes with PLAY system before mocking internal Play objects"
            + FaultCode.INVALID_CUSTOMER_GUID);
    }

//    @AfterClass
    public static void stop() {
        Helpers.stop(fakeApp);
    }

//    @Before
    public void setup() throws CacheAccessException, OfferServiceException {

        pdOfferMapper = new PDOfferMapper();
        ccOfferMapper = new CCOfferMapper();
        ycsOfferMapper = new YCSOfferMapper();



        service = new  PurchaseHistoryService( purchasedItemDAO
            ,  clubPriceDAO,
            offerStatusService,  pdAllocationDAO,
            offerStorePriceDAO,  cCAllocationDAO,
            retailScanCache,  offerDetailCache,
            ycsOfferMapper,  pdOfferMapper,  ccOfferMapper,
            partnerAllocationService,retailScanOfferDAO) ;

        Long householdId = 100L;
        Integer storeId = 1;
        Integer regionId = 123;
        List<Long> retailScanIds = Arrays.asList(201L, 202L, 203L);

        request = new PurchaseHistoryRequest();

        request.setHouseholdId(householdId);
        request.setStoreId(storeId);
        request.setTimezone("America/Los_Angeles");
        request.setCustomerGUID("cutomer1");
        request.setRegionId(regionId);

        // All the offers
        List<Long> offerIds = Arrays.asList(603L, 604L, 605L, 606L, 607L, 608L, 609L, 610L, 611L);

        // PD offers
        List<Long> pdOfferIds = Arrays.asList(605L, 607L);

        // CC offers
        List<Long> ccOfferIds = Arrays.asList(606L, 608L, 611L);
        List<Long> catalinaOfferIds = Arrays.asList(610L);

        // redeemed offers
        List<Long> redeemedOfferIds = Arrays.asList(604L, 609L);

        // clipped offers
        List<Long> clippedOfferIds = Arrays.asList(607L, 603L);

        // mylist items
        List<Long> myListRetailScanIds = Arrays.asList(203L);


        Map<Long, PurchasedItem> purchasedItems = getPurchasedItems(householdId, retailScanIds);
        Map<Long, RetailScanOffer> retailScanOffers = getRetailScanOffers(retailScanIds);
        Map<Long, ClubPrice> clubPrices = getClubPrices(storeId, retailScanIds);
        Map<Long, PDCustomOffer> pdOffers = getPDOffers(householdId, pdOfferIds);
        Map<Long, OfferStorePrice> pdOfferStorePrices = getPDOfferStorePrices(storeId, pdOfferIds);

        Map<Long, CCAllocatedOffer> ccOffers = getCCOffers(ccOfferIds);
        Map<Long, OfferClipStatus> clippedOffers = getClippedOffers(clippedOfferIds);
        Map<String, MyListItemStatus> myListItemsStatus = getMyListItemsStatus(storeId, myListRetailScanIds);

        when(purchasedItemDAO.findItemsByHousehold(any(Long.class))).thenReturn(purchasedItems);
        when(retailScanCache.getRetailScanOfferDetailByScanCds(anyVararg())).thenReturn(retailScanOffers);

        //YCS offers
        when(clubPriceDAO.findItemPrices(any(String.class), any(Integer.class), anyListOf(Long.class))).thenReturn(clubPrices);
        when(offerStatusService.findRedeemedOffersForRemoval(any(Long.class))).thenReturn(redeemedOfferIds);

        // PD offers
        when(pdAllocationDAO.findPDCustomAllocation(any(Long.class),any(Integer.class))).thenReturn(pdOffers);
        when(offerStorePriceDAO.findOfferPrices(any(Integer.class), anyListOf(Long.class))).thenReturn(pdOfferStorePrices);

        //CC offers, flag is for riq HTO call
        when(cCAllocationDAO.findCCAllocation(any(String.class))).thenReturn(ccOffers);
        when(partnerAllocationService.getAllocations(
            any(PartnerAllocationType.class), any(PartnerAllocationRequest.class), true)).thenReturn(catalinaOfferIds);

        //clipped offers
        when(offerStatusService.findOfferClipStatus(
            any(String.class), any(Long.class),anyListOf(Long.class), any(String[].class))).thenReturn(clippedOffers);

        //my list item status
        when(offerStatusService.findMyListItems(
            any(String.class), any(Long.class), any(Integer.class), any(String[].class))).thenReturn(myListItemsStatus);

    }


    @Test
    public void testFindPurchaseItemsAndOffersForSuccess() throws Exception {

//        PurchasedItemOffers result = service.findPurchaseItemsAndOffers(request);
//
//        assertNotNull(result);

        //assertEquals("CurrentYearSavings should be 99.98", "99.98", result.getCurrentYearSavings());
        //assertEquals("LifetimeSavings should be 199.99", "199.99", result.getLifetimeSavings());
    }

    /*
    @Test
    public void testGetCustomerSavingsForNullResponse() throws Exception {

        when(dao.getHouseholdSavings(any(ClientRequest.class))).thenReturn(null);

        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setHouseholdId(100001L);
        CustomerSavings result = service.findCustomerSavings(clientRequest);
        assertNotNull(result);
        assertEquals("CurrentYearSavings should be 0.00", "0.00", result.getCurrentYearSavings());
        assertEquals("LifetimeSavings should be 0.00", "0.00", result.getLifetimeSavings());
    }

    @Test
    public void testGetCustomerSavingsForFailure() throws Exception {

        OfferServiceException serviceE =
            new OfferServiceException(
                FaultCodeBase.DB_SELECT_FAILURE, "Simulated Error", new DriverException("Error"));
        when(dao.getHouseholdSavings(any(ClientRequest.class))).thenThrow(serviceE);

        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setHouseholdId(100001L);

        try {
            service.findCustomerSavings(clientRequest);
            Assert.fail("Error Expected");
        }
        catch (OfferServiceException e) {
            LOGGER.error(e.getMessage(), e);
            assertEquals("FaultCode should match", FaultCodeBase.DB_SELECT_FAILURE, e.getFaultCode());
        }

    }
     */
    private PurchasedItemOffers preparePurchasedItemOffers() {

        PurchasedItemOffers purchasedItemOffers = new PurchasedItemOffers();

        List<PurchasedItemOffer> purchasedItemOfferList = new ArrayList<PurchasedItemOffer>();

        Map<Integer, OfferHierarchy> categoryMap = new HashMap<Integer, OfferHierarchy>(25);

        for(int i=0;i<10;i++) {

            PurchasedItemOffer purchasedItemOffer = new PurchasedItemOffer();

            purchasedItemOffer.setUpcId(new Long(i));
            purchasedItemOffer.setPurchaseCount(new Long(i+1));
            purchasedItemOffer.setTitleDsc1("Desc"+i);
            purchasedItemOffer.setProdDsc1("ProdDesc"+i);
            purchasedItemOffer.setCategoryId(new Long(i+1));
            purchasedItemOffer.setCategoryName("Category"+i);
            purchasedItemOffer.setLastPurchasedTs(new Date());
            purchasedItemOffer.setItemType("UPC");

            purchasedItemOfferList.add(purchasedItemOffer);

            // Setup Category Hierarchy

            OfferHierarchy category = new OfferHierarchy();

            category.setCode(purchasedItemOffer.getCategoryId().toString());
            category.setName(purchasedItemOffer.getCategoryName());
            category.setCount(1L);

            categoryMap.put(purchasedItemOffer.getCategoryId().intValue(), category);

        }

        // convert list to array and set in PurchasedItemOffers object
        purchasedItemOffers.setItems(
            purchasedItemOfferList.toArray(new PurchasedItemOffer[purchasedItemOfferList.size()]));

        Collection<OfferHierarchy> categories = categoryMap.values();
        // convert collection to array and set in PurchasedItemOffers object
        purchasedItemOffers.setCategories(
            categories.toArray(new OfferHierarchy[categories.size()]));

        return purchasedItemOffers;

    }

    private Map<Long, RetailScanOffer> getRetailScanOffers(final List<Long> retailScanIds) {

        Map<Long, RetailScanOffer> retailScanOffers = new HashMap<Long, RetailScanOffer>(3);

        for(Long retailScanId : retailScanIds) {

            RetailScanOffer item = new RetailScanOffer();
            item.setRetailScanCd(retailScanId);
            item.setCategoryId(retailScanId.intValue());
            item.setCategoryNm("category"+retailScanId);
            item.setItemDesc("ItemDesc"+retailScanId);

            //For example retailScanId : 201
            // the offerIds are 603, 604, 605
            Long offerId = retailScanId * 3 ;
            item.setOfferIdList(
                Arrays.asList(offerId, offerId+1, offerId+2));

            retailScanOffers.put(retailScanId, item);
        }

        return retailScanOffers;

    }

    private Map<Long, ClubPrice> getClubPrices(final Integer storeId, final List<Long> retailScanIds) {

        Map<Long, ClubPrice> clubPrices = new HashMap<Long, ClubPrice>(3);

        for(Long retailScanId : retailScanIds) {

            ClubPrice item = new ClubPrice();
            item.setRetailScanCd(retailScanId);
            item.setCategoryId(retailScanId.intValue());
            item.setCategoryNm("category"+retailScanId);
            item.setStoreId(storeId);

            clubPrices.put(retailScanId, item);
        }

        return clubPrices;

    }

    private Map<Long, PurchasedItem> getPurchasedItems(final Long householdId, final List<Long> retailScanIds) {

        Map<Long, PurchasedItem> purchasedItems = new HashMap<Long, PurchasedItem>(3);

        for(Long retailScanId: retailScanIds) {
            PurchasedItem item = new PurchasedItem();
            item.setHouseholdId(householdId);
            item.setRetailScanCd(retailScanId);
            item.setPurchaseCnt(1);
            item.setPurchaseDt(new Date());
            purchasedItems.put(retailScanId, item);
        }

        return purchasedItems;

    }

    private Map<String, MyListItemStatus> getMyListItemsStatus(final Integer storeId, final List<Long> myListRetailScanIds) {

        Map<String, MyListItemStatus> myListItems = new HashMap<String, MyListItemStatus>(1);

        for(Long myListItemId: myListRetailScanIds) {
            MyListItemStatus item = new MyListItemStatus();

            item.setItemId(myListItemId.toString());

            String itemRefId = new ListItemReference(ItemType.YCS, myListItemId.toString(), storeId).getItemRefId();

            myListItems.put(itemRefId, item);
        }

        return myListItems;
    }

    private Map<Long, OfferClipStatus> getClippedOffers(final List<Long> clippedOfferIds) {

        Map<Long, OfferClipStatus> clippedOffers = new HashMap<Long, OfferClipStatus>(2);

        for(Long clippedOfferId: clippedOfferIds) {
            OfferClipStatus item = new OfferClipStatus();
            item.setOfferId(clippedOfferId);
            item.setClipStatus(ClipStatus.ADDED_TO_CARD);
            clippedOffers.put(clippedOfferId, item);
        }

        return clippedOffers;
    }

    private Map<Long, CCAllocatedOffer> getCCOffers(final List<Long> ccOfferIds) {

        Map<Long, CCAllocatedOffer> ccOfferPrices = new HashMap<Long, CCAllocatedOffer>(4);

        for(Long ccOfferId: ccOfferIds) {
            CCAllocatedOffer item = new CCAllocatedOffer();
            item.setPostalCd("94566");
            item.setOfferId(ccOfferId);
            ccOfferPrices.put(ccOfferId, item);
        }

        return ccOfferPrices;
    }

    private Map<Long, OfferStorePrice> getPDOfferStorePrices(final Integer storeId, final List<Long> pdOfferIds) {

        Map<Long, OfferStorePrice> pdOfferPrices = new HashMap<Long, OfferStorePrice>(2);

        for(Long pdOfferId: pdOfferIds) {
            OfferStorePrice item = new OfferStorePrice();
            item.setStoreId(storeId);
            item.setRegularPrice(100d);
            item.setOfferId(pdOfferId);
            pdOfferPrices.put(pdOfferId, item);
        }

        return pdOfferPrices;
    }

    private Map<Long, PDCustomOffer> getPDOffers(final Long householdId, final List<Long> pdOfferIds) {

        Map<Long, PDCustomOffer> pdOffers = new HashMap<Long, PDCustomOffer>(2);

        for(Long pdOfferId: pdOfferIds) {

            PDCustomOffer item = new PDCustomOffer();
            item.setHouseholdId(householdId);
            item.setOfferId(pdOfferId);
            item.setRank(1L);
            item.setRegionId(1);
            item.setPreviouslyPurchased(1);

            pdOffers.put(pdOfferId, item);
        }

        return pdOffers;
    }
}

