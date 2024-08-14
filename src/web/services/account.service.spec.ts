import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AccountService } from './account.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { AccountCreateRequest } from '../types/api-request';

describe('AccountService', () => {
  let spyHttpRequestService: any;
  let service: AccountService;
  const id: string = 'TestID';

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
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
    const testRequest: AccountCreateRequest = {
      instructorEmail: 'testEmail',
      instructorInstitution: 'testInstitution',
      instructorName: 'testName',
    };
    service.createAccountRequest(testRequest);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, {}, testRequest);
  });

  it('should execute DELETE on account endpoint', () => {
    service.deleteAccount(id);
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute DELETE on account request endpoint', () => {
    service.deleteAccountRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  });

  it('should execute PUT on account request reset endpoint', () => {
    service.resetAccountRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
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
