package com.safeway.app.emju.mylist.dao;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.mylist.entity.WeeklyAdd;

@ImplementedBy(WeeklyAddDAOImp.class)
public interface WeeklyAddDAO {

	public List<WeeklyAdd> getWeeklyAddByOfferId(List<String> offerIds) throws ApplicationException;
}
