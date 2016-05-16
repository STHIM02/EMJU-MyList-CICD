package com.safeway.app.emju.mylist.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MailListVO {

	private Long listId;
	private String[] toEmails;
	private ShoppingListGroup[] groups;
	
	public Long getListId() {
		return listId;
	}
	public void setListId(Long listId) {
		this.listId = listId;
	}
	
	public String[] getToEmails() {
		return toEmails;
	}
	public void setToEmails(String[] toEmails) {
		if (toEmails != null) {
			this.toEmails = Arrays.copyOf(toEmails, toEmails.length);
		}
	}
	
	public ShoppingListGroup[] getGroups() {
		return groups;
	}
	public void setGroups(ShoppingListGroup[] groups) {
		this.groups = groups;
	}
	
	@Override
	public String toString() {
		return "MailListVO [listId=" + listId + ", toEmails=" + Arrays.toString(toEmails) + ", groups="
				+ Arrays.toString(groups) + "]";
	}	
	
}
