import { Injectable } from '@angular/core';

import { WebPageEndpoints } from '../types/api-endpoints';
import { Instructor, Student } from '../types/api-output';
import { environment } from './../environments/environment';
import { NavigationService } from './navigation.service';

/**
 * Handles the logic for generating links on the client.
 */

@Injectable({
  providedIn: 'root',
})
export class LinkService {

  constructor(private navigationService: NavigationService) {}

  /**
   * Generates course join link for student.
   */
  generateCourseJoinLinkStudent(student: Student): string {
    const { frontendUrl }: { frontendUrl: string } = environment;
    const { key = '', email: studentemail, courseId: courseid }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      key,
      studentemail,
      courseid,
      entitytype: 'student',
    };
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${WebPageEndpoints.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates course join link for instructor.
   */
  generateCourseJoinLinkInstructor(instructor: Instructor): string {
    const { frontendUrl }: { frontendUrl: string } = environment;
    const { key = '' }: Instructor = instructor;
    const params: {
      [key: string]: string,
    } = {
      key,
      entitytype: 'instructor',
    };
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${WebPageEndpoints.JOIN_PAGE}${encodedParams}`;
  }

  /**
   * Generates home page link.
   */
  generateHomePageLink(googleId: string, homePage: string): string {
    const params: {[key: string]: string} = {
      user: googleId,
    };

    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${homePage}${encodedParams}`;
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
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${accountsPage}${encodedParams}`;
  }

  /**
   * Generates record page link.
   * If the instructor id is not valid, return empty string.
   */
  generateRecordsPageLink(student: Student, instructorGoogleId: string): string {
    const { courseId: courseid, email: studentemail }: Student = student;
    const { frontendUrl }: { frontendUrl: string } = environment;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      studentemail,
      user: instructorGoogleId,
    };
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${WebPageEndpoints.INSTRUCTOR_STUDENT_RECORDS_PAGE}${encodedParams}`;
  }

  /**
   * Generates submit url for a feedback session.
   */
  generateSubmitUrl(student: Student, fsName: string): string {
    const { frontendUrl }: { frontendUrl: string } = environment;
    const { courseId: courseid, key = '', email: studentemail }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      key,
      studentemail,
      fsName,
    };
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${WebPageEndpoints.SESSION_SUBMISSION_PAGE}${encodedParams}`;
  }

  /**
   * Generates a result url for a feedback session.
   */
  generateResultUrl(student: Student, fsName: string): string {
    const { frontendUrl }: { frontendUrl: string } = environment;
    const { courseId: courseid, key = '', email: studentemail }: Student = student;
    const params: {
      [key: string]: string,
    } = {
      courseid,
      key,
      studentemail,
      fsName,
    };
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${WebPageEndpoints.SESSIONS_RESULT_PAGE}${encodedParams}`;
  }
}
