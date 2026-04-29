import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminHomePageComponent } from './admin-home-page.component';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequestStatus } from '../../../types/api-output';

describe('AdminHomePageComponent', () => {
  let component: AdminHomePageComponent;
  let fixture: ComponentFixture<AdminHomePageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHomePageComponent);
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    jest.spyOn(accountService, 'getPendingAccountRequests').mockReturnValue(of({ accountRequests: [] }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create account request directly if all fields are filled', () => {
    const spyAccountService: SpyInstance = jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      of({
        id: 'some.person@example.com%NUS',
        email: 'some.person@example.com',
        name: 'Some Person',
        institute: 'NUS',
        status: AccountRequestStatus.PENDING,
        registrationKey: 'registrationKey',
        createdAt: 528,
      }),
    );
    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Account request was successfully created');
      });
    const spyFetchAccountRequests: SpyInstance = jest
      .spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of({ accountRequests: [] }));
    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(spyAccountService).toHaveBeenCalled();
    expect(spyStatusMessageService).toHaveBeenCalledTimes(1);
    expect(spyFetchAccountRequests).toHaveBeenCalled();
    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('');
  });

  it('should not create account request if some fields are empty', () => {
    const spyAccountService: SpyInstance = jest.spyOn(accountService, 'createAccountRequest');
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showWarningToast');

    component.instructorName = 'Instructor Name';
    component.instructorEmail = '';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(spyStatusMessageService).toHaveBeenCalledWith('Please fill in all fields: Name, Email, and Institution.');
    expect(spyAccountService).not.toHaveBeenCalled();

    component.instructorName = '';
    component.instructorEmail = 'instructor@example.com';

    button.click();

    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(spyStatusMessageService).toHaveBeenCalledWith('Please fill in all fields: Name, Email, and Institution.');
    expect(spyAccountService).not.toHaveBeenCalled();

    component.instructorName = 'Instructor Name';
    component.instructorInstitution = '';

    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('');
    expect(spyStatusMessageService).toHaveBeenCalledWith('Please fill in all fields: Name, Email, and Institution.');
    expect(spyAccountService).not.toHaveBeenCalled();
  });

  it('should not create any account request if multiple instructor details contain invalid format', () => {
    const spyAccountService: SpyInstance = jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      of({
        id: 'some.person@example.com%NUS',
        email: 'some.person@example.com',
        name: 'Some Person',
        institute: 'NUS',
        status: AccountRequestStatus.PENDING,
        registrationKey: 'registrationKey',
        createdAt: 528,
      }),
    );
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showWarningToast');
    const instructorDetails: string = [
      'Instructor A | instructora@example.com | Institution A',
      'Instructor B | instructorb@example.com',
      'Instructor C | | instructorc@example.com',
      'Instructor D | instructord@example.com | Institution D',
      '| instructore@example.com | Institution E',
    ].join('\n');
    component.instructorDetails = instructorDetails;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalledWith(
      '3 line(s) with missing or invalid fields. ' + 'Format required: Name | Email | Institution',
    );
    expect(component.instructorDetails).toEqual(instructorDetails);
    expect(spyAccountService).toHaveBeenCalledTimes(0);
  });

  it('should create account requests for all multiple instructor details split by vertical bars', () => {
    const spyAccountService: SpyInstance = jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      of({
        id: 'some.person@example.com%NUS',
        email: 'some.person@example.com',
        name: 'Some Person',
        institute: 'NUS',
        status: AccountRequestStatus.PENDING,
        registrationKey: 'registrationKey',
        createdAt: 528,
      }),
    );
    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('2 account request(s) were successfully created');
      });
    const spyFetchAccountRequests: SpyInstance = jest
      .spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of({ accountRequests: [] }));
    component.instructorDetails = [
      'Instructor A | instructora@example.com | Institution A',
      'Instructor B | instructorb@example.com | Institution B',
    ].join('\n');
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(spyAccountService).toHaveBeenCalledTimes(2);
    expect(spyStatusMessageService).toHaveBeenCalledTimes(1);
    expect(spyFetchAccountRequests).toHaveBeenCalled();
  });

  it('should show error toast when account request creation fails', () => {
    jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message',
        },
      })),
    );
    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    component.validateAndAddInstructorDetail();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should create account requests for all multiple instructor details split by tabs with empty lines', () => {
    const spyAccountService: SpyInstance = jest.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      of({
        id: 'some.person@example.com%NUS',
        email: 'some.person@example.com',
        name: 'Some Person',
        institute: 'NUS',
        status: AccountRequestStatus.PENDING,
        registrationKey: 'registrationKey',
        createdAt: 528,
      }),
    );
    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('2 account request(s) were successfully created');
      });
    const spyFetchAccountRequests: SpyInstance = jest
      .spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of({ accountRequests: [] }));
    component.instructorDetails = [
      'Instructor A   \t  instructora@example.com \t  Sample Institution A',
      'Instructor B \t instructorb@example.com \t Sample Institution B',
      '',
    ].join('\n');

    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#add-instructor-single-line');
    button.click();

    expect(spyAccountService).toHaveBeenCalledTimes(2);
    expect(spyStatusMessageService).toHaveBeenCalledTimes(1);
    expect(spyFetchAccountRequests).toHaveBeenCalled();
  });
});
