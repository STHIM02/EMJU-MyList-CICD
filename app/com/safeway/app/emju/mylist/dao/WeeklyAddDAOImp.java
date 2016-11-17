package com.safeway.app.emju.mylist.dao;

import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.mapping.Result;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.safeway.app.emju.dao.connector.CassandraConnector;
import com.safeway.app.emju.dao.exception.ConnectionException;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.entity.WeeklyAdd;

@Singleton
public class WeeklyAddDAOImp implements WeeklyAddDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyAddDAOImp.class);
	
	 private CassandraConnector connector;
	 
	 @Inject
	 public WeeklyAddDAOImp(CassandraConnector connector) {
		 
		 this.connector = connector;
	 }

	@Override
	public List<WeeklyAdd> getWeeklyAddByOfferId(List<String> offerIds) throws ApplicationException {

		LOGGER.debug("At WeeklyAddDAOImp.getWeeklyAddByOfferId");
		List<WeeklyAdd> weeklyAdds = null;
		
		try{
			
			String sql = "SELECT * FROM emju.offer_weekly_ad WHERE offer_id IN ?";
			LOGGER.debug("Query to execute: " + sql);

            BoundStatement boundStatement = connector.getStatement(sql, connector.getSession());

            ResultSet rs = connector.getSession().execute(boundStatement.bind(offerIds));
            Result<WeeklyAdd> result = connector.getMappingManager().mapper(WeeklyAdd.class).map(rs);
			
            weeklyAdds = result.all();
			LOGGER.debug("number of records retrieved by acessor: " + weeklyAdds.size());
			
		} catch (ConnectionException | DriverException e) {
            LOGGER.error("ConnectionException " + e.getMessage(), e);
            throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
        } catch(Exception e) {
        	LOGGER.error("The error is " + e.getMessage(), e);
        	throw new ApplicationException(FaultCodeBase.EMLS_UNABLE_TO_PROCESS, e.getMessage(), e);
        }
		
		return weeklyAdds;
	}

}
