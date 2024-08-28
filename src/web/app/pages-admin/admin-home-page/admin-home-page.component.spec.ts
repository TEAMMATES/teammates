import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { AdminHomePageComponent } from './admin-home-page.component';
import { NewInstructorDataRowComponent } from './new-instructor-data-row/new-instructor-data-row.component';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { AccountRequest, AccountRequests, AccountRequestStatus, MessageOutput } from '../../../types/api-output';
import { AccountCreateRequest } from '../../../types/api-request';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableModule } from '../../components/account-requests-table/account-request-table.module';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';
import { ErrorMessageOutput } from '../../error-message-output';

const accountCreateRequestBuilder = createBuilder<AccountCreateRequest>({
  instructorEmail: '',
  instructorName: '',
  instructorInstitution: '',
});

const accountRequestBuilder = createBuilder<AccountRequest>({
  id: '',
  email: '',
  name: '',
  institute: '',
  registrationKey: '',
  status: AccountRequestStatus.PENDING,
  createdAt: 0,
});

const messageOutputBuilder = createBuilder<MessageOutput>({
  message: '',
});

const errorMessageOutputBuilder = createBuilder<ErrorMessageOutput>({
  error: messageOutputBuilder.build(),
  status: 0,
});

const accountRequestTableRowModelBuilder = createBuilder<AccountRequestTableRowModel>({
  id: '',
  name: '',
  email: '',
  status: AccountRequestStatus.PENDING,
  instituteAndCountry: '',
  createdAtText: '',
  registeredAtText: '',
  comments: '',
  registrationLink: '',
  showLinks: false,
});

