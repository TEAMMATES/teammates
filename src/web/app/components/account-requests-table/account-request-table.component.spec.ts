import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AccountRequestTableComponent } from './account-request-table.component';
import { AccountRequestTableModule } from './account-request-table.module';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AccountRequestStatus } from '../../../types/api-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

describe('AccountRequestTableComponent', () => {
    let component: AccountRequestTableComponent;
    let fixture: ComponentFixture<AccountRequestTableComponent>;
    let accountService: AccountService;
    let statusMessageService: StatusMessageService;
    let simpleModalService: SimpleModalService;

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

    const DEFAULT_ACCOUNT_REQUEST_PENDING = accountRequestDetailsBuilder
        .email('email')
        .name('name')
        .status(AccountRequestStatus.PENDING)
        .instituteAndCountry('institute')
        .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
        .comments('comment');

    const DEFAULT_ACCOUNT_REQUEST_APPROVED = accountRequestDetailsBuilder
        .email('email')
        .name('name')
        .status(AccountRequestStatus.APPROVED)
        .registrationLink('registrationLink')
        .instituteAndCountry('institute')
        .createdAtText('createdTime')
        .comments('comment');

    const DEFAULT_ACCOUNT_REQUEST_REGISTERED = accountRequestDetailsBuilder
        .email('email')
        .name('name')
        .status(AccountRequestStatus.REGISTERED)
        .registrationLink('registrationLink')
        .instituteAndCountry('institute')
        .registeredAtText('registeredTime')
        .createdAtText('createdTime')
        .comments('comment');

    const DEFAULT_ACCOUNT_REQUEST_REJECTED = accountRequestDetailsBuilder
        .email('email')
        .name('name')
        .status(AccountRequestStatus.REJECTED)
        .registrationLink('registrationLink')
        .instituteAndCountry('institute')
        .createdAtText('createdTime')
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
        fixture.detectChanges();
      });

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should snap with an expanded account requests table', () => {
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
        ];

        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should show account request links when expand all button clicked', () => {
        const accountRequestResult: AccountRequestTableRowModel = DEFAULT_ACCOUNT_REQUEST_APPROVED.build();
        component.accountRequests = [accountRequestResult];
        component.searchString = 'test';
        fixture.detectChanges();

        const button: any = fixture.debugElement.nativeElement.querySelector('#show-account-request-links');
        button.click();
        expect(component.accountRequests[0].showLinks).toEqual(true);
      });

      it('should display account requests with no reset or expand links button', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
        ];

        component.accountRequests = accountRequestResults;

        expect(component.accountRequests.length).toEqual(2);
        expect(component.accountRequests).toEqual(accountRequestResults);
        expect(component.accountRequests[0].showLinks).toEqual(false);
        expect(component.accountRequests[1].showLinks).toEqual(false);

        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should display account requests with reset button and expandable links buttons',
      () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            DEFAULT_ACCOUNT_REQUEST_APPROVED.build(),
            DEFAULT_ACCOUNT_REQUEST_REJECTED.build(),
        ];

        component.accountRequests = accountRequestResults;
        component.searchString = 'test';

        expect(component.accountRequests.length).toEqual(2);
        expect(component.accountRequests).toEqual(accountRequestResults);
        expect(component.accountRequests[0].showLinks).toEqual(false);
        expect(component.accountRequests[1].showLinks).toEqual(false);

        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should show success message when deleting account request is successful', () => {
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
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
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
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
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST_REGISTERED.build(),
        ];

        component.searchString = 'test';
        fixture.detectChanges();

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
            return createMockNgbModalRef({});
        });

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
        component.accountRequests = [
            DEFAULT_ACCOUNT_REQUEST_REGISTERED.build(),
        ];
        component.searchString = 'test';
        fixture.detectChanges();

        const modalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
          return createMockNgbModalRef({});
        });

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
            DEFAULT_ACCOUNT_REQUEST_PENDING.build(),
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
});
