import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ConfigService } from './config.service';
import { createMockHttpRequestService, MockHttpRequestService } from '../test-helpers/mock-http-request';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Config } from '../types/api-output';

describe('ConfigService', () => {
  let service: ConfigService;
  let spyHttpRequestService: MockHttpRequestService;
  const mockConfig: Config = {
    loginMethods: [],
    frontendUrl: '',
  };

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [{ provide: HttpRequestService, useValue: spyHttpRequestService }],
    });
    service = TestBed.inject(ConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET on config endpoint', () => {
    spyHttpRequestService.get.mockReturnValue(of(mockConfig));
    service.getConfig();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.CONFIG);
  });

  it('should return cached config if already fetched', () => {
    spyHttpRequestService.get.mockReturnValue(of(mockConfig));

    service.getConfig().subscribe();
    service.getConfig().subscribe((cachedConfig: Config) => {
      expect(cachedConfig).toEqual(mockConfig);
    });

    expect(spyHttpRequestService.get).toHaveBeenCalledOnce();
  });
});
