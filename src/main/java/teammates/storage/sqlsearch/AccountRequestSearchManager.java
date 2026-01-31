package teammates.storage.sqlsearch;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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

    private final AccountRequestsDb accountRequestsDb;

    /**
     * Creates an AccountRequestSearchManager with the given Solr client and AccountRequestsDb.
     * This constructor allows dependency injection for testing purposes.
     *
     * @param client the Solr client to use (can be null or a mock)
     * @param accountRequestsDb the AccountRequestsDb to use (can be a mock)
     * @param isResetAllowed whether reset operations are allowed
     */
    public AccountRequestSearchManager(HttpSolrClient client, AccountRequestsDb accountRequestsDb,
            boolean isResetAllowed) {
        super(client, isResetAllowed);
        this.accountRequestsDb = accountRequestsDb;
    }

    /**
     * Creates an AccountRequestSearchManager with the given Solr client.
     * This constructor allows dependency injection for testing purposes.
     *
     * @param client the Solr client to use (can be null or a mock)
     * @param isResetAllowed whether reset operations are allowed
     */
    public AccountRequestSearchManager(HttpSolrClient client, boolean isResetAllowed) {
        super(client, isResetAllowed);
        this.accountRequestsDb = AccountRequestsDb.inst();
    }

    /**
     * Creates an AccountRequestSearchManager with the given search service host.
     * This constructor maintains backward compatibility.
     *
     * @param searchServiceHost the Solr service host URL
     * @param isResetAllowed whether reset operations are allowed
     */
    public AccountRequestSearchManager(String searchServiceHost, boolean isResetAllowed) {
        super(searchServiceHost, isResetAllowed);
        this.accountRequestsDb = AccountRequestsDb.inst();
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
