package com.safeway.app.emju.mylist.cache;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.inject.Inject;
import com.safeway.app.emju.cache.CacheAccessException;
import com.safeway.emju.redis.RedisCacheManager;
import com.safeway.app.emju.cache.dao.StoreDBException;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.exception.FaultCodeBase;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mylist.dao.WeeklyAddDAO;
import com.safeway.app.emju.mylist.entity.WeeklyAdd;
import com.safeway.app.emju.mylist.model.WeeklyAddVO;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class WeeklyAddCacheImp implements WeeklyAddCache {

	private final static Logger LOGGER = LoggerFactory.getLogger(WeeklyAddCacheImp.class);
	private final static ObjectReader JSON_MAPPER = new ObjectMapper().reader(WeeklyAddVO.class);

	private RedisCacheManager redisManager;
	private final WeeklyAddDAO weeklyAddDAO;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Inject
	private WeeklyAddCacheImp(final RedisCacheManager redisCacheManager, final WeeklyAddDAO weeklyAddDAO) {

		this.redisManager = redisCacheManager;
		this.weeklyAddDAO = weeklyAddDAO;
	}

	@Override
	public Map<String, WeeklyAddVO> getWeeklyAddByOfferId(List<String> offerIds) throws CacheAccessException {

		Map<String, WeeklyAddVO> result = new HashMap<String, WeeklyAddVO>();

		LOGGER.info("@WeeklyAddCacheImp.getWeeklyAddByOfferId");
		try {

			LOGGER.debug("Got redis connection to retrieve WS details");
			List<String> redisKeys = offerIds.stream().map(id -> {
				return WEEKLYADD_CACHE_KEY_PREFIX + id;
			}).collect(Collectors.toList());

			List<String> jsonValues = new ArrayList<String>();
			if (!redisKeys.isEmpty()) {
				String[] strKeys = redisKeys.toArray(new String[redisKeys.size()]);
				jsonValues = redisManager.mget(strKeys);
			}

			if (!jsonValues.isEmpty()) {
				jsonValues.forEach((final String jsonValue) -> {
					if (jsonValue != null) {
						WeeklyAddVO weeklyAdd = mapJsonToObject(jsonValue);
						if (weeklyAdd != null) {
							result.put(weeklyAdd.getOfferId(), weeklyAdd);
						}
					}
				});

				LOGGER.info("OfferId Keys got from Redis Cache : " + result.keySet());
			}

			List<String> cacheMissedOffers = offerIds.stream().filter(offerId -> {
				return !result.containsKey(offerId);
			}).collect(Collectors.toList());

			List<WeeklyAdd> weeklyAddList = weeklyAddDAO.getWeeklyAddByOfferId(cacheMissedOffers);

			for (WeeklyAdd weeklyAdd : weeklyAddList) {

				String jsonValue = weeklyAdd.getResponse();
				WeeklyAddVO weeklyAddVO = mapJsonToObject(jsonValue);

				String sKey = WEEKLYADD_CACHE_KEY_PREFIX + weeklyAdd.getOfferId();

				if(weeklyAddVO != null) {
					String endDate = weeklyAddVO.getEndDate();
					Date expDate = null;
					Date initDate = new Date();
					if (ValidationHelper.isNonEmpty(endDate)) {
						expDate = dateFormat.parse(endDate);
					} else {
						expDate = new Date(new Date().getTime() + 24 * 60 * 60 * 1000);
					}
					
					Duration duration = Duration.between(initDate.toInstant(), expDate.toInstant());
					redisManager.setString(sKey, jsonValue, duration);
				}
				result.put(weeklyAdd.getOfferId(), weeklyAddVO);
			}

		} catch (JedisConnectionException jcx) {
			LOGGER.error(new StringBuilder(FaultCodeBase.CACHE_UNAVAILABLE.getCode()).append(" - ")
					.append(FaultCodeBase.CACHE_UNAVAILABLE.getDescription()).append(" : ").append(jcx.getMessage())
					.toString(), jcx);
			refresh(offerIds);
		} catch(ApplicationException ae) {			
			LOGGER.error(ae.toString(), ae);
			
			try {
				
				LOGGER.debug("Retrieving WS details from weeklyAddDAO");
				List<WeeklyAdd> weeklyAddList = weeklyAddDAO.getWeeklyAddByOfferId(offerIds);

				for (WeeklyAdd weeklyAdd : weeklyAddList) {
					
					String jsonValue = weeklyAdd.getResponse();
					WeeklyAddVO weeklyAddVO = mapJsonToObject(jsonValue);
					result.put(weeklyAdd.getOfferId(), weeklyAddVO);
					
				}
				LOGGER.debug("Retrieved WS details from weeklyAddDAO");
			} catch (ApplicationException e) {
				LOGGER.error(e.toString(), e);
			}
		} catch (Exception ex) {
			LOGGER.error(new StringBuilder(FaultCodeBase.CACHE_READ_FAILURE.getCode()).append(" - ")
					.append(FaultCodeBase.CACHE_READ_FAILURE.getDescription()).append(" : ").append(ex.getMessage())
					.toString(), ex);

			refresh(offerIds);
		}

		return result;
	}

	private WeeklyAddVO mapJsonToObject(final String weeklyAddJson) {

		WeeklyAddVO weeklyAdd = null;
		try {
			weeklyAdd = JSON_MAPPER.readValue(weeklyAddJson);
		} catch (JsonParseException jpex) {
			LOGGER.error(new StringBuffer("Store_Json parsing error :").append(weeklyAddJson).toString(), jpex);
		} catch (JsonMappingException jmex) {
			LOGGER.error(new StringBuffer("Store_Json parsing error :").append(weeklyAddJson).toString(), jmex);
		} catch (IOException ioe) {
			LOGGER.error(new StringBuffer("Store_Json io error :").append(weeklyAddJson).toString(), ioe);
		}
		return weeklyAdd;
	}

	public void refresh(final List<String> offerIds) throws CacheAccessException {

		try {

			List<String> redisKeys = offerIds.stream().map(id -> {
				return WEEKLYADD_CACHE_KEY_PREFIX + id;
			}).collect(Collectors.toList());

			if (!redisKeys.isEmpty()) {
				String[] strKeys = redisKeys.toArray(new String[redisKeys.size()]);
				LOGGER.info("Removing from Redis Cache : " + redisKeys.size() + " offerIds");
				Long status = redisManager.del(strKeys);
				if (status > 0) {
					LOGGER.info("Successfully removed OfferIds from Redis Cache");
				}
			}

			List<WeeklyAdd> weeklyAddList = weeklyAddDAO.getWeeklyAddByOfferId(offerIds);

			if (!weeklyAddList.isEmpty()) {
				weeklyAddList.forEach((final WeeklyAdd weeklyAdd) -> {
					String weeklyAddJson = weeklyAdd.getResponse();
					String jedisKey = WEEKLYADD_CACHE_KEY_PREFIX + weeklyAdd.getOfferId();
					redisManager.set(jedisKey, weeklyAddJson);
				});
			}

		} catch (JedisConnectionException jcx) {

			LOGGER.error(new StringBuilder(FaultCodeBase.CACHE_UNAVAILABLE.getCode()).append(" - ")
					.append(FaultCodeBase.CACHE_UNAVAILABLE.getDescription()).append(" : ").append(jcx.getMessage())
					.toString(), jcx);

		} catch (StoreDBException dbe) {
			LOGGER.error(new StringBuilder(FaultCodeBase.CACHE_REFRESH_FAILURE.getCode()).append(" - ")
					.append(FaultCodeBase.CACHE_REFRESH_FAILURE.getDescription()).append(" : ").append(dbe.getMessage())
					.toString(), dbe);
			throw new CacheAccessException(FaultCodeBase.CACHE_REFRESH_FAILURE, dbe.getMessage(), dbe);
		} catch (Exception ex) {

			LOGGER.error(new StringBuilder(FaultCodeBase.CACHE_WRITE_FAILURE.getCode()).append(" - ")
					.append(FaultCodeBase.CACHE_WRITE_FAILURE.getDescription()).append(" : ").append(ex.getMessage())
					.toString(), ex);
		}
	}

}
