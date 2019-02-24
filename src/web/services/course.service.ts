import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JoinStatus, MessageOutput } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles course related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Join a course by calling API.
   */
  joinCourse(regKey: string, entityType: string): Observable<JoinStatus> {
    const paramMap: { [key: string]: string } = {
      key: regKey,
      entitytype: entityType,
    };
    return this.httpRequestService.get('/join', paramMap);
  }

  /**
   * Send join reminder emails to unregistered students.
   */
  remindUnregisteredStudentsForJoin(courseId: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }

  /**
   * Send join reminder email to a student.
   */
  remindStudentForJoin(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }

  /**
   * Send join reminder email to an instructor.
   */
  remindInstructorForJoin(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }
}
