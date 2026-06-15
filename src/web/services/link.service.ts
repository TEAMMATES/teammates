import { Injectable, inject } from '@angular/core';

import { NavigationService } from './navigation.service';
import { Student } from '../types/api-output';

/**
 * Handles the logic for generating links on the client.
 */
@Injectable({
  providedIn: 'root',
})
export class LinkService {
  private readonly navigationService = inject(NavigationService);

  URI_PREFIX = '/web';
  JOIN_PAGE = '/join';
  STUDENT_HOME_PAGE = '/student/home';
  INSTRUCTOR_HOME_PAGE = '/instructor/home';
  ADMIN_ACCOUNTS_PAGE = '/admin/accounts';
  INSTRUCTOR_STUDENT_PROFILE_PAGE = '/instructor/courses/student/details';
  SESSIONS_SUBMISSION_PAGE = '/sessions/submission';
  SESSIONS_RESULT_PAGE = '/sessions/result';

  /**
   * Generates instructor welcome link for an account request.
   */
  generateAccountRegistrationLink(accountVerificationRequestId: string): string {
    const frontendUrl: string = globalThis.location.origin;
    const params: {
      [key: string]: string;
    } = {
      accountVerificationRequestId,
    };

    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${frontendUrl}${this.URI_PREFIX}/instructor-welcome${encodedParams}`;
  }

  /**
   * Generates home page link.
   */
  generateHomePageLink(accountId: string, homePage: string): string {
    const params: { [key: string]: string } = {
      masqueradeaccountid: accountId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${homePage}${encodedParams}`;
  }

  /**
   * Generates manage account link.
   */
  generateManageAccountLink(accountId: string, accountsPage: string): string {
    const params: {
      [key: string]: string;
    } = {
      accountid: accountId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${accountsPage}${encodedParams}`;
  }

  /**
   * Generates student profile page link.
   */
  generateProfilePageLink(student: Student, instructorAccountId: string): string {
    const { courseId: courseid, userId: userid }: Student = student;
    const params: {
      [key: string]: string;
    } = {
      courseid,
      userid,
      masqueradeaccountid: instructorAccountId,
    };

    this.filterEmptyParams(params);
    const encodedParams: string = this.navigationService.encodeParams(params);
    return `${this.URI_PREFIX}${this.INSTRUCTOR_STUDENT_PROFILE_PAGE}${encodedParams}`;
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
