import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MessageOutput } from '../types/api-output';
import { CourseCreateRequest, CourseSaveRequest } from '../types/api-request';
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
  createCourse(paramMap: { [key: string]: string }, request: CourseCreateRequest): Observable<MessageOutput> {
    return this.httpRequestService.post('/course', paramMap, request);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(paramMap: { [key: string]: string }, request: CourseSaveRequest): Observable<MessageOutput> {
    return this.httpRequestService.put('/course', paramMap, request);
  }

  /**
   * Deletes a course by calling API.
   */
  deleteCourse(paramMap: { [key: string]: string }): Observable<MessageOutput> {
    return this.httpRequestService.delete('/course', paramMap);
  }
}
