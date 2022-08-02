import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResourceEndpoints } from '../types/api-const';
import { AccountRequestCreateIntent, AccountRequestCreateRequest, AccountRequestType } from '../types/api-request';
import { AccountService } from './account.service';
import { HttpRequestService } from './http-request.service';

describe('AccountService', () => {
  let spyHttpRequestService: any;
  let service: AccountService;
  const id: string = 'TestID';

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(AccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on account endpoint', () => {
    service.getAccount(id);
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute GET on accounts endpoint', () => {
    const email = 'email@gmail.tmt';
    service.getAccounts(email);
    const paramMap: Record<string, string> = {
      useremail: email,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNTS, paramMap);
  });

  it('should execute POST on account endpoint with timezone string', () => {
    const testKey: string = 'testKey';
    const testTimezone: string = 'UTC';
    const paramMap: Record<string, string> = {
      key: testKey,
      timezone: testTimezone,
    };
    service.createAccount(testKey, testTimezone);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute POST on account endpoint with empty timezone string', () => {
    const testKey: string = 'testKey';
    const paramMap: Record<string, string> = {
      key: testKey,
    };
    service.createAccount(testKey, '');
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute POST on account request endpoint', () => {
    const testRequest: AccountRequestCreateRequest = {
      instructorName: 'testName',
      instructorInstitute: 'testInstitution, Singapore',
      instructorCountry: '',
      instructorEmail: 'testEmail@tmt.tmt',
      instructorHomePageUrl: '',
      comments: '',
    };
    const paramsMap: Record<string, string> = {
      intent: AccountRequestCreateIntent.ADMIN_CREATE,
      accountrequesttype: AccountRequestType.INSTRUCTOR_ACCOUNT,
    };
    service.createAccountRequest(testRequest);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, testRequest);
  });

  it('should execute DELETE on account endpoint', () => {
    service.deleteAccount(id);
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute DELETE on account request endpoint', () => {
    service.deleteAccountRequest('testEmail', 'testInstitution');
    const paramMap: Record<string, string> = {
      instructoremail: 'testEmail',
      instructorinstitution: 'testInstitution',
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  });

  it('should execute PUT on account request reset endpoint', () => {
    service.resetAccountRequest('testEmail', 'testInstitution');
    const paramMap: Record<string, string> = {
      instructoremail: 'testEmail',
      instructorinstitution: 'testInstitution',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_RESET, paramMap);
  });

  it('should execute PUT on account/reset endpoint for student', () => {
    service.resetStudentAccount(id, 'testStudentEmail');
    const paramMap: Record<string, string> = {
      courseid: id,
      studentemail: 'testStudentEmail',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  });

  it('should execute PUT on account/reset endpoint for instructor', () => {
    service.resetInstructorAccount(id, 'testInstructorEmail');
    const paramMap: Record<string, string> = {
      courseid: id,
      instructoremail: 'testInstructorEmail',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  });
});
