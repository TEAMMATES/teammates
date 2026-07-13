import { Injectable } from '@angular/core';

/**
 * Handles the logic for generating links on the client.
 */
@Injectable({
  providedIn: 'root',
})
export class LinkService {
  URI_PREFIX = '/web';
  JOIN_PAGE = '/join';
  STUDENT_HOME_PAGE = '/student/home';
  INSTRUCTOR_HOME_PAGE = '/instructor/home';
  ADMIN_ACCOUNTS_PAGE = '/admin/accounts';
  SESSIONS_SUBMISSION_PAGE = '/sessions/{feedbackSessionId}/submission';
  SESSIONS_RESULT_PAGE = '/sessions/{feedbackSessionId}/result';

  /**
   * Generates instructor welcome link.
   */
  generateInstructorWelcomeLink(accountVerificationRequestId: string): string {
    const frontendUrl: string = globalThis.location.origin;
    return `${frontendUrl}${this.URI_PREFIX}/instructor/welcome/${accountVerificationRequestId}`;
  }

  /**
   * Generates instructor home page link.
   */
  generateInstructorHomePageLink(): string {
    return `${this.URI_PREFIX}${this.INSTRUCTOR_HOME_PAGE}`;
  }

  /**
   * Generates manage account link.
   */
  generateManageAccountLink(accountId: string, _accountsPage: string): string {
    return `${this.URI_PREFIX}${this.ADMIN_ACCOUNTS_PAGE}/${accountId}`;
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
