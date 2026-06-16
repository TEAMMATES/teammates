import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { AccountVerificationRequestTableRowModel } from './account-verification-request-table-model';
import { AccountVerificationRequestTableComponent } from './account-verification-request-table.component';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import { RejectWithReasonModalComponent } from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

describe('AccountVerificationRequestTableComponent', () => {
  let component: AccountVerificationRequestTableComponent;
  let fixture: ComponentFixture<AccountVerificationRequestTableComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  const accountVerificationRequestDetailsBuilder = createBuilder<AccountVerificationRequestTableRowModel>({
    id: '',
    email: '',
    name: '',
    institute: '',
    country: '',
    registrationLink: '',
    status: AccountVerificationRequestStatus.PENDING,
    comments: '',
    createdDemoCourseAtText: '',
    createdAtText: '',
    showLinks: false,
  });

  const DEFAULT_ACCOUNT_REQUEST = accountVerificationRequestDetailsBuilder
    .email('email')
    .name('name')
    .status(AccountVerificationRequestStatus.PENDING)
    .institute('institute')
    .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
    .comments('comment');

  const deleteModalContent = `Are you sure you want to <strong>delete</strong> the account verification request for
        <strong>name</strong> with email <strong>email</strong> from
        <strong>institute</strong>?`;
  const deleteModalTitle = 'Delete account verification request for <strong>name</strong>?';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountVerificationRequestTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    simpleModalService = TestBed.inject(SimpleModalService);
    ngbModal = TestBed.inject(NgbModal);
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

  it('should show success message when deleting account verification request is successful', () => {
    component.accountVerificationRequests = [DEFAULT_ACCOUNT_REQUEST.build()];
    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    vi.spyOn(accountService, 'deleteAccountVerificationRequest').mockReturnValue(
      of({
        message: 'Account verification request successfully deleted.',
      }),
    );

    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Account verification request successfully deleted.');
      });

    const deleteButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      '#delete-account-verification-request-0',
    );
    deleteButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
  });

  it('should show error message when deleting account verification request is unsuccessful', () => {
    component.accountVerificationRequests = [DEFAULT_ACCOUNT_REQUEST.build()];

    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    vi.spyOn(accountService, 'deleteAccountVerificationRequest').mockReturnValue(
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

    const deleteButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      '#delete-account-verification-request-0',
    );
    deleteButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
  });

  it('should display comment modal', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    const viewCommentButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      '#view-account-verification-request-0',
    );
    viewCommentButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(
      'Comments for <strong>name</strong> Request',
      SimpleModalType.INFO,
      '<strong>Comment:</strong> comment',
    );
  });

  it('should display edit modal when edit button is clicked', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountVerificationRequestName: 'name',
      accountVerificationRequestEmail: 'email',
      accountVerificationRequestInstitution: 'institute',
      accountVerificationRequestComment: 'comment',
    };
    const mockAccountVerificationRequest: AccountVerificationRequest = {
      accountVerificationRequestId: '',
      email: 'email',
      name: 'name',
      institute: 'institute',
      country: '',
      status: AccountVerificationRequestStatus.PENDING,
      createdAt: 0,
      comments: 'comment',
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));
    vi.spyOn(accountService, 'editAccountVerificationRequest').mockReturnValue(of(mockAccountVerificationRequest));

    const editButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      '#edit-account-verification-request-0',
    );
    editButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);
  });

  it('should display reject modal when reject button is clicked', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const resultData = {
      rejectionReasonTitle: 'Title',
      rejectionReasonBody: 'Body',
    };
    const mockAccountVerificationRequest: AccountVerificationRequest = {
      accountVerificationRequestId: '',
      email: 'email',
      name: 'name',
      institute: 'institute',
      country: '',
      status: AccountVerificationRequestStatus.REJECTED,
      createdAt: 0,
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));
    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(of(mockAccountVerificationRequest));

    const rejectButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#reject-request-with-reason-0');
    rejectButton.click();
    fixture.detectChanges();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(RejectWithReasonModalComponent);
  });

  it('should display error message when rejection was unsuccessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const resultData = {
      rejectionReasonTitle: 'Title',
      rejectionReasonBody: 'Body',
    };
    vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(
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

    const rejectButton = fixture.debugElement.nativeElement.querySelector('#reject-request-0');
    rejectButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display error message when approval was unsuccessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
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
      '#approve-account-verification-request-0',
    );
    approveButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display error message when edit was unsuccessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountVerificationRequestName: 'name',
      accountVerificationRequestEmail: 'email',
      accountVerificationRequestInstitution: 'institute',
      accountVerificationRequestComment: 'comment',
    };
    vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    vi.spyOn(accountService, 'editAccountVerificationRequest').mockReturnValue(
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

    const editButton = fixture.debugElement.nativeElement.querySelector('#edit-account-verification-request-0');
    editButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should update request when edit is succcessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountVerificationRequestName: 'name',
      accountVerificationRequestEmail: 'new email',
      accountVerificationRequestInstitution: 'new institute',
      accountVerificationRequestComment: 'new comment',
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    const editedAccountVerificationRequest: AccountVerificationRequest = {
      accountVerificationRequestId: 'id',
      comments: 'new comment',
      email: 'new email',
      institute: 'new institute',
      country: '',

      name: 'new name',
      createdAt: 1,
      status: AccountVerificationRequestStatus.PENDING,
    };

    vi.spyOn(accountService, 'editAccountVerificationRequest').mockReturnValue(of(editedAccountVerificationRequest));

    const editButton: HTMLElement = fixture.debugElement.nativeElement.querySelector(
      '#edit-account-verification-request-0',
    );
    editButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);

    fixture.detectChanges();
    expect(component.accountVerificationRequests[0].comments).toEqual('new comment');
    expect(component.accountVerificationRequests[0].email).toEqual('new email');
    expect(component.accountVerificationRequests[0].institute).toEqual('new institute');
    expect(component.accountVerificationRequests[0].name).toEqual('new name');
  });

  it('should update status when approval is succcessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const approvedRequest: AccountVerificationRequest = {
      accountVerificationRequestId: component.accountVerificationRequests[0].id,
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
      '#approve-account-verification-request-0',
    );
    approveButton.click();

    fixture.detectChanges();
    expect(component.accountVerificationRequests[0].status).toEqual(AccountVerificationRequestStatus.APPROVED);
  });

  it('should update status when rejection is succcessful', () => {
    const accountVerificationRequestResults: AccountVerificationRequestTableRowModel[] = [
      DEFAULT_ACCOUNT_REQUEST.build(),
    ];

    component.accountVerificationRequests = accountVerificationRequestResults;
    fixture.detectChanges();

    const rejectedRequest: AccountVerificationRequest = {
      accountVerificationRequestId: component.accountVerificationRequests[0].id,
      comments: component.accountVerificationRequests[0].comments,
      email: component.accountVerificationRequests[0].email,
      institute: component.accountVerificationRequests[0].institute,
      country: '',

      name: component.accountVerificationRequests[0].name,
      createdAt: 1,
      status: AccountVerificationRequestStatus.REJECTED,
    };

    vi.spyOn(accountService, 'rejectAccountVerificationRequest').mockReturnValue(of(rejectedRequest));

    const rejectButton = fixture.debugElement.nativeElement.querySelector('#reject-request-0');
    rejectButton.click();

    fixture.detectChanges();
    expect(component.accountVerificationRequests[0].status).toEqual(AccountVerificationRequestStatus.REJECTED);
  });
});
