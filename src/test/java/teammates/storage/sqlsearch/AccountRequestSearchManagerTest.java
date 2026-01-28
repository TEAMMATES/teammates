package teammates.storage.sqlsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestSearchManager}.
 */
public class AccountRequestSearchManagerTest extends BaseTestCase {

    private AccountRequestSearchManager searchManager;

    @BeforeMethod
    public void setUp() {
        // Use mocked DB to avoid AccountRequestsDb.inst() before DB is ready in full suite
        AccountRequestsDb mockAccountRequestsDb = mock(AccountRequestsDb.class);
        searchManager = new AccountRequestSearchManager(null, mockAccountRequestsDb, false);
    }

    @Test
    public void testGetCollectionName_returnsCorrectCollectionName() throws Exception {
        // Use reflection to test protected method
        Method method = AccountRequestSearchManager.class.getDeclaredMethod("getCollectionName");
        method.setAccessible(true);
        String collectionName = (String) method.invoke(searchManager);
        assertEquals(collectionName, "accountrequests");
    }

    @Test
    public void testCreateDocument_createsCorrectDocumentType() throws Exception {
        AccountRequest accountRequest = createTestAccountRequest();

        // Use reflection to access protected method
        Method method = AccountRequestSearchManager.class.getDeclaredMethod("createDocument", AccountRequest.class);
        method.setAccessible(true);
        AccountRequestSearchDocument document = (AccountRequestSearchDocument) method.invoke(searchManager, accountRequest);

        assertNotNull(document);
        // Verify it's the correct type by checking its fields
        var fields = document.getSearchableFields();
        assertEquals(fields.get("email"), accountRequest.getEmail());
        assertEquals(fields.get("institute"), accountRequest.getInstitute());
    }

    @Test
    public void testSortResult_sortsCorrectly() throws Exception {
        AccountRequest accountRequest1 = createTestAccountRequest("req1@example.com", "Institute A", "Alice");
        // Sleep to ensure different createdAt timestamps
        Thread.sleep(10);
        AccountRequest accountRequest2 = createTestAccountRequest("req2@example.com", "Institute B", "Bob");
        Thread.sleep(10);
        AccountRequest accountRequest3 = createTestAccountRequest("req3@example.com", "Institute A", "Charlie");

        List<AccountRequest> accountRequests =
                new ArrayList<>(Arrays.asList(accountRequest1, accountRequest2, accountRequest3));

        // Use reflection to access protected method
        Method method = AccountRequestSearchManager.class.getDeclaredMethod("sortResult", List.class);
        method.setAccessible(true);
        method.invoke(searchManager, accountRequests);

        // Verify sorted by createdAt descending (newest first)
        assertEquals(accountRequests.get(0), accountRequest3); // Created last
        assertEquals(accountRequests.get(1), accountRequest2); // Created second
        assertEquals(accountRequests.get(2), accountRequest1); // Created first
    }

    @Test
    public void testGetBasicQuery_buildsQueryCorrectly() throws Exception {
        // Use reflection to access package-private method on base class
        Method method = AccountRequestSearchManager.class.getSuperclass()
                .getDeclaredMethod("getBasicQuery", String.class);
        method.setAccessible(true);

        String queryString = "test query";
        SolrQuery query = (SolrQuery) method.invoke(searchManager, queryString);

        assertNotNull(query);
        assertEquals((int) query.getStart(), 0);
        assertEquals((int) query.getRows(), Const.SEARCH_QUERY_SIZE_LIMIT);
    }

    @Test
    public void testSearchAccountRequests_withMockedSolrClient_returnsResults() throws Exception {
        // Setup: Create mocks for Solr client and database
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        AccountRequestsDb mockAccountRequestsDb = mock(AccountRequestsDb.class);
        AccountRequestSearchManager managerWithMock =
                new AccountRequestSearchManager(mockClient, mockAccountRequestsDb, false);

        // Create test data
        AccountRequest accountRequest = createTestAccountRequest();

        // Create mock Solr response
        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("id", accountRequest.getId().toString());
        mockDoc.addField("email", accountRequest.getEmail());
        mockDoc.addField("institute", accountRequest.getInstitute());
        mockResults.add(mockDoc);

        // Setup mock behavior
        when(mockClient.query(eq("accountrequests"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockAccountRequestsDb.getAccountRequest(accountRequest.getId()))
                .thenReturn(accountRequest);

        // Execute
        List<AccountRequest> results = managerWithMock.searchAccountRequests("request");

        // Verify
        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), accountRequest);
        assertEquals(results.get(0).getEmail(), accountRequest.getEmail());
    }

    @Test
    public void testPutDocument_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        AccountRequestsDb mockAccountRequestsDb = mock(AccountRequestsDb.class);
        AccountRequestSearchManager managerWithMock =
                new AccountRequestSearchManager(mockClient, mockAccountRequestsDb, false);

        AccountRequest accountRequest = createTestAccountRequest();

        // Execute
        managerWithMock.putDocument(accountRequest);

        // Verify: Capture the collection argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<SolrInputDocument>> collectionCaptor =
                ArgumentCaptor.forClass(Collection.class);
        verify(mockClient).add(eq("accountrequests"), collectionCaptor.capture());
        verify(mockClient).commit(eq("accountrequests"));

        // Verify the collection contains exactly one document
        Collection<SolrInputDocument> capturedCollection = collectionCaptor.getValue();
        assertNotNull(capturedCollection);
        assertEquals(capturedCollection.size(), 1);

        // Verify the document content
        SolrInputDocument document = capturedCollection.iterator().next();
        assertNotNull(document);
        assertEquals(document.getFieldValue("id"), accountRequest.getId().toString());
        assertEquals(document.getFieldValue("email"), accountRequest.getEmail());
        assertEquals(document.getFieldValue("institute"), accountRequest.getInstitute());
        assertNotNull(document.getFieldValue("_text_"));
    }

    @Test
    public void testDeleteDocuments_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        AccountRequestSearchManager managerWithMock = new AccountRequestSearchManager(mockClient, false);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<String> keys = Arrays.asList(id1.toString(), id2.toString());

        // Execute
        managerWithMock.deleteDocuments(keys);

        // Verify: Method was called with correct collection name
        verify(mockClient).deleteById("accountrequests", keys);
        verify(mockClient).commit("accountrequests");
    }

    @Test
    public void testDeleteDocuments_withEmptyList_doesNothing() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        AccountRequestSearchManager managerWithMock = new AccountRequestSearchManager(mockClient, false);

        managerWithMock.deleteDocuments(new ArrayList<>());

        // Verify that deleteById and commit were never called (specify List<String> to resolve overload ambiguity)
        verify(mockClient, never()).deleteById(anyString(), ArgumentMatchers.<List<String>>any());
        verify(mockClient, never()).commit(anyString());
    }

    // Helper methods to create test entities
    private AccountRequest createTestAccountRequest() {
        return createTestAccountRequest("test@example.com", "Test Institute", "Test Name");
    }

    private AccountRequest createTestAccountRequest(String email, String institute, String name) {
        return new AccountRequest(email, name, institute,
                teammates.common.datatransfer.AccountRequestStatus.PENDING, "");
    }
}
