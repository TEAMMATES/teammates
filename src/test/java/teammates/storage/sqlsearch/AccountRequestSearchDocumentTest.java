package teammates.storage.sqlsearch;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestSearchDocument}.
 */
public class AccountRequestSearchDocumentTest extends BaseTestCase {

    @Test
    public void testGetSearchableFields_containsAllRequiredFields() {
        // Setup
        AccountRequest accountRequest = createTestAccountRequest(
                "Alice",
                "alice@example.com",
                "Test Institute",
                "Some comments",
                AccountRequestStatus.PENDING);

        AccountRequestSearchDocument document =
                new AccountRequestSearchDocument(accountRequest);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify
        assertNotNull(fields);
        assertEquals(fields.get("id"), accountRequest.getId().toString());
        assertEquals(fields.get("email"), "alice@example.com");
        assertEquals(fields.get("institute"), "Test Institute");
        assertEquals(fields.get("comments"), "Some comments");
        assertEquals(fields.get("status"), AccountRequestStatus.PENDING.toString());
        assertNotNull(fields.get("_text_"));

        // Verify _text_ content
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("Alice"));
        assertTrue(searchableText.contains("alice@example.com"));
        assertTrue(searchableText.contains("Test Institute"));
        assertTrue(searchableText.contains("Some comments"));
        assertTrue(searchableText.contains(AccountRequestStatus.PENDING.toString()));
    }

    @Test
    public void testGetSearchableFields_withNullOptionalFields_handlesGracefully() {
        // Setup: comments and status are null
        AccountRequest accountRequest = createTestAccountRequest(
                "Bob",
                "bob@example.com",
                "Another Institute",
                null,
                null);

        AccountRequestSearchDocument document =
                new AccountRequestSearchDocument(accountRequest);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify
        assertNotNull(fields);
        assertEquals(fields.get("email"), "bob@example.com");
        assertEquals(fields.get("institute"), "Another Institute");
        assertFalse(fields.containsKey("comments"));
        assertFalse(fields.containsKey("status"));

        // _text_ should still exist and contain basic fields
        String searchableText = (String) fields.get("_text_");
        assertNotNull(searchableText);
        assertTrue(searchableText.contains("Bob"));
        assertTrue(searchableText.contains("bob@example.com"));
        assertTrue(searchableText.contains("Another Institute"));
    }

    @Test
    public void testGetSearchableFields_withSpecialCharacters_includesCorrectly() {
        // Setup
        AccountRequest accountRequest = createTestAccountRequest(
                "O'Brien",
                "test+email@example.com",
                "Inst & Co.",
                "Needs access ASAP!",
                AccountRequestStatus.APPROVED);

        AccountRequestSearchDocument document =
                new AccountRequestSearchDocument(accountRequest);

        // Execute
        Map<String, Object> fields = document.getSearchableFields();

        // Verify
        assertNotNull(fields);
        String searchableText = (String) fields.get("_text_");
        assertTrue(searchableText.contains("O'Brien"));
        assertTrue(searchableText.contains("test+email@example.com"));
        assertTrue(searchableText.contains("Inst & Co."));
        assertTrue(searchableText.contains("Needs access ASAP!"));
    }

    @Test
    public void testGetSearchableFields_differentAccountRequests_haveDifferentFields() {
        // Setup
        AccountRequest ar1 = createTestAccountRequest(
                "Alice",
                "alice@example.com",
                "Institute A",
                null,
                AccountRequestStatus.PENDING);

        AccountRequest ar2 = createTestAccountRequest(
                "Bob",
                "bob@example.com",
                "Institute B",
                null,
                AccountRequestStatus.APPROVED);

        AccountRequestSearchDocument doc1 =
                new AccountRequestSearchDocument(ar1);
        AccountRequestSearchDocument doc2 =
                new AccountRequestSearchDocument(ar2);

        // Execute
        Map<String, Object> fields1 = doc1.getSearchableFields();
        Map<String, Object> fields2 = doc2.getSearchableFields();

        // Verify
        assertNotNull(fields1);
        assertNotNull(fields2);
        assertEquals(fields1.get("email"), "alice@example.com");
        assertEquals(fields2.get("email"), "bob@example.com");
        assertEquals(fields1.get("institute"), "Institute A");
        assertEquals(fields2.get("institute"), "Institute B");
        assertNotEquals(fields1.get("id"), fields2.get("id"));
    }

    // Helper method
    private AccountRequest createTestAccountRequest(
            String name,
            String email,
            String institute,
            String comments,
            AccountRequestStatus status) {
        return new AccountRequest(email, name, institute, status, comments);
    }
}
