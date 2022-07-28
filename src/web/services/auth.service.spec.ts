import { TestBed } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { ResourceEndpoints } from '../types/api-const';
import { Intent } from '../types/api-request';
import { AuthService } from './auth.service';
import { HttpRequestService } from './http-request.service';

describe('AuthService', () => {
  const frontendUrl: string = environment.frontendUrl;

  let spyHttpRequestService: any;
  let service: AuthService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
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

  it('should execute getAuthRegkeyValidity', () => {
    const key: string = 'key';
    const intent: Intent = Intent.FULL_DETAIL;
    const paramMap: Record<string, string> = { key, intent };
    service.getAuthRegkeyValidity(key, intent);
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.AUTH_REGKEY, paramMap);
  });

  it('should execute sendLoginEmail', () => {
    const userEmail: string = 'abc@gmail.com';
    const continueUrl: string = 'continueUrl';
    const captchaResponse: string = 'captchaResponse';
    const queryParam = { userEmail, continueUrl, captchaResponse };
    const paramMap: Record<string, string> = {
      useremail: queryParam.userEmail,
      continueurl: queryParam.continueUrl,
      captcharesponse: queryParam.captchaResponse,
    };
    service.sendLoginEmail(queryParam);
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.LOGIN_EMAIL, paramMap);
  });

});
