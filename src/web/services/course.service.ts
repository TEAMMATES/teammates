import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MessageOutput } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles sessions related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Creates a course by calling API.
   */
  createCourse(paramMap: { [key: string]: string }): Observable<MessageOutput> {
    return this.httpRequestService.post('/course', paramMap);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(paramMap: { [key: string]: string }, request: CourseSaveRequest): Observable<MessageOutput> {
    return this.httpRequestService.put('/course', paramMap, request);
  }
}
