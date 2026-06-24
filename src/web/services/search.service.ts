import { Injectable, inject } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { CourseService } from './course.service';
import { InstructorService } from './instructor.service';
import { LinkService } from './link.service';
import { StudentService } from './student.service';
import { ApiConst } from '../types/api-const';
import {
  Course,
  CourseView,
  Instructor,
  InstructorPermissionRole,
  InstructorPrivilege,
  Instructors,
  Student,
  Students,
} from '../types/api-output';

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
        return forkJoin([of(students), of(instructors), this.getDistinctFields(students, instructors)]);
      }),
      map((value: [Student[], Instructor[], DistinctFields]) => {
        return {
          students: this.createStudentAccountSearchResults(value[0], ...value[2]),
          instructors: this.createInstructorAccountSearchResults(value[1], value[2][1]),
        };
      }),
    );
  }

  createStudentAccountSearchResults(
    students: Student[],
    distinctInstructorsMap: DistinctInstructorsMap,
    distinctCoursesMap: DistinctCoursesMap,
    distinctInstructorPrivilegesMap: DistinctInstructorPrivilegesMap,
  ): StudentAccountSearchResult[] {
    return students.map((student: Student) => {
      const { courseId }: Student = student;
      return this.joinAdminStudent(
        student,
        distinctInstructorsMap[courseId],
        distinctCoursesMap[courseId],
        distinctInstructorPrivilegesMap[courseId],
      );
    });
  }

  joinAdminStudent(
    student: Student,
    instructors: Instructors,
    course: Course,
    instructorPrivileges: InstructorPrivilege[],
  ): StudentAccountSearchResult {
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
      profilePageLink: '',
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

    let masqueradeAccountId = '';
    for (const instructor of instructors.instructors) {
      if (instructor.accountId && instructor.role === InstructorPermissionRole.COOWNER) {
        masqueradeAccountId = instructor.accountId;
        break;
      }
    }
    // no instructor with co-owner privileges
    // there is usually at least one instructor with "modify instructor" permission
    if (masqueradeAccountId === '') {
      for (const instructor of instructors.instructors) {
        const instructorPrivilege: InstructorPrivilege | undefined = instructorPrivileges.shift();
        if (instructor.accountId && instructorPrivilege?.privileges.courseLevel.canModifyInstructor) {
          masqueradeAccountId = instructor.accountId;
          break;
        }
      }
    }

    // Generate links for students
    studentResult.profilePageLink = this.linkService.generateProfilePageLink(student, masqueradeAccountId);
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

  private getDistinctFields(students: Student[], instructors: Instructor[]): Observable<DistinctFields> {
    const distinctCourseIds: string[] = Array.from(
      new Set([
        ...students.map((student: Student) => student.courseId),
        ...instructors.map((instructor: Instructor) => instructor.courseId),
      ]),
    );
    if (distinctCourseIds.length === 0) {
      return forkJoin([of({}), of({}), of({})]);
    }
    return forkJoin([this.getDistinctInstructors(distinctCourseIds), this.getDistinctCourses(distinctCourseIds)]).pipe(
      mergeMap((value: [DistinctInstructorsMap, DistinctCoursesMap]) => {
        return forkJoin([of(value[0]), of(value[1]), this.getDistinctInstructorPrivileges(value[0])]);
      }),
    );
  }

  private getDistinctInstructors(distinctCourseIds: string[]): Observable<DistinctInstructorsMap> {
    return forkJoin(
      distinctCourseIds.map((courseId: string) => this.instructorService.loadInstructors({ courseId })),
    ).pipe(
      map((instructorsArray: Instructors[]) => {
        const distinctInstructorsMap: DistinctInstructorsMap = {};
        instructorsArray.forEach((instructors: Instructors, index: number) => {
          distinctInstructorsMap[distinctCourseIds[index]] = instructors;
        });
        return distinctInstructorsMap;
      }),
    );
  }

  private getDistinctInstructorPrivileges(
    distinctInstructorsMap: DistinctInstructorsMap,
  ): Observable<DistinctInstructorPrivilegesMap> {
    const distinctCourseIds: string[] = Object.keys(distinctInstructorsMap);
    const instructorsArray: Instructors[] = Object.values(distinctInstructorsMap);
    return forkJoin([
      of(distinctCourseIds),
      forkJoin(
        instructorsArray.map((instructors: Instructors) => {
          return forkJoin(
            instructors.instructors.map((instructor: Instructor) =>
              this.instructorService.loadInstructorPrivilege({
                userId: instructor.userId,
              }),
            ),
          );
        }),
      ),
    ]).pipe(
      map((value: [string[], InstructorPrivilege[][]]) => {
        const distinctInstructorPrivilegesMap: DistinctInstructorPrivilegesMap = {};
        value[1].forEach((instructorPrivilegesArray: InstructorPrivilege[], index: number) => {
          distinctInstructorPrivilegesMap[value[0][index]] = instructorPrivilegesArray;
        });
        return distinctInstructorPrivilegesMap;
      }),
    );
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
  profilePageLink: string;
}

interface DistinctInstructorsMap {
  [courseId: string]: Instructors;
}

interface DistinctCoursesMap {
  [courseId: string]: Course;
}

interface DistinctInstructorPrivilegesMap {
  [courseId: string]: InstructorPrivilege[];
}

type DistinctFields = [DistinctInstructorsMap, DistinctCoursesMap, DistinctInstructorPrivilegesMap];
