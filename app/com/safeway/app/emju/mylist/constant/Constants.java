package com.safeway.app.emju.mylist.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Constants {
	
	enum Banner {

		DOMINICKS("dominicks"), SAFEWAY("safeway"), VONS("vons"), RANDALLS(
				"randalls"), TOMTHUMB("tomthumb"), CARRSQC("carrsqc"), PAVILIONS(
				"pavilions" ), JUSTFORU ("justforu");

		private String bannerName;

		Banner(final String banner) {
			setBannerName(banner);
		}

		/**
		 * @param bannerName
		 *            the bannerName to set
		 */
		public void setBannerName(final String bannerName) {
			this.bannerName = bannerName;
		}

		/**
		 * @return the bannerName
		 */
		public String getBannerName() {
			return bannerName;
		}

	}
	
	enum ItemTypeCode {
		
		COUPON_ITEM("CC"),
		PERSONAL_DEAL_ITEM("PD"),
		STANDARD_PRODUCT_ITEM("UPC"),
		CLUB_SPECIAL_ITEM("YCS"),
		MOBILE_SPECIAL_ITEM("MCS"),
		MANUAL_ITEM("FF"),
		WEEKLY_SPECIAL_ITEM("WS"),
		LANDING_PAGE_ITEM("ELP"),
		RECIPE_ITEM("REC");
		
		private String itemType;
		
		ItemTypeCode(final String itemType) {
			
			this.itemType = itemType;
		}
		
		public String toString() {
			return itemType;
		}
		
	}
	
	public final static String[] REMOVABLE_MATCHED_OFFERS = {
			ItemTypeCode.CLUB_SPECIAL_ITEM.toString(),
			ItemTypeCode.PERSONAL_DEAL_ITEM.toString(),
			ItemTypeCode.COUPON_ITEM.toString()
			};
	
	Set<String> VERSIONS = new HashSet<String>(
    		Arrays.asList(new String[] 
    		      { "1.0","1.1", "1.2", 
    				"1.3", "1.4", "1.5"}));
	
	/**
     * Set boolean based on each versions
     * Enable YCS filter: YCS items will use the paramStoreId passed in the URL. Other Items will use PreferStoreId.
     * Enable All Items filter: PreferStoreId will be used for all items including YCS.
     */
	static final boolean[] filter_1_0 = new boolean[]{false,true,false};//Disable WS + Enable All Items filter + Disable MCS
	static final boolean[] filter_1_1 = new boolean[]{true,true,true}; //Enable WS + Enable All Items filter + Enable MCS
	static final boolean[] filter_1_2 = new boolean[]{false,false,false}; //Disable WS + Enable YCS filter + Disable MCS
	static final boolean[] filter_1_3 = new boolean[]{true,true,false};  //Enable WS + Enable YCS filter + Disable MCS
	static final boolean[] filter_1_4 = new boolean[]{false,false,true};  //Disable WS + Enable YCS filter + Enable MCS
	static final boolean[] filter_1_5 = new boolean[]{true,false,true};   //Enable WS + Enable YCS filter + Enable MCS
	
	static final String DEV_MODE = "dev";
	
	static final String APP_KEY = "X-SWY_API_KEY";
	static final String APP_VERSION = "X-SWY_VERSION";
	static final String SWY_BANNER = "X-SWY_BANNER";
	static final String SWY_REMEMBERME_UID = "SWY_REMEMBERME_UID";
	static final String SWY_LOGONID = "SWY_LOGONID";
	
	static final String SWY_CUST_GUID = "swycustguid";
	static final String SWY_HOUSE_HOLD_ID = "swyhouseholdid";
	static final String SWY_COREMA_CLUB_CARD = "swycoremaclubcard";
	static final String POSTAL_CODE = "postalcode";
	static final String POSTAL_BANNER_CD = "postalbannercd";
	static final String SWY_CC_PRICE_ZONE = "swyccpricezone";
	static final String SWY_CUST_STORE_ID = "swycuststoreid";
	static final String USER_SESSION_TOKEN = "swyConsumerDirectoryPro";
	static final String HEADER_SESSION_TOKEN = "X-swyConsumerDirectoryPro";
	
	static final String LOGIN_ID = "uid";
	static final String YES = "y";
	
	static final String ISO_DATE_FORMAT= "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
	static final String DEFAULT_TIMEZONE = "America/Los_Angeles";
	
	static final int TITLE_MAX_SIZE = 100;
	static final int DESCR_MAX_SIZE = 400;
	static final int MAX_LENGTH = 32;
	
	static final String MY_ADDED_ITEMS = "My Added Items";
    
    //CCA APPLICATION
  	String SWY_CUST_GUID_CCA = "cust_guid";
  	String SWY_HOUSE_HOLD_ID_CCA = "cust_householdid";
  	String SWY_COREMA_CLUB_CARD_CCA = "cust_clubcard";
  	String POSTAL_CODE_CCA = "cust_postalcode";
  	String SWY_CC_PRICE_ZONE_CCA = "cust_pricezone";
  	String SWY_CUST_STORE_ID_CCA = "cust_storeid";
  	String AGENT_ID = "AGENT_ID";
  	
  	public static final String EMPTY_STRING = "";
    public static final String CHAR_PERIOD = ".";
    public static final String CHAR_UNDERSCORE = "_";
    public static final String CHAR_TILDE = "~";
    public static final String CHAR_PIPE = "|";
    public static final String CHAR_HASH = "#";
    public static final String CHAR_COLON = ":";
    public static final String CHAR_CARET = "^";
    public static final String CHAR_BACKSLASH = "\\";
    
    String PRICE_METHOD_TYPE_BOGO = "BG";
    String PRICE_METHOD_SUB_TYPE_B1G1 = "B1G1";
    String B1G1_SAVINGS_DESC = "Buy 1 Get 1 Free";
    String PRICE_METHOD_TYPE_MB = "MB";
    String PRICE_METHOD_SUB_TYPE_MB2 = "MB2";
    String MB2_SAVINGS_DESC = "Must Buy 2";
    
    Set<String> J4U = new HashSet<String>(Arrays.asList(new String[] { "SC",
			"MF", "PD", "YCS" }));
    Set<String> J4U_ADD = new HashSet<String>(Arrays.asList(new String[] {
			"SC", "MF", "PD" }));
    
    public static final class ClientApiKey {
        private ClientApiKey() {}
        public static final String SVCT_APP = "svct";
        public static final String EMMD_APP = "emmd";
        public static final String MOBILE_APP = "mobile";
        public static final String PARTNER_COKE = "coke";
    }
    
    public static final class ProfileDataRestrictions {
        private ProfileDataRestrictions() {}
        public static final int SWY_API_KEY_MAX_LENGTH = 6;

    }
}
