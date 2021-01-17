import { TestBed } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { ResourceEndpoints } from '../types/api-const';
import { AuthService } from './auth.service';
import { HttpRequestService } from './http-request.service';

describe('AuthService', () => {
  const frontendUrl: string = environment.frontendUrl;

  let spyHttpRequestService: any;
  let service: AuthService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
    };
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute getAuthUser', () => {
    service.getAuthUser();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.AUTH, { frontendUrl });
  });

});
