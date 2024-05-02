package teammates.storage.sqlsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import teammates.storage.sqlentity.AccountRequest;

/**
 * The {@link SearchDocument} object that defines how we store document for
 * account requests.
 */
class AccountRequestSearchDocument extends SearchDocument<AccountRequest> {

    AccountRequestSearchDocument(AccountRequest accountRequest) {
        super(accountRequest);
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        AccountRequest accountRequest = entity;

        ArrayList<String> searchableTexts = new ArrayList<>();
        searchableTexts.add(accountRequest.getName());
        searchableTexts.add(accountRequest.getEmail());
        searchableTexts.add(accountRequest.getInstitute());

        if (accountRequest.getComments() != null) {
            searchableTexts.add(accountRequest.getComments());
        }
        if (accountRequest.getStatus() != null) {
            searchableTexts.add(accountRequest.getStatus().toString());
        }

        fields.put("id", accountRequest.getId().toString());
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("email", accountRequest.getEmail());
        fields.put("institute", accountRequest.getInstitute());
        if (accountRequest.getComments() != null) {
            fields.put("comments", accountRequest.getComments());
        }
        if (accountRequest.getStatus() != null) {
            fields.put("status", accountRequest.getStatus().toString());
        }

        return fields;
    }

}
