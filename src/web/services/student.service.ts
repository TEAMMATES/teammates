import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { ResourceEndpoints } from '../types/api-endpoints';
import { Course, MessageOutput, Student, Students } from '../types/api-output';
import { StudentsEnrollRequest, StudentUpdateRequest } from '../types/api-request';
import { CsvHelper } from './csv-helper';
import { HttpRequestService } from './http-request.service';
import { StringHelper } from './string-helper';

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
  getStudentsFromCourse(queryParams: { courseId: string, teamName?: string }): Observable<Students> {
    const paramsMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
    };

    if (queryParams.teamName) {
      paramsMap.teamname = queryParams.teamName;
    }

    return this.httpRequestService.get(ResourceEndpoints.STUDENTS, paramsMap);

  }

  /**
   * Gets student of a course by calling API.
   *
   * <p> If both studentEmail and regKey are not provided, get the student of current logged-in user.
   *
   * @param courseId courseId of the course
   * @param studentEmail if provided, get the student of the course of the given email
   * @param regKey if provided, get the student of the course with regKey
   */
  getStudent(courseId: string, studentEmail?: string, regKey?: string): Observable<Student> {
    const paramsMap: { [key: string]: string } = {
      courseid: courseId,
    };
    if (studentEmail) {
      paramsMap.studentemail = studentEmail;
    }
    if (regKey) {
      paramsMap.key = regKey;
    }
    return this.httpRequestService.get(ResourceEndpoints.STUDENT, paramsMap);
  }

  /**
   * Updates the details of a student in a course by calling API.
   */
  updateStudent(queryParams: { courseId: string, studentEmail: string, requestBody: StudentUpdateRequest }):
      Observable<MessageOutput> {
    const paramsMap: { [key: string]: string } = {
      courseid: queryParams.courseId,
      studentemail: queryParams.studentEmail,
    };
    return this.httpRequestService.put(ResourceEndpoints.STUDENT, paramsMap, queryParams.requestBody);
  }

  /**
   * Deletes a student in a course by calling API.
   */
  deleteStudent(queryParams: {
    googleId: string,
    courseId: string,
  }): Observable<any> {
    const paramsMap: Record<string, string> = {
      googleid: queryParams.googleId,
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENT, paramsMap);
  }

  /**
   * Regenerates the links for a student in a course.
   */
  regenerateStudentCourseLinks(courseId: string, studentEmail: string): Observable<any> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.post(ResourceEndpoints.STUDENT_COURSE_LINKS_REGENERATION, paramsMap);
  }

  /**
   * Enroll a list of students to a course by calling API.
   * Students who are enrolled successfully will be returned.
   */
  enrollStudents(courseId: string, requestBody: StudentsEnrollRequest): Observable<Students> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
    };
    return this.httpRequestService.put(ResourceEndpoints.STUDENTS, paramsMap, requestBody);
  }

  /**
   * Gets all students in a course and team as a student by calling API.
   */
  getStudentsFromCourseAndTeam(courseId: string, teamName: string): Observable<Students> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      teamname: teamName,
    };
    return this.httpRequestService.get(ResourceEndpoints.STUDENTS, paramsMap);
  }

  /**
   * Deletes all students in a course by calling API.
   */
  deleteAllStudentsFromCourse(queryParams: { courseId: string }): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      courseid: queryParams.courseId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENTS, paramsMap);
  }

  /**
   * Loads list of students from a course in CSV format by calling API.
   */
  loadStudentListAsCsv(queryParams: { courseId: string }): Observable<string> {
    return this.httpRequestService.get(ResourceEndpoints.COURSE, {
      courseid: queryParams.courseId,
      entitytype: 'instructor',
    }).pipe(mergeMap((course: Course) => {
      return this.httpRequestService.get(ResourceEndpoints.STUDENTS, {
        courseid: queryParams.courseId,
      }).pipe(map((students: Students) => {
        return this.processStudentsToCsv(course.courseId, course.courseName, students.students);
      }));
    }));
  }

  processStudentsToCsv(courseId: string, courseName: string, students: Student[]): string {
    const csvRows: string[][] = [];
    csvRows.push(['Course ID:', StringHelper.removeExtraSpace(courseId)]);
    csvRows.push(['Course Name:', StringHelper.removeExtraSpace(courseName)]);
    csvRows.push([]);
    csvRows.push(['Section', 'Team', 'Full Name', 'Last Name', 'Status', 'Email']);
    students.forEach((student: Student) => {
      csvRows.push([
        student.sectionName ? StringHelper.removeExtraSpace(student.sectionName) : '',
        student.teamName ? StringHelper.removeExtraSpace(student.teamName) : '',
        StringHelper.removeExtraSpace(student.name),
        student.lastName ? StringHelper.removeExtraSpace(student.lastName) : '',
        StringHelper.removeExtraSpace(student.joinState),
        StringHelper.removeExtraSpace(student.email),
      ]);
    });
    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }
}
