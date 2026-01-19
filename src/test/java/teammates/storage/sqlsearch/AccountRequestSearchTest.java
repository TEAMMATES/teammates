package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlsearch.AccountRequestSearchManager;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestSearchManager}.
 */
public class AccountRequestSearchTest extends BaseTestCase {

    private AccountRequestSearchManager searchManager;

    @BeforeMethod
    public void setUpMethod() {
        searchManager = mock(AccountRequestSearchManager.class);
    }

    // ==================== SEARCH Tests ====================

    @Test
    public void testSearchAccountRequests_queryMatchesSome_success() throws SearchServiceException {
        AccountRequest ar1 = createAccountRequest("Instructor 1", "instr1@test.com", "Institute A");
        AccountRequest ar2 = createAccountRequest("Instructor 1 Course2", "instr1course2@test.com", "Institute B");
        List<AccountRequest> expectedResults = Arrays.asList(ar1, ar2);

        when(searchManager.searchAccountRequests("\"Instructor 1\"")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("\"Instructor 1\"");

        assertEquals(2, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
        verify(searchManager, times(1)).searchAccountRequests("\"Instructor 1\"");
    }

    @Test
    public void testSearchAccountRequests_queryMatchesNone_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchAccountRequests("non-existent")).thenReturn(new ArrayList<>());

        List<AccountRequest> results = searchManager.searchAccountRequests("non-existent");

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchAccountRequests_emptyQuery_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchAccountRequests("")).thenReturn(new ArrayList<>());

        List<AccountRequest> results = searchManager.searchAccountRequests("");

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchAccountRequests_caseInsensitive_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor 2", "instr2@test.com", "Institute A");
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("\"InStRuCtOr 2\"")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("\"InStRuCtOr 2\"");

        assertEquals(1, results.size());
        assertEquals(ar, results.get(0));
    }

    @Test
    public void testSearchAccountRequests_searchByEmail_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor", "specific.email@test.com", "Institute A");
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("specific.email@test.com")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("specific.email@test.com");

        assertEquals(1, results.size());
        assertEquals("specific.email@test.com", results.get(0).getEmail());
    }

    @Test
    public void testSearchAccountRequests_searchByInstitute_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor", "instr@test.com", "Unique Institute Name");
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("\"Unique Institute Name\"")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("\"Unique Institute Name\"");

        assertEquals(1, results.size());
        assertEquals("Unique Institute Name", results.get(0).getInstitute());
    }

    @Test
    public void testSearchAccountRequests_searchByName_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Professor John Smith", "john@test.com", "Institute A");
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("\"Professor John Smith\"")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("\"Professor John Smith\"");

        assertEquals(1, results.size());
        assertEquals("Professor John Smith", results.get(0).getName());
    }

    @Test
    public void testSearchAccountRequests_multipleMatches_success() throws SearchServiceException {
        AccountRequest ar1 = createAccountRequest("Instructor 1 Course1", "instr1c1@test.com", "Institute A");
        AccountRequest ar2 = createAccountRequest("Instructor 1 Course2", "instr1c2@test.com", "Institute B");
        AccountRequest ar3 = createAccountRequest("Instructor 1 Course3", "instr1c3@test.com", "Institute C");
        List<AccountRequest> expectedResults = Arrays.asList(ar1, ar2, ar3);

        when(searchManager.searchAccountRequests("Instructor 1")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("Instructor 1");

        assertEquals(3, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
    }

    // ==================== DELETE DOCUMENT Tests ====================

    @Test
    public void testDeleteDocuments_singleDocument_success() {
        String documentId = UUID.randomUUID().toString();
        List<String> documentIds = Collections.singletonList(documentId);

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    @Test
    public void testDeleteDocuments_multipleDocuments_success() {
        List<String> documentIds = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    @Test
    public void testDeleteDocuments_emptyList_success() {
        List<String> documentIds = new ArrayList<>();

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    // ==================== PUT DOCUMENT Tests ====================

    @Test
    public void testPutDocument_validAccountRequest_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor", "instr@test.com", "Institute A");

        searchManager.putDocument(ar);

        verify(searchManager, times(1)).putDocument(ar);
    }

    // ==================== EDGE CASE Tests ====================

    @Test
    public void testSearchAccountRequests_specialCharactersInQuery_success() throws SearchServiceException {
        when(searchManager.searchAccountRequests("test@email.com")).thenReturn(new ArrayList<>());

        List<AccountRequest> results = searchManager.searchAccountRequests("test@email.com");

        assertNotNull(results);
    }

    @Test
    public void testSearchAccountRequests_searchByStatus_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor", "instr@test.com", "Institute A");
        ar.setStatus(AccountRequestStatus.APPROVED);
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("approved")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("approved");

        assertEquals(1, results.size());
        assertEquals(AccountRequestStatus.APPROVED, results.get(0).getStatus());
    }

    @Test
    public void testSearchAccountRequests_pendingStatus_success() throws SearchServiceException {
        AccountRequest ar = createAccountRequest("Instructor", "instr@test.com", "Institute A");
        ar.setStatus(AccountRequestStatus.PENDING);
        List<AccountRequest> expectedResults = Collections.singletonList(ar);

        when(searchManager.searchAccountRequests("pending")).thenReturn(expectedResults);

        List<AccountRequest> results = searchManager.searchAccountRequests("pending");

        assertEquals(1, results.size());
        assertEquals(AccountRequestStatus.PENDING, results.get(0).getStatus());
    }

    // ==================== Helper Methods ====================

    private AccountRequest createAccountRequest(String name, String email, String institute) {
        return new AccountRequest(email, name, institute, AccountRequestStatus.PENDING, "Test comments");
    }
}
