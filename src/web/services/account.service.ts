import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminSearchResult, JoinLink, MessageOutput } from '../types/api-output';
import { AccountCreateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Represents course attributes.
 */
export interface CourseAttributes {
  id: string;
  name: string;
}

/**
 * Represents account attributes.
 */
export interface AccountAttributes {
  googleId: string;
  name: string;
  email: string;
  institute?: string;
  isInstructor: boolean;
}

/**
 * Represents detailed information of an account.
 */
export interface AccountInfo {
  accountInfo: AccountAttributes;
  instructorCourses: CourseAttributes[];
  studentCourses: CourseAttributes[];
}

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
  createAccount(paramMap: {[key: string]: string}, request: AccountCreateRequest): Observable<JoinLink> {
    return this.httpRequestService.post('/account', paramMap, request);
  }

  /**
   * Gets an account info by calling API.
   */
  getAccount(paramMap: {[key: string]: string}): Observable<AccountInfo> {
    return this.httpRequestService.get('/accounts', paramMap);
  }

  /**
   * Downgrades an account from instructor to student by calling API.
   */
  downgradeAccount(paramMap: {[key: string]: string}): Observable<MessageOutput> {
    return this.httpRequestService.put('/account/downgrade', paramMap);
  }

  /**
   * Deletes an account by calling API.
   */
  deleteAccount(paramMap: {[key: string]: string}): Observable<MessageOutput> {
    return this.httpRequestService.delete('/account', paramMap);
  }

  /**
   * Resets an account by calling API.
   */
  resetAccount(paramMap: {[key: string]: string}): Observable<MessageOutput> {
    return this.httpRequestService.put('/account/reset', paramMap);
  }

  /**
   * Search accounts by calling API.
   */
  searchAccounts(paramMap: {[key: string]: string}): Observable<AdminSearchResult> {
    return this.httpRequestService.get('/accounts/search', paramMap);
  }

}
