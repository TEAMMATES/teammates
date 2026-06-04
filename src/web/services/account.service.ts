import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { AccountRequest, AccountRequests, MessageOutput, AccountRequestStatus, Account } from '../types/api-output';
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
  private httpRequestService = inject(HttpRequestService);

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
  deleteAccount(accountId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      accountid: accountId,
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
   * Resets a user account by calling API.
   */
  resetAccount(userId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      userid: userId,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_RESET, paramMap);
  }

  /**
   * Approves account request by calling API
   */
  approveAccountRequest(id: string): Observable<AccountRequest> {
    const paramMap: Record<string, string> = {
      id,
    };

    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_REQUEST_APPROVE, paramMap);
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
  getAccount(accountId: string): Observable<Account> {
    const paramMap: Record<string, string> = {
      accountid: accountId,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT, paramMap);
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
