package teammates.storage.search;

import java.util.Comparator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.storage.api.AccountRequestsDb;

/**
 * Acts as a proxy to search service for account request related search features.
 */
public class AccountRequestSearchManager extends SearchManager<AccountRequestAttributes> {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    public AccountRequestSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "accountrequests";
    }

    @Override
    AccountRequestSearchDocument createDocument(AccountRequestAttributes accountRequest) {
        return new AccountRequestSearchDocument(accountRequest);
    }

    @Override
    AccountRequestAttributes getAttributeFromDocument(SolrDocument document) {
        String email = (String) document.getFirstValue("email");
        String institute = (String) document.getFirstValue("institute");
        return accountRequestsDb.getAccountRequest(email, institute);
    }

    @Override
    void sortResult(List<AccountRequestAttributes> result) {
        result.sort(Comparator.comparing((AccountRequestAttributes accountRequest) -> accountRequest.getCreatedAt())
                .reversed());
    }

    /**
     * Searches for account requests.
     */
    public List<AccountRequestAttributes> searchAccountRequests(String queryString) throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return convertDocumentToAttributes(response.getResults());
    }

}
