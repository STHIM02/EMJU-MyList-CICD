package com.safeway.app.emju.mylist.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeaderVO {
	
	private String swycustguid;
	private String swyhouseholdid;
	private String swycoremaclubcard;
	private String postalcode;
	private String swyccpricezone;
	private String appKey;
	private String appVersion;
	private String bannner;
	private String details = "n";
	private String timeZone;
	private String loggedUserId;
	private String paramStoreId;
	private boolean[] versionValues;
	private String sessionToken;
	private String postalBanner;
	private PreferredStore preferredStore;
	private String timestamp;
	
	public String getSwycustguid() {
		return swycustguid;
	}
	public void setSwycustguid(String swycustguid) {
		this.swycustguid = swycustguid;
	}
	public String getSwyhouseholdid() {
		return swyhouseholdid;
	}
	public void setSwyhouseholdid(String swyhouseholdid) {
		this.swyhouseholdid = swyhouseholdid;
	}
	public String getSwycoremaclubcard() {
		return swycoremaclubcard;
	}
	public void setSwycoremaclubcard(String swycoremaclubcard) {
		this.swycoremaclubcard = swycoremaclubcard;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getSwyccpricezone() {
		return swyccpricezone;
	}
	public void setSwyccpricezone(String swyccpricezone) {
		this.swyccpricezone = swyccpricezone;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getBannner() {
		return bannner;
	}
	public void setBannner(String bannner) {
		this.bannner = bannner;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getLoggedUserId() {
		return loggedUserId;
	}
	public void setLoggedUserId(String loggedUserId) {
		this.loggedUserId = loggedUserId;
	}
	public String getParamStoreId() {
		return paramStoreId;
	}
	public void setParamStoreId(String paramStoreId) {
		this.paramStoreId = paramStoreId;
	}
	public boolean[] getVersionValues() {
		return versionValues;
	}
	public void setVersionValues(boolean[] versionValues) {
		this.versionValues = versionValues;
	}
	public String getSessionToken() {
		return sessionToken;
	}
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	public String getPostalBanner() {
		return postalBanner;
	}
	public void setPostalBanner(String postalBanner) {
		this.postalBanner = postalBanner;
	}
	public PreferredStore getPreferredStore() {
		return preferredStore;
	}
	public void setPreferredStore(PreferredStore preferredStore) {
		this.preferredStore = preferredStore;
	}
	
	@Override
    public String toString() {
        return "HeaderVO [swycustguid=" + swycustguid + ", swyhouseholdid=" + swyhouseholdid + ", swycoremaclubcard="
            + swycoremaclubcard + ", postalcode=" + postalcode + ", postalBanner=" + postalBanner + ", swyccpricezone="
            + swyccpricezone + ", appKey=" + appKey + ", appVersion=" + appVersion + ", bannner=" + bannner
            + ", details=" + details + ", timeZone=" + timeZone
            + ", loggedUserId=" + loggedUserId + ", paramStoreId=" + paramStoreId + ", versionValues="
            + Arrays.toString(versionValues) + ", sessionToken=" + sessionToken + "]";
    }
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
