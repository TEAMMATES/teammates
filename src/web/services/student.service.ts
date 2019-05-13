import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Students } from '../types/api-output';
import { StudentsEnrollRequest } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles student related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StudentService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Get a list of students of a course by calling API.
   * If teamName is provided, only students in that team will be returned.
   * Otherwise, all students in the course will be returned.
   */
  getStudentsFromCourse(courseId: string, teamName?: string): Observable<Students> {
    if (teamName) {
      const paramsMapWithTeamName: { [key: string]: string } = {
        courseid: courseId,
        teamname: teamName,
      };
      return this.httpRequestService.get('/students', paramsMapWithTeamName);
    }
    const paramsMapWithoutTeamName: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.get('/students', paramsMapWithoutTeamName);

  }

  /**
   * Enroll a list of students to a course by calling API.
   * Students who are enrolled successfully will be returned.
   */
  enrollStudents(courseId: string, requestBody: StudentsEnrollRequest): Observable<Students> {
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
    };
    return this.httpRequestService.put('/students', paramsMap, requestBody);
  }

  /**
   * Gets all students in a course and team as a student by calling API.
   */
  getStudentsFromCourseAndTeam(courseId: string, teamName: string): Observable<Students> {
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
      teamname: teamName,
    };
    return this.httpRequestService.get('/students', paramsMap);
  }
}
