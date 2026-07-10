import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { Instructor, InstructorPrivilege, Instructors, MessageOutput } from '../types/api-output';
import { InstructorCreateRequest, InstructorUpdateRequest } from '../types/api-request';

/**
 * Handles instructor related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class InstructorService {
  private readonly httpRequestService = inject(HttpRequestService);

  /**
   * Get instructors by course ID or search key by calling API.
   */
  loadInstructors(queryParams: { courseId?: string; searchKey?: string; limit?: number }): Observable<Instructors> {
    const paramMap: Record<string, string> = {};

    if (queryParams.courseId !== undefined) {
      paramMap['courseid'] = queryParams.courseId;
    }
    if (queryParams.searchKey !== undefined) {
      paramMap['searchkey'] = queryParams.searchKey;
    }
    if (queryParams.limit !== undefined) {
      paramMap['limit'] = String(queryParams.limit);
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS, paramMap);
  }

  /**
   * Get the student-visible list of instructors of a course by calling API.
   */
  loadDisplayedInstructors(queryParams: { courseId: string }): Observable<Instructors> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTORS_DISPLAYED, paramMap);
  }

  /**
   * Get an instructor by user ID by calling API.
   */
  getInstructor(queryParams: { userId: string }): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: queryParams.userId,
    };

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR, paramMap);
  }

  /**
   * Get the instructor associated with the current request in a course by calling API.
   */
  getOwnInstructor(queryParams: { courseId: string }): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };

    return this.httpRequestService.get(ResourceEndpoints.OWN_INSTRUCTOR, paramMap);
  }

  /**
   * Creates an instructor in a course by calling API.
   */
  createInstructor(queryParams: { courseId: string }, requestBody: InstructorCreateRequest): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.post(ResourceEndpoints.INSTRUCTOR, paramMap, requestBody);
  }

  /**
   * Updates an instructor in a course by calling API.
   */
  updateInstructor(
    queryParams: { instructorId: string },
    requestBody: InstructorUpdateRequest,
  ): Observable<Instructor> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: queryParams.instructorId,
    };
    return this.httpRequestService.put(ResourceEndpoints.INSTRUCTOR, paramMap, requestBody);
  }

  /**
   * Deletes an instructor by calling API.
   */
  deleteInstructor(queryParams: { userId: string }): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: queryParams.userId,
    };

    return this.httpRequestService.delete(ResourceEndpoints.INSTRUCTOR, paramMap);
  }

  /**
   * Loads privilege of an instructor for a specified course or user.
   *
   * The query parameter can either be `courseId` or `userId`.
   * If courseId is provided, the API will return the privilege of the current logged in instructor for the specified course.
   * If userId is provided, the API will return the privilege of the instructor with the specified userId.
   */
  loadInstructorPrivilege(queryParams: { courseId: string } | { userId: string }): Observable<InstructorPrivilege> {
    const paramMap: Record<string, string> = {};

    if ('courseId' in queryParams) {
      paramMap['courseid'] = queryParams.courseId;
    } else {
      paramMap['userid'] = queryParams.userId;
    }

    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, paramMap);
  }
}
