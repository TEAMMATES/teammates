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
  createAccount(instructorEmail: string, request: AccountCreateRequest): Observable<JoinLink> {
    const paramMap: { [key: string]: string } = { instructorEmail };
    return this.httpRequestService.post('/account', paramMap, request);
  }

  /**
   * Gets an account info by calling API.
   */
  getAccount(instructorid: string): Observable<AccountInfo> {
    const paramMap: { [key: string]: string } = { instructorid };
    return this.httpRequestService.get('/accounts', paramMap);
  }

  /**
   * Downgrades an account from instructor to student by calling API.
   */
  downgradeAccount(id: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      instructorid: id,
    };
    return this.httpRequestService.put('/account/downgrade', paramMap);
  }

  /**
   * Deletes an account by calling API.
   */
  deleteAccount(id: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      instructorid: id,
    };
    return this.httpRequestService.delete('/account', paramMap);
  }

  /**
   * Resets an account by calling API.
   */
  resetAccount(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.put('/account/reset', paramMap);
  }

  /**
   * Search accounts by calling API.
   */
  searchAccounts(searchKey: string): Observable<AdminSearchResult> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get('/accounts/search', paramMap);
  }

}
