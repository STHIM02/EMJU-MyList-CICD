package com.safeway.app.emju.mylist.dao;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Mapper.Option;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.safeway.app.emju.dao.connector.CassandraConnector;
import com.safeway.app.emju.dao.exception.ConnectionException;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.util.GenericConstants;

@Singleton
public class ShoppingListDAOImp implements ShoppingListDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListDAOImp.class);

    private CassandraConnector connector;
    
    private static final int QUERY_THRESHOLD_IN_MS = 100;
    
    private static java.util.Date _lastLoggedTime = null;
    private static final int MINIMAL_LOGGING_THRESHOLD_IN_MS = 300000; //five minutes
    
    
    @Inject
    public ShoppingListDAOImp(CassandraConnector connector) {
    	
    	this.connector = connector;
    }
	
	@Override
	public List<ShoppingListItem> getShoppingListItems(ShoppingListVO shoppingListVO) 
			throws ApplicationException {
		
		LOGGER.debug("At ShoppingListDAOImp.getShoppingListItems");
		List<ShoppingListItem> shoppingList = null;
		
		try{
			
			String custGUID = shoppingListVO.getHeaderVO().getSwycustguid();
			Long householdId = Long.valueOf(shoppingListVO.getHeaderVO().getSwyhouseholdid());
			String shoppingListNm = GenericConstants.DEFAULT_SHOPPING_LIST;
			
			String sql = "SELECT retail_customer_id, household_id, shopping_list_nm, item_type_cd, store_id, " +
    		"item_id, category_id, checked_ind, clip_id, clip_ts, club_card_nbr, delete_ts, " +
    		"item_desc, item_end_dt, item_image, item_price_value, item_promo_price, item_quantity, " +
    		"item_ref_id, item_start_dt, item_title, last_upd_ts, last_upd_usr_id, shopping_list_desc, " +
    		"source_application_cd, ttl(clip_id) FROM emju.mylist_items " + 
    		"WHERE retail_customer_id = ? AND household_id = ? AND shopping_list_nm = ?";
			LOGGER.debug("Query to execute: " + sql);

			long startTime = System.currentTimeMillis();
            BoundStatement boundStatement = connector.getStatement(sql, connector.getSession());
            
			LOGGER.debug("Accessing database records with params custGUID " + custGUID + ", " +
					"householdId " + householdId + ", shoppingListNm " + shoppingListNm);
            ResultSet rs = connector.getSession().execute(boundStatement.bind(custGUID, householdId, shoppingListNm));
            long elapsed = System.currentTimeMillis() - startTime;
            logTracing(startTime, elapsed, rs, sql);
            
            Result<ShoppingListItem> result = connector.getMappingManager().mapper(ShoppingListItem.class).map(rs);
			
			shoppingList = result.all();
			LOGGER.debug("number of records retrieved by acessor: " + shoppingList.size());
			
		} catch (ConnectionException | DriverException e) {
            LOGGER.error("ConnectionException " + e.getMessage(), e);
            throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
        } catch(Exception e) {
        	LOGGER.error("The error is " + e.getMessage(), e);
        	throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
        }
		
		return shoppingList;
	}

	@Override
	public void insertShoppingListItems(List<ShoppingListItem> shoppingListItems) throws ApplicationException {
		
		try {
            MappingManager manager = connector.getMappingManager();
            Mapper<ShoppingListItem> mapper = manager.mapper(ShoppingListItem.class);
            BatchStatement batch = new BatchStatement(BatchStatement.Type.UNLOGGED);
            batch.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
            
            shoppingListItems.forEach((final ShoppingListItem item) -> {
            	 batch.add(mapper.saveQuery(item, Option.ttl(item.getTtl())));
            });
            
            connector.getSession().execute(batch);
        }
        catch (ConnectionException e) {
            LOGGER.error(
                FaultCodeBase.CONNECTION_UNAVAILABLE, "Failed to save item", e, false);
            throw new ApplicationException(FaultCodeBase.DB_INSERT_FAILURE, e.getMessage(), e);
        }
        catch (RuntimeException e) {
            LOGGER.error(
                FaultCodeBase.DB_INSERT_FAILURE, "Failed to save item", e, false);
            throw new ApplicationException(FaultCodeBase.DB_INSERT_FAILURE, e.getMessage(), e);
        }
		
	}
	
    private void logTracing(long startTime, long  elapsed, ResultSet result, String str) {
        boolean isQueryThresholdExceeded = (elapsed > QUERY_THRESHOLD_IN_MS);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(_lastLoggedTime);
        calendar.add(Calendar.MILLISECOND, MINIMAL_LOGGING_THRESHOLD_IN_MS);
        boolean isMinimalLoggingThresholdExceeded = (startTime > calendar.getTimeInMillis());
        
        if (isQueryThresholdExceeded || isMinimalLoggingThresholdExceeded) {
            ExecutionInfo executionInfo = result.getExecutionInfo();
 
            QueryTrace queryTrace = executionInfo.getQueryTrace();
 
            //you can use the traceid value to lookup info in the cluster (see example output below class:
            // select * from system_traces.sessions where session_id = {trace id value};
            // select * from system_traces.events where session_id = {trace id value};
            
            StringBuilder sb = new StringBuilder();
            sb.append(str).append("                            \n");
            sb.append("Trace ID: " + queryTrace.getTraceId()).append("                       \n");
 
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            
            
            for (QueryTrace.Event event : queryTrace.getEvents()){
            	sb.append(MessageFormat.format(
                        "time: %s; host: %s; elapsed (usec): %d; description: %s",
                        sdf.format(event.getTimestamp()),
                        event.getSource(),
                        event.getSourceElapsedMicros(),
                        event.getDescription()
                ));
            	sb.append("                       \n");
            }
 
            LOGGER.error(sb.toString());
            
            //keep track of the fact that we just logged a trace so that we don't immediately do it again on the next request
            _lastLoggedTime.setTime(startTime);
        }
    }

}
