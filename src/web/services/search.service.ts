import { Injectable, inject } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { CourseService } from './course.service';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';
import { LinkService } from './link.service';
import { TimezoneService } from './timezone.service';
import { ResourceEndpoints } from '../types/api-const';
import { AccountService } from './account.service';
import {
  AccountVerificationRequest,
  AccountVerificationRequests,
  AccountVerificationRequestStatus,
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
  private accountService = inject(AccountService);
  private instructorService = inject(InstructorService);
  private httpRequestService = inject(HttpRequestService);
  private courseService = inject(CourseService);
  private linkService = inject(LinkService);
  private timezoneService = inject(TimezoneService);

  searchInstructor(searchKey: string): Observable<InstructorSearchResult> {
    return this.searchStudents(searchKey, 'instructor').pipe(
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
      this.searchStudents(searchKey, 'admin'),
      this.instructorService.loadInstructors({ searchKey, limit: 50 }),
      this.accountService.getAccountVerificationRequests({
        searchKey,
        limit: 50,
      }),
    ]).pipe(
      map(
        (
          value: [Students, Instructors, AccountVerificationRequests],
        ): [Student[], Instructor[], AccountVerificationRequest[]] => [
          value[0].students,
          value[1].instructors,
          value[2].accountVerificationRequests,
        ],
      ),
      mergeMap((value: [Student[], Instructor[], AccountVerificationRequest[]]) => {
        const [students, instructors, accountVerificationRequests]: [
          Student[],
          Instructor[],
          AccountVerificationRequest[],
        ] = value;
        return forkJoin([
          of(students),
          of(instructors),
          of(accountVerificationRequests),
          this.getDistinctFields(students, instructors),
        ]);
      }),
      map((value: [Student[], Instructor[], AccountVerificationRequest[], DistinctFields]) => {
        return {
          students: this.createStudentAccountSearchResults(value[0], ...value[3]),
          instructors: this.createInstructorAccountSearchResults(value[1], value[3][1]),
          accountVerificationRequests: this.createAccountVerificationRequestSearchResults(value[2]),
        };
      }),
    );
  }

  searchStudents(searchKey: string, entityType: string): Observable<Students> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
      entitytype: entityType,
    };
    return this.httpRequestService.get(ResourceEndpoints.SEARCH_STUDENTS, paramMap);
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

  createAccountVerificationRequestSearchResults(
    accountVerificationRequests: AccountVerificationRequest[],
  ): AccountVerificationRequestSearchResult[] {
    return accountVerificationRequests.map((accountVerificationRequest: AccountVerificationRequest) =>
      this.joinAdminAccountVerificationRequest(accountVerificationRequest),
    );
  }

  joinAdminAccountVerificationRequest(
    accountVerificationRequest: AccountVerificationRequest,
  ): AccountVerificationRequestSearchResult {
    let accountVerificationRequestResult: AccountVerificationRequestSearchResult = {
      accountVerificationRequestId: '',
      name: '',
      email: '',
      institute: '',
      country: '',
      accountId: '',
      createdAtText: '',
      createdDemoCourseAtText: '',
      registrationLink: '',
      showLinks: false,
      status: AccountVerificationRequestStatus.PENDING,
      comments: '',
    };

    const {
      accountVerificationRequestId,
      createdAt,
      createdDemoCourseAt,
      name,
      institute,
      country,
      email,
      status,
      comments,
    }: AccountVerificationRequest = accountVerificationRequest;

    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    accountVerificationRequestResult.createdAtText = this.formatTimestampAsString(createdAt, timezone);
    accountVerificationRequestResult.createdDemoCourseAtText = createdDemoCourseAt
      ? this.formatTimestampAsString(createdDemoCourseAt, timezone)
      : null;
    accountVerificationRequestResult.comments = comments ?? '';

    const registrationLink: string = this.linkService.generateInstructorWelcomeLink(accountVerificationRequestId);
    accountVerificationRequestResult = {
      ...accountVerificationRequestResult,
      accountVerificationRequestId,
      name,
      email,
      institute,
      country,
      registrationLink,
      status,
    };

    return accountVerificationRequestResult;
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

  private formatTimestampAsString(timestamp: number, timezone: string): string {
    const dateFormatWithZoneInfo = 'ddd, DD MMM YYYY, hh:mm A Z';

    return this.timezoneService.formatToString(timestamp, timezone, dateFormatWithZoneInfo);
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
  accountVerificationRequests: AccountVerificationRequestSearchResult[];
}

/**
 * Search results for account verification requests from the admin endpoint.
 */
export interface AccountVerificationRequestSearchResult {
  accountVerificationRequestId: string;
  accountId: string;
  name: string;
  email: string;
  status: AccountVerificationRequestStatus;
  institute: string;
  country: string;
  createdAtText: string;
  createdDemoCourseAtText: string | null;
  registrationLink: string;
  showLinks: boolean;
  comments: string;
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
