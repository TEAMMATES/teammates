import { TestBed } from '@angular/core/testing';
import { ConfigService } from './config.service';
import { createMockHttpRequestService, MockHttpRequestService } from '../test-helpers/mock-http-request';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';

describe('ConfigService', () => {
  let service: ConfigService;
  let spyHttpRequestService: MockHttpRequestService;

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
    spyHttpRequestService.get.mockReturnValue({});
    service.getConfig();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.CONFIG);
  });
});
