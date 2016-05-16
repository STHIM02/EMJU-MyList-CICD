package com.safeway.app.emju.mylist.api;

import com.google.inject.Inject;
import com.safeway.app.emju.mylist.api.service.MylistServiceAPI;

public class MylistServiceAPIFactory {
	
	private MylistServiceAPI mylistServiceAPI;

	@Inject
	public MylistServiceAPIFactory(MylistServiceAPI mylistServiceAPI) {
		
		this.mylistServiceAPI = mylistServiceAPI;
	}
	
	public MylistServiceAPI getMylistServiceAPI() {
		
		return mylistServiceAPI;
	}
}
