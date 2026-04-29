import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AccountService } from './account.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { AccountRequestStatus } from '../types/api-output';
import { AccountCreateRequest, AccountRequestUpdateRequest } from '../types/api-request';

describe('AccountService', () => {
  let spyHttpRequestService: any;
  let service: AccountService;
  const id = 'TestID';

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
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
    const testKey = 'testKey';
    const testTimezone = 'UTC';
    const paramMap: Record<string, string> = {
      key: testKey,
      timezone: testTimezone,
    };
    service.createAccount(testKey, testTimezone);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute POST on account endpoint with empty timezone string', () => {
    const testKey = 'testKey';
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

  it('should execute POST on account request approval endpoint', () => {
    service.approveAccountRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_APPROVE, paramMap);
  });

  it('should execute POST on account request rejection endpoint without reason', () => {
    service.rejectAccountRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_REJECT, paramMap, {});
  });

  it('should execute POST on account request rejection endpoint with reason', () => {
    service.rejectAccountRequest('testId', 'Title', 'Body');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    const requestBody = {
      reasonTitle: 'Title',
      reasonBody: 'Body',
    };
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.ACCOUNT_REQUEST_REJECT,
      paramMap,
      requestBody,
    );
  });

  it('should execute PUT on account request endpoint for edit', () => {
    const updateRequest: AccountRequestUpdateRequest = {
      name: 'name',
      email: 'email@email.com',
      institute: 'institute',
      status: AccountRequestStatus.PENDING,
      comments: 'comments',
    };
    service.editAccountRequest('testId', updateRequest);
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramMap, updateRequest);
  });
});
