import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  Account,
  AccountRequestCreateResponse,
  AccountRequests,
  Accounts,
  JoinLink,
  MessageOutput,
} from '../types/api-output';
import {
  AccountRequestCreateIntent,
  AccountRequestCreateRequest,
  AccountRequestsGetIntent,
  AccountRequestType,
} from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles account related logic provision
 */
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Creates an account by calling API.
   */
  createAccount(key: string, timezone: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = { key };
    if (timezone) {
      paramMap.timezone = timezone;
    }
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Creates an account request by calling API.
   */
  createAccountRequestAsAdmin(requestBody: AccountRequestCreateRequest): Observable<AccountRequestCreateResponse> {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestCreateIntent.ADMIN_CREATE,
      accountrequesttype: AccountRequestType.INSTRUCTOR_ACCOUNT,
      captcharesponse: '',
    };
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, requestBody);
  }

  /**
   * Creates an account request by calling API.
   */
  createAccountRequestAsPublic(queryParams: {
    accountRequestType: AccountRequestType,
    captchaResponse: string,
    requestBody: AccountRequestCreateRequest,
  }): Observable<AccountRequestCreateResponse> {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestCreateIntent.PUBLIC_CREATE,
      accountrequesttype: queryParams.accountRequestType,
      captcharesponse: queryParams.captchaResponse,
    };
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, queryParams.requestBody);
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

  /**
   * Gets all account requests pending processing by calling API.
   */
  getAccountRequestsPendingProcessing(): Observable<AccountRequests> {
    const paramMap: Record<string, string> = {
      intent: AccountRequestsGetIntent.PENDING_PROCESSING,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_REQUESTS, paramMap);
  }

}
