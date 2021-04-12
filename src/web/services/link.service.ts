import { Injectable } from '@angular/core';

import { Instructor, Student } from '../types/api-output';
import { NavigationService } from './navigation.service';

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
  INSTRUCTOR_STUDENT_RECORDS_PAGE: string = '/instructor/students/records';
  SESSIONS_SUBMISSION_PAGE: string = '/sessions/submission';
  SESSIONS_RESULT_PAGE: string = '/sessions/result';

  constructor(private navigationService: NavigationService) {}

  /**
   * Generates course join link for student.
   */
  generateCourseJoinLinkStudent(student: Student): string {
    const frontendUrl: string = window.location.origin;
    const { key = '', email: studentemail, courseId: courseid }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      key,
      studentemail,
      courseid,
      entitytype: 'student',
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates course join link for instructor.
   */
  generateCourseJoinLinkInstructor(instructor: Instructor): string {
    const frontendUrl: string = window.location.origin;
    const { key = '' }: Instructor = instructor;
    const params: {
      [key: string]: string,
    } = {
      key,
      entitytype: 'instructor',
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates home page link.
   */
  generateHomePageLink(googleId: string, homePage: string): string {
    const params: {[key: string]: string} = {
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
   * Generates record page link.
   */
  generateRecordsPageLink(student: Student, instructorGoogleId: string): string {
    const { courseId: courseid, email: studentemail }: Student = student;
    const frontendUrl: string = window.location.origin;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      studentemail,
      user: instructorGoogleId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.INSTRUCTOR_STUDENT_RECORDS_PAGE}${encodedParams}`;
  }

  /**
   * Generates submit url for a feedback session.
   */
  generateSubmitUrl(student: Student, fsname: string): string {
    const frontendUrl: string = window.location.origin;
    const { courseId: courseid, key = '', email: studentemail }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      key,
      studentemail,
      fsname,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.SESSIONS_SUBMISSION_PAGE}${encodedParams}`;
  }

  /**
   * Generates a result url for a feedback session.
   */
  generateResultUrl(student: Student, fsname: string): string {
    const frontendUrl: string = window.location.origin;
    const { courseId: courseid, key = '', email: studentemail }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      key,
      studentemail,
      fsname,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}${this.SESSIONS_RESULT_PAGE}${encodedParams}`;
  }

  /**
   * Removes params keys whose values are the empty string
   */
  filterEmptyParams(params: {[key: string]: string}): void {
    Object.keys(params).forEach((key: string) => {
      if (params[key] === '') {
        delete params[key];
      }
    });
  }
}
