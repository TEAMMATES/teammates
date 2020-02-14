import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Course, CourseArchive, Courses,  HasResponses, JoinStatus, MessageOutput } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
const {
  BIN_COURSE,
  COURSE,
  COURSES,
  COURSE_ARCHIVE,
  HAS_RESPONSES,
  JOIN,
  JOIN_REMIND,
  STUDENT,
}: typeof ResourceEndpoints = ResourceEndpoints;

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
   * Gets all course data for an instructor by calling API.
   */
  getAllCoursesAsInstructor(courseStatus: string): Observable<Courses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      coursestatus: courseStatus,
    };
    return this.httpRequestService.get(COURSES, paramMap);
  }

  /**
   * Get course data by calling API as an instructor.
   */
  getCourseAsInstructor(courseId: string): Observable<Course> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      entitytype: 'instructor',
    };
    return this.httpRequestService.get(COURSE, paramMap);
  }

  /**
   * Gets all course data for a student by calling API.
   */
  getAllCoursesAsStudent(): Observable<Courses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
    };
    return this.httpRequestService.get(COURSES, paramMap);
  }

  /**
   * Get course data by calling API as a student.
   */
  getCourseAsStudent(courseId: string): Observable<Course> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      entitytype: 'student',
    };
    return this.httpRequestService.get(COURSE, paramMap);
  }

  /**
   * Get student courses data of a given google id in masquerade mode by calling API.
   */
  getStudentCoursesInMasqueradeMode(googleId: string): Observable<Courses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'student',
      user: googleId,
    };
    return this.httpRequestService.get(COURSES, paramMap);
  }

  /**
   * Get instructor courses data of a given google id in masquerade mode by calling API.
   */
  getInstructorCoursesInMasqueradeMode(googleId: string): Observable<Courses> {
    const activeCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'active',
      entitytype: 'instructor',
      user: googleId,
    };
    const archivedCoursesParamMap: { [key: string]: string } = {
      coursestatus: 'archived',
      entitytype: 'instructor',
      user: googleId,
    };

    return forkJoin(
        this.httpRequestService.get(COURSES, activeCoursesParamMap),
        this.httpRequestService.get(COURSES, archivedCoursesParamMap),
    ).pipe(
        map((vals: Courses[]) => {
          return {
            courses: vals[0].courses.concat(vals[1].courses),
          };
        }),
    );
  }

  /**
   * Creates a course by calling API.
   */
  createCourse(request: CourseCreateRequest): Observable<Course> {
    const paramMap: { [key: string]: string } = {};
    return this.httpRequestService.post(COURSE, paramMap, request);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(courseid: string, request: CourseUpdateRequest): Observable<Course> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put(COURSE, paramMap, request);
  }

  /**
   * Deletes a course by calling API.
   */
  deleteCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.delete(COURSE, paramMap);
  }

  /**
   * Changes the archive status of a course by calling API.
   */
  changeArchiveStatus(courseid: string, request: CourseArchiveRequest): Observable<CourseArchive> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put(COURSE_ARCHIVE, paramMap, request);
  }

  /**
   * Bin (soft-delete) a course by calling API.
   */
  binCourse(courseid: string): Observable<Course> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put(BIN_COURSE, paramMap);
  }

  /**
   * Restore a soft-deleted course by calling API.
   */
  restoreCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.delete(BIN_COURSE, paramMap);
  }

  /**
   * Get the status of whether the entity has joined the course by calling API.
   */
  getJoinCourseStatus(regKey: string, entityType: string): Observable<JoinStatus> {
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
    };
    return this.httpRequestService.get(JOIN, paramMap);
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKey: string, entityType: string, institute: string): Observable<any> {
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
      instructorinstitution: institute,
    };
    return this.httpRequestService.put(JOIN, paramMap);
  }

  /**
   * Send join reminder emails to unregistered students.
   */
  remindUnregisteredStudentsForJoin(courseId: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.post(JOIN_REMIND, paramMap);
  }

  /**
   * Send join reminder email to a student.
   */
  remindStudentForJoin(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.post(JOIN_REMIND, paramMap);
  }

  /**
   * Send join reminder email to an instructor.
   */
  remindInstructorForJoin(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.post(JOIN_REMIND, paramMap);
  }

  /**
   * Checks if there are responses for a course (request sent by instructor).
   */
  hasResponsesForCourse(courseId: string): Observable<HasResponses> {
    const paramMap: { [key: string]: string } = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    return this.httpRequestService.get(HAS_RESPONSES, paramMap);
  }

  /**
   * Removes student from course.
   */
  removeStudentFromCourse(courseId: string, studentEmail: string): Observable<any> {
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.delete(STUDENT, paramsMap);
  }
}
