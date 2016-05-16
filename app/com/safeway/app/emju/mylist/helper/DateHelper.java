package com.safeway.app.emju.mylist.helper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import com.safeway.app.emju.mylist.constant.Constants;

public class DateHelper {

	private DateHelper() {
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DateHelper.class);

	public static Date getDateInClientTimezone(final String clientTimezone,
			final Date actualDt) {
		Date clientDt = null;
		if (actualDt != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(actualDt);
			Calendar clientCalendar = Calendar.getInstance(TimeZone
					.getTimeZone(clientTimezone));
			//clearTimeFields(clientCalendar);
			clientCalendar.set(Calendar.DAY_OF_MONTH,
					calendar.get(Calendar.DAY_OF_MONTH));
			clientCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
			clientCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			clientCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			clientCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
			clientCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
			clientCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
			clientCalendar.set(Calendar.MILLISECOND,calendar.get(Calendar.MILLISECOND));
			clientDt = new Date(clientCalendar.getTimeInMillis());
		}
		return clientDt;
	}

	public static Long getClientCurrDateInDBLocaleMS(final String clientTimezone) {
		Date clientDBDt = null;
		Calendar clientCalendar = Calendar.getInstance(TimeZone
				.getTimeZone(clientTimezone));

		clearTimeFields(clientCalendar);
		Calendar dbCalendar = Calendar.getInstance();

		clearTimeFields(dbCalendar);
		dbCalendar.set(Calendar.DAY_OF_MONTH,
				clientCalendar.get(Calendar.DAY_OF_MONTH));
		dbCalendar.set(Calendar.DATE, clientCalendar.get(Calendar.DATE));
		dbCalendar.set(Calendar.MONTH, clientCalendar.get(Calendar.MONTH));
		dbCalendar.set(Calendar.YEAR, clientCalendar.get(Calendar.YEAR));
		clientDBDt = new Date(dbCalendar.getTimeInMillis());
		return clientDBDt.getTime();
	}

	public static void clearTimeFields(final Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	public static String getISODate(Date dateTs, String clientTimezone) {
	    
	    DateFormat formatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
		return formatter.format(dateTs);
	}
	
	public static String getEffectiveISODate(Date dateTs, String clientTimezone) {
	    
	    DateFormat formatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
		Date inputDate = getEffectiveDateInClient(clientTimezone, dateTs);
		return formatter.format(inputDate);
	}
	
	public static Date getEffectiveDateInClient(final String clientTimezone, final Date actualDt) {
        Date clientDt = null;
        if (actualDt != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(actualDt);
            Calendar clientCalendar = Calendar.getInstance(TimeZone.getTimeZone(clientTimezone));
            clearTimeFields(clientCalendar);
            clientCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
            clientCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
            clientCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            clientCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            clientCalendar.set(Calendar.HOUR_OF_DAY, 11);
            clientDt = new Date(clientCalendar.getTimeInMillis());
        }
        return clientDt;
    }
	
	public static Timestamp setActualTs(String inDate, boolean isEndDate) throws Exception {
		
		Timestamp editedTime = null;
		if(!isEndDate){
			editedTime = new Timestamp(System.currentTimeMillis());
		}
		DateFormat inputFormatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
		Date itemEditedDate = null;
		try{
			if(null!= inDate){
				itemEditedDate = inputFormatter.parse(inDate);
				editedTime = new Timestamp(itemEditedDate.getTime());
			}
			
		}catch (ParseException e) {
			LOGGER.error("JSON input ISO Date in wrong format: "+inDate);
		}
		
		return editedTime;
	}
}
