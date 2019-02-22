import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JoinStatus } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles course related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKey: string, entityType: string): Observable<JoinStatus> {
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
    };
    return this.httpRequestService.get('/join', paramMap);
  }
}
