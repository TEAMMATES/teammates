import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MessageOutput } from '../types/api-output';
import { StudentProfileUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { StudentDetails } from "../app/pages-student/student-profile-page/student-profile-page.component";

/**
 * Handles student profile related logic provision.
 */

@Injectable({
  providedIn: 'root',
})

export class StudentProfileService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Updates a student profile by calling API.
   */
  updateStudentProfile(user: string, googleId: string, requestBody: StudentProfileUpdateRequest)
      : Observable<MessageOutput> {
    const paramsMap: { [key: string]: string } = {
      user,
      googleid: googleId,
    };
    return this.httpRequestService.put('/student/profile', paramsMap, requestBody);
  }

  /**
   * Gets a student profile by calling API.
   */
  getStudentProfile(user: string, googleId: string): Observable<StudentDetails> {
    const paramsMap: { [key: string]: string } = {
      user,
      googleid: googleId,
    };
    return this.httpRequestService.get('/student/profile', paramsMap);
  }
}
