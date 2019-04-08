import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MessageOutput, StudentProfile } from '../types/api-output';
import { StudentProfileUpdateRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

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
   * Gets a student profile by calling API.
   * If both studentEmail and courseId are provided, it returns profile of that student.
   * If either one is missing, it returns the profile of the current login student.
   */
  getStudentProfile(studentEmail?: string, courseId?: string): Observable<StudentProfile> {
    if (studentEmail && courseId) {
      const paramsMap: { [key: string]: string } = {
        studentemail: studentEmail,
        courseid: courseId,
      };
      return this.httpRequestService.get('/student/profile', paramsMap);
    }
    return this.httpRequestService.get('/student/profile');
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
}
