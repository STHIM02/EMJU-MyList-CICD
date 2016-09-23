package com.safeway.app.emju.mylist.dao;

import java.util.List;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.datastax.driver.mapping.Result;
import com.google.inject.Inject;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.safeway.app.emju.dao.connector.CassandraConnector;
import com.safeway.app.emju.dao.exception.ConnectionException;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;
import com.safeway.app.emju.mylist.model.ShoppingListVO;
import com.safeway.app.emju.util.GenericConstants;

public class ShoppingListDAOImp implements ShoppingListDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListDAOImp.class);

    private CassandraConnector connector;
    
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

            PreparedStatement select = connector.getSession().prepare(sql);
            select.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
            BoundStatement boundStatement = new BoundStatement(select);
            
			LOGGER.debug("Accessing database records with params custGUID " + custGUID + ", " +
					"householdId " + householdId + ", shoppingListNm " + shoppingListNm);
            ResultSet rs = connector.getSession().execute(boundStatement.bind(custGUID, householdId, shoppingListNm));
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
                batch.add(mapper.saveQuery(item));
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

}
