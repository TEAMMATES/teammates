import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AccountService } from './account.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';
import { AccountVerificationRequestStatus } from '../types/api-output';
import { AccountCreateRequest, AccountVerificationRequestRejectionType, AccountVerificationRequestUpdateRequest } from '../types/api-request';

describe('AccountService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: AccountService;
  const id = 'TestID';

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
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
      accountid: id,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute GET on account verification request endpoint', () => {
    service.getAccountVerificationRequest(id);
    const paramMap: Record<string, string> = {
      id,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST, paramMap);
  });

  it('should execute POST on account verification request endpoint', () => {
    const testRequest: AccountCreateRequest = {
      instructorEmail: 'testEmail',
      instructorInstitution: 'testInstitution',
      instructorCountry: 'SG',
      instructorName: 'testName',
    };
    service.createAccountVerificationRequest(testRequest);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST,
      {},
      testRequest,
    );
  });

  it('should execute DELETE on account endpoint', () => {
    service.deleteAccount(id);
    const paramMap: Record<string, string> = {
      accountid: id,
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT, paramMap);
  });

  it('should execute DELETE on account verification request endpoint', () => {
    service.deleteAccountVerificationRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST, paramMap);
  });

  it('should execute PUT on account/unlink endpoint', () => {
    service.unlinkAccount('testUserId');
    const paramMap: Record<string, string> = {
      userid: 'testUserId',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.ACCOUNT_UNLINK, paramMap);
  });

  it('should execute POST on account verification request approval endpoint', () => {
    service.approveAccountVerificationRequest('testId');
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST_APPROVE,
      paramMap,
    );
  });

  it('should execute POST on account verification request rejection endpoint', () => {
    const paramMap: Record<string, string> = { id: 'testId' };
    const requestBody = { rejectionType: AccountVerificationRequestRejectionType.OTHERS };
    service.rejectAccountVerificationRequest(paramMap, requestBody);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST_REJECT,
      paramMap,
      requestBody,
    );
  });

  it('should execute PUT on account verification request endpoint for edit', () => {
    const updateRequest: AccountVerificationRequestUpdateRequest = {
      name: 'name',
      email: 'email@email.com',
      institute: 'institute',
      country: 'SG',
      status: AccountVerificationRequestStatus.PENDING,
      comments: 'comments',
    };
    service.editAccountVerificationRequest('testId', updateRequest);
    const paramMap: Record<string, string> = {
      id: 'testId',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST,
      paramMap,
      updateRequest,
    );
  });
});
