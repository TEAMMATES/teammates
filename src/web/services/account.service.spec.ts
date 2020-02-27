import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AccountCreateRequest } from '../types/api-request';
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
    service = TestBed.get(AccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on account endpoint', () => {
    service.getAccount(id);
    const paramMap: { [key: string]: string } = {
      instructorid: id,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith('/account', paramMap);
  });

  it('should execute POST on account endpoint', () => {
    const testRequest: AccountCreateRequest = new class implements AccountCreateRequest {
      instructorEmail: string = 'testEmail';
      instructorInstitution: string = 'testInstitution';
      instructorName: string = 'testName';
    };
    service.createAccount(testRequest);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith('/account', {}, testRequest);
  });

  it('should execute DELETE on account endpoint', () => {
    service.deleteAccount(id);
    const paramMap: { [key: string]: string } = {
      instructorid: id,
    };
    expect(spyHttpRequestService.delete).toHaveBeenCalledWith('/account', paramMap);
  });

  it('should execute GET on accounts/search endpoint', () => {
    service.searchAccounts(id);
    const paramMap: { [key: string]: string } = {
      searchkey: id,
    };
    expect(spyHttpRequestService.get).toHaveBeenCalledWith('/accounts/search', paramMap);
  });

  it('should execute PUT on account/downgrade endpoint', () => {
    service.downgradeAccount(id);
    const paramMap: { [key: string]: string } = {
      instructorid: id,
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith('/account/downgrade', paramMap);
  });

  it('should execute PUT on account/reset endpoint for student', () => {
    service.resetStudentAccount(id, 'testStudentEmail');
    const paramMap: { [key: string]: string } = {
      courseid: id,
      studentemail: 'testStudentEmail',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith('/account/reset', paramMap);
  });

  it('should execute PUT on account/reset endpoint for instructor', () => {
    service.resetInstructorAccount(id, 'testInstructorEmail');
    const paramMap: { [key: string]: string } = {
      courseid: id,
      instructoremail: 'testInstructorEmail',
    };
    expect(spyHttpRequestService.put).toHaveBeenCalledWith('/account/reset', paramMap);
  });
});
