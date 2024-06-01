import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AdminHomePageComponent } from './admin-home-page.component';
import { NewInstructorDataRowComponent } from './new-instructor-data-row/new-instructor-data-row.component';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createBuilder } from '../../../test-helpers/generic-builder';
import { AccountRequest, AccountRequests, AccountRequestStatus } from '../../../types/api-output';
import { AccountRequestTableRowModel } from '../../components/account-requests-table/account-request-table-model';
import { AccountRequestTableModule } from '../../components/account-requests-table/account-request-table.module';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';

const accountRequestBuilder = createBuilder<AccountRequest>({
  id: '',
  email: '',
  name: '',
  institute: '',
  registrationKey: '',
  status: AccountRequestStatus.PENDING,
  createdAt: 0,
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

  it('validateAndAddInstructorDetail: should add one instructor to list if all fields are filled', () => {
    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    const accountRequest = accountRequestBuilder
      .name('Instructor Name')
      .email('instructor@example.com')
      .institute('Instructor Institution')
      .build();
    const accountRequests: AccountRequests = {
      accountRequests: [
        accountRequest,
      ],
    };

    jest.spyOn(accountService, 'createAccountRequest')
      .mockReturnValue(of(accountRequest));
    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    // Clear instructor fields
    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('');

    expect(component.accountReqs.length).toEqual(1);
    expect(component.accountReqs[0].name).toEqual('Instructor Name');
    expect(component.accountReqs[0].email).toEqual('instructor@example.com');
    expect(component.accountReqs[0].instituteAndCountry).toEqual('Instructor Institution');
  });

  it('validateAndAddInstructorDetail: should not add one instructor to list if some fields are empty', () => {
    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    const createAccountRequestSpy = jest.spyOn(accountService, 'createAccountRequest');

    component.instructorName = '';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    button.click();

    component.instructorName = 'Instructor Name';
    component.instructorEmail = '';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    button.click();

    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = '';
    fixture.detectChanges();

    button.click();

    expect(createAccountRequestSpy).not.toHaveBeenCalled();
  });

  it('validateAndAddInstructorDetails: should only add valid instructor details in the single line field', () => {
    component.instructorDetails = [
      'Instructor A | instructora@example.com | Institution A',
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      'Instructor D | instructord@example.com | Institution D',
      '| instructore@example.com | Institution E',
    ].join('\n');
    fixture.detectChanges();

    const accountRequestA = accountRequestBuilder
      .name('Instructor A')
      .email('instructora@example.com')
      .institute('Institution A')
      .build();
    const accountRequestD = accountRequestBuilder
      .name('Instructor D')
      .email('instructord@example.com')
      .institute('Institution D')
      .build();
    const accountRequests: AccountRequests = {
      accountRequests: [
        accountRequestA,
        accountRequestD,
      ],
    };

    // We are not testing the value createAccountRequest is being called with so it can be any value
    jest.spyOn(accountService, 'createAccountRequest')
      .mockReturnValue(of(accountRequestA));
    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual([
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      '| instructore@example.com | Institution E',
    ].join('\r\n'));

    expect(component.accountReqs.length).toEqual(2);

    expect(component.accountReqs[0].name).toEqual('Instructor A');
    expect(component.accountReqs[0].email).toEqual('instructora@example.com');
    expect(component.accountReqs[0].instituteAndCountry).toEqual('Institution A');

    expect(component.accountReqs[1].name).toEqual('Instructor D');
    expect(component.accountReqs[1].email).toEqual('instructord@example.com');
    expect(component.accountReqs[1].instituteAndCountry).toEqual('Institution D');
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
        .instituteAndCountry('Sample Institution and Country A')
        .createdAtText('Sample Created Time A')
        .comments('Sample Comment A')
        .build(),

      accountRequestTableRowModelBuilder
        .name('Instructor B')
        .email('instructorb@example.com')
        .status(AccountRequestStatus.APPROVED)
        .instituteAndCountry('Sample Institution and Country B')
        .createdAtText('Sample Created Time B')
        .comments('Sample Comment B')
        .build(),

      accountRequestTableRowModelBuilder
        .name('Instructor C')
        .email('instructorc@example.com')
        .status(AccountRequestStatus.REJECTED)
        .instituteAndCountry('Sample Institution and Country C')
        .createdAtText('Sample Created Time C')
        .comments('Sample Comment C')
        .build(),
    ];
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('validateAndAddInstructorDetails: should add multiple instructors split by tabs', () => {
    component.instructorDetails =
      `Instructor A   \t  instructora@example.com \t  Sample Institution A\n
      Instructor B \t instructorb@example.com \t Sample Institution B`;
    fixture.detectChanges();

    const accountRequestA = accountRequestBuilder
      .name('Instructor A')
      .email('instructora@example.com')
      .institute('Sample Institution A')
      .build();
    const accountRequestB = accountRequestBuilder
      .name('Instructor B')
      .email('instructorb@example.com')
      .institute('Sample Institution B')
      .build();
    const accountRequests: AccountRequests = {
      accountRequests: [
        accountRequestA,
        accountRequestB,
      ],
    };

    jest.spyOn(accountService, 'createAccountRequest')
      .mockReturnValue(of(accountRequestA));
    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual('');
    expect(component.accountReqs.length).toEqual(2);

    expect(component.accountReqs[0].name).toEqual('Instructor A');
    expect(component.accountReqs[0].email).toEqual('instructora@example.com');
    expect(component.accountReqs[0].instituteAndCountry).toEqual('Sample Institution A');

    expect(component.accountReqs[1].name).toEqual('Instructor B');
    expect(component.accountReqs[1].email).toEqual('instructorb@example.com');
    expect(component.accountReqs[1].instituteAndCountry).toEqual('Sample Institution B');
  });

  it('validateAndAddInstructorDetails: should add multiple instructors split by vertical bars', () => {
    component.instructorDetails =
      `Instructor A | instructora@example.com | Sample Institution A\n
      Instructor B | instructorb@example.com | Sample Institution B`;
    fixture.detectChanges();

    const accountRequestA = accountRequestBuilder
      .name('Instructor A')
      .email('instructora@example.com')
      .institute('Sample Institution A')
      .build();
    const accountRequestB = accountRequestBuilder
      .name('Instructor B')
      .email('instructorb@example.com')
      .institute('Sample Institution B')
      .build();
    const accountRequests: AccountRequests = {
      accountRequests: [
        accountRequestA,
        accountRequestB,
      ],
    };

    jest.spyOn(accountService, 'createAccountRequest')
      .mockReturnValueOnce(of(accountRequestA));
    jest.spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of(accountRequests));

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(component.instructorDetails).toEqual('');
    expect(component.accountReqs.length).toEqual(2);

    expect(component.accountReqs[0].name).toEqual('Instructor A');
    expect(component.accountReqs[0].email).toEqual('instructora@example.com');
    expect(component.accountReqs[0].instituteAndCountry).toEqual('Sample Institution A');

    expect(component.accountReqs[1].name).toEqual('Instructor B');
    expect(component.accountReqs[1].email).toEqual('instructorb@example.com');
    expect(component.accountReqs[1].instituteAndCountry).toEqual('Sample Institution B');
  });
});