describe('AdminHomePageComponent', () => {
  let component: AdminHomePageComponent;
  let fixture: ComponentFixture<AdminHomePageComponent>;
  let accountService: AccountService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        AdminHomePageComponent,
        NewInstructorDataRowComponent,
      ],
      imports: [
        FormsModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        AccountRequestTableModule,
        AjaxLoadingModule,
        RouterTestingModule,
      ],
      providers: [
        AccountService,
        FormatDateDetailPipe,
        StatusMessageService,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHomePageComponent);
    accountService = TestBed.inject(AccountService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('validateAndAddInstructorDetail: should create one instructor account request if all fields are filled', () => {
    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    const accountCreateRequest = accountCreateRequestBuilder
      .instructorName('Instructor Name')
      .instructorEmail('instructor@example.com')
      .instructorInstitution('Instructor Institution')
      .build();

    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest')
      .mockReturnValue(of(accountRequestBuilder.build()));

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(createAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(createAccountRequestSpy).toHaveBeenCalledWith(accountCreateRequest);

    // Clear instructor fields
    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('');
  });

  it('validateAndAddInstructorDetail: should not create one instructor account request '
  + 'if some fields are empty', () => {
    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest');

    component.instructorName = '';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    button.click();

    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(createAccountRequestSpy).not.toHaveBeenCalled();

    component.instructorName = 'Instructor Name';
    component.instructorEmail = '';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(createAccountRequestSpy).not.toHaveBeenCalled();

    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = '';
    fixture.detectChanges();

    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('');
    expect(createAccountRequestSpy).not.toHaveBeenCalled();
  });

  it('validateAndAddInstructorDetails: should only create account requests for valid instructor details '
  + 'when there are invalid lines in the single line field', () => {
    component.instructorDetails = [
      'Instructor A | instructora@example.com | Institution A',
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      'Instructor D | instructord@example.com | Institution D',
      '| instructore@example.com | Institution E',
    ].join('\n');
    fixture.detectChanges();

    const accountCreateRequestA = accountCreateRequestBuilder
      .instructorName('Instructor A')
      .instructorEmail('instructora@example.com')
      .instructorInstitution('Institution A')
      .build();
    const accountCreateRequestD = accountCreateRequestBuilder
      .instructorName('Instructor D')
      .instructorEmail('instructord@example.com')
      .instructorInstitution('Institution D')
      .build();

    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest')
      .mockImplementation((request) => {
        switch (request.instructorEmail) {
          case accountCreateRequestA.instructorEmail:
            return of(accountRequestBuilder.build());
          case accountCreateRequestD.instructorEmail:
            return of(accountRequestBuilder.build());
          default:
            return throwError(() => errorMessageOutputBuilder.build());
        }
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual([
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      '| instructore@example.com | Institution E',
    ].join('\r\n'));

    expect(createAccountRequestSpy).toHaveBeenCalledTimes(2);
    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(1, accountCreateRequestA);
    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(2, accountCreateRequestD);
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some instructors details', () => {
    component.accountReqs = [
      accountRequestTableRowModelBuilder
        .name('Instructor A')
        .email('instructora@example.com')
        .status(AccountRequestStatus.PENDING)
        .instituteAndCountry('Institution and Country A')
        .createdAtText('Created Time A')
        .comments('Comment A')
        .build(),

      accountRequestTableRowModelBuilder
        .name('Instructor B')
        .email('instructorb@example.com')
        .status(AccountRequestStatus.APPROVED)
        .instituteAndCountry('Institution and Country B')
        .createdAtText('Created Time B')
        .comments('Comment B')
        .build(),

      accountRequestTableRowModelBuilder
        .name('Instructor C')
        .email('instructorc@example.com')
        .status(AccountRequestStatus.REJECTED)
        .instituteAndCountry('Institution and Country C')
        .createdAtText('Created Time C')
        .comments('Comment C')
        .build(),
    ];
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('validateAndAddInstructorDetails: should create multiple instructor account requests when split by tabs', () => {
    component.instructorDetails =
      `Instructor A   \t  instructora@example.com \t  Institution A\n
      Instructor B \t instructorb@example.com \t Institution B`;
    fixture.detectChanges();

    const accountCreateRequestA = accountCreateRequestBuilder
      .instructorName('Instructor A')
      .instructorEmail('instructora@example.com')
      .instructorInstitution('Institution A')
      .build();
    const accountCreateRequestB = accountCreateRequestBuilder
      .instructorName('Instructor B')
      .instructorEmail('instructorb@example.com')
      .instructorInstitution('Institution B')
      .build();

    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest')
      .mockImplementation((request) => {
        switch (request.instructorEmail) {
          case accountCreateRequestA.instructorEmail:
            return of(accountRequestBuilder.build());
          case accountCreateRequestB.instructorEmail:
            return of(accountRequestBuilder.build());
          default:
            return throwError(() => errorMessageOutputBuilder.build());
        }
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual('');

    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(1, accountCreateRequestA);
    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(2, accountCreateRequestB);
  });

  it('validateAndAddInstructorDetails: should create multiple instructor account requests '
  + 'when split by vertical bars', () => {
    component.instructorDetails =
      `Instructor A | instructora@example.com | Institution A\n
      Instructor B | instructorb@example.com | Institution B`;
    fixture.detectChanges();

    const accountCreateRequestA = accountCreateRequestBuilder
      .instructorName('Instructor A')
      .instructorEmail('instructora@example.com')
      .instructorInstitution('Institution A')
      .build();
    const accountCreateRequestB = accountCreateRequestBuilder
      .instructorName('Instructor B')
      .instructorEmail('instructorb@example.com')
      .instructorInstitution('Institution B')
      .build();

    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest')
      .mockImplementation((request) => {
        switch (request.instructorEmail) {
          case accountCreateRequestA.instructorEmail:
            return of(accountRequestBuilder.build());
          case accountCreateRequestB.instructorEmail:
            return of(accountRequestBuilder.build());
          default:
            return throwError(() => errorMessageOutputBuilder.build());
        }
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual('');

    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(1, accountCreateRequestA);
    expect(createAccountRequestSpy).toHaveBeenNthCalledWith(2, accountCreateRequestB);
  });

  it('fetchAccountRequests: should update account requests binding if pending account requests exist', () => {
    const accountRequestA = accountRequestBuilder
      .name('Instructor A')
      .email('instructora@example.com')
      .institute('Institution A')
      .build();
    const accountRequestB = accountRequestBuilder
      .name('Instructor B')
      .email('instructorb@example.com')
      .institute('Institution B')
      .build();
    const accountRequests: AccountRequests = {
      accountRequests: [
        accountRequestA,
        accountRequestB,
      ],
    };

    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    component.fetchAccountRequests();

    expect(component.accountReqs.length).toEqual(2);

    expect(component.accountReqs[0].name).toEqual('Instructor A');
    expect(component.accountReqs[0].email).toEqual('instructora@example.com');
    expect(component.accountReqs[0].instituteAndCountry).toEqual('Institution A');

    expect(component.accountReqs[1].name).toEqual('Instructor B');
    expect(component.accountReqs[1].email).toEqual('instructorb@example.com');
    expect(component.accountReqs[1].instituteAndCountry).toEqual('Institution B');
  });

  it('fetchAccountRequests: should not update account requests binding if no pending account requests exist', () => {
    const accountRequests: AccountRequests = {
      accountRequests: [],
    };

    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    component.fetchAccountRequests();

    expect(component.accountReqs.length).toEqual(0);
  });
});
