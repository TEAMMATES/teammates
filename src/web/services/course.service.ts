import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, forkJoin, from, Observable, of, Subject, zip } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';
import { CopyCourseModalResult } from '../app/components/copy-course-modal/copy-course-modal-model';
import { ResourceEndpoints } from '../types/api-const';
import { Course, CourseArchive, Courses, HasResponses, JoinStatus, MessageOutput, Student } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest } from '../types/api-request';
import { FeedbackSessionsService, TweakedTimestampData } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';

export interface CourseModel {
  course: Course;
  canModifyCourse: boolean;
  canModifyStudent: boolean;
  isLoadingCourseStats: boolean;
}

/**
 * The statistics of a course
 */
export interface CourseStatistics {
  numOfSections: number;
  numOfTeams: number;
  numOfStudents: number;
}

const combineModified = (
  modified: Record<string, TweakedTimestampData | undefined>[],
): Record<string, TweakedTimestampData> =>
  modified.filter((v) => v !== undefined)
    .reduce((a, v) => ({ ...a, ...v }), {}) as Record<string, TweakedTimestampData>;

/**
 * Handles course related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {
  isCopyingCourse = new BehaviorSubject<boolean>(false);
  copyProgress = new Subject<number>();

  constructor(
    private httpRequestService: HttpRequestService,
    private feedbackSessionsService: FeedbackSessionsService,
    private instructorService: InstructorService,
  ) { }

  /**
   * Creates a new course with the selected feedback sessions
   */
  createCopiedCourse(result: CopyCourseModalResult): Observable<{
    course: Course,
    modified: Record<string, TweakedTimestampData>,
  }> {
    this.isCopyingCourse.next(true);

    let numberOfSessionsCopied = 0;
    const totalNumberOfSessionsToCopy = result.totalNumberOfSessions;
    let copyProgressPercentage = 0;

    return this.createCourse(result.newCourseInstitute, {
      courseName: result.newCourseName,
      timeZone: result.newTimeZone,
      courseId: result.newCourseId,
    }).pipe(
      mergeMap(() => combineLatest([...result.selectedFeedbackSessionList].map((sess) => {
        const { session, modified } = this.feedbackSessionsService.copyFeedbackSession(
          sess,
          result.newCourseId,
          result.newTimeZone,
          result.oldCourseId,
        );

        return from(session).pipe(
          tap(() => {
            numberOfSessionsCopied += 1;
            copyProgressPercentage = Math.round(100 * numberOfSessionsCopied / totalNumberOfSessionsToCopy);
            this.copyProgress.next(copyProgressPercentage);
          }),
          map(() => ({
            [sess.feedbackSessionName]: modified,
          })),
        );
      }))),
      mergeMap((modified) => zip(
        this.instructorService.getCourseAsInstructor(result.newCourseId),
        of(combineModified(modified)),
      )),
      map(([course, modified]) => {
        this.isCopyingCourse.next(false);

        return {
          course,
          modified,
        };
      }),
    );
  }

  /**
   * Gets a CourseModel from courseID
   */
  getCourseModelFromCourse(course: Course): CourseModel {
    let canModifyCourse: boolean = false;
    let canModifyStudent: boolean = false;
    if (course.privileges) {
      canModifyCourse = course.privileges.canModifyCourse;
      canModifyStudent = course.privileges.canModifyStudent;
    }
    const isLoadingCourseStats: boolean = false;
    return { course, canModifyCourse, canModifyStudent, isLoadingCourseStats };
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
  getCourseAsStudent(courseId: string, regKey?: string): Observable<Course> {
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
  createCourse(institute: string, request: CourseCreateRequest): Observable<Course> {
    const paramMap: Record<string, string> = {
      instructorinstitution: institute,
    };
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
  getJoinCourseStatus(regKey: string, entityType: string, isCreatingAccount: boolean): Observable<JoinStatus> {
    const paramMap: Record<string, string> = {
      key: regKey,
      entitytype: entityType,
    };

    if (isCreatingAccount) {
      paramMap['iscreatingaccount'] = 'true';
    }

    return this.httpRequestService.get(ResourceEndpoints.JOIN, paramMap);
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKey: string, entityType: string): Observable<any> {
    const paramMap: Record<string, string> = {
      key: regKey,
      entitytype: entityType,
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
