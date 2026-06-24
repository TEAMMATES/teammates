import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import '@angular/compiler';
import { of, throwError } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  AccountVerificationRequest,
  AccountVerificationRequestStatus,
  AccountVerificationRequests,
} from '../../../types/api-output';
import { AccountVerificationRequestRejectionType } from '../../../types/api-request';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AdminAccountVerificationRequestPageComponent } from './admin-account-verification-request-page.component';

const mockPendingRequest: AccountVerificationRequest = {
  accountVerificationRequestId: 'test-id-123',
  accountId: 'account-id-123',
  email: 'test@example.com',
  name: 'Test Instructor',
  institute: 'Test University',
  country: 'SG',
  status: AccountVerificationRequestStatus.PENDING,
  comments: 'Please verify my account.',
  createdAt: 1000000,
};

const mockHistoricalRequests: AccountVerificationRequests = {
  accountVerificationRequests: [
    {
      accountVerificationRequestId: 'history-approved-request',
      accountId: 'account-id-123',
      email: 'test@example.com',
      name: 'Test Instructor',
      institute: 'Example Graduate School',
      country: 'SG',
      status: AccountVerificationRequestStatus.APPROVED,
      comments: '',
      createdAt: 900000,
    },
    {
      accountVerificationRequestId: 'history-rejected-request',
      accountId: 'account-id-123',
      email: 'test@example.com',
      name: 'Test Instructor',
      institute: 'Example Teaching Institute',
      country: 'SG',
      status: AccountVerificationRequestStatus.REJECTED,
      comments: '',
      createdAt: 800000,
    },
    {
      accountVerificationRequestId: 'test-id-123',
      accountId: 'account-id-123',
      email: 'test@example.com',
      name: 'Test Instructor',
      institute: 'Test University',
      country: 'SG',
      status: AccountVerificationRequestStatus.PENDING,
      comments: 'Please verify my account.',
      createdAt: 1000000,
    },
  ],
};

