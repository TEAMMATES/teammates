import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { FeedbackResponseRecords } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles requests to the back-end related to feedback response statistics.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackResponseStatsService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Loads feedback response statistics by calling API.
   */
  loadResponseStats(durationMinutes: string, intervalMinutes: string): Observable<FeedbackResponseRecords> {
    const paramMap: Record<string, string> = {
      duration: durationMinutes,
      interval: intervalMinutes,
    };

    return this.httpRequestService.get(ResourceEndpoints.RESPONSE_STATS, paramMap);
  }

}
