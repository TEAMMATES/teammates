import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  Course,
  CourseSections,
  CourseView,
  Courses,
  InstructorCourses,
  JoinStatus,
  MessageOutput,
  Student,
} from '../types/api-output';
import { CourseCreateRequest, CourseUpdateRequest, RegKeyRequest } from '../types/api-request';

/**
 * The statistics of a course
 */
export interface CourseStatistics {
  numOfSections: number;
  numOfTeams: number;
  numOfStudents: number;
}

/**
 * Handles course related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Gets all course data for an instructor by calling API.
   */
  getAllCoursesAsInstructor(courseStatus: 'active' | 'softDeleted'): Observable<InstructorCourses> {
    const paramMap: Record<string, string> = {
      coursestatus: courseStatus,
    };
    return this.httpRequestService.get(ResourceEndpoints.INSTRUCTOR_COURSES, paramMap);
  }

  /**
   * Get course data by calling API as an instructor.
   */
  getCourseAsInstructor(courseId: string, regKey?: string): Observable<CourseView> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      entitytype: 'instructor',
    };
    if (regKey) {
      paramMap['key'] = regKey;
    }
    return this.httpRequestService.get(ResourceEndpoints.COURSE, paramMap);
  }

  /**
   * Gets all course data for a student by calling API.
   */
  getAllCoursesAsStudent(): Observable<Courses> {
    return this.httpRequestService.get(ResourceEndpoints.STUDENT_COURSES, {});
  }

  /**
   * Get course data by calling API as a student.
   */
  getCourseAsStudent(courseId: string, regKey?: string): Observable<CourseView> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      entitytype: 'student',
    };
    if (regKey) {
      paramMap['key'] = regKey;
    }
    return this.httpRequestService.get(ResourceEndpoints.COURSE, paramMap);
  }

  /**
   * Creates a course by calling API.
   */
  createCourse(request: CourseCreateRequest): Observable<Course> {
    return this.httpRequestService.post(ResourceEndpoints.COURSE, {}, request);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(courseid: string, request: CourseUpdateRequest): Observable<Course> {
    const paramMap: Record<string, string> = { courseid };
    return this.httpRequestService.put(ResourceEndpoints.COURSE, paramMap, request);
  }

  /**
   * Deletes a course by calling API.
   */
  deleteCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = { courseid };
    return this.httpRequestService.delete(ResourceEndpoints.COURSE, paramMap);
  }

  /**
   * Bin (soft-delete) a course by calling API.
   */
  binCourse(courseid: string): Observable<Course> {
    const paramMap: Record<string, string> = { courseid };
    return this.httpRequestService.put(ResourceEndpoints.BIN_COURSE, paramMap);
  }

  /**
   * Restore a soft-deleted course by calling API.
   */
  restoreCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = { courseid };
    return this.httpRequestService.delete(ResourceEndpoints.BIN_COURSE, paramMap);
  }

  /**
   * Get the status of whether the entity has joined the course by calling API.
   */
  getJoinCourseStatus(regKey: string): Observable<JoinStatus> {
    const paramMap: Record<string, string> = {
      key: regKey,
    };

    return this.httpRequestService.get(ResourceEndpoints.JOIN, paramMap);
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKeyRequest: RegKeyRequest): Observable<MessageOutput> {
    return this.httpRequestService.put(ResourceEndpoints.JOIN, {}, regKeyRequest);
  }

  /**
   * Send join reminder emails to unregistered students.
   */
  remindUnregisteredStudentsForJoin(courseId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
    };
    return this.httpRequestService.post(ResourceEndpoints.JOIN_REMIND, paramMap);
  }

  /**
   * Send join reminder email to a user.
   */
  remindUserForJoin(userId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      userid: userId,
    };
    return this.httpRequestService.post(ResourceEndpoints.JOIN_REMIND, paramMap);
  }

  /**
   * Gets the sections for a course.
   */
  getCourseSections(courseId: string): Observable<CourseSections> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSE_SECTIONS, paramsMap);
  }

  /**
   * Calculates the statistics for a course from a list of students in the course
   */
  calculateCourseStatistics(students: Student[]): CourseStatistics {
    const teams: Set<string> = new Set();
    const sections: Set<string> = new Set();
    students.forEach((student: Student) => {
      teams.add(student.teamName);
      sections.add(student.sectionName);
    });
    return {
      numOfSections: sections.size,
      numOfTeams: teams.size,
      numOfStudents: students.length,
    };
  }

  /**
   * Creates a demo course.
   */
  createDemoCourse(queryParams: { accountVerificationRequestId: string; timezone: string }): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      id: queryParams.accountVerificationRequestId,
      timezone: queryParams.timezone,
    };

    return this.httpRequestService.post(ResourceEndpoints.DEMO_COURSE, paramMap);
  }
}
