package teammates.storage.sqlsearch;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Acts as a proxy to search service for account request related search
 * features.
 */
public class AccountRequestSearchManager extends SearchManager<AccountRequest> {

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    public AccountRequestSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
    }

    @Override
    String getCollectionName() {
        return "accountrequests";
    }

    @Override
    AccountRequestSearchDocument createDocument(AccountRequest accountRequest) {
        return new AccountRequestSearchDocument(accountRequest);
    }

    @Override
    AccountRequest getEntityFromDocument(SolrDocument document) {
        UUID id = UUID.fromString((String) document.getFieldValue("id"));
        return accountRequestsDb.getAccountRequest(id);
    }

    @Override
    void sortResult(List<AccountRequest> result) {
        result.sort(Comparator.comparing((AccountRequest accountRequest) -> accountRequest.getCreatedAt())
                .reversed());
    }

    /**
     * Searches for account requests.
     */
    public List<AccountRequest> searchAccountRequests(String queryString) throws SearchServiceException {
        SolrQuery query = getBasicQuery(queryString);

        QueryResponse response = performQuery(query);
        return convertDocumentToEntities(response.getResults());
    }

}
