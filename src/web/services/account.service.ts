import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminSearchResult, JoinLink, MessageOutput } from '../types/api-output';
import { AccountCreateRequest } from '../types/api-request';
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
  createAccount(request: AccountCreateRequest): Observable<JoinLink> {
    return this.httpRequestService.post('/account', {}, request);
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
   * Resets a student account by calling API.
   */
  resetStudentAccount(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.put('/account/reset', paramMap);
  }

  /**
   * Resets an instructor account by calling API.
   */
  resetInstructorAccount(courseId: string, instructorEmail: string): Observable<MessageOutput> {
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
