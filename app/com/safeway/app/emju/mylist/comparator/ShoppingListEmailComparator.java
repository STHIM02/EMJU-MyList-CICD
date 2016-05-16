package com.safeway.app.emju.mylist.comparator;

import java.util.Comparator;

import com.safeway.app.emju.mylist.model.ShoppingListItemVO;

public class ShoppingListEmailComparator implements Comparator<ShoppingListItemVO> {

	@Override
	public int compare(ShoppingListItemVO vo1, ShoppingListItemVO vo2) {

		String category1 = vo1.getCategoryName();
		String category2 = vo2.getCategoryName();
		
		String title1 = vo1.getTitle();
		String title2 = vo2.getTitle();
		
		StringBuffer vo1Comparator = new StringBuffer();
		vo1Comparator.append(category1);
		vo1Comparator.append(title1);
		
		StringBuffer vo2Comparator = new StringBuffer();
		vo2Comparator.append(category2);
		vo2Comparator.append(title2);
			
		return vo1Comparator.toString().compareTo(vo2Comparator.toString());
	}
}
