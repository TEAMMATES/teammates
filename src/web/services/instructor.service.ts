import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Instructors } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles instructor related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class InstructorService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Get a list of instructors of a course by calling API.
   */
  getInstructorsFromCourse(courseId: string): Observable<Instructors> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS, paramMap);
  }
}
