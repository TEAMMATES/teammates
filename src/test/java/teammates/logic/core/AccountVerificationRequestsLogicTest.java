package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.email.AccountVerificationEmailsLogic;
import teammates.storage.api.AccountVerificationRequestsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountVerificationsLogic}.
 */
public class AccountVerificationRequestsLogicTest extends BaseTestCase {

    private AccountVerificationsLogic accountVerificationsLogic = AccountVerificationsLogic.inst();
    private AccountVerificationRequestsDb accountVerificationRequestsDb;
    private AccountsLogic accountsLogic;
    private InstitutesLogic institutesLogic;
    private AccountVerificationEmailsLogic accountVerificationEmailsLogic;

    @BeforeMethod
    public void setUpMethod() {
        accountVerificationRequestsDb = mock(AccountVerificationRequestsDb.class);
        accountsLogic = mock(AccountsLogic.class);
        institutesLogic = mock(InstitutesLogic.class);
        accountVerificationEmailsLogic = mock(AccountVerificationEmailsLogic.class);
        accountVerificationsLogic.initLogicDependencies(accountVerificationRequestsDb, accountsLogic,
                institutesLogic, accountVerificationEmailsLogic);
    }

    @Test
    public void testCreateAccountVerificationRequest_typicalRequest_success() throws Exception {
        AccountVerificationRequest accountVerificationRequest = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest))
                .thenReturn(accountVerificationRequest);
        AccountVerificationRequest createdAccountVerificationRequest =
                accountVerificationsLogic.createAccountVerificationRequest(accountVerificationRequest);

        assertEquals(accountVerificationRequest, createdAccountVerificationRequest);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest);
    }

    @Test
    public void testCreateAccountVerificationRequest_requestAlreadyExists_success() throws Exception {
        AccountVerificationRequest accountVerificationRequest1 = getTypicalAccountVerificationRequest();
        AccountVerificationRequest accountVerificationRequest2 = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest1))
                .thenReturn(accountVerificationRequest1);
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest2))
                        .thenReturn(accountVerificationRequest2);

        accountVerificationsLogic.createAccountVerificationRequest(accountVerificationRequest1);
        accountVerificationsLogic.createAccountVerificationRequest(accountVerificationRequest2);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest1);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest2);
    }

    @Test
    public void testCreateAccountVerificationRequest_invalidParams_failure() {
        AccountVerificationRequest invalidEmailAccountVerificationRequest = getTypicalAccountVerificationRequest();
        invalidEmailAccountVerificationRequest.setEmail("invalid email");

        assertThrows(InvalidParametersException.class, () -> {
            accountVerificationsLogic.createAccountVerificationRequest(invalidEmailAccountVerificationRequest);
        });
        verify(accountVerificationRequestsDb, never())
                .persistAccountVerificationRequest(invalidEmailAccountVerificationRequest);
    }

    @Test
    public void testCreatePendingAccountVerificationRequest_validParams_enqueuesCreatedEmails() throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = getTypicalAccount();
        Institute institute = new Institute("institute", "SG");
        AccountVerificationRequest createdRequest = new AccountVerificationRequest(
                "test@email.com", "name", AccountVerificationRequestStatus.PENDING, "comments");
        institute.addAccountVerificationRequest(createdRequest);
        account.addAccountVerificationRequest(createdRequest);

        when(institutesLogic.getOrCreateInstitute("institute", "SG")).thenReturn(institute);
        when(accountsLogic.getAccount(accountId)).thenReturn(account);
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(any(AccountVerificationRequest.class)))
                .thenReturn(createdRequest);

        AccountVerificationRequest actual = accountVerificationsLogic.createAccountVerificationRequest(
                "name", "test@email.com", "institute", "SG", "comments", accountId);

        assertEquals(createdRequest, actual);
        verify(accountVerificationEmailsLogic).enqueueCreatedAdminAlertEmail(any());
        verify(accountVerificationEmailsLogic).enqueueCreatedAcknowledgementEmail(any());
    }

    @Test
    public void testCreateAccountVerificationRequestWithExplicitStatus_validParams_doesNotEnqueueCreatedEmails()
            throws Exception {
        UUID accountId = UUID.randomUUID();
        Account account = getTypicalAccount();
        Institute institute = new Institute("institute", "SG");
        AccountVerificationRequest createdRequest = new AccountVerificationRequest(
                "test@email.com", "name", AccountVerificationRequestStatus.APPROVED, "comments");
        institute.addAccountVerificationRequest(createdRequest);
        account.addAccountVerificationRequest(createdRequest);

        when(institutesLogic.getOrCreateInstitute("institute", "SG")).thenReturn(institute);
        when(accountsLogic.getAccount(accountId)).thenReturn(account);
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(any(AccountVerificationRequest.class)))
                .thenReturn(createdRequest);

        AccountVerificationRequest actual = accountVerificationsLogic.createAccountVerificationRequest(
                "name", "test@email.com", "institute", "SG",
                AccountVerificationRequestStatus.APPROVED, "comments", accountId);

        assertEquals(createdRequest, actual);
        verify(accountVerificationEmailsLogic, never()).enqueueCreatedAdminAlertEmail(any());
        verify(accountVerificationEmailsLogic, never()).enqueueCreatedAcknowledgementEmail(any());
    }

    @Test
    public void testApproveAccountVerificationRequest_pendingRequest_enqueuesApprovalEmail() throws Exception {
        AccountVerificationRequest request = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(request.getId())).thenReturn(request);

        AccountVerificationRequest actual = accountVerificationsLogic.approveAccountVerificationRequest(request.getId());

        assertEquals(AccountVerificationRequestStatus.APPROVED, actual.getStatus());
        verify(accountVerificationEmailsLogic).enqueueApprovalEmail(any());
    }

    @Test
    public void testRejectAccountVerificationRequest_withReason_enqueuesRejectionEmail() throws Exception {
        AccountVerificationRequest request = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(request.getId())).thenReturn(request);

        AccountVerificationRequest actual = accountVerificationsLogic.rejectAccountVerificationRequest(
                request.getId(), "Verification request update", "<p>Rejected</p>");

        assertEquals(AccountVerificationRequestStatus.REJECTED, actual.getStatus());
        verify(accountVerificationEmailsLogic).enqueueRejectionEmail(any());
    }

    @Test
    public void testRejectAccountVerificationRequest_withoutReason_doesNotEnqueueRejectionEmail() throws Exception {
        AccountVerificationRequest request = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(request.getId())).thenReturn(request);

        AccountVerificationRequest actual = accountVerificationsLogic.rejectAccountVerificationRequest(
                request.getId(), null, null);

        assertEquals(AccountVerificationRequestStatus.REJECTED, actual.getStatus());
        verify(accountVerificationEmailsLogic, never()).enqueueRejectionEmail(any());
    }

    @Test
    public void testDeleteAccountVerificationRequest_typicalRequest_success() {
        AccountVerificationRequest ar = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(ar.getId())).thenReturn(ar);
        accountVerificationsLogic.deleteAccountVerificationRequest(ar.getId());

        verify(accountVerificationRequestsDb, times(1))
                .removeAccountVerificationRequest(any(AccountVerificationRequest.class));
    }

    @Test
    public void testDeleteAccountVerificationRequest_nonexistentRequest_shouldSilentlyDelete() {
        UUID nonexistentUuid = UUID.fromString("00000000-0000-4000-8000-000000000100");
        accountVerificationsLogic.deleteAccountVerificationRequest(nonexistentUuid);

        verify(accountVerificationRequestsDb, times(1))
                .removeAccountVerificationRequest(nullable(AccountVerificationRequest.class));
    }

    @Test
    public void testGetAccountVerificationRequest_nonExistentAccountVerificationRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(id)).thenReturn(null);
        AccountVerificationRequest actualAccountVerificationRequest =
                accountVerificationsLogic.getAccountVerificationRequest(id);
        verify(accountVerificationRequestsDb).getAccountVerificationRequest(id);
        assertNull(actualAccountVerificationRequest);
    }

    @Test
    public void testGetAccountVerificationRequest_existingAccountVerificationRequest_getsSuccessfully() {
        AccountVerificationRequest expectedAccountVerificationRequest =
                new AccountVerificationRequest("test@gmail.com", "name",
                        AccountVerificationRequestStatus.PENDING, "comments");
        new Institute("institute", "SG").addAccountVerificationRequest(expectedAccountVerificationRequest);
        UUID id = expectedAccountVerificationRequest.getId();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(id))
                .thenReturn(expectedAccountVerificationRequest);
        AccountVerificationRequest actualAccountVerificationRequest =
                accountVerificationsLogic.getAccountVerificationRequest(id);
        verify(accountVerificationRequestsDb).getAccountVerificationRequest(id);
        assertEquals(expectedAccountVerificationRequest, actualAccountVerificationRequest);
    }

    @Test
    public void testGetVerifiedName_noMatchingRequest_returnsNull() {
        UUID accountId = UUID.randomUUID();
        UUID instituteId = UUID.randomUUID();
        when(accountVerificationRequestsDb.getApprovedAccountVerificationRequest(accountId, instituteId))
                .thenReturn(null);

        String actual = accountVerificationsLogic.getVerifiedName(accountId, instituteId);

        verify(accountVerificationRequestsDb)
                .getApprovedAccountVerificationRequest(accountId, instituteId);
        assertNull(actual);
    }

    @Test
    public void testGetVerifiedName_matchingRequest_returnsName() {
        UUID accountId = UUID.randomUUID();
        UUID instituteId = UUID.randomUUID();
        AccountVerificationRequest request = new AccountVerificationRequest(
                "test@gmail.com", "verified name", AccountVerificationRequestStatus.APPROVED, "comments");
        when(accountVerificationRequestsDb.getApprovedAccountVerificationRequest(accountId, instituteId))
                .thenReturn(request);

        String actual = accountVerificationsLogic.getVerifiedName(accountId, instituteId);

        verify(accountVerificationRequestsDb)
                .getApprovedAccountVerificationRequest(accountId, instituteId);
        assertEquals("verified name", actual);
    }
}
