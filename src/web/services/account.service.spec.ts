import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResourceEndpoints } from '../types/api-const';
import {
  AccountRequestCreateIntent,
  AccountRequestCreateRequest,
  AccountRequestsGetIntent,
  AccountRequestStatusUpdateIntent,
  AccountRequestType,
  AccountRequestUpdateRequest,
} from '../types/api-request';
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

  it('should execute DELETE on account endpoint', () => {
    service.deleteAccount(id);
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
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

  it('should execute GET on account/request endpoint when getting an account request', () => {
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
    };

    service.getAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution);

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap);
  });

  it('should execute POST on account/request endpoint when creating an account request as admin', () => {
    const createRequest: AccountRequestCreateRequest = {
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

    service.createAccountRequestAsAdmin(createRequest);

    expect(spyHttpRequestService.post)
      .toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, createRequest);
  });

  it('should execute POST on account/request endpoint when creating an account request as public', () => {
    const createRequest: AccountRequestCreateRequest = {
      instructorName: 'testName',
      instructorInstitute: 'testInstitution',
      instructorCountry: 'Singapore',
      instructorEmail: 'testEmail@tmt.tmt',
      instructorHomePageUrl: 'https://www.google.com/',
      comments: 'Is TEAMMATES free to use?',
    };
    const paramsMap: Record<string, string> = {
      intent: AccountRequestCreateIntent.PUBLIC_CREATE,
      accountrequesttype: AccountRequestType.INSTRUCTOR_ACCOUNT,
      captcharesponse: '123456',
    };

    service.createAccountRequestAsPublic({
      accountRequestType: AccountRequestType.INSTRUCTOR_ACCOUNT,
      captchaResponse: '123456',
      requestBody: createRequest,
    });

    expect(spyHttpRequestService.post)
      .toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, createRequest);
  });

  it('should execute PUT on account/request endpoint when updating an account request as admin', () => {
    const updateRequest: AccountRequestUpdateRequest = {
      instructorName: 'updatedName',
      instructorInstitute: 'updatedInstitution, Singapore',
      instructorEmail: 'updatedEmail@tmt.tmt',
    };
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
    };

    service.updateAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution, updateRequest);

    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, updateRequest);
  });

  it('should execute DELETE on account/request endpoint when deleting an account request', () => {
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
    };

    service.deleteAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution);

    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap);
  });

  it('should execute PUT on account/request/status endpoint when approving an account request', () => {
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
      intent: AccountRequestStatusUpdateIntent.TO_APPROVE,
    };

    service.approveAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution);

    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
  });

  it('should execute PUT on account/request/status endpoint when rejecting an account request', () => {
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
      intent: AccountRequestStatusUpdateIntent.TO_REJECT,
    };

    service.rejectAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution);

    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
  });

  it('should execute PUT on account/request/status endpoint when resetting an account request', () => {
    const paramsMap: Record<string, string> = {
      instructoremail: 'testEmail@tmt.tmt',
      instructorinstitution: 'testInstitution, Singapore',
      intent: AccountRequestStatusUpdateIntent.TO_RESET,
    };

    service.resetAccountRequest(paramsMap.instructoremail, paramsMap.instructorinstitution);

    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
  });

  it('should execute GET on account/requests endpoint when getting account requests pending processing', () => {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestsGetIntent.PENDING_PROCESSING,
    };

    service.getAccountRequestsPendingProcessing();

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUESTS, paramsMap);
  });

  it('should execute GET on account/requests endpoint when getting account requests within period', () => {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestsGetIntent.WITHIN_PERIOD,
      starttime: '1659456000',
      endtime: '1659542400',
    };

    service.getAccountRequestsWithinPeriod(Number(paramsMap.starttime), Number(paramsMap.endtime));

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_REQUESTS, paramsMap);
  });
});
