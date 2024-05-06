import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import {
  Account,
  AccountRequest,
  Accounts,
  AccountRequests,
  JoinLink,
  MessageOutput,
  AccountRequestStatus,
} from '../types/api-output';
import {
  AccountCreateRequest,
  AccountRequestUpdateRequest,
  AccountRequestRejectionRequest,
} from '../types/api-request';

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
      paramMap['timezone'] = timezone;
    }
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Creates an account request by calling API.
   */
  createAccountRequest(request: AccountCreateRequest): Observable<AccountRequest> {
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
  deleteAccountRequest(id: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      id,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_REQUEST, paramMap);
  }

  /**
   * Resets an account request by calling API.
   */
  resetAccountRequest(id: string): Observable<JoinLink> {
    const paramMap: Record<string, string> = {
      id,
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
   * Approves account request by calling API
   */
  approveAccountRequest(id: string, name: string, email: string, institute: string)
  : Observable<AccountRequest> {
    const paramMap: Record<string, string> = {
      id,
    };
    const accountReqUpdateRequest : AccountRequestUpdateRequest = {
      name,
      email,
      institute,
      status: AccountRequestStatus.APPROVED,
    };

    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST, paramMap, accountReqUpdateRequest);
  }

  /**
   * Edits an account request by calling API.
   */
  editAccountRequest(id: string, accountReqUpdateRequest: AccountRequestUpdateRequest): Observable<AccountRequest> {
    const paramMap: Record<string, string> = {
      id,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_REQUEST, paramMap, accountReqUpdateRequest);
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
   * Gets account requests by calling API.
   */
  getPendingAccountRequests(): Observable<AccountRequests> {
    const paramMap = {
      status: AccountRequestStatus.PENDING,
    };

    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_REQUESTS, paramMap);
  }

  /**
   * Rejects an account request by calling API.
   */
  rejectAccountRequest(id: string, title?: string, body?: string): Observable<AccountRequest> {
    let accountReqRejectRequest: AccountRequestRejectionRequest = {};

    if (title !== undefined && body !== undefined) {
      accountReqRejectRequest = {
        reasonTitle: title,
        reasonBody: body,
      };
    }

    const paramMap: Record<string, string> = {
      id,
    };

    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST_REJECT, paramMap, accountReqRejectRequest);
  }

}
