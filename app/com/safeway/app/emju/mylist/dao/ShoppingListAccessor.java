package com.safeway.app.emju.mylist.dao;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;
import com.safeway.app.emju.mylist.entity.ShoppingListItem;

@Accessor
public interface ShoppingListAccessor {
	
	@Query("SELECT * FROM emju.mylist_items " +
	        "WHERE retail_customer_id = :retailCustomerId AND household_id = :householdId "
	        + "AND shopping_list_nm = :shoppingListNm AND item_type_cd = :itemTypeCd "
	        + "AND store_id = :storeId")
	@QueryParameters(consistency = "LOCAL_ONE")
	Result<ShoppingListItem> getShoppingListItems(@Param("retailCustomerId")String retailCustomerId,
			@Param("householdId")Long householdId,
			@Param("shoppingListNm")String shoppingListNm,
			@Param("itemTypeCd") String itemTypeCd,
			@Param("storeId") Integer storeId);
	
	@Query("SELECT * FROM emju.mylist_items " +
	        "WHERE retail_customer_id = :retailCustomerId AND household_id = :householdId "
	        + "AND shopping_list_nm = :shoppingListNm AND item_type_cd = :itemTypeCd")
	@QueryParameters(consistency = "LOCAL_ONE")
	Result<ShoppingListItem> getShoppingListItems(@Param("retailCustomerId")String retailCustomerId,
			@Param("householdId")Long householdId,
			@Param("shoppingListNm")String shoppingListNm,
			@Param("itemTypeCd") String itemTypeCd);
	
	@Query("SELECT * FROM emju.mylist_items " +
	        "WHERE retail_customer_id = :retailCustomerId AND household_id = :householdId "
	        + "AND shopping_list_nm = :shoppingListNm")
	@QueryParameters(consistency = "LOCAL_ONE")
	Result<ShoppingListItem> getShoppingListItems(@Param("retailCustomerId")String retailCustomerId,
			@Param("householdId")Long householdId,
			@Param("shoppingListNm")String shoppingListNm);

}
