import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { UsageStatisticsRange } from '../types/api-output';

/**
 * Handles usage statistics provision.
 */
@Injectable({
  providedIn: 'root',
})
export class UsageStatisticsService {
  private httpRequestService = inject(HttpRequestService);

  getUsageStatistics(startTime: number, endTime: number): Observable<UsageStatisticsRange> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.QUERY_LOGS_STARTTIME]: `${startTime}`,
      [QueryParamKeys.QUERY_LOGS_ENDTIME]: `${endTime}`,
    };

    return this.httpRequestService.get(ResourceEndpoints.USAGE_STATISTICS, paramMap);
  }
}
