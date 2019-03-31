import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Course, CourseArchive, HasResponses, JoinStatus, MessageOutput } from '../types/api-output';
import { CourseArchiveRequest, CourseCreateRequest, CourseUpdateRequest } from '../types/api-request';
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
   * Get course data by calling API.
   */
  getCourse(courseId: string): Observable<Course> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/course', paramMap);
  }

  /**
   * Creates a course by calling API.
   */
  createCourse(request: CourseCreateRequest): Observable<Course> {
    const paramMap: { [key: string]: string } = {};
    return this.httpRequestService.post('/course', paramMap, request);
  }

  /**
   * Updates a course by calling API.
   */
  updateCourse(courseid: string, request: CourseUpdateRequest): Observable<Course> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put('/course', paramMap, request);
  }

  /**
   * Deletes a course by calling API.
   */
  deleteCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.delete('/course', paramMap);
  }

  /**
   * Changes the archive status of a course by calling API.
   */
  changeArchiveStatus(courseid: string, request: CourseArchiveRequest): Observable<CourseArchive> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put('/course/archive', paramMap, request);
  }

  /**
   * Bin (soft-delete) a course by calling API.
   */
  binCourse(courseid: string): Observable<Course> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.put('/bin/course', paramMap);
  }

  /**
   * Restore a soft-deleted course by calling API.
   */
  restoreCourse(courseid: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = { courseid };
    return this.httpRequestService.delete('/bin/course', paramMap);
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
    return this.httpRequestService.post('/join/remind', paramMap);
  }

  /**
   * Send join reminder email to a student.
   */
  remindStudentForJoin(courseId: string, studentEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.post('/join/remind', paramMap);
  }

  /**
   * Send join reminder email to an instructor.
   */
  remindInstructorForJoin(courseId: string, instructorEmail: string): Observable<MessageOutput> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      instructoremail: instructorEmail,
    };
    return this.httpRequestService.post('/join/remind', paramMap);
  }

  /**
   * Checks if there are responses for a course.
   */
  hasResponsesForCourse(courseId: string): Observable<HasResponses> {
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/hasResponses', paramMap);
  }
}
