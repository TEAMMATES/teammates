import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { UsageStatisticsRange } from '../types/api-output';

/**
 * Handles usage statistics provision.
 */
@Injectable({
  providedIn: 'root',
})
export class UsageStatisticsService {

  constructor(private httpRequestService: HttpRequestService) { }

  getUsageStatistics(startTime: number, endTime: number): Observable<UsageStatisticsRange> {
    const paramMap: Record<string, string> = {
      starttime: `${startTime}`,
      endtime: `${endTime}`,
    };

    return this.httpRequestService.get(ResourceEndpoints.USAGE_STATISTICS, paramMap);
  }

}
