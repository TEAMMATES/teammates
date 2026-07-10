import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { UserService } from './user.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { createMockHttpRequestService, MockHttpRequestService } from '../test-helpers/mock-http-request';

describe('UserService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: UserService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute POST when regenerating key of a user in a course', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: 'user-id',
    };
    vi.spyOn(spyHttpRequestService, 'post');

    service.regenerateUserKey(paramMap[QueryParamKeys.USER_ID]);

    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.USER_KEY, paramMap);
  });

  it('should execute GET when retrieving session links for a user', () => {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: 'user-id',
    };
    vi.spyOn(spyHttpRequestService, 'get');

    service.getSessionLinks(paramMap[QueryParamKeys.USER_ID]);

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.SESSION_LINKS, paramMap);
  });
});
