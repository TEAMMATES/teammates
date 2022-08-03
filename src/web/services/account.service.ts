import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import {
  Account,
  AccountRequest,
  AccountRequests,
  Accounts,
  JoinLink,
  MessageOutput,
} from '../types/api-output';
import {
  AccountRequestCreateIntent,
  AccountRequestCreateRequest,
  AccountRequestsGetIntent, AccountRequestStatusUpdateIntent,
  AccountRequestType, AccountRequestUpdateRequest,
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
  createAccountRequestAsAdmin(requestBody: AccountRequestCreateRequest): Observable<JoinLink> {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestCreateIntent.ADMIN_CREATE,
      accountrequesttype: AccountRequestType.INSTRUCTOR_ACCOUNT,
    };
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, requestBody);
  }

  /**
   * Updates an account request by calling API.
   */
  updateAccountRequest(email: string, institute: string,
                       request: AccountRequestUpdateRequest): Observable<AccountRequest> {
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap, request);
  }

  /**
   * Approves an account request by calling API.
   */
  approveAccountRequest(email: string, institute: string): Observable<AccountRequest> {
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
      intent: AccountRequestStatusUpdateIntent.TO_APPROVE,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
  }

  /**
   * Rejects an account request by calling API.
   */
  rejectAccountRequest(email: string, institute: string): Observable<AccountRequest> {
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
      intent: AccountRequestStatusUpdateIntent.TO_REJECT,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
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
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap);
  }

  /**
   * Resets an account request by calling API.
   */
  resetAccountRequest(email: string, institute: string): Observable<AccountRequest> {
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
      intent: AccountRequestStatusUpdateIntent.TO_RESET,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST_STATUS, paramsMap);
  }

  /**
   * Resets a student account by calling API.
   */
  resetStudentAccount(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramsMap);
  }

  /**
   * Resets an instructor account by calling API.
   */
  resetInstructorAccount(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramsMap);
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
   * Gets an account request by calling API.
   */
  getAccountRequest(email: string, institute: string): Observable<AccountRequest> {
    const paramsMap: Record<string, string> = {
      instructoremail: email,
      instructorinstitution: institute,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_REQUEST, paramsMap);
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

  /**
   * Gets all account requests submitted within the period by calling API.
   */
  getAccountRequestsWithinPeriod(startTime: number, endTime: number): Observable<AccountRequests> {
    const paramsMap: Record<string, string> = {
      intent: AccountRequestsGetIntent.WITHIN_PERIOD,
      starttime: `${startTime}`,
      endtime: `${endTime}`,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_REQUESTS, paramsMap);
  }

}
