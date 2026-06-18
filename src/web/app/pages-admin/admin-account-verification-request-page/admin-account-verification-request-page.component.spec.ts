import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';
import { AdminAccountVerificationRequestPageComponent } from './admin-account-verification-request-page.component';

const mockPendingRequest: AccountVerificationRequest = {
  accountVerificationRequestId: 'test-id-123',
  email: 'test@example.com',
  name: 'Test Instructor',
  institute: 'Test University',
  country: 'SG',
  status: AccountVerificationRequestStatus.PENDING,
  comments: 'Please verify my account.',
  createdAt: 1000000,
};

describe('AdminAccountVerificationRequestPageComponent', () => {
  let component: AdminAccountVerificationRequestPageComponent;
  let fixture: ComponentFixture<AdminAccountVerificationRequestPageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;

  async function setup(accountVerificationRequestId = 'test-id-123') {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminAccountVerificationRequestPageComponent);
    fixture.componentRef.setInput('accountVerificationRequestId', accountVerificationRequestId);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
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

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Test Instructor');
    expect(fixture.nativeElement.textContent).toContain('test@example.com');
    expect(fixture.nativeElement.textContent).toContain('Test University');
    expect(fixture.nativeElement.textContent).toContain('Please verify my account.');
    expect(fixture.nativeElement.textContent).toContain('Request History For This Account');
    expect(fixture.nativeElement.textContent).toContain('Mocked for now until the request-history endpoint is available.');
    expect(fixture.nativeElement.textContent).toContain('Example Graduate School');
    expect(fixture.nativeElement.textContent).toContain('Example Teaching Institute');
    expect((fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(false);
    expect((fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(false);
  });

  it('should disable actions for approved requests', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.APPROVED }),
    );

    fixture.detectChanges();

    expect((fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(true);
    expect((fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(true);
  });

  it('should disable actions for rejected requests', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.REJECTED }),
    );

    fixture.detectChanges();

    expect((fixture.debugElement.query(By.css('#btn-approve-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(true);
    expect((fixture.debugElement.query(By.css('#btn-reject-request')).nativeElement as HTMLButtonElement).disabled)
      .toBe(true);
  });

  it('should update state and show success toast on approve', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
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
    expect(component.isApproving()).toBe(false);
  });

  it('should update state and show success toast on reject', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, status: AccountVerificationRequestStatus.REJECTED }),
    );
    const successSpy = vi.spyOn(statusMessageService, 'showSuccessToast');

    fixture.detectChanges();
    component.rejectRequest();

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.REJECTED);
    expect(successSpy).toHaveBeenCalledWith('Account verification request was successfully rejected.');
    expect(component.isRejecting()).toBe(false);
  });

  it('should show error toast and reset state when approve fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'approveAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Approval failed' }, status: 400 })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    fixture.detectChanges();
    component.approveRequest();

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.PENDING);
    expect(errorSpy).toHaveBeenCalledWith('Approval failed');
    expect(component.isApproving()).toBe(false);
  });

  it('should show error toast and reset state when reject fails', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));
    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(
      throwError(() => ({ error: { message: 'Rejection failed' }, status: 400 })),
    );
    const errorSpy = vi.spyOn(statusMessageService, 'showErrorToast');

    fixture.detectChanges();
    component.rejectRequest();

    expect(component.accountVerificationRequest()?.status).toBe(AccountVerificationRequestStatus.PENDING);
    expect(errorSpy).toHaveBeenCalledWith('Rejection failed');
    expect(component.isRejecting()).toBe(false);
  });

  it('should render demo course timestamp only when present', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(
      of({ ...mockPendingRequest, createdDemoCourseAt: 2000000 }),
    );

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Demo Course Created');
  });

  it('should not render demo course timestamp when absent', async () => {
    await setup();
    vi.spyOn(accountService, 'getAccountVerificationRequest').mockReturnValue(of(mockPendingRequest));

    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('Demo Course Created');
  });
});
