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
   * Send reminder emails to everyone.
   */
  sendReminderEmail(courseId: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }

  /**
   * Send reminder emails to a student.
   */
  sendReminderEmailToStudent(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }

  /**
   * Send reminder emails to an instructor.
   */
  sendReminderEmailToInstructor(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.get('/join/remind', paramMap);
  }
}
