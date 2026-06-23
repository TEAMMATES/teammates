import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AccountVerificationRequestTableRowModel } from './account-verification-request-table-model';
import { AccountVerificationRequestTableComponent } from './account-verification-request-table.component';
import { AccountService } from '../../../services/account.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';

describe('AccountVerificationRequestTableComponent', () => {
  let component: AccountVerificationRequestTableComponent;
  let fixture: ComponentFixture<AccountVerificationRequestTableComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;
  let navigationService: NavigationService;

  const accountVerificationRequestDetailsBuilder = createBuilder<AccountVerificationRequestTableRowModel>({
    id: '',
    accountId: '',
    email: '',
    name: '',
    institute: '',
    country: '',
    status: AccountVerificationRequestStatus.PENDING,
    comments: '',
    createdDemoCourseAtText: '',
    createdAtText: '',
  });

  const DEFAULT_ACCOUNT_REQUEST = accountVerificationRequestDetailsBuilder
    .email('email')
    .name('name')
    .status(AccountVerificationRequestStatus.PENDING)
    .institute('institute')
    .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
    .comments('comment');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountVerificationRequestTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    navigationService = TestBed.inject(NavigationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with an account verification requests table', () => {
    const accountVerificationRequestResult: AccountVerificationRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
    component.accountVerificationRequests = [accountVerificationRequestResult];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display account verification requests table', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'id-1' },
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'id-2' },
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display error message when approval was unsuccessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'test-id-error' },
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    vi.spyOn(accountService, 'approveAccountVerificationRequest').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const approveButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      `#approve-account-verification-request-${accountVerificationRequestResults[0].id}`,
    );
    approveButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should update status when approval is succcessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'test-id-approve' },
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const approvedRequest: AccountVerificationRequest = {
      accountVerificationRequestId: component.accountVerificationRequests[0].id,
      accountId: component.accountVerificationRequests[0].accountId,
      comments: component.accountVerificationRequests[0].comments,
      email: component.accountVerificationRequests[0].email,
      institute: component.accountVerificationRequests[0].institute,
      country: '',
      name: component.accountVerificationRequests[0].name,
      createdAt: 1,
      status: AccountVerificationRequestStatus.APPROVED,
    };

    vi.spyOn(accountService, 'approveAccountVerificationRequest').mockReturnValue(of(approvedRequest));

    const approveButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      `#approve-account-verification-request-${accountVerificationRequestResults[0].id}`,
    );
    approveButton.click();

    fixture.detectChanges();
    expect(component.accountVerificationRequests[0].status).toEqual(AccountVerificationRequestStatus.APPROVED);
  });

  it('should navigate to account verification request detail page when review button is clicked', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'test-id-123' },
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const navigationSpy = vi.spyOn(navigationService, 'navigateByURL').mockResolvedValue(true);

    const reviewButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      `#review-account-verification-request-${accountVerificationRequestResults[0].id}`,
    );
    reviewButton.click();

    expect(navigationSpy).toHaveBeenCalledWith('/web/admin/account-verification-requests/test-id-123');
  });
});
