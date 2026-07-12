import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { CourseService } from './course.service';
import { CsvHelper } from './csv-helper';
import { HttpRequestService } from './http-request.service';
import { TableComparatorService } from './table-comparator.service';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import { CourseView, EnrollStudents, MessageOutput, Student, Students } from '../types/api-output';
import { StudentsEnrollRequest, StudentUpdateRequest } from '../types/api-request';
import { SortBy, SortOrder } from '../types/sort-properties';
import { joinStateToString } from '../app/utils/join-state.util';

/**
 * Handles student related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private httpRequestService = inject(HttpRequestService);
  private tableComparatorService = inject(TableComparatorService);
  private courseService = inject(CourseService);

  /**
   * Get a list of students by calling API.
   */
  getStudents(queryParams: { courseIds?: string[]; searchKey?: string; limit?: number }): Observable<Students> {
    const paramsMap: { [key: string]: string | string[] } = {};

    if (queryParams.courseIds !== undefined) {
      paramsMap['courseid'] = queryParams.courseIds;
    }
    if (queryParams.searchKey !== undefined) {
      paramsMap['searchkey'] = queryParams.searchKey;
    }
    if (queryParams.limit !== undefined) {
      paramsMap['limit'] = String(queryParams.limit);
    }

    return this.httpRequestService.get(ResourceEndpoints.STUDENTS, paramsMap);
  }

  /**
   * Get the list of students in the current student's own team by calling API.
   */
  getOwnTeamStudents(queryParams: { courseId: string }): Observable<Students> {
    const paramsMap: { [key: string]: string } = {
      [QueryParamKeys.COURSE_ID]: queryParams.courseId,
    };

    return this.httpRequestService.get(ResourceEndpoints.OWN_TEAM_STUDENTS, paramsMap);
  }

  /**
   * Gets a student by user ID by calling API.
   */
  getStudent(queryParams: { userId: string }): Observable<Student> {
    const paramsMap: { [key: string]: string } = {
      userid: queryParams.userId,
    };

    return this.httpRequestService.get(ResourceEndpoints.STUDENT, paramsMap);
  }

  /**
   * Gets the student associated with the current request by calling API.
   */
  getOwnStudent(queryParams: { courseId: string; key?: string }): Observable<Student> {
    const paramsMap: { [key: string]: string } = {
      [QueryParamKeys.COURSE_ID]: queryParams.courseId,
    };
    if (queryParams.key) {
      paramsMap['key'] = queryParams.key;
    }

    return this.httpRequestService.get(ResourceEndpoints.OWN_STUDENT, paramsMap);
  }

  /**
   * Updates the details of a student in a course by calling API.
   */
  updateStudent(
    queryParams: {
      userId: string;
    },
    requestBody: StudentUpdateRequest,
  ): Observable<MessageOutput> {
    const paramsMap: { [key: string]: string } = {
      userid: queryParams.userId,
    };
    return this.httpRequestService.put(ResourceEndpoints.STUDENT, paramsMap, requestBody);
  }

  /**
   * Deletes a student by calling API.
   */
  deleteStudent(queryParams: { userId: string }): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      userid: queryParams.userId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENT, paramsMap);
  }

  /**
   * Enroll a list of students to a course by calling API.
   * Students who are enrolled successfully will be returned.
   */
  enrollStudents(courseId: string, requestBody: StudentsEnrollRequest): Observable<EnrollStudents> {
    const paramsMap: Record<string, string> = {
      [QueryParamKeys.COURSE_ID]: courseId,
    };
    return this.httpRequestService.put(ResourceEndpoints.STUDENTS, paramsMap, requestBody);
  }

  /**
   * Deletes all students in a course by calling API.
   */
  deleteStudentsFromCourse(queryParams: { courseId: string }): Observable<MessageOutput> {
    const paramsMap: Record<string, string> = {
      [QueryParamKeys.COURSE_ID]: queryParams.courseId,
    };
    return this.httpRequestService.delete(ResourceEndpoints.STUDENTS, paramsMap);
  }

  /**
   * Loads list of students from a course in CSV format by calling API.
   */
  loadStudentListAsCsv(queryParams: { courseId: string }): Observable<string> {
    return this.courseService.getCourseAsInstructor(queryParams.courseId).pipe(
      mergeMap((courseView: CourseView) => {
        const course = courseView.course;
        return this.getStudents({ courseIds: [queryParams.courseId] }).pipe(
          map((students: Students) => {
            return this.processStudentsToCsv(course.courseId, course.courseName, students.students);
          }),
        );
      }),
    );
  }

  processStudentsToCsv(courseId: string, courseName: string, students: Student[]): string {
    const csvRows: string[][] = [];
    csvRows.push(['Course ID', courseId]);
    csvRows.push(['Course Name', courseName]);
    csvRows.push([]);
    const hasSection: boolean = students.some(
      (student: Student) => student.sectionName !== 'None' && student.sectionName !== '',
    );
    const headers: string[] = ['Team', 'Name', 'Status', 'Email'];
    csvRows.push(hasSection ? ['Section'].concat(headers) : headers);
    students.sort((a: Student, b: Student) => {
      return (
        this.tableComparatorService.compare(SortBy.SECTION_NAME, SortOrder.ASC, a.sectionName, b.sectionName) ||
        this.tableComparatorService.compare(SortBy.TEAM_NAME, SortOrder.ASC, a.teamName, b.teamName) ||
        this.tableComparatorService.compare(SortBy.RESPONDENT_NAME, SortOrder.ASC, a.name, b.name)
      );
    });
    students.forEach((student: Student) => {
      const studentRow: string[] = [
        student.teamName ? student.teamName : '',
        student.name,
        joinStateToString(student.joinState),
        student.email,
      ];
      csvRows.push(hasSection ? [student.sectionName].concat(studentRow) : studentRow);
    });
    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }
}
