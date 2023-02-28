import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CopyCourseModalResult } from '../app/components/copy-course-modal/copy-course-modal-model';
import { ResourceEndpoints } from '../types/api-const';
import { Course, CourseArchive, Courses, FeedbackSession, HasResponses, JoinStatus, MessageOutput, Student } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest, FeedbackSessionCreateRequest, ResponseVisibleSetting, SessionVisibleSetting } from '../types/api-request';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';
import { ProgressBarService } from './progress-bar.service';

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

  constructor(
    private httpRequestService: HttpRequestService,
    private progressBarService: ProgressBarService,
    private feedbackSessionsService: FeedbackSessionsService
  ) { }

  /**
   * Creates a new course with the selected feedback sessions
   */
  createCopiedCourse(result: CopyCourseModalResult): Observable<any> {
    console.log(result);

    // this.setIsCopyingCourse(true);
    // this.modifiedSessions = {};
    // this.numberOfSessionsCopied = 0;
    // this.totalNumberOfSessionsToCopy = result.totalNumberOfSessions;
    // this.copyProgressPercentage = 0;

    return this.createCourse(result.newCourseInstitute, {
      courseName: result.newCourseName,
      timeZone: result.newTimeZone,
      courseId: result.newCourseId,
    }).pipe(
      tap(() => {
        // Wrap in a Promise to wait for all feedback sessions to be copied
        const promise: Promise<void> = new Promise<void>((resolve: () => void) => {
          if (result.selectedFeedbackSessionList.size === 0) {
            this.progressBarService.updateProgress(100);
            resolve();

            return;
          }

          result.selectedFeedbackSessionList.forEach((session: FeedbackSession) => {
            this.copyFeedbackSession(session, result.newCourseId, result.newTimeZone, result.oldCourseId)
              .pipe(finalize(() => {
                this.numberOfSessionsCopied += 1;
                this.copyProgressPercentage =
                  Math.round(100 * this.numberOfSessionsCopied / this.totalNumberOfSessionsToCopy);
                this.progressBarService.updateProgress(this.copyProgressPercentage);

                if (this.numberOfSessionsCopied === this.totalNumberOfSessionsToCopy) {
                  resolve();
                }
              }))
              .subscribe();
          });
        });
      })
    );
    // .subscribe({
    //   next: () => {
    //     // Wrap in a Promise to wait for all feedback sessions to be copied
    //     const promise: Promise<void> = new Promise<void>((resolve: () => void) => {
    //       if (result.selectedFeedbackSessionList.size === 0) {
    //         this.progressBarService.updateProgress(100);
    //         resolve();

    //         return;
    //       }

    //       result.selectedFeedbackSessionList.forEach((session: FeedbackSession) => {
    //         this.copyFeedbackSession(session, result.newCourseId, result.newTimeZone, result.oldCourseId)
    //             .pipe(finalize(() => {
    //               this.numberOfSessionsCopied += 1;
    //               this.copyProgressPercentage =
    //                   Math.round(100 * this.numberOfSessionsCopied / this.totalNumberOfSessionsToCopy);
    //               this.progressBarService.updateProgress(this.copyProgressPercentage);

    //               if (this.numberOfSessionsCopied === this.totalNumberOfSessionsToCopy) {
    //                 resolve();
    //               }
    //             }))
    //             .subscribe();
    //       });
    //     });

    //     promise.then(() => {
    //       this.courseService
    //           .getCourseAsInstructor(result.newCourseId)
    //           .subscribe((course: Course) => {
    //             this.activeCourses.push(this.getCourseModelFromCourse(course));
    //             this.activeCoursesList.push(course);
    //             this.allCoursesList.push(course);
    //             this.activeCoursesDefaultSort();
    //             this.setIsCopyingCourse(false);
    //             if (Object.keys(this.modifiedSessions).length > 0) {
    //               this.simpleModalService.openInformationModal('Note On Modified Session Timings',
    //                   SimpleModalType.WARNING, this.modifiedTimestampsModal);
    //             } else {
    //               this.statusMessageService.showSuccessToast('The course has been added.');
    //             }
    //           });
    //     });
    //   },
    //   error: (resp: ErrorMessageOutput) => {
    //     this.statusMessageService.showErrorToast(resp.error.message);
    //     this.setIsCopyingCourse(false);
    //     this.hasLoadingFailed = true;
    //   },
    // });
  }

  // /**
  //  * Gets a CourseModel from courseID
  //  */
  // private getCourseModelFromCourse(course: Course): CourseModel {
  //   let canModifyCourse: boolean = false;
  //   let canModifyStudent: boolean = false;
  //   if (course.privileges) {
  //     canModifyCourse = course.privileges.canModifyCourse;
  //     canModifyStudent = course.privileges.canModifyStudent;
  //   }
  //   const isLoadingCourseStats: boolean = false;
  //   return { course, canModifyCourse, canModifyStudent, isLoadingCourseStats };
  // }

  /**
   * Copies a feedback session.
   */
  private copyFeedbackSession(fromFeedbackSession: FeedbackSession, newCourseId: string,
                              newTimeZone: string, oldCourseId: string): Observable<FeedbackSession> {
    return this.feedbackSessionsService.createFeedbackSession(newCourseId,
        this.toFbSessionCreationReqWithName(fromFeedbackSession, newTimeZone, oldCourseId));
  }

  /**
   * Creates a FeedbackSessionCreateRequest with the provided name.
   */
  private toFbSessionCreationReqWithName(fromFeedbackSession: FeedbackSession, newTimeZone: string,
                                         oldCourseId: string): FeedbackSessionCreateRequest {
    // Local constants
    const twoHoursBeforeNow = moment().tz(newTimeZone).subtract(2, 'hours')
        .valueOf();
    const twoDaysFromNowRoundedUp = moment().tz(newTimeZone).add(2, 'days').startOf('hour')
        .valueOf();
    const sevenDaysFromNowRoundedUp = moment().tz(newTimeZone).add(7, 'days').startOf('hour')
        .valueOf();
    const ninetyDaysFromNow = moment().tz(newTimeZone).add(90, 'days')
        .valueOf();
    const ninetyDaysFromNowRoundedUp = moment().tz(newTimeZone).add(90, 'days').startOf('hour')
        .valueOf();
    const oneHundredAndEightyDaysFromNow = moment().tz(newTimeZone).add(180, 'days')
        .valueOf();
    const oneHundredAndEightyDaysFromNowRoundedUp = moment().tz(newTimeZone).add(180, 'days')
        .startOf('hour')
        .valueOf();

    // Preprocess timestamps to adhere to feedback session timestamps constraints
    let isModified = false;

    let copiedSubmissionStartTimestamp = fromFeedbackSession.submissionStartTimestamp;
    if (copiedSubmissionStartTimestamp < twoHoursBeforeNow) {
      copiedSubmissionStartTimestamp = twoDaysFromNowRoundedUp;
      isModified = true;
    } else if (copiedSubmissionStartTimestamp > ninetyDaysFromNow) {
      copiedSubmissionStartTimestamp = ninetyDaysFromNowRoundedUp;
      isModified = true;
    }

    let copiedSubmissionEndTimestamp = fromFeedbackSession.submissionEndTimestamp;
    if (copiedSubmissionEndTimestamp < copiedSubmissionStartTimestamp) {
      copiedSubmissionEndTimestamp = sevenDaysFromNowRoundedUp;
      isModified = true;
    } else if (copiedSubmissionEndTimestamp > oneHundredAndEightyDaysFromNow) {
      copiedSubmissionEndTimestamp = oneHundredAndEightyDaysFromNowRoundedUp;
      isModified = true;
    }

    let copiedSessionVisibleSetting = fromFeedbackSession.sessionVisibleSetting;
    let copiedCustomSessionVisibleTimestamp = fromFeedbackSession.customSessionVisibleTimestamp!;
    const thirtyDaysBeforeSubmissionStart = moment(copiedSubmissionStartTimestamp)
        .tz(newTimeZone).subtract(30, 'days')
        .valueOf();
    const thirtyDaysBeforeSubmissionStartRoundedUp = moment(copiedSubmissionStartTimestamp)
        .tz(newTimeZone).subtract(30, 'days').startOf('hour')
        .valueOf();
    if (copiedSessionVisibleSetting === SessionVisibleSetting.CUSTOM) {
      if (copiedCustomSessionVisibleTimestamp < thirtyDaysBeforeSubmissionStart) {
        copiedCustomSessionVisibleTimestamp = thirtyDaysBeforeSubmissionStartRoundedUp;
        isModified = true;
      } else if (copiedCustomSessionVisibleTimestamp > copiedSubmissionStartTimestamp) {
        copiedSessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
        isModified = true;
      }
    }

    let copiedResponseVisibleSetting = fromFeedbackSession.responseVisibleSetting;
    const copiedCustomResponseVisibleTimestamp = fromFeedbackSession.customResponseVisibleTimestamp!;
    if (copiedResponseVisibleSetting === ResponseVisibleSetting.CUSTOM
        && ((copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
            && copiedCustomResponseVisibleTimestamp < copiedSubmissionStartTimestamp)
            || copiedCustomResponseVisibleTimestamp < copiedCustomSessionVisibleTimestamp)) {
      copiedResponseVisibleSetting = ResponseVisibleSetting.LATER;
      isModified = true;
    }

    if (isModified) {
      this.modifiedSessions[fromFeedbackSession.feedbackSessionName] = {
        oldTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(fromFeedbackSession.submissionStartTimestamp,
              fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(fromFeedbackSession.submissionEndTimestamp,
              fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: fromFeedbackSession.sessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(fromFeedbackSession.customSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
        newTimestamp: {
          submissionStartTimestamp: this.formatTimestamp(copiedSubmissionStartTimestamp, fromFeedbackSession.timeZone),
          submissionEndTimestamp: this.formatTimestamp(copiedSubmissionEndTimestamp, fromFeedbackSession.timeZone),
          sessionVisibleTimestamp: copiedSessionVisibleSetting === SessionVisibleSetting.AT_OPEN
              ? 'On submission opening time'
              : this.formatTimestamp(copiedCustomSessionVisibleTimestamp!, fromFeedbackSession.timeZone),
          responseVisibleTimestamp: '',
        },
      };

      if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (fromFeedbackSession.responseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].oldTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(fromFeedbackSession.customResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }

      if (copiedResponseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            'On session visible time';
      } else if (copiedResponseVisibleSetting === ResponseVisibleSetting.LATER) {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            'Not now (publish manually)';
      } else {
        this.modifiedSessions[fromFeedbackSession.feedbackSessionName].newTimestamp.responseVisibleTimestamp =
            this.formatTimestamp(copiedCustomResponseVisibleTimestamp!, fromFeedbackSession.timeZone);
      }
    }

    return {
      feedbackSessionName: fromFeedbackSession.feedbackSessionName,
      toCopyCourseId: oldCourseId,
      toCopySessionName: fromFeedbackSession.feedbackSessionName,
      instructions: fromFeedbackSession.instructions,

      submissionStartTimestamp: copiedSubmissionStartTimestamp,
      submissionEndTimestamp: copiedSubmissionEndTimestamp,
      gracePeriod: fromFeedbackSession.gracePeriod,

      sessionVisibleSetting: copiedSessionVisibleSetting,
      customSessionVisibleTimestamp: copiedCustomSessionVisibleTimestamp,

      responseVisibleSetting: copiedResponseVisibleSetting,
      customResponseVisibleTimestamp: fromFeedbackSession.customResponseVisibleTimestamp,

      isClosingEmailEnabled: fromFeedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: fromFeedbackSession.isPublishedEmailEnabled,
    };
  }

  // private formatTimestamp(timestamp: number, timeZone: string): string {
  //   return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM YYYY h:mm A');
  // }

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
  getCourseAsInstructor(courseId: string, regKey?: string): Observable<Course> {
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