describe('AdminAccountVerificationRequestPageComponent', () => {
  let component: AdminAccountVerificationRequestPageComponent;
  let fixture: ComponentFixture<AdminAccountVerificationRequestPageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;
  let ngbModal: NgbModal;

  async function setup(accountVerificationRequestId = 'test-id-123') {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminAccountVerificationRequestPageComponent);
    fixture.componentRef.setInput('accountVerificationRequestId', accountVerificationRequestId);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    ngbModal = TestBed.inject(NgbModal);
  }

  it('should show invalid state when account verification request API call fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Not found' }, status: 404 })),
    );

    fixture.detectChanges();

    expect(component.isInvalidLink()).toBe(true);
    expect(component.isLoading()).toBe(false);
    expect(component.accountVerificationRequest()).toBeNull();
  });

  it('should render pending request details and enable actions', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));

    fixture.detectChanges();
    expect(accountService.getAccountVerificationRequests).toHaveBeenCalledWith({ accountId: 'account-id-123' });

    expect(fixture.nativeElement.textContent).toContain('Test Instructor');
    expect(fixture.nativeElement.textContent).toContain('test@example.com');
    expect(fixture.nativeElement.textContent).toContain('Test University');
    expect(fixture.nativeElement.textContent).toContain('Please verify my account.');
    expect(fixture.nativeElement.textContent).toContain('Request History For This Account');
    expect(fixture.nativeElement.textContent).toContain('Example Graduate School');
    expect(fixture.nativeElement.textContent).toContain('Test Instructor (test@example.com)');
    expect(fixture.nativeElement.textContent).toContain('Example Teaching Institute');
    expect(fixture.debugElement.queryAll(By.css('a[href*="/web/admin/account-verification-requests/"]')).length).toBe(
      3,
    );
    expect(
      (fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(false);
    expect(
      (fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(false);
    expect(fixture.debugElement.query(By.css('#btn-edit-request-details'))).toBeTruthy();
  });

  it('should disable actions for approved requests', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.APPROVED }),
    );
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));

    fixture.detectChanges();

    expect(
      (fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
    expect(
      (fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
    expect(fixture.debugElement.query(By.css('#btn-edit-request-details'))).toBeFalsy();
  });

  it('should disable actions for rejected requests', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.REJECTED }),
    );
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));

    fixture.detectChanges();

    expect(
      (fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
    expect(
      (fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
    expect(fixture.debugElement.query(By.css('#btn-edit-request-details'))).toBeFalsy();
  });

  it('should enter edit mode and disable approve/reject actions', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));

    fixture.detectChanges();
    (fixture.debugElement.query(By.css('#btn-edit-request-details')).nativeElement as HTMLButtonElement).click();
    fixture.detectChanges();

    expect(component.isEditing()).toBe(true);
    expect(fixture.debugElement.query(By.css('#btn-save-request-details'))).toBeTruthy();
    expect(
      (fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
    expect(
      (fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled,
    ).toBe(true);
  });

  it('should save inline edits and update the request', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    const saveSpy = vi
      .spyOn(accountService, 'editAccountVerificationRequest')
      .mockReturnValue(of({ ...mockPendingRequest, name: 'Updated Instructor', comments: 'Updated comments' }));

    fixture.detectChanges();
    (fixture.debugElement.query(By.css('#btn-edit-request-details')).nativeElement as HTMLButtonElement).click();
    fixture.detectChanges();

    const nameInput = fixture.debugElement.query(By.css('#request-name')).nativeElement as HTMLInputElement;
    nameInput.value = 'Updated Instructor';
    nameInput.dispatchEvent(new Event('input'));

    const commentsInput = fixture.debugElement.query(By.css('#request-comments')).nativeElement as HTMLTextAreaElement;
    commentsInput.value = 'Updated comments';
    commentsInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    (fixture.debugElement.query(By.css('#btn-save-request-details')).nativeElement as HTMLButtonElement).click();
    await vi.waitFor(() => {
      expect(saveSpy).toHaveBeenCalledWith('test-id-123', {
        name: 'Updated Instructor',
        email: 'test@example.com',
        institute: 'Test University',
        country: 'SG',
        status: AccountVerificationRequestStatus.PENDING,
        comments: 'Updated comments',
      });
    });
    fixture.detectChanges();

    expect(component.accountVerificationRequest()?.name).toBe('Updated Instructor');
    expect(component.accountVerificationRequest()?.comments).toBe('Updated comments');
    expect(component.isEditing()).toBe(false);
  });

  it('should remain in edit mode when saving inline edits fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    vi.spyOn(accountService, 'editAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Update failed' }, status: 400 })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    fixture.detectChanges();
    (fixture.debugElement.query(By.css('#btn-edit-request-details')).nativeElement as HTMLButtonElement).click();
    fixture.detectChanges();

    const commentsInput = fixture.debugElement.query(By.css('#request-comments')).nativeElement as HTMLTextAreaElement;
    commentsInput.value = 'Changed before failing save';
    commentsInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    (fixture.debugElement.query(By.css('#btn-save-request-details')).nativeElement as HTMLButtonElement).click();
    await vi.waitFor(() => {
      expect(errorSpy).toHaveBeenCalledWith('Update failed');
    });
    fixture.detectChanges();

    expect(component.isEditing()).toBe(true);
  });

  it('should update state and show success toast on approve', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    vi.spyOn(accountService, 'approveAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.APPROVED }),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast');

    fixture.detectChanges();
    component.approveRequest();

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.APPROVED);
    expect(successSpy).toHaveBeenCalledWith(
      'Account verification request was successfully approved. Email has been sent to test@example.com.',
    );
    expect(component.isApprovingOrRejecting()).toBe(false);
  });

  it('should update state and show success toast on reject', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.REJECTED }),
    );
    vi.spyOn(ngbModal, 'open').mockReturnValue(
      createMockNgbModalRef({}, Promise.resolve({ rejectionType: AccountVerificationRequestRejectionType.OTHERS })),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast');

    fixture.detectChanges();
    component.rejectRequest();
    await vi.waitFor(() => {
      expect(successSpy).toHaveBeenCalledWith('Account verification request was successfully rejected.');
    });

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.REJECTED);
    expect(component.isApprovingOrRejecting()).toBe(false);
  });

  it('should show error toast and reset state when approve fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    vi.spyOn(accountService, 'approveAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Approval failed' }, status: 400 })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    fixture.detectChanges();
    component.approveRequest();

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.PENDING);
    expect(errorSpy).toHaveBeenCalledWith('Approval failed');
    expect(component.isApprovingOrRejecting()).toBe(false);
  });

  it('should show error toast and reset state when reject fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of(mockHistoricalRequests));
    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Rejection failed' }, status: 400 })),
    );
    vi.spyOn(ngbModal, 'open').mockReturnValue(
      createMockNgbModalRef({}, Promise.resolve({ rejectionType: AccountVerificationRequestRejectionType.OTHERS })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    fixture.detectChanges();
    component.rejectRequest();
    await vi.waitFor(() => {
      expect(errorSpy).toHaveBeenCalledWith('Rejection failed');
    });

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.PENDING);
    expect(component.isApprovingOrRejecting()).toBe(false);
  });

  it('should render demo course timestamp only when present', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, createdDemoCourseAt: 2000000 }),
    );
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of({ accountVerificationRequests: [] }));

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Demo Course Created');
  });

  it('should not render demo course timestamp when absent', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'getAccountVerificationRequests').mockReturnValue(of({ accountVerificationRequests: [] }));

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Demo Course Created');
  });
});
