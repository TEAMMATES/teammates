package teammates.storage.search;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;

/**
 * The {@link SearchDocument} object that defines how we store document for account requests.
 */
class AccountRequestSearchDocument extends SearchDocument<AccountRequestAttributes> {

    AccountRequestSearchDocument(AccountRequestAttributes accountRequest) {
        super(accountRequest);
    }

    @Override
    Map<String, Object> getSearchableFields() {
        Map<String, Object> fields = new HashMap<>();
        AccountRequestAttributes accountRequest = attribute;
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
