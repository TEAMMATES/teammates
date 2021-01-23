import { Injectable } from '@angular/core';
import {HttpRequestService} from "./http-request.service";
import {Observable} from "rxjs";
import {FeedbackResponseStats} from "../types/api-output";
import {ResourceEndpoints} from "../types/api-const";

/**
 * Handles requests to the back-end related to feedback response statistics.
 */
@Injectable({
  providedIn: 'root'
})
export class FeedbackResponseStatsService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Loads feedback response statistics by calling API.
   */
  loadResponseStats(duration: string, interval: string): Observable<FeedbackResponseStats> {
    const paramMap: Record<string, string> = {
      duration: duration,
      interval: interval
    };

    return this.httpRequestService.get(ResourceEndpoints.RESPONSE_STATS, paramMap)
  }

}
