import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import {
  AccountVerificationRequest,
  AccountVerificationRequests,
  MessageOutput,
  AccountVerificationRequestStatus,
  Account,
} from '../types/api-output';
import {
  AccountCreateRequest,
  AccountVerificationRequestRejectionRequest,
  AccountVerificationRequestUpdateRequest,
  LinkAccountRequest,
} from '../types/api-request';

export interface AccountVerificationRequestQueryParams {
  instituteId?: string;
  accountId?: string;
  status?: AccountVerificationRequestStatus;
  searchKey?: string;
  limit?: number;
}

/**
 * Handles account related logic provision
 */
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private httpRequestService = inject(HttpRequestService);

  /**
   * Creates an account verification request by calling API.
   */
  createAccountVerificationRequest(request: AccountCreateRequest): Observable<AccountVerificationRequest> {
    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST, {}, request);
  }

  /**
   * Deletes an account by calling API.
   */
  deleteAccount(accountId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.ACCOUNT_ID]: accountId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Deletes an account verification request by calling API.
   */
  deleteAccountVerificationRequest(id: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      id,
    };
    return this.httpRequestService.delete(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST, paramMap);
  }

  /**
   * Unlinks a user account by calling API.
   */
  unlinkAccount(userId: string): Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.USER_ID]: userId,
    };
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_UNLINK, paramMap);
  }

  /**
   * Links the current account to a student by calling API.
   */
  linkAccount(request: LinkAccountRequest, key: string): Observable<MessageOutput> {
    return this.httpRequestService.put(ResourceEndpoints.ACCOUNT_LINK, { key }, request);
  }

  /**
   * Approves account verification request by calling API
   */
  approveAccountVerificationRequest(id: string): Observable<AccountVerificationRequest> {
    const paramMap: Record<string, string> = {
      id,
    };

    return this.httpRequestService.post(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST_APPROVE, paramMap);
  }

  /**
   * Edits an account verification request by calling API.
   */
  editAccountVerificationRequest(
    id: string,
    accountReqUpdateRequest: AccountVerificationRequestUpdateRequest,
  ): Observable<AccountVerificationRequest> {
    const paramMap: Record<string, string> = {
      id,
    };
    return this.httpRequestService.put(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST,
      paramMap,
      accountReqUpdateRequest,
    );
  }

  /**
   * Gets an account verification request by calling API.
   */
  getAccountVerificationRequest(id: string): Observable<AccountVerificationRequest> {
    const paramMap: Record<string, string> = { id };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST, paramMap);
  }

  /**
   * Gets an account by calling API.
   */
  getAccount(accountId: string): Observable<Account> {
    const paramMap: Record<string, string> = {
      [QueryParamKeys.ACCOUNT_ID]: accountId,
    };
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT, paramMap);
  }

  /**
   * Gets account verification requests by calling API.
   */
  getAccountVerificationRequests(
    queryParams: AccountVerificationRequestQueryParams = {},
  ): Observable<AccountVerificationRequests> {
    const paramMap: Record<string, string> = {};
    if (queryParams.instituteId) {
      paramMap['instituteid'] = queryParams.instituteId;
    }
    if (queryParams.accountId) {
      paramMap['accountid'] = queryParams.accountId;
    }
    if (queryParams.status) {
      paramMap['status'] = queryParams.status;
    }
    if (queryParams.searchKey) {
      paramMap['searchkey'] = queryParams.searchKey;
    }
    if (queryParams.limit !== undefined) {
      paramMap['limit'] = String(queryParams.limit);
    }
    return this.httpRequestService.get(ResourceEndpoints.ACCOUNT_VERIFICATION_REQUESTS, paramMap);
  }

  /**
   * Rejects an account verification request by calling API.
   */
  rejectAccountVerificationRequest(
    queryParams: { id: string },
    rejectionRequest: AccountVerificationRequestRejectionRequest,
  ): Observable<AccountVerificationRequest> {
    const paramMap: Record<string, string> = {
      id: queryParams.id,
    };
    return this.httpRequestService.post(
      ResourceEndpoints.ACCOUNT_VERIFICATION_REQUEST_REJECT,
      paramMap,
      rejectionRequest,
    );
  }
}
