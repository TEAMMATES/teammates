import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { ErrorReportRequest } from '../types/api-request';

/**
 * Handles Error reporting related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class ErrorReportService {
  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Sends an error report.
   */
  sendErrorReport(queryParams: {
    request: ErrorReportRequest,
  }): Observable<any> {
    return this.httpRequestService.post(ResourceEndpoints.ERROR_REPORT, {}, queryParams.request);
  }
}
