package com.safeway.app.emju.mylist.controller;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import com.safeway.app.emju.allocation.exception.ErrorDescriptor;
import com.safeway.app.emju.allocation.exception.FaultCode;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.dao.StoreDAO;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.mylist.service.ShoppingListService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import play.Application;
import play.Configuration;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingListControllerTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(ShoppingListControllerTest.class);

    private static Application fakeApp;
    
    @Mock
    private Http.Request request;
    @Mock
    private ShoppingListService service;
    @Mock
    private StoreDAO storeDAO;
    
    private ShoppingListController controller;
    
    
//    @BeforeClass
    public static void start() {
        LOGGER.debug("Application initializing...");
        Config config = ConfigFactory.parseFile(new File("test/mylist-test.conf"));
        config = ConfigFactory.load(config);
        Configuration configuration = new Configuration(config);
        fakeApp = new GuiceApplicationBuilder().configure(configuration).build();
        Helpers.start(fakeApp);
        LOGGER.debug("Initializing FaultCodes with PLAY system before mocking internal Play objects "
            + FaultCode.INVALID_CUSTOMER_GUID);
        LOGGER.debug("Application started");
    }

//    @AfterClass
    public static void stop() {
        LOGGER.debug("Application stopping...");
        Helpers.stop(fakeApp);
        LOGGER.debug("Application stopped");
    }
    
//    @Before
    public void setup() {
    	
    	Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 2L;
        play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
        Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
        Http.Context.current.set(context);
    }
    
    @Test
    public void testInvalidCustGuid() {
    	
//    	controller = new ShoppingListController(service, storeDAO);
//    	List<ShoppingListVO> shoppingLists = new ArrayList<ShoppingListVO>();
//    	try {
//    		when(service.getShoppingList(Mockito.any(ShoppingListVO.class))).thenReturn(shoppingLists);
//    	} catch (ApplicationException e) {
//    		
//    	}
//    	
//    	Map<String, ErrorDescriptor[]> errorMap = getCustomErrorMap(FaultCode.EMLS_INVALID_CUST_ID.getCode(),
//                FaultCode.EMLS_INVALID_CUST_ID.getDescription());
//    	
//    	Promise<Result> promise = controller.getShoppingList(null, null, null);
//    	Result result = promise.get(60000);
//    	assertNotNull("Result should not be null", result);
//    	assertEquals("Http Status should be 401", 401, result.status());
//    	assertEquals("JSON response should be same", Json.toJson(errorMap).toString(), Helpers.contentAsString(result));
    }
    
    private Map<String, ErrorDescriptor[]> getCustomErrorMap(final String errorCode,final String description){
		Map<String, ErrorDescriptor[]> errorMap = new HashMap<String, ErrorDescriptor[]>(1);
		ErrorDescriptor error = new ErrorDescriptor(
				errorCode,
				description);
		errorMap.put("errors", new ErrorDescriptor[] { error });
		return errorMap;
	}
}
