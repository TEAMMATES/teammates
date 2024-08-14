import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AccountRequestTableComponent } from './account-request-table.component';
import { AccountRequestTableModule } from './account-request-table.module';
import { EditRequestModalComponent } from './admin-edit-request-modal/admin-edit-request-modal.component';
import {
  RejectWithReasonModalComponent,
} from './admin-reject-with-reason-modal/admin-reject-with-reason-modal.component';
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
        instituteAndCountry: '',
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
        .instituteAndCountry('institute')
        .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
        .comments('comment');

    const resetModalContent = `Are you sure you want to reset the account request for
        <strong>name</strong> with email <strong>email</strong> from
        <strong>institute</strong>?
        An email with the account registration link will also be sent to the instructor.`;
    const resetModalTitle = 'Reset account request for <strong>name</strong>?';
    const deleteModalContent = `Are you sure you want to <strong>delete</strong> the account request for
        <strong>name</strong> with email <strong>email</strong> from
        <strong>institute</strong>?`;
    const deleteModalTitle = 'Delete account request for <strong>name</strong>?';

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [AccountRequestTableComponent],
            imports: [
                AccountRequestTableModule,
                BrowserAnimationsModule,
                HttpClientTestingModule,
            ],
            providers: [
                AccountService, SimpleModalService,
            ],
        }).compileComponents();
    }));

    beforeEach(() => {
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

      it('should snap with an expanded account requests table', () => {
        const accountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        component.accountRequests = [
          accountRequestResult,
        ];

        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should show account request links when expand all button clicked', () => {
        const accountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        accountRequestResult.status = AccountRequestStatus.APPROVED;
        accountRequestResult.registrationLink = 'registrationLink';
        component.accountRequests = [
          accountRequestResult,
        ];
        component.searchString = 'test';
        fixture.detectChanges();

        const button: any = fixture.debugElement.nativeElement.querySelector('#show-account-request-links');
        button.click();
        expect(component.accountRequests[0].showLinks).toEqual(true);
      });

      it('should display account requests with no reset or expand links button', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            DEFAULT_ACCOUNT_REQUEST.build(),
            DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should display account requests with reset button and expandable links buttons',
      () => {
        const approvedAccountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        approvedAccountRequestResult.status = AccountRequestStatus.APPROVED;
        approvedAccountRequestResult.registrationLink = 'registrationLink';

        const registeredAccountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        registeredAccountRequestResult.status = AccountRequestStatus.REGISTERED;
        registeredAccountRequestResult.registrationLink = 'registrationLink';

        const accountRequestResults: AccountRequestTableRowModel[] = [
            approvedAccountRequestResult,
            registeredAccountRequestResult,
        ];

        component.accountRequests = accountRequestResults;
        component.searchString = 'test';
        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should show success message when deleting account request is successful', () => {
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST.build(),
        ];
        fixture.detectChanges();

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
            return createMockNgbModalRef({});
        });

        jest.spyOn(accountService, 'deleteAccountRequest').mockReturnValue(of({
          message: 'Account request successfully deleted.',
        }));

        const spyStatusMessageService: any = jest.spyOn(statusMessageService, 'showSuccessToast')
            .mockImplementation((args: string) => {
              expect(args).toEqual('Account request successfully deleted.');
            });

        const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-account-request-0');
        deleteButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
      });

      it('should show error message when deleting account request is unsuccessful', () => {
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        fixture.detectChanges();

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
            return createMockNgbModalRef({});
        });

        jest.spyOn(accountService, 'deleteAccountRequest').mockReturnValue(throwError(() => ({
          error: {
            message: 'This is the error message.',
          },
        })));

        const spyStatusMessageService: any = jest.spyOn(statusMessageService, 'showErrorToast')
          .mockImplementation((args: string) => {
            expect(args).toEqual('This is the error message.');
          });

        const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-account-request-0');
        deleteButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(deleteModalTitle, SimpleModalType.DANGER, deleteModalContent);
      });

      it('should show success message when resetting account request is successful', () => {
        const registeredAccountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        registeredAccountRequestResult.status = AccountRequestStatus.REGISTERED;
        registeredAccountRequestResult.registrationLink = 'registrationLink';
        registeredAccountRequestResult.registeredAtText = 'registeredTime';
        component.accountRequests = [
            registeredAccountRequestResult,
        ];

        component.searchString = 'test';
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
          dismissed: {
            subscribe: jest.fn(),
          },
        };

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef as any);

        jest.spyOn(accountService, 'resetAccountRequest').mockReturnValue(of({
          joinLink: 'joinlink',
        }));

        const spyStatusMessageService = jest.spyOn(statusMessageService, 'showSuccessToast')
          .mockImplementation((args: string) => {
            expect(args)
                .toEqual('Reset successful. An email has been sent to email.');
          });

        const resetButton = fixture.debugElement.nativeElement.querySelector('#reset-account-request-0');
        resetButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(resetModalTitle, SimpleModalType.WARNING, resetModalContent);
      });

      it('should show error message when resetting account request is unsuccessful', () => {
        const registeredAccountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST.build();
        registeredAccountRequestResult.status = AccountRequestStatus.REGISTERED;
        registeredAccountRequestResult.registrationLink = 'registrationLink';
        registeredAccountRequestResult.registeredAtText = 'registeredTime';
        component.accountRequests = [
            registeredAccountRequestResult,
        ];

        component.searchString = 'test';
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
          dismissed: {
            subscribe: jest.fn(),
          },
        };

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef as any);

        jest.spyOn(accountService, 'resetAccountRequest').mockReturnValue(throwError(() => ({
          error: {
            message: 'This is the error message.',
          },
        })));

        const spyStatusMessageService = jest.spyOn(statusMessageService, 'showErrorToast')
          .mockImplementation((args: string) => {
            expect(args).toEqual('This is the error message.');
          });

        const resetButton = fixture.debugElement.nativeElement.querySelector('#reset-account-request-0');
        resetButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(resetModalTitle, SimpleModalType.WARNING, resetModalContent);
      });

      it('should display comment modal', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const modalSpy = jest.spyOn(simpleModalService, 'openInformationModal')
            .mockReturnValue(createMockNgbModalRef());

        const viewCommentButton: any = fixture.debugElement.nativeElement.querySelector('#view-account-request-0');
        viewCommentButton.click();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith('Comments for <strong>name</strong> Request',
            SimpleModalType.INFO, '<strong>Comment:</strong> comment');
      });

      it('should display edit modal when edit button is clicked', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
        };

        const modalSpy = jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

        const editButton: any = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
        editButton.click();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);
      });

      it('should display reject modal when reject button is clicked', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
          dismissed: {
            subscribe: jest.fn(),
          },
        };

        const modalSpy = jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

        const rejectButton: any = fixture.debugElement.nativeElement.querySelector('#reject-request-with-reason-0');
        rejectButton.click();
        fixture.detectChanges();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(RejectWithReasonModalComponent);
      });

      it('should display error message when rejection was unsuccessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        jest.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(throwError(() => ({
          error: {
            message: 'This is the error message.',
          },
        })));

        const spyStatusMessageService = jest.spyOn(statusMessageService, 'showErrorToast')
          .mockImplementation((args: string) => {
            expect(args).toEqual('This is the error message.');
          });

        const rejectButton = fixture.debugElement.nativeElement.querySelector('#reject-request-0');
        rejectButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
      });

      it('should display error message when approval was unsuccessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        jest.spyOn(accountService, 'approveAccountRequest').mockReturnValue(throwError(() => ({
          error: {
            message: 'This is the error message.',
          },
        })));

        const spyStatusMessageService: any = jest.spyOn(statusMessageService, 'showErrorToast')
          .mockImplementation((args: string) => {
            expect(args).toEqual('This is the error message.');
        });

        const approveButton: any = fixture.debugElement.nativeElement.querySelector('#approve-account-request-0');
        approveButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
      });

      it('should display error message when edit was unsuccessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
        };

        jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

        jest.spyOn(accountService, 'editAccountRequest').mockReturnValue(throwError(() => ({
          error: {
            message: 'This is the error message.',
          },
        })));

        const spyStatusMessageService = jest.spyOn(statusMessageService, 'showErrorToast')
          .mockImplementation((args: string) => {
            expect(args).toEqual('This is the error message.');
          });

        const editButton = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
        editButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
      });

      it('should update request when edit is succcessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const mockModalRef = {
          componentInstance: {},
          result: Promise.resolve({}),
        };

        const modalSpy = jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

        const editedAccountRequest : AccountRequest = {
          id: 'id',
          comments: 'new comment',
          email: 'new email',
          institute: 'new institute',
          registrationKey: 'registration key',
          name: 'new name',
          createdAt: 1,
          status: AccountRequestStatus.PENDING,
        };

        jest.spyOn(accountService, 'editAccountRequest').mockReturnValue(of(editedAccountRequest));

        const editButton: any = fixture.debugElement.nativeElement.querySelector('#edit-account-request-0');
        editButton.click();
        expect(modalSpy).toHaveBeenCalledTimes(1);
        expect(modalSpy).toHaveBeenCalledWith(EditRequestModalComponent);

        fixture.detectChanges();
        expect(component.accountRequests[0].comments).toEqual('new comment');
        expect(component.accountRequests[0].email).toEqual('new email');
        expect(component.accountRequests[0].instituteAndCountry).toEqual('new institute');
        expect(component.accountRequests[0].name).toEqual('new name');
      });

      it('should update status when approval is succcessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const approvedRequest : AccountRequest = {
          id: component.accountRequests[0].id,
          comments: component.accountRequests[0].comments,
          email: component.accountRequests[0].email,
          institute: component.accountRequests[0].instituteAndCountry,
          registrationKey: 'registration key',
          name: component.accountRequests[0].name,
          createdAt: 1,
          status: AccountRequestStatus.APPROVED,
        };

        jest.spyOn(accountService, 'approveAccountRequest').mockReturnValue(of(approvedRequest));

        const approveButton: any = fixture.debugElement.nativeElement.querySelector('#approve-account-request-0');
        approveButton.click();

        fixture.detectChanges();
        expect(component.accountRequests[0].status).toEqual(AccountRequestStatus.APPROVED);
      });

      it('should update status when rejection is succcessful', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
          DEFAULT_ACCOUNT_REQUEST.build(),
        ];

        component.accountRequests = accountRequestResults;
        fixture.detectChanges();

        const rejectedRequest : AccountRequest = {
          id: component.accountRequests[0].id,
          comments: component.accountRequests[0].comments,
          email: component.accountRequests[0].email,
          institute: component.accountRequests[0].instituteAndCountry,
          registrationKey: 'registration key',
          name: component.accountRequests[0].name,
          createdAt: 1,
          status: AccountRequestStatus.REJECTED,
        };

        jest.spyOn(accountService, 'rejectAccountRequest').mockReturnValue(of(rejectedRequest));

        const rejectButton: any = fixture.debugElement.nativeElement.querySelector('#reject-request-0');
        rejectButton.click();

        fixture.detectChanges();
        expect(component.accountRequests[0].status).toEqual(AccountRequestStatus.REJECTED);
      });
});
