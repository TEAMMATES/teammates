import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ResourceEndpoints } from '../types/api-const';
import { ErrorReportRequest } from '../types/api-request';
import { ErrorReportService } from './error-report.service';
import { HttpRequestService } from './http-request.service';

describe('ErrorReportService', () => {
  let spyHttpRequestService: any;
  let service: ErrorReportService;

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
    service = TestBed.inject(ErrorReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should execute POST when sending an error report', () => {
    const request: ErrorReportRequest = {
      requestId: '',
      subject: '',
      content: '',
    };
    service.sendErrorReport({ request });
    expect(spyHttpRequestService.post).toHaveBeenCalledWith(ResourceEndpoints.ERROR_REPORT, {}, request);
  });
});
