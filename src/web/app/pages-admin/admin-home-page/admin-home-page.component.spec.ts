import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminHomePageComponent } from './admin-home-page.component';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequestStatus } from '../../../types/api-output';

describe('AdminHomePageComponent', () => {
  let component: AdminHomePageComponent;
  let fixture: ComponentFixture<AdminHomePageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminHomePageComponent);
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    vi.spyOn(accountService, 'getPendingAccountRequests').mockReturnValue(of({ accountRequests: [] }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create account request directly if all fields are filled', () => {
    const spyAccountService = vi.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      of({
        accountRequestId: 'some.person@example.com%NUS',
        email: 'some.person@example.com',
        name: 'Some Person',
        institute: 'NUS',
        country: 'SG',
        status: AccountRequestStatus.PENDING,
        registrationKey: 'registrationKey',
        createdAt: 528,
      }),
    );
    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Account request was successfully created');
      });
    const spyFetchAccountRequests = vi
      .spyOn(accountService, 'getPendingAccountRequests')
      .mockReturnValue(of({ accountRequests: [] }));
    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = 'SG';
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(spyAccountService).toHaveBeenCalled();
    expect(spyStatusMessageService).toHaveBeenCalledTimes(1);
    expect(spyFetchAccountRequests).toHaveBeenCalled();
    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('');
    expect(component.instructorCountry).toEqual('');
  });

  it('should not create account request if some fields are empty', () => {
    const spyAccountService = vi.spyOn(accountService, 'createAccountRequest');
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showWarningToast');

    component.instructorName = 'Instructor Name';
    component.instructorEmail = '';
    component.instructorInstitution = 'Instructor Institution';
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#add-instructor');
    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(spyStatusMessageService).toHaveBeenCalledWith(
      'Please fill in all fields: Name, Email, Institution, and Country.',
    );
    expect(spyAccountService).not.toHaveBeenCalled();

    component.instructorName = '';
    component.instructorEmail = 'instructor@example.com';

    button.click();

    expect(component.instructorName).toEqual('');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('Instructor Institution');
    expect(spyStatusMessageService).toHaveBeenCalledWith(
      'Please fill in all fields: Name, Email, Institution, and Country.',
    );
    expect(spyAccountService).not.toHaveBeenCalled();

    component.instructorName = 'Instructor Name';
    component.instructorInstitution = '';

    button.click();

    expect(component.instructorName).toEqual('Instructor Name');
    expect(component.instructorEmail).toEqual('instructor@example.com');
    expect(component.instructorInstitution).toEqual('');
    expect(spyStatusMessageService).toHaveBeenCalledWith(
      'Please fill in all fields: Name, Email, Institution, and Country.',
    );
    expect(spyAccountService).not.toHaveBeenCalled();
  });

  it('should show error toast when account request creation fails', () => {
    vi.spyOn(accountService, 'createAccountRequest').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message',
        },
      })),
    );
    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    component.instructorName = 'Instructor Name';
    component.instructorEmail = 'instructor@example.com';
    component.instructorInstitution = 'Instructor Institution';
    component.instructorCountry = 'SG';
    component.validateAndAddInstructorDetail();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
