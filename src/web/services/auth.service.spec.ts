import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
import { ResourceEndpoints } from '../types/api-const';
import { of } from 'rxjs';

describe('AuthService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: AuthService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [{ provide: HttpRequestService, useValue: spyHttpRequestService }],
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on auth endpoint', () => {
    spyHttpRequestService.get.mockReturnValue(of({ loginUrl: '/login', masquerade: false }));
    service.getAuthUser();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.AUTH, {});
  });
});
