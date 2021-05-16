import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
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
      const paramsMap: Record<string, string> = {
        studentemail: studentEmail,
        courseid: courseId,
      };
      return this.httpRequestService.get(ResourceEndpoints.STUDENT_PROFILE, paramsMap);
    }
    return this.httpRequestService.get(ResourceEndpoints.STUDENT_PROFILE);
  }

  /**
   * Updates a student profile by calling API.
   */
  updateStudentProfile(googleId: string, requestBody: StudentProfileUpdateRequest)
      : Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      googleid: googleId,
    };
    return this.httpRequestService.put(ResourceEndpoints.STUDENT_PROFILE, paramsMap, requestBody);
  }

  /**
   * Gets the profile picture as blob image.
   */
  getProfilePicture(): Observable<Blob> {
    return this.httpRequestService.get(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, 'blob');
  }

  /**
   * Posts the profile picture.
   */
  postProfilePicture(formData: FormData): Observable<any> {
    return this.httpRequestService.post(ResourceEndpoints.STUDENT_PROFILE_PICTURE, {}, formData);
  }

  /**
   * Deletes the profile picture.
   */
  deleteProfilePicture(paramMap: Record<string, string>): Observable<any> {
    return this.httpRequestService.delete(ResourceEndpoints.STUDENT_PROFILE_PICTURE, paramMap);
  }
}
