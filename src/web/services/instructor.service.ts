import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Instructor, InstructorPrivilege, Instructors } from '../types/api-output';
import { InstructorCreateRequest, InstructorPrivilegeUpdateRequest, Intent } from '../types/api-request';
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
  getInstructorsFromCourse(courseId: string, intent?: Intent): Observable<Instructors> {

    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };

    if (intent) {
      paramMap[intent] = intent;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS, paramMap);
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadInstructorPrivilege(queryParams: {
    courseId: string,
    sectionName?: string,
    feedbackSessionName?: string,
    instructorRole?: string,
    instructorEmail?: string,
  }):
    Observable<InstructorPrivilege> {

    const paramMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
    };

    if (queryParams.feedbackSessionName) {
      paramMap.fsname = queryParams.feedbackSessionName;
    }

    if (queryParams.sectionName) {
      paramMap.sectionname = queryParams.sectionName;
    }

    if (queryParams.instructorRole) {
      paramMap.sectionname = queryParams.instructorRole;
    }

    if (queryParams.instructorEmail) {
      paramMap.instructorEmail = queryParams.instructorEmail;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap);
  }

  updateInstructorPrivilege(queryParams: {
    courseId: string,
    instructorEmail: string,
    requestBody: InstructorPrivilegeUpdateRequest }): Observable<InstructorPrivilege> {
    const paramMap: any = {
      courseid: queryParams.courseId,
      instructoremail: queryParams.instructorEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap, queryParams.requestBody);
  }

  createInstructor(queryParams: { courseId: string, requestBody: InstructorCreateRequest }): Observable<Instructor> {
    const paramMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.post(ResourceEndpoints.INSTRUCTOR, paramMap, queryParams.requestBody);
  }

  updateInstructor(queryParams: { courseId: string, requestBody: InstructorCreateRequest }): Observable<Instructor> {
    const paramMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.put(ResourceEndpoints.INSTRUCTOR, paramMap, queryParams.requestBody);
  }

  destroyInstructor(queryParams: { courseId: string, instructorEmail: string }): Observable<any> {
    const paramMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
      instructoremail: queryParams.instructorEmail,
    };
    return this.httpRequestService.delete('/instructor', paramMap);
  }
}
