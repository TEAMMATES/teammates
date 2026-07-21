import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UsageStatisticsService } from './usage-statistics.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { createMockHttpRequestService, MockHttpRequestService } from '../test-helpers/mock-http-request';
import { HttpRequestService } from './http-request.service';

describe('UsageStatisticsService', () => {
  let service: UsageStatisticsService;
  let spyHttpRequestService: MockHttpRequestService;

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(UsageStatisticsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute GET when retrieving usage statistics', () => {
    const startTime = 0;
    const endTime = 1;
    const paramMap: Record<string, string> = {
      [QueryParamKeys.QUERY_LOGS_STARTTIME]: `${startTime}`,
      [QueryParamKeys.QUERY_LOGS_ENDTIME]: `${endTime}`,
    };

    vi.spyOn(spyHttpRequestService, 'get');

    service.getUsageStatistics(startTime, endTime);

    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.USAGE_STATISTICS, paramMap);
  });
});
