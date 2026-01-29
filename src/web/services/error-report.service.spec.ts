import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ErrorReportService } from './error-report.service';
import { HttpRequestService } from './http-request.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { ErrorReportRequest } from '../types/api-request';

describe('ErrorReportService', () => {
  let spyHttpRequestService: any;
  let service: ErrorReportService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
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
