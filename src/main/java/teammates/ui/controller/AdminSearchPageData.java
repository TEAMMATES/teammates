package teammates.ui.controller;

import java.util.List;

import com.google.appengine.api.search.Document;

import teammates.common.datatransfer.AccountAttributes;

public class AdminSearchPageData extends PageData {
	
	public List<Document> results;

	public AdminSearchPageData(AccountAttributes account) {
		super(account);
	}

}
