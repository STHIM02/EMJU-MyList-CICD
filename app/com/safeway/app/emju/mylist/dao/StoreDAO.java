package com.safeway.app.emju.mylist.dao;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.mylist.model.PreferredStore;

@ImplementedBy(StoreDAOImp.class)
public interface StoreDAO {

	public PreferredStore findStoreInfo(final Integer storeId, final Long householdID, final String registeredZipCode)
	        throws Exception;
}
