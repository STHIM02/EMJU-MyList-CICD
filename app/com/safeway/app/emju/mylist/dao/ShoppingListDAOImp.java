package com.safeway.app.emju.mylist.dao;

import java.util.List;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.datastax.driver.mapping.Result;
import com.google.inject.Inject;
import com.datastax.driver.core.exceptions.DriverException;
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
	public List<ShoppingListItem> getShoppingListItems(ShoppingListVO shoppingListVO, String itemTypeCd, Integer selectedStoreId)
			throws ApplicationException {
		
		LOGGER.debug("At ShoppingListDAOImp.getShoppingListItems");
		List<ShoppingListItem> shoppingList = null;
		
		try{
			
			String custGUID = shoppingListVO.getHeaderVO().getSwycustguid();
			Long householdId = Long.valueOf(shoppingListVO.getHeaderVO().getSwyhouseholdid());
			String shoppingListNm = GenericConstants.DEFAULT_SHOPPING_LIST;
			
			LOGGER.debug("Creating MappingManager");
			MappingManager manager = connector.getMappingManager();
			LOGGER.debug("Creating ShoppingListAccessor");
			ShoppingListAccessor accessor = manager.createAccessor(ShoppingListAccessor.class);
			Result<ShoppingListItem> result = null;
			
			LOGGER.debug("Accessing database records with params custGUID " + custGUID + ", " +
			"householdId " + householdId + ", shoppingListNm " + shoppingListNm);
			if(selectedStoreId != null) {
				result = accessor.getShoppingListItems(custGUID, householdId, shoppingListNm, itemTypeCd, selectedStoreId);
			} else {
				result = accessor.getShoppingListItems(custGUID, householdId, shoppingListNm, itemTypeCd);
			}
			
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
	public List<ShoppingListItem> getShoppingListItems(ShoppingListVO shoppingListVO) 
			throws ApplicationException {
		
		LOGGER.debug("At ShoppingListDAOImp.getShoppingListItems");
		List<ShoppingListItem> shoppingList = null;
		
		try{
			
			String custGUID = shoppingListVO.getHeaderVO().getSwycustguid();
			Long householdId = Long.valueOf(shoppingListVO.getHeaderVO().getSwyhouseholdid());
			String shoppingListNm = GenericConstants.DEFAULT_SHOPPING_LIST;
			
			LOGGER.debug("Creating MappingManager");
			MappingManager manager = connector.getMappingManager();
			LOGGER.debug("Creating ShoppingListAccessor");
			ShoppingListAccessor accessor = manager.createAccessor(ShoppingListAccessor.class);
			Result<ShoppingListItem> result = null;
			
			LOGGER.debug("Accessing database records with params custGUID " + custGUID + ", " +
			"householdId " + householdId + ", shoppingListNm " + shoppingListNm);
			result = accessor.getShoppingListItems(custGUID, householdId, shoppingListNm);
			
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

}
