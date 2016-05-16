package com.safeway.app.emju.mylist.model;

public class PreferredStore {
	
	private String postalCode;
	private String[] postalBanners;
	private Integer regionId;
	private Integer storeId;
	private String priceZone;
	private boolean isDefault = false;
	private String timeZone;
	
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String[] getPostalBanners() {
		return postalBanners;
	}
	public void setPostalBanners(String[] postalBanners) {
		this.postalBanners = postalBanners;
	}
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public String getPriceZone() {
		return priceZone;
	}
	public void setPriceZone(String priceZone) {
		this.priceZone = priceZone;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	@Override
    public String toString() {
        return "PreferredStore [postalCode=" + postalCode + ",  regionId=" + regionId + ", storeId=" + storeId
            + ", priceZone=" + priceZone + ", isDefault=" + isDefault + ", timeZone=" + timeZone + "]";
    }
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isDefault ? 1231 : 1237);
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((priceZone == null) ? 0 : priceZone.hashCode());
        result = prime * result + ((regionId == null) ? 0 : regionId.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
        return result;
    }
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PreferredStore other = (PreferredStore) obj;
        if (isDefault != other.isDefault)
            return false;
        if (postalCode == null) {
            if (other.postalCode != null)
                return false;
        }
        else if (!postalCode.equals(other.postalCode))
            return false;
        if (priceZone == null) {
            if (other.priceZone != null)
                return false;
        }
        else if (!priceZone.equals(other.priceZone))
            return false;
        if (regionId == null) {
            if (other.regionId != null)
                return false;
        }
        else if (!regionId.equals(other.regionId))
            return false;
        if (storeId == null) {
            if (other.storeId != null)
                return false;
        }
        else if (!storeId.equals(other.storeId))
            return false;
        if (timeZone == null) {
            if (other.timeZone != null)
                return false;
        }
        else if (!timeZone.equals(other.timeZone))
            return false;
        return true;
    }

}
