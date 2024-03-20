import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Account, Accounts, JoinLink, MessageOutput } from '../types/api-output';
import { AccountCreateRequest } from '../types/api-request';
import { AccountRequests } from '../types/api-output';
import { TimezoneService } from './timezone.service';
import { AccountRequestSearchResult } from './search.service';

/**
 * Handles account related logic provision
 */
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(
    private httpRequestService: HttpRequestService,
    private timezoneService: TimezoneService,
    ) {
  }

  /**
   * Creates an account by calling API.
   */
  createAccount(key: string, timezone: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = { key };
    if (timezone) {
      paramMap['timezone'] = timezone;
    }
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Creates an account request by calling API.
   */
  createAccountRequest(request: AccountCreateRequest): Observable<JoinLink> {
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST, {}, request);
  }

  /**
   * Deletes an account by calling API.
   */
  deleteAccount(id: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      instructorid: id,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Deletes an account request by calling API.
   */
  deleteAccountRequest(email: string, institute: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  }

  /**
   * Approves an account request by calling API.
   */
  approveAccountRequest(email: string, institute: string): Observable<MessageOutput> {
      return new Observable<MessageOutput>(observer => {
        observer.next({ message: 'Account request approved successfully, details: ' + email + ', ' + institute });
        observer.complete();
      })
  }

  /**
   * Rejects an account request by calling API.
   */
  rejectAccountRequest(email: string, institute: string, title?: string, reason?: string): Observable<MessageOutput> {
    // mock response for now
    return new Observable<MessageOutput>(observer => {
      observer.next({ message: 'Account request rejected successfully, details: ' + email + ', ' + institute  + ', ' + title + ', ' + reason});
      observer.complete();
    });

    // return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  }

  /**
   * Rejects an account request by calling API.
   */
    editAccountRequest(name: string, email: string, institute: string, comment: string): Observable<MessageOutput> {
      // return a mock response for now
      return new Observable<MessageOutput>(observer => {
        observer.next({ message: 'Account request edited successfully, details: ' + name + ', ' + email + ', ' + institute + ', ' + comment });
        observer.complete();
      })
    }

  /**
   * Resets an account request by calling API.
   */
  resetAccountRequest(email: string, institute: string): Observable<JoinLink> {
    const paramMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST_RESET, paramMap);
  }

  /**
   * Resets a student account by calling API.
   */
  resetStudentAccount(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  }

  /**
   * Resets an instructor account by calling API.
   */
  resetInstructorAccount(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  }

  /**
   * Gets an account by calling API.
   */
  getAccount(googleId: string): Observable<Account> {
    const paramMap: Record<string, string> = {
      instructorid: googleId,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Gets accounts by calling API.
   */
  getAccounts(email: string): Observable<Accounts> {
    const paramMap: Record<string, string> = {
      useremail: email,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNTS, paramMap);
  }



  private formatTimestampAsString(timestamp: number, timezone: string): string {
      const dateFormatWithZoneInfo: string = 'ddd, DD MMM YYYY, hh:mm A Z';

      return this.timezoneService
          .formatToString(timestamp, timezone, dateFormatWithZoneInfo);
  }

  private formatAccountRequests(requests: AccountRequests): AccountRequestSearchResult[] {
    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    return requests.accountRequests.map((request) => {
      return {
        email: request.email,
        institute: request.institute,
        name: request.name,
        status: request.status,
        registrationKey: request.registrationKey,
        registeredAt: request.registeredAt,
        registeredAtText: request.registeredAt ? this.formatTimestampAsString(request.registeredAt, timezone) : '',
        createdAt: request.createdAt,
        createdAtText: this.formatTimestampAsString(request.createdAt, timezone),
        comments: request.comments,
        registrationLink: '',
        showLinks: false,
      };
    });
  }

  /**
   * Gets account requests by calling API.
   */
  getPendingAccountRequests(): Observable<AccountRequestSearchResult[]> {
    // mock a response with all pending for now

    const requests: AccountRequests = {
      accountRequests: [
        {
          email: 'instructor1@gmail.com',
          institute: 'University of Toronto',
          name: 'John Doe',
          status: 'PENDING',
          registrationKey: '123456',
          createdAt: 1234567890,
          comments: 'This is a short comment',
        },
        {
          email: 'instructor2@gmail.com',
          institute: 'University of Toronto',
          name: 'Jane Doe',
          status: 'PENDING',
          registrationKey: '1234567',
          createdAt: 1234567890,
          comments: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Magna fringilla urna porttitor rhoncus dolor purus non enim praesent. Habitant morbi tristique senectus et.',
        },
      ]
    };

    const formattedRequests: AccountRequestSearchResult[] = this.formatAccountRequests(requests);

    return new Observable<AccountRequestSearchResult[]>(observer => {
      observer.next(formattedRequests);
      observer.complete();
    });

  }
}
