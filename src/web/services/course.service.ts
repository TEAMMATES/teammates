import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MessageOutput } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseSaveRequest } from '../types/api-request';
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
   * Creates a course by calling API.
   */
  createCourse(courseid: string, request: CourseCreateRequest): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.post('/course', paramMap, request);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(courseid: string, request: CourseSaveRequest): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put('/course', paramMap, request);
  }

  /**
   * Deletes a course by calling API.
   */
  deleteCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.delete('/course', paramMap);
  }

  /**
   * Archives a course by calling API.
   */
  archiveCourse(courseid: string, request: CourseArchiveRequest): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put('/course/archive', paramMap, request);
  }
}
