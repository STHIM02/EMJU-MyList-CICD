package com.safeway.app.emju.mylist.entity;

import java.util.Date;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Computed;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(name = "mylist_items")
public class ShoppingListItem {

	@PartitionKey(0)
	@Column(name = "retail_customer_id")
	private String retailCustomerId;
	
	@PartitionKey(1)
	@Column(name = "household_id")
	private Long householdId;
	
	@PartitionKey(2)
	@Column(name = "shopping_list_nm")
	private String shoppingListNm;
	
	@ClusteringColumn(0)
	@Column(name = "item_type_cd")
	private String itemTypeCd;
	
	@ClusteringColumn(1)
	@Column(name = "store_id")
	private Integer storeId;
	
	@ClusteringColumn(2)
	@Column(name = "item_id")
	private String itemId;
	
	@Column(name = "club_card_nbr")
	private Long clubCardNbr;
	
	@Column(name = "shopping_list_desc")
	private String shoppingListDesc;
	
	@Column(name = "item_ref_id")
	private String itemRefId;
	
	@Column(name = "clip_ts")
	private Date clipTs;
	
	@Column(name = "checked_ind")
	private String checkedId;
	
	@Column(name = "clip_id")
	private String clipId;
	
	@Column(name = "category_id")
	private Integer categoryId;
	
	@Column(name = "delete_ts")
	private Date deleteTs;
	
	@Column(name = "source_application_cd")
	private String sourceApplicationCd;
	
	@Column(name = "item_title")
	private String itemTitle;
	
	@Column(name = "item_desc")
	private String itemDesc;
	
	@Column(name = "item_end_dt")
	private Date itemEndDate;
	
	@Column(name = "item_quantity")
	private String itemQuantity;
	
	@Column(name = "item_price_value")
	private String itemPriceValue;
	
	@Column(name = "item_promo_price")
	private String itemPromoPrice;
	
	@Column(name = "item_start_dt")
	private Date itemStartDate;
	
	@Column(name = "last_upd_ts")
	private Date lastUpdTs;
	
	@Column(name = "last_upd_usr_id")
	private String lastUpdUsrId;
	
	@Column(name = "item_image")
	private String itemImage;
	
	@Computed("ttl(clip_id)")
	private Integer ttl;
	
	public String getRetailCustomerId() {
		return retailCustomerId;
	}

	public void setRetailCustomerId(String retailCustomerId) {
		this.retailCustomerId = retailCustomerId;
	}

	public Long getHouseholdId() {
		return householdId;
	}

	public void setHouseholdId(Long householdId) {
		this.householdId = householdId;
	}

	public String getShoppingListNm() {
		return shoppingListNm;
	}

	public void setShoppingListNm(String shoppingListNm) {
		this.shoppingListNm = shoppingListNm;
	}

	public String getItemTypeCd() {
		return itemTypeCd;
	}

	public void setItemTypeCd(String itemTypeCd) {
		this.itemTypeCd = itemTypeCd;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Long getClubCardNbr() {
		return clubCardNbr;
	}

	public void setClubCardNbr(Long clubCardNbr) {
		this.clubCardNbr = clubCardNbr;
	}

	public String getShoppingListDesc() {
		return shoppingListDesc;
	}

	public void setShoppingListDesc(String shoppingListDesc) {
		this.shoppingListDesc = shoppingListDesc;
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public Date getClipTs() {
		return clipTs;
	}

	public void setClipTs(Date clipTs) {
		this.clipTs = clipTs;
	}

	public String getCheckedId() {
		return checkedId;
	}

	public void setCheckedId(String checkedId) {
		this.checkedId = checkedId;
	}

	public String getClipId() {
		return clipId;
	}

	public void setClipId(String clipId) {
		this.clipId = clipId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Date getDeleteTs() {
		return deleteTs;
	}

	public void setDeleteTs(Date deleteTs) {
		this.deleteTs = deleteTs;
	}

	public String getSourceApplicationCd() {
		return sourceApplicationCd;
	}

	public void setSourceApplicationCd(String sourceApplicationCd) {
		this.sourceApplicationCd = sourceApplicationCd;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public Date getItemEndDate() {
		return itemEndDate;
	}

	public void setItemEndDate(Date itemEndDate) {
		this.itemEndDate = itemEndDate;
	}

	public String getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public String getItemPriceValue() {
		return itemPriceValue;
	}

	public void setItemPriceValue(String itemPriceValue) {
		this.itemPriceValue = itemPriceValue;
	}

	public String getItemPromoPrice() {
		return itemPromoPrice;
	}

	public void setItemPromoPrice(String itemPromoPrice) {
		this.itemPromoPrice = itemPromoPrice;
	}

	public Date getItemStartDate() {
		return itemStartDate;
	}

	public void setItemStartDate(Date itemStartDate) {
		this.itemStartDate = itemStartDate;
	}

	public Date getLastUpdTs() {
		return lastUpdTs;
	}

	public void setLastUpdTs(Date lastUpdTs) {
		this.lastUpdTs = lastUpdTs;
	}

	public String getLastUpdUsrId() {
		return lastUpdUsrId;
	}

	public void setLastUpdUsrId(String lastUpdUsrId) {
		this.lastUpdUsrId = lastUpdUsrId;
	}

	public String getItemImage() {
		return itemImage;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("ShoppingListItem[retail_customer_id=" + retailCustomerId);
		result.append(", household_id=" + householdId);
		result.append(", item_type_cd=" + itemTypeCd);
		result.append(", item_id=" + itemId);
		result.append(", store_id="  + storeId);
		
		return result.toString();
	}
}
