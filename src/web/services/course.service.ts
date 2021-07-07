import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResourceEndpoints } from '../types/api-const';
import { Course, CourseArchive, Courses, HasResponses, JoinStatus, MessageOutput, Student } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

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

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Gets all course data for an instructor by calling API.
   */
  getAllCoursesAsInstructor(courseStatus: string): Observable<Courses> {
    const paramMap: Record<string, string> = {
      entitytype: 'instructor',
      coursestatus: courseStatus,
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSES, paramMap);
  }

  /**
   * Get course data by calling API as an instructor.
   */
  getCourseAsInstructor(courseId: string): Observable<Course> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      entitytype: 'instructor',
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSE, paramMap);
  }

  /**
   * Gets all course data for a student by calling API.
   */
  getAllCoursesAsStudent(): Observable<Courses> {
    const paramMap: Record<string, string> = {
      entitytype: 'student',
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSES, paramMap);
  }

  /**
   * Get course data by calling API as a student.
   */
  getCourseAsStudent(courseId: string): Observable<Course> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      entitytype: 'student',
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSE, paramMap);
  }

  /**
   * Get student courses data of a given google id in masquerade mode by calling API.
   */
  getStudentCoursesInMasqueradeMode(googleId: string): Observable<Courses> {
    const paramMap: Record<string, string> = {
      entitytype: 'student',
      user: googleId,
    };
    return this.httpRequestService.get(ResourceEndpoints.COURSES, paramMap);
  }

  /**
   * Get instructor courses data of a given google id in masquerade mode by calling API.
   */
  getInstructorCoursesInMasqueradeMode(googleId: string): Observable<Courses> {
    const activeCoursesParamMap: Record<string, string> = {
      coursestatus: 'active',
      entitytype: 'instructor',
      user: googleId,
    };
    const archivedCoursesParamMap: Record<string, string> = {
      coursestatus: 'archived',
      entitytype: 'instructor',
      user: googleId,
    };

    return forkJoin([
      this.httpRequestService.get(ResourceEndpoints.COURSES, activeCoursesParamMap),
      this.httpRequestService.get(ResourceEndpoints.COURSES, archivedCoursesParamMap),
    ]).pipe(
        map((vals: Courses[]) => {
          return {
            courses: vals[0].courses.concat(vals[1].courses),
          };
        }),
    );
  }

  /**
   * Get active instructor courses.
   */
  getInstructorCoursesThatAreActive(): Observable<Courses> {
    return this.httpRequestService.get(ResourceEndpoints.COURSES, {
      entitytype: 'instructor',
      coursestatus: 'active',
    });
  }

  /**
   * Creates a course by calling API.
   */
  createCourse(request: CourseCreateRequest): Observable<Course> {
    const paramMap: Record<string, string> = {};
    return this.httpRequestService.post(ResourceEndpoints.COURSE, paramMap, request);
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
   * Changes the archive status of a course by calling API.
   */
  changeArchiveStatus(courseid: string, request: CourseArchiveRequest): Observable<CourseArchive> {
    const paramMap: Record<string, string> = { courseid };
    return this.httpRequestService.put(ResourceEndpoints.COURSE_ARCHIVE, paramMap, request);
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
  getJoinCourseStatus(regKey: string, entityType: string): Observable<JoinStatus> {
    const paramMap: Record<string, string> = {
      key: regKey,
      entitytype: entityType,
    };
    return this.httpRequestService.get(ResourceEndpoints.JOIN, paramMap);
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKey: string, entityType: string, institute: string, institutemac: string): Observable<any> {
    const paramMap: Record<string, string> = {
      key: regKey,
      entitytype: entityType,
      instructorinstitution: institute,
      mac: institutemac,
    };
    return this.httpRequestService.put(ResourceEndpoints.JOIN, paramMap);
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
   * Send join reminder email to a student.
   */
  remindStudentForJoin(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.post(ResourceEndpoints.JOIN_REMIND, paramMap);
  }

  /**
   * Send join reminder email to an instructor.
   */
  remindInstructorForJoin(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.post(ResourceEndpoints.JOIN_REMIND, paramMap);
  }

  /**
   * Checks if there are responses for a course (request sent by instructor).
   */
  hasResponsesForCourse(courseId: string): Observable<HasResponses> {
    const paramMap: Record<string, string> = {
      entitytype: 'instructor',
      courseid: courseId,
    };
    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
  }

  /**
   * Removes student from course.
   */
  removeStudentFromCourse(courseId: string, studentEmail: string): Observable<any> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENT, paramsMap);
  }

  /**
   * Gets a list of course section names.
   */
  getCourseSectionNames(courseId: string): Observable<any> {
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
}
