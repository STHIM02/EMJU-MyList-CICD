package com.safeway.app.emju.mylist.email;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.safeway.app.emju.cache.StoreCache;
import com.safeway.app.emju.cache.entity.Store;
import com.safeway.app.emju.exception.ApplicationException;
import com.safeway.app.emju.helper.ValidationHelper;
import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;
import com.safeway.app.emju.mail.domain.EmailInformation;
import com.safeway.app.emju.mail.domain.EmailItemDetail;
import com.safeway.app.emju.mail.domain.EmailItemGroup;
import com.safeway.app.emju.mail.service.EmailBroker;
import com.safeway.app.emju.mail.util.EmailType;
import com.safeway.app.emju.mylist.comparator.ShoppingListEmailComparator;
import com.safeway.app.emju.mylist.constant.Constants;
import com.safeway.app.emju.mylist.model.MailListVO;
import com.safeway.app.emju.mylist.model.ShoppingListGroup;
import com.safeway.app.emju.mylist.model.ShoppingListItemVO;

public class EmailDispatcher implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailDispatcher.class);
	
	private String J4U_IMAGE_URL;
	private String YCS_IMAGE_URL;
	private String YCS_IMAGE_EXT;
	private String WS_IMAGE_URL;
	private String WS_IMAGE_EXT;
	
	// the savings suffix
	private static Map<String, String> savingsSuffix;
	// types
	private static Map<String, String> types;
	// usage limits
	private static Map<String, String> usageLimits;
	
	private EmailBroker emailBroker;
	private StoreCache storeCache;
	private List<ShoppingListItemVO> items;
	private MailListVO mailListVO;
	private String bannerId;
	private EmailInformation slNotification;
	private String ycsStoreId;
	
	static{
		
		savingsSuffix = new HashMap<String, String>();
		savingsSuffix.put("MF", "");
		savingsSuffix.put("SC", "");
		savingsSuffix.put("PD", "Your Price ");
		savingsSuffix.put("YCS", "Club Price* $");
		
		types = new HashMap<String, String>();
		types.put("PD", "Personalized Price");
		types.put("SC", "Store Coupon");
		types.put("MF", "MFG Coupon");
		types.put("DM", "Deal Match**");
		types.put("YCS", "Club Price*");

		usageLimits = new HashMap<String, String>();
		usageLimits.put("O", "One-time");
		usageLimits.put("U", "Unlimited");
	}
	
	public EmailDispatcher(EmailBroker emailBroker, StoreCache storeCache, List<ShoppingListItemVO> items, 
			MailListVO mailListVO, String bannerId, EmailInformation slNotification, String ycsStoreId, 
			String j4UImageUrl, String ycsImageUrl, String ycsImageExt, String wsImageUrl, String wsImageExt) {
		
		this.emailBroker = emailBroker;
		this.storeCache = storeCache;
		this.items = items;
		this.mailListVO = mailListVO;
		this.bannerId = bannerId;
		this.slNotification = slNotification;
		this.ycsStoreId = ycsStoreId;
		this.J4U_IMAGE_URL = j4UImageUrl;
		this.YCS_IMAGE_URL = ycsImageUrl;
		this.YCS_IMAGE_EXT = ycsImageExt;
		this.WS_IMAGE_URL = wsImageUrl;
		this.WS_IMAGE_EXT = wsImageExt;
		
	}

	@Override
	public void run() {
		
		try{
			
			if(slNotification != null) {
				
				slNotification.setGroups(orderShoppingListForEmail(items, mailListVO));
				slNotification.setBannerId(bannerId);
				slNotification.setStoreAddress(getStoreAdress(ycsStoreId));
				LOGGER.info("before dispatchShoppingList");
			
			}
			
			emailBroker.sendEmail(slNotification, EmailType.SHOPPING_LIST);
			
		}catch(ApplicationException e) {
			LOGGER.error("An exception happened when trying to send the email: " + e);
		}

	}
	
	private String getStoreAdress(String storeId) throws ApplicationException {

		String address = null;

		if (ValidationHelper.isNumber(storeId)) {

			Store storeEntity = storeCache.getStoreDetailsById(Integer.parseInt(storeId));
			address = storeEntity.getAddress1();
		}

		return address;
	}
	
	private EmailItemGroup[] orderShoppingListForEmail(final List<ShoppingListItemVO> itemsVOS,
			final MailListVO criteria) {

		LOGGER.info("start orderShoppingListForEmail");
		ArrayList<EmailItemGroup> shoppingList = new ArrayList<EmailItemGroup>();
		String presentCategory = "";
		String categoryNm = null;
		List<EmailItemDetail> items = null;
		EmailItemGroup shoppingListItemGroup = null;
		int numberOfResults = 0;
		int numberOfCategories = 0;

		// for sorting by category name, title
		List<ShoppingListItemVO> itemVOSList = new ArrayList<ShoppingListItemVO>();
		itemVOSList.addAll(itemsVOS);
		Collections.sort(itemVOSList, new ShoppingListEmailComparator());
		ShoppingListGroup[] shoppingListGroupList = criteria.getGroups();

		try {
			for (ShoppingListGroup shoppingListGroup : shoppingListGroupList) {
	
				List<String> itemIds = shoppingListGroup.getItemIds();
				for (String itemId : itemIds) {
	
					for (ShoppingListItemVO currentItem : itemVOSList) {
	
						if (itemId.equals(currentItem.getId())) {
	
							LOGGER.debug("Item retrieved for mail to be processed: " + currentItem.getReferenceId() + "/" + itemId);
							categoryNm = shoppingListGroup.getGroupName();
							if (ValidationHelper.isEmpty(categoryNm)) {
								categoryNm = Constants.MY_ADDED_ITEMS;
							}
	
							if (!presentCategory.equals(categoryNm)) {
								numberOfCategories++;
								if (!(numberOfResults == 0)) {
									shoppingList.add(shoppingListItemGroup);
								}
								shoppingListItemGroup = new EmailItemGroup();
								shoppingListItemGroup.setGroupName(categoryNm);
								presentCategory = categoryNm;
								items = new ArrayList<EmailItemDetail>();
								shoppingListItemGroup.setItems(items);
							}
	
							EmailItemDetail shoppingListItemDetail = new EmailItemDetail();
							shoppingListItemDetail.setCategoryName(currentItem.getCategoryName());
	
							DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
							DateFormat fromGetFormatter = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
	
							Date startDate = null;
							Date endDate = null;
							String parsedStart = null;
							String parsedEnd = null;
	
							try {
								if (null != currentItem.getStartDate()) {
									startDate = fromGetFormatter.parse(currentItem.getStartDate());
									parsedStart = formatter.format(startDate);
								}
								shoppingListItemDetail.setEffective(parsedStart);
								if (null != currentItem.getEndDate()) {
									endDate = fromGetFormatter.parse(currentItem.getEndDate());
									parsedEnd = formatter.format(endDate);
								}
								shoppingListItemDetail.setExpiration(parsedEnd);
							} catch (ParseException e) {
								LOGGER.error("Error Parsing Dates- Start Date: " + currentItem.getStartDate()
										+ ":: End Date: " + currentItem.getEndDate());
							}
	
							if (null != currentItem.getImage()) {
								StringBuffer imageUrl = new StringBuffer();
	
								if (Constants.J4U.contains(currentItem.getItemType())) {
									imageUrl.append(J4U_IMAGE_URL);
									imageUrl.append(currentItem.getImage());
									shoppingListItemDetail.setImage(imageUrl.toString());
								}
							}
	
							String itemTitle = currentItem.getTitle() == null ? null : currentItem.getTitle();
							LOGGER.debug("Item title: " + itemTitle);
							shoppingListItemDetail.setName(itemTitle);
							LOGGER.debug("Item quantity " + currentItem.getQuantity());
							String itemDescription = currentItem.getDescription() == null ? null
									: parseDescription(currentItem);
							LOGGER.debug("Item description: " + itemDescription);
							shoppingListItemDetail.setDescription(itemDescription);
							shoppingListItemDetail.setSavings(currentItem.getSavingsValue());
							shoppingListItemDetail.setType(currentItem.getSavingsType());
							shoppingListItemDetail.setUsageLimit(currentItem.getUsage());
	
							LOGGER.debug("Item Type: " + currentItem.getItemType());
	
							if (currentItem.getItemType()
									.equalsIgnoreCase(Constants.ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
									|| currentItem.getItemType()
											.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {
	
								LOGGER.debug("Image Type: " + currentItem.getImage());
								// Validate the image url is not exist then it
								// default to ycs url
								if (null == currentItem.getImage()) {
									StringBuffer imageUrl = new StringBuffer();
	
									imageUrl.append(YCS_IMAGE_URL).append(currentItem.getReferenceId()).append(YCS_IMAGE_EXT);
									LOGGER.debug("YCS Image Path: " + imageUrl.toString());
									if (canImageLoad(imageUrl.toString())) {
										
										LOGGER.debug("YCS Set Image Path: " + imageUrl.toString());
										shoppingListItemDetail.setImage(imageUrl.toString());
									}
								}
	
								if (currentItem.getItemType()
										.equalsIgnoreCase(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString())) {
									double promoPrice = 0;
	
									if (currentItem.getSavingsValue() != null) {
										// promoPrice = ((BigDecimal)
										// Long.valueOf(currentItem.getSavingsValue())).doubleValue();
										promoPrice = Double.parseDouble(currentItem.getSavingsValue());
									}
									String priceMethodType = currentItem.getSavingsCode();
									String priceMethodSubType = currentItem.getSavingsSubCode();
									DecimalFormat decFormat = new DecimalFormat("0.00");
									if (priceMethodType != null
											&& priceMethodType.equalsIgnoreCase(Constants.PRICE_METHOD_TYPE_BOGO)) {
										if (priceMethodSubType != null && priceMethodSubType
												.equalsIgnoreCase(Constants.PRICE_METHOD_SUB_TYPE_B1G1)) {
											shoppingListItemDetail.setSavings(Constants.B1G1_SAVINGS_DESC);
										}
									} else if (priceMethodType != null
											&& priceMethodType.equalsIgnoreCase(Constants.PRICE_METHOD_TYPE_MB)) {
										if (priceMethodSubType != null && priceMethodSubType
												.equalsIgnoreCase(Constants.PRICE_METHOD_SUB_TYPE_MB2)) {
											shoppingListItemDetail.setSavings(savingsSuffix.get("YCS") + " "
													+ decFormat.format(promoPrice) + "<br>" + Constants.MB2_SAVINGS_DESC);
										}
									} else {
										shoppingListItemDetail
												.setSavings(savingsSuffix.get("YCS") + decFormat.format(promoPrice));
									}
	
									shoppingListItemDetail
											.setType(types.get(Constants.ItemTypeCode.CLUB_SPECIAL_ITEM.toString()));
									shoppingListItemDetail.setUsageLimit(usageLimits.get("U"));
								}
							} else if (Constants.J4U_ADD.contains(currentItem.getItemType())) {

								shoppingListItemDetail.setUsageLimit(usageLimits.get(currentItem.getUsage()));
								shoppingListItemDetail.setType(types.get(currentItem.getItemType()));
								shoppingListItemDetail.setSavings(
										savingsSuffix.get(currentItem.getItemType()) + currentItem.getSavingsValue());
							} else if (currentItem.getItemType().equalsIgnoreCase(Constants.ItemTypeCode.WEEKLY_SPECIAL_ITEM.toString())) {

								if (null != currentItem.getImage()) {
									StringBuffer imageUrl = new StringBuffer();
	
									imageUrl.append(WS_IMAGE_URL).append(currentItem.getImage()).append(WS_IMAGE_EXT);
									LOGGER.debug("WS Image Path: " + imageUrl.toString());
									if (canImageLoad(imageUrl.toString())) {
										
										LOGGER.debug("WS Set Image Path: " + imageUrl.toString());
										shoppingListItemDetail.setImage(imageUrl.toString());
									}
								}
							} else if (currentItem.getItemType().equalsIgnoreCase(Constants.ItemTypeCode.LANDING_PAGE_ITEM.toString())) {

								if (null != currentItem.getImage()) {
									
									LOGGER.debug("ELP Image Path: " + currentItem.getImage());
									if (canImageLoad(currentItem.getImage())) {
										
										LOGGER.debug("ELP Set Image Path: " + currentItem.getImage());
										shoppingListItemDetail.setImage(currentItem.getImage());
									}
								}
							}
							else {
								LOGGER.debug("item type no match " + currentItem.getItemType());
							}
							
							items.add(shoppingListItemDetail);
							numberOfResults++;
							itemVOSList.remove(currentItem);
							break;
						}
					}
				}
			}
		}
		catch(Exception e) { 
			e.printStackTrace();
			LOGGER.error("err msg " + e.getMessage());
		}

		if (numberOfCategories != shoppingList.size()) {
			shoppingList.add(shoppingListItemGroup);
		}
		LOGGER.info("End orderShoppingListForEmail");
		return shoppingList.toArray(new EmailItemGroup[shoppingList.size()]);

	}
	
	private boolean canImageLoad(final String imageURL) {

		Image image = null;
		boolean isImageExist = true;
		LOGGER.debug("start method canImageLoad(). . .");
		try {
			URL url = new URL(imageURL);
			LOGGER.debug("URL begin. . .");
			image = ImageIO.read(url);
			LOGGER.debug("image. . .");
			if (image == null) {
				isImageExist = false;
			}

		} catch (IOException e) {
			LOGGER.debug("No Images Exists. . .");
			isImageExist = false;
		}
		return isImageExist;
	}

	private String parseDescription(final ShoppingListItemVO currentItem) {

		StringBuffer sb = new StringBuffer();

		LOGGER.debug("The type of item " + currentItem.getId() + " is: " + currentItem.getItemType());

		try {
			sb.append(currentItem.getDescription());
			if (Constants.J4U_ADD.contains(currentItem.getItemType())
					&& ValidationHelper.isNonEmpty(currentItem.getSummary())) {

				sb = new StringBuffer();
				sb.append(currentItem.getSummary().trim());
				sb.append(", ");
				sb.append(currentItem.getDescription());

			}
			if ((currentItem.getItemType().equals(Constants.ItemTypeCode.STANDARD_PRODUCT_ITEM.toString())
					|| currentItem.getItemType().equals(Constants.ItemTypeCode.MANUAL_ITEM.toString()))
					&& ValidationHelper.isNonEmpty(currentItem.getQuantity())) {
				sb.append(" - ");
				sb.append(currentItem.getQuantity());
			}
		} catch (Exception ex) {
			return currentItem.getDescription();
		}
		return sb.toString();
	}

}
