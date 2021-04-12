import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
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
  loadInstructors(queryParams: { courseId: string, intent?: Intent }): Observable<Instructors> {

    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };

    if (queryParams.intent) {
      paramMap.intent = queryParams.intent;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS, paramMap);
  }

  /**
   * Get an instructor in a course by calling API.
   */
  getInstructor(queryParams: {
    courseId: string,
    feedbackSessionName?: string,
    intent: Intent,
    key?: string,
    moderatedPerson?: string,
    previewAs?: string,
  }): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      intent: queryParams.intent,
    };
    if (queryParams.feedbackSessionName) {
      paramMap.fsname = queryParams.feedbackSessionName;
    }
    if (queryParams.key) {
      paramMap.key = queryParams.key;
    }
    if (queryParams.moderatedPerson) {
      paramMap.moderatedperson = queryParams.moderatedPerson;
    }
    if (queryParams.previewAs) {
      paramMap.previewas = queryParams.previewAs;
    }
    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR, paramMap);
  }

  /**
   * Creates an instructor in a course by calling API.
   */
  createInstructor(queryParams: { courseId: string, requestBody: InstructorCreateRequest }): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.post(ResourceEndpoints.INSTRUCTOR, paramMap, queryParams.requestBody);
  }

  /**
   * Updates an instructor in a course by calling API.
   */
  updateInstructor(queryParams: { courseId: string, requestBody: InstructorCreateRequest }): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.put(ResourceEndpoints.INSTRUCTOR, paramMap, queryParams.requestBody);
  }

  /**
   * Deletes an instructor from a course by calling API.
   */
  deleteInstructor(queryParams: {
    courseId: string,
    instructorEmail?: string,
    instructorId?: string,
  }): Observable<any> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };

    if (queryParams.instructorEmail) {
      paramMap.instructoremail = queryParams.instructorEmail;
    }

    if (queryParams.instructorId) {
      paramMap.instructorid = queryParams.instructorId;
    }

    return this.httpRequestService.delete(ResourceEndpoints.INSTRUCTOR, paramMap);
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
    instructorId?: string,
  }):
    Observable<InstructorPrivilege> {

    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };

    if (queryParams.feedbackSessionName) {
      paramMap.fsname = queryParams.feedbackSessionName;
    }

    if (queryParams.sectionName) {
      paramMap.sectionname = queryParams.sectionName;
    }

    if (queryParams.instructorRole) {
      paramMap.instructorrole = queryParams.instructorRole;
    }

    if (queryParams.instructorEmail) {
      paramMap.instructoremail = queryParams.instructorEmail;
    }

    if (queryParams.instructorId) {
      paramMap.instructorid = queryParams.instructorId;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap);
  }

  /**
   * Updates the privilege of an instructor for a specified course.
   */
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
}
