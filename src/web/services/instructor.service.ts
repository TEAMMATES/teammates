import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CourseTab } from '../app/pages-instructor/instructor-student-list-page/instructor-student-list-page.component';
import { ResourceEndpoints } from '../types/api-endpoints';
import { InstructorPrivilege, Instructors } from '../types/api-output';
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

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadInstructorPrivilege(courseTab: CourseTab, sectionName: string): Observable<InstructorPrivilege> {
    return this.httpRequestService.get('/instructor/privilege', {
      courseid: courseTab.course.courseId,
      sectionname: sectionName,
    });
  }
}
