package teammates.storage.sqlsearch;

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
        String email = accountRequest.getEmail();
        String institute = accountRequest.getInstitute();

        String[] searchableTexts = {
                accountRequest.getName(), email, institute,
        };

        fields.put("id", email + '%' + institute);
        fields.put("_text_", String.join(" ", searchableTexts));
        fields.put("email", email);
        fields.put("institute", institute);

        return fields;
    }

}
