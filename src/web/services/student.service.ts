import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { CourseService } from './course.service';
import { CsvHelper } from './csv-helper';
import { HttpRequestService } from './http-request.service';
import { TableComparatorService } from './table-comparator.service';
import { JoinStatePipe } from '../app/components/student-list/join-state.pipe';
import { ResourceEndpoints } from '../types/api-const';
import { Course, EnrollStudents, MessageOutput, RegenerateKey, Student, Students } from '../types/api-output';
import { StudentsEnrollRequest, StudentUpdateRequest } from '../types/api-request';
import { SortBy, SortOrder } from '../types/sort-properties';

/**
 * Handles student related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StudentService {

  constructor(private httpRequestService: HttpRequestService,
              private tableComparatorService: TableComparatorService,
              private courseService: CourseService) {
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
      paramsMap['teamname'] = queryParams.teamName;
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
      paramsMap['studentemail'] = studentEmail;
    }
    if (regKey) {
      paramsMap['key'] = regKey;
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
   * Regenerates the registration key for a student in a course.
   */
  regenerateStudentKey(courseId: string, studentEmail: string): Observable<RegenerateKey> {
    const paramsMap: Record<string, string> = {
      courseid: courseId,
      studentemail: studentEmail,
    };
    return this.httpRequestService.post(ResourceEndpoints.STUDENT_KEY, paramsMap);
  }

  /**
   * Enroll a list of students to a course by calling API.
   * Students who are enrolled successfully will be returned.
   */
  enrollStudents(courseId: string, requestBody: StudentsEnrollRequest): Observable<EnrollStudents> {
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
   * Deletes up to a limited number of students in a course by calling API.
   */
  batchDeleteStudentsFromCourse(queryParams: { courseId: string, limit: number }): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      courseid: queryParams.courseId,
      limit: queryParams.limit.toString(),
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENTS, paramsMap);
  }

  /**
   * Loads list of students from a course in CSV format by calling API.
   */
  loadStudentListAsCsv(queryParams: { courseId: string }): Observable<string> {
    return this.courseService.getCourseAsInstructor(queryParams.courseId).pipe(mergeMap((course: Course) => {
      return this.getStudentsFromCourse({ courseId: queryParams.courseId }).pipe(map((students: Students) => {
        return this.processStudentsToCsv(course.courseId, course.courseName, students.students);
      }));
    }));
  }

  processStudentsToCsv(courseId: string, courseName: string, students: Student[]): string {
    const csvRows: string[][] = [];
    csvRows.push(['Course ID', courseId]);
    csvRows.push(['Course Name', courseName]);
    csvRows.push([]);
    const hasSection: boolean =
        students.some((student: Student) => student.sectionName !== 'None' && student.sectionName !== '');
    const headers: string[] = ['Team', 'Name', 'Status', 'Email'];
    csvRows.push(hasSection ? ['Section'].concat(headers) : headers);
    students.sort((a: Student, b: Student) => {
      return this.tableComparatorService.compare(SortBy.SECTION_NAME, SortOrder.ASC, a.sectionName, b.sectionName)
          || this.tableComparatorService.compare(SortBy.TEAM_NAME, SortOrder.ASC, a.teamName, b.teamName)
          || this.tableComparatorService.compare(SortBy.RESPONDENT_NAME, SortOrder.ASC, a.name, b.name);
    });
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();
    students.forEach((student: Student) => {
      const studentRow: string[] = [
        student.teamName ? student.teamName : '',
        student.name,
        joinStatePipe.transform(student.joinState),
        student.email,
      ];
      csvRows.push(hasSection ? [student.sectionName].concat(studentRow) : studentRow);
    });
    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }
}
