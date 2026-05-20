import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { UserService } from './user.service';
import { ResourceEndpoints } from '../types/api-const';

describe('UserService', () => {
  let spyHttpRequestService: any;
  let service: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [HttpRequestService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    spyHttpRequestService = TestBed.inject(HttpRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute POST when regenerating key of a user in a course', () => {
    const paramMap: Record<string, string> = {
      userid: 'user-id',
    };
    vi.spyOn(spyHttpRequestService, 'post');

    service.regenerateUserKey(paramMap['userid']);

    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.USER_KEY, paramMap);
  });
});
