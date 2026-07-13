import { Injectable, inject } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { CourseService } from './course.service';
import { InstructorService } from './instructor.service';
import { LinkService } from './link.service';
import { StudentService } from './student.service';
import { ApiConst } from '../types/api-const';
import { Course, CourseView, Instructor, Instructors, Student, Students } from '../types/api-output';

/**
 * Handles the logic for search.
 */
@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private instructorService = inject(InstructorService);
  private courseService = inject(CourseService);
  private linkService = inject(LinkService);
  private studentService = inject(StudentService);

  searchInstructor(searchKey: string, courseIds: string[]): Observable<InstructorSearchResult> {
    if (courseIds.length === 0) {
      return of({
        students: [],
        comments: [],
      });
    }

    return this.studentService
      .getStudents({
        courseIds,
        searchKey,
        limit: ApiConst.SEARCH_QUERY_SIZE_LIMIT,
      })
      .pipe(
        map((studentsRes: Students) => {
          return {
            students: studentsRes.students,
            comments: [],
          };
        }),
      );
  }

  searchAdmin(searchKey: string): Observable<AdminSearchResult> {
    return forkJoin([
      this.studentService.getStudents({ searchKey, limit: ApiConst.SEARCH_QUERY_SIZE_LIMIT }),
      this.instructorService.loadInstructors({ searchKey, limit: ApiConst.SEARCH_QUERY_SIZE_LIMIT }),
    ]).pipe(
      map((value: [Students, Instructors]): [Student[], Instructor[]] => [value[0].students, value[1].instructors]),
      mergeMap((value: [Student[], Instructor[]]) => {
        const [students, instructors] = value;
        return forkJoin([of(students), of(instructors), this.getDistinctCoursesMap(students, instructors)]);
      }),
      map(([students, instructors, distinctCoursesMap]: [Student[], Instructor[], DistinctCoursesMap]) => {
        return {
          students: this.createStudentAccountSearchResults(students, distinctCoursesMap),
          instructors: this.createInstructorAccountSearchResults(instructors, distinctCoursesMap),
        };
      }),
    );
  }

  createStudentAccountSearchResults(
    students: Student[],
    distinctCoursesMap: DistinctCoursesMap,
  ): StudentAccountSearchResult[] {
    return students.map((student: Student) => {
      const { courseId }: Student = student;
      return this.joinAdminStudent(student, distinctCoursesMap[courseId]);
    });
  }

  joinAdminStudent(student: Student, course: Course): StudentAccountSearchResult {
    let studentResult: StudentAccountSearchResult = {
      userId: '',
      email: '',
      name: '',
      comments: '',
      team: '',
      section: '',
      courseId: '',
      courseName: '',
      isCourseDeleted: false,
      institute: '',
      manageAccountLink: '',
    };
    const {
      userId,
      email,
      name,
      comments = '',
      teamName: team,
      sectionName: section,
      accountId = '',
      institute = '',
    }: Student = student;
    studentResult = { ...studentResult, userId, email, name, comments, team, section, institute };

    const { courseId, courseName, deletionTimestamp }: Course = course;
    studentResult = { ...studentResult, courseId, courseName, isCourseDeleted: Boolean(deletionTimestamp) };

    // Generate links for students
    studentResult.manageAccountLink = accountId
      ? this.linkService.generateManageAccountLink(accountId, this.linkService.ADMIN_ACCOUNTS_PAGE)
      : '';

    return studentResult;
  }

  createInstructorAccountSearchResults(
    instructors: Instructor[],
    distinctCoursesMap: DistinctCoursesMap,
  ): InstructorAccountSearchResult[] {
    return instructors.map((instructor: Instructor) =>
      this.joinAdminInstructor(instructor, distinctCoursesMap[instructor.courseId]),
    );
  }

  joinAdminInstructor(instructor: Instructor, course: Course): InstructorAccountSearchResult {
    let instructorResult: InstructorAccountSearchResult = {
      userId: '',
      email: '',
      name: '',
      courseId: '',
      courseName: '',
      isCourseDeleted: false,
      institute: '',
      manageAccountLink: '',
    };
    const { userId, email, name, accountId = '', institute = '' }: Instructor = instructor;
    instructorResult = { ...instructorResult, userId, email, name, institute };

    const { courseId, courseName, deletionTimestamp }: Course = course;
    instructorResult = { ...instructorResult, courseId, courseName, isCourseDeleted: Boolean(deletionTimestamp) };

    // Generate links for instructors
    instructorResult.manageAccountLink = accountId
      ? this.linkService.generateManageAccountLink(accountId, this.linkService.ADMIN_ACCOUNTS_PAGE)
      : '';

    return instructorResult;
  }

  private getDistinctCoursesMap(students: Student[], instructors: Instructor[]): Observable<DistinctCoursesMap> {
    const distinctCourseIds: string[] = Array.from(
      new Set([
        ...students.map((student: Student) => student.courseId),
        ...instructors.map((instructor: Instructor) => instructor.courseId),
      ]),
    );
    if (distinctCourseIds.length === 0) {
      return of({});
    }
    return this.getDistinctCourses(distinctCourseIds);
  }

  private getDistinctCourses(distinctCourseIds: string[]): Observable<DistinctCoursesMap> {
    return forkJoin(distinctCourseIds.map((id: string) => this.courseService.getCourseAsInstructor(id))).pipe(
      map((courses: CourseView[]) => {
        const distinctCoursesMap: DistinctCoursesMap = {};
        courses.forEach((courseView: CourseView, index: number) => {
          distinctCoursesMap[distinctCourseIds[index]] = courseView.course;
        });
        return distinctCoursesMap;
      }),
    );
  }
}

/**
 * The typings for the response object returned by the instructor search service.
 */
export interface InstructorSearchResult {
  students: Student[];
}

/**
 * The typings for the response object returned by admin search service.
 */
export interface AdminSearchResult {
  students: StudentAccountSearchResult[];
  instructors: InstructorAccountSearchResult[];
}

/**
 * Search results for instructors for the admin endpoint
 */
export interface InstructorAccountSearchResult {
  userId: string;
  name: string;
  email: string;
  courseId: string;
  courseName: string;
  isCourseDeleted: boolean;
  institute: string;
  manageAccountLink: string;
}

/**
 * Search results for students from the Admin endpoint.
 */
export interface StudentAccountSearchResult extends InstructorAccountSearchResult {
  section: string;
  team: string;
  comments: string;
}

interface DistinctCoursesMap {
  [courseId: string]: Course;
}
