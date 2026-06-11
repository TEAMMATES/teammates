import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AccountRequestTableComponent } from './account-request-table.component';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import { RejectWithReasonModalComponent } from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AccountRequest, AccountRequestStatus } from '../../../types/api-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

describe('AccountRequestTableComponent', () => {
  let component: AccountRequestTableComponent;
  let fixture: ComponentFixture<AccountRequestTableComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  const accountRequestDetailsBuilder = createBuilder<AccountRequestTableRowModel>({
    id: '',
    email: '',
    name: '',
    institute: '',
    country: '',
    registrationLink: '',
    status: AccountRequestStatus.PENDING,
    comments: '',
    registeredAtText: '',
    createdAtText: '',
    showLinks: false,
  });

  const DEFAULT_ACCOUNT_REQUEST = accountRequestDetailsBuilder
    .email('email')
    .name('name')
    .status(AccountRequestStatus.PENDING)
    .institute('institute')
    .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
    .comments('comment');

  const deleteModalContent = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>name</strong> with email <strong>email</strong> from
        <strong>institute</strong>?`;
  const deleteModalTitle = 'Delete account request for <strong>name</strong>?';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountRequestTableComponent);
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

  it('should snap with an account requests table', () => {
    const accountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
    component.accountRequests = [accountRequestResult];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display account requests table', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'id-1' },
      { ...DEFAULT_ACCOUNT_REQUEST.build(), id: 'id-2' },
    ];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should show success message when deleting account request is successful', () => {
    component.accountRequests = [DEFAULT_ACCOUNT_REQUEST.build()];
    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    vi.spyOn(accountService, 'deleteAccountRequest').mockReturnValue(
      of({
        message: 'Account request successfully deleted.',
      }),
    );

    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Account request successfully deleted.');
      });

    const deleteButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#delete-account-request-0');
    deleteButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
  });

  it('should show error message when deleting account request is unsuccessful', () => {
    component.accountRequests = [DEFAULT_ACCOUNT_REQUEST.build()];

    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    vi.spyOn(accountService, 'deleteAccountRequest').mockReturnValue(
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

    const deleteButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#delete-account-request-0');
    deleteButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
  });

  it('should display comment modal', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    const viewCommentButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#view-account-request-0');
    viewCommentButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(
      'Comments for <strong>name</strong> Request',
      SimpleModalType.INFO,
      '<strong>Comment:</strong> comment',
    );
  });

  it('should display edit modal when edit button is clicked', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountRequestName: 'name',
      accountRequestEmail: 'email',
      accountRequestInstitution: 'institute',
      accountRequestComment: 'comment',
    };
    const mockAccountRequest: AccountRequest = {
      accountRequestId: '',
      email: 'email',
      name: 'name',
      institute: 'institute',
      country: '',
      registrationKey: '',
      status: AccountRequestStatus.PENDING,
      createdAt: 0,
      comments: 'comment',
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));
    vi.spyOn(accountService, 'editAccountRequest').mockReturnValue(of(mockAccountRequest));

    const editButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
    editButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);
  });

  it('should display reject modal when reject button is clicked', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const resultData = {
      rejectionReasonTitle: 'Title',
      rejectionReasonBody: 'Body',
    };
    const mockAccountRequest: AccountRequest = {
      accountRequestId: '',
      email: 'email',
      name: 'name',
      institute: 'institute',
      country: '',
      registrationKey: '',
      status: AccountRequestStatus.REJECTED,
      createdAt: 0,
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));
    vi.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(of(mockAccountRequest));

    const rejectButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#reject-request-with-reason-0');
    rejectButton.click();
    fixture.detectChanges();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(RejectWithReasonModalComponent);
  });

  it('should display error message when rejection was unsuccessful', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const resultData = {
      rejectionReasonTitle: 'Title',
      rejectionReasonBody: 'Body',
    };
    vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    vi.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(
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
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    vi.spyOn(accountService, 'approveAccountRequest').mockReturnValue(
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

    const approveButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#approve-account-request-0');
    approveButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display error message when edit was unsuccessful', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountRequestName: 'name',
      accountRequestEmail: 'email',
      accountRequestInstitution: 'institute',
      accountRequestComment: 'comment',
    };
    vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    vi.spyOn(accountService, 'editAccountRequest').mockReturnValue(
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

    const editButton = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
    editButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should update request when edit is succcessful', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const resultData = {
      accountRequestName: 'name',
      accountRequestEmail: 'new email',
      accountRequestInstitution: 'new institute',
      accountRequestComment: 'new comment',
    };
    const modalSpy = vi.spyOn(ngbModal, 'open').mockReturnValue(createMockNgbModalRef({}, Promise.resolve(resultData)));

    const editedAccountRequest: AccountRequest = {
      accountRequestId: 'id',
      comments: 'new comment',
      email: 'new email',
      institute: 'new institute',
      country: '',
      registrationKey: 'registration key',
      name: 'new name',
      createdAt: 1,
      status: AccountRequestStatus.PENDING,
    };

    vi.spyOn(accountService, 'editAccountRequest').mockReturnValue(of(editedAccountRequest));

    const editButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
    editButton.click();
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);

    fixture.detectChanges();
    expect(component.accountRequests[0].comments).toEqual('new comment');
    expect(component.accountRequests[0].email).toEqual('new email');
    expect(component.accountRequests[0].institute).toEqual('new institute');
    expect(component.accountRequests[0].name).toEqual('new name');
  });

  it('should update status when approval is succcessful', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const approvedRequest: AccountRequest = {
      accountRequestId: component.accountRequests[0].id,
      comments: component.accountRequests[0].comments,
      email: component.accountRequests[0].email,
      institute: component.accountRequests[0].institute,
      country: '',
      registrationKey: 'registration key',
      name: component.accountRequests[0].name,
      createdAt: 1,
      status: AccountRequestStatus.APPROVED,
    };

    vi.spyOn(accountService, 'approveAccountRequest').mockReturnValue(of(approvedRequest));

    const approveButton: HTMLElement = fixture.debugElement.nativeElement.querySelector('#approve-account-request-0');
    approveButton.click();

    fixture.detectChanges();
    expect(component.accountRequests[0].status).toEqual(AccountRequestStatus.APPROVED);
  });

  it('should update status when rejection is succcessful', () => {
    const accountRequestResults: AccountRequestTableRowModel[] = [DEFAULT_ACCOUNT_REQUEST.build()];

    component.accountRequests = accountRequestResults;
    fixture.detectChanges();

    const rejectedRequest: AccountRequest = {
      accountRequestId: component.accountRequests[0].id,
      comments: component.accountRequests[0].comments,
      email: component.accountRequests[0].email,
      institute: component.accountRequests[0].institute,
      country: '',
      registrationKey: 'registration key',
      name: component.accountRequests[0].name,
      createdAt: 1,
      status: AccountRequestStatus.REJECTED,
    };

    vi.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(of(rejectedRequest));

    const rejectButton = fixture.debugElement.nativeElement.querySelector('#reject-request-0');
    rejectButton.click();

    fixture.detectChanges();
    expect(component.accountRequests[0].status).toEqual(AccountRequestStatus.REJECTED);
  });
});
