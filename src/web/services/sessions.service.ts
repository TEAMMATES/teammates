import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OngoingSessions } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles sessions related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class SessionsService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Gets all ongoing session by calling API.
   */
  getOngoingSessions(startTime: any, endTime: any): Observable<OngoingSessions> {
    const paramMap: { [key: string]: string } = {
      starttime: startTime.toDate().getTime(),
      endtime: endTime.toDate().getTime(),
    };
    return this.httpRequestService.get('/sessions/ongoing', paramMap);
  }
}
