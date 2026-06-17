import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { InstituteService } from './institute.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';

describe('InstituteService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: InstituteService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(InstituteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET with accountId when retrieving verified institutes', () => {
    const accountId = 'test-account-id';
    service.getVerifiedInstitutes(accountId);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.INSTITUTES, { accountid: accountId });
  });
});
