package com.safeway.app.emju.mylist.entity;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(name = "offer_weekly_ad")
public class WeeklyAdd {

	@PartitionKey(0)
	@Column(name = "offer_id")
	private String offerId;
	
	@Column(name = "response")
	private String response;

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
