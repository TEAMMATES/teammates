import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Students } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles student related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StudentService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Gets all students in a course as an instructor by calling API.
   */
  getStudentsFromCourse(courseId: string): Observable<Students> {
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/students', paramsMap);
  }
}
