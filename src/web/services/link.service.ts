import { Injectable } from '@angular/core';

import { NavigationService } from './navigation.service';
import { Instructor, Student } from '../types/api-output';

/**
 * Handles the logic for generating links on the client.
 */
@Injectable({
  providedIn: 'root',
})
export class LinkService {

  URI_PREFIX: string = '/web';
  JOIN_PAGE: string = '/join';
  STUDENT_HOME_PAGE: string = '/student/home';
  INSTRUCTOR_HOME_PAGE: string = '/instructor/home';
  ADMIN_ACCOUNTS_PAGE: string = '/admin/accounts';
  INSTRUCTOR_STUDENT_PROFILE_PAGE: string = '/instructor/courses/student/details';
  SESSIONS_SUBMISSION_PAGE: string = '/sessions/submission';
  SESSIONS_RESULT_PAGE: string = '/sessions/result';

  constructor(private navigationService: NavigationService) {}

  /**
   * Generates course join link for student/instructor.
   */
  generateCourseJoinLink(entity: Student | Instructor, entityType: string): string {
    const frontendUrl: string = window.location.origin;
    const key: string = entity.key || '';
    const params: {
      [key: string]: string,
    } = {
      key,
      entitytype: entityType,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates account registration link for instructor.
   */
  generateAccountRegistrationLink(registrationKey: string): string {
    const frontendUrl: string = window.location.origin;
    const params: {
      [key: string]: string,
    } = {
      iscreatingaccount: 'true',
      key: registrationKey,
    };

    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates home page link.
   */
  generateHomePageLink(googleId: string, homePage: string): string {
    const params: { [key: string]: string } = {
      user: googleId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${homePage}${encodedParams}`;
  }

  /**
   * Generates manage account link.
   */
  generateManageAccountLink(googleId: string, accountsPage: string): string {
    const params: {
      [key: string]: string,
    } = {
      instructorid: googleId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${accountsPage}${encodedParams}`;
  }

  /**
   * Generates student profile page link.
   */
  generateProfilePageLink(student: Student, instructorGoogleId: string): string {
    const { courseId: courseid, email: studentemail }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      studentemail,
      user: instructorGoogleId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${this.INSTRUCTOR_STUDENT_PROFILE_PAGE}${encodedParams}`;
  }

  /**
   * Generates submit url for a feedback session.
   */
  generateSubmitUrl(entity: Student | Instructor, fsname: string, isInstructor: boolean): string {
    const frontendUrl: string = window.location.origin;
    const courseId: string = entity.courseId;
    const key: string = entity.key || '';
    const params: {
      [key: string]: string,
    } = {
      key,
      fsname,
      courseid: courseId,
    };
    if (isInstructor) {
      params['entitytype'] = 'instructor';
    }

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.SESSIONS_SUBMISSION_PAGE}${encodedParams}`;
  }

  /**
   * Generates a result url for a feedback session.
   */
  generateResultUrl(entity: Student | Instructor, fsname: string, isInstructor: boolean): string {
    const frontendUrl: string = window.location.origin;
    const courseId: string = entity.courseId;
    const key: string = entity.key || '';
    const params: {
      [key: string]: string,
    } = {
      key,
      fsname,
      courseid: courseId,
    };
    if (isInstructor) {
      params['entitytype'] = 'instructor';
    }

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.SESSIONS_RESULT_PAGE}${encodedParams}`;
  }

  /**
   * Removes params keys whose values are the empty string
   */
  filterEmptyParams(params: { [key: string]: string }): void {
    Object.keys(params).forEach((key: string) => {
      if (params[key] === '') {
        delete params[key];
      }
    });
  }
}
