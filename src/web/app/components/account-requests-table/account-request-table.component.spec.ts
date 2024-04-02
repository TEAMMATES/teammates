import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import { AccountRequestTableRowModel } from './account-request-table-model';
import { AccountRequestTableComponent } from './account-request-table.component';
import { AccountRequestTableModule } from './account-request-table.module';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AccountRequestStatus } from '../../../types/api-output';

describe('AccountRequestTableComponent', () => {
    let component: AccountRequestTableComponent;
    let fixture: ComponentFixture<AccountRequestTableComponent>;
    let accountService: AccountService;
    let statusMessageService: StatusMessageService;
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

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [AccountRequestTableComponent],
            imports: [
                AccountRequestTableModule,
                BrowserAnimationsModule,
                HttpClientTestingModule,
            ],
            providers: [
                AccountService, NgbModal,
            ],
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AccountRequestTableComponent);
        component = fixture.componentInstance;
        accountService = TestBed.inject(AccountService);
        statusMessageService = TestBed.inject(StatusMessageService);
        ngbModal = TestBed.inject(NgbModal);
        fixture.detectChanges();
      });

      it('should create', () => {
        expect(component).toBeTruthy();
      });

      it('should snap with an expanded account requests table', () => {
        component.accountRequests = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.PENDING)
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .build(),
        ];

        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

      it('should show account request links when expand all button clicked', () => {
        const accountRequestResult: AccountRequestTableRowModel = accountRequestDetailsBuilder
                                                                    .id('1234')
                                                                    .email('test@email.com')
                                                                    .name('John Doe')
                                                                    .instituteAndCountry('NUS')
                                                                    .registrationLink('regLink1')
                                                                    .status(AccountRequestStatus.APPROVED)
                                                                    .build();
        component.accountRequests = [accountRequestResult];
        component.searchString = 'test'; // To allow expandable links
        fixture.detectChanges();

        const button: any = fixture.debugElement.nativeElement.querySelector('#show-account-request-links');
        button.click();
        expect(component.accountRequests[0].showLinks).toEqual(true);
      });

      it('should display account requests with no reset or expand links button', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.PENDING)
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .comments('Test')
            .build(),
            accountRequestDetailsBuilder
            .id('1235')
            .email('test2@email.com')
            .name('Jane Doe')
            .status(AccountRequestStatus.PENDING)
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .comments('Test')
            .build(),
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
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.APPROVED)
            .registrationLink('link1')
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .comments('Test')
            .build(),
            accountRequestDetailsBuilder
            .id('1235')
            .email('test2@email.com')
            .name('Jane Doe')
            .status(AccountRequestStatus.APPROVED)
            .registrationLink('link2')
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .comments('Test')
            .build(),
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
            accountRequestDetailsBuilder
                .id('1234')
                .email('test@email.com')
                .name('John Doe')
                .instituteAndCountry('NUS')
                .build(),
        ];
        fixture.detectChanges();

        jest.spyOn(ngbModal, 'open').mockImplementation(() => {
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
      });

      it('should show error message when deleting account request is unsuccessful', () => {
        component.accountRequests = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.PENDING)
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .build(),
          ];

        fixture.detectChanges();

        jest.spyOn(ngbModal, 'open').mockImplementation(() => {
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
      });

      it('should show success message when resetting account request is successful', () => {
        component.accountRequests = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.APPROVED)
            .instituteAndCountry('NUS')
            .registrationLink('registrationLink')
            .registeredAtText('Wed, 09 Feb 2022, 10:23 AM +00:00')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .build(),
          ];

        component.searchString = 'test';
        fixture.detectChanges();

        jest.spyOn(ngbModal, 'open').mockImplementation(() => {
          return createMockNgbModalRef({});
        });

        jest.spyOn(accountService, 'resetAccountRequest').mockReturnValue(of({
          joinLink: 'joinlink',
        }));

        const spyStatusMessageService = jest.spyOn(statusMessageService, 'showSuccessToast')
          .mockImplementation((args: string) => {
            expect(args)
                .toEqual(`Reset successful. An email has been sent to ${'test@email.com'}.`);
          });

        const resetButton = fixture.debugElement.nativeElement.querySelector('#reset-account-request-0');
        resetButton.click();

        expect(spyStatusMessageService).toHaveBeenCalled();
      });

      it('should show error message when resetting account request is unsuccessful', () => {
        component.accountRequests = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.APPROVED)
            .instituteAndCountry('NUS')
            .registrationLink('registrationLink')
            .registeredAtText('Wed, 09 Feb 2022, 10:23 AM +00:00')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .build(),
        ];
        component.searchString = 'test';
        fixture.detectChanges();

        jest.spyOn(ngbModal, 'open').mockImplementation(() => {
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
      });

      it('should display comment modal', () => {
        const accountRequestResults: AccountRequestTableRowModel[] = [
            accountRequestDetailsBuilder
            .id('1234')
            .email('test@email.com')
            .name('John Doe')
            .status(AccountRequestStatus.PENDING)
            .instituteAndCountry('NUS')
            .createdAtText('Tue, 08 Feb 2022, 08:23 AM +00:00')
            .comments('Test')
            .build(),
        ];

        component.accountRequests = accountRequestResults;
        const modalSpy = jest.spyOn(ngbModal, 'open').mockImplementation(() => {
            return createMockNgbModalRef({});
        });
        fixture.detectChanges();

        const viewCommentButton: any = fixture.debugElement.nativeElement.querySelector('#view-account-request-0');
        viewCommentButton.click();
        expect(modalSpy).toHaveBeenCalledTimes(1);
      });
});
