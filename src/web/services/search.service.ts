import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { ResourceEndpoints } from '../types/api-const';
import {
  AccountRequest,
  AccountRequests,
  Course, FeedbackSession,
  FeedbackSessions,
  Instructor,
  InstructorPermissionRole,
  InstructorPrivilege,
  Instructors,
  Student,
  Students,
} from '../types/api-output';
import { Intent } from '../types/api-request';
import { CourseService } from './course.service';
import { FeedbackSessionsService } from './feedback-sessions.service';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';
import { LinkService } from './link.service';
import { TimezoneService } from './timezone.service';

/**
 * Handles the logic for search.
 */
@Injectable({
  providedIn: 'root',
})
export class SearchService {

  constructor(
    private instructorService: InstructorService,
    private httpRequestService: HttpRequestService,
    private feedbackSessionService: FeedbackSessionsService,
    private courseService: CourseService,
    private linkService: LinkService,
    private timezoneService: TimezoneService,
  ) {}

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
      this.searchInstructors(searchKey),
      this.searchAccountRequests(searchKey),
    ]).pipe(
      map((value: [Students, Instructors, AccountRequests]): [Student[], Instructor[], AccountRequest[]] =>
        [value[0].students, value[1].instructors, value[2].accountRequests],
      ),
      mergeMap((value: [Student[], Instructor[], AccountRequest[]]) => {
        const [students, instructors, accountRequests]: [Student[], Instructor[], AccountRequest[]] = value;
        return forkJoin([
          of(students),
          of(instructors),
          of(accountRequests),
          this.getDistinctFields(students, instructors),
        ]);
      }),
      map((value: [Student[], Instructor[], AccountRequest[], DistinctFields]) => {
        return {
          students: this.createStudentAccountSearchResults(value[0], ...value[3]),
          instructors: this.createInstructorAccountSearchResults(value[1], value[3][1], value[3][2]),
          accountRequests: this.createAccountRequestSearchResults(value[2]),
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

  searchInstructors(searchKey: string): Observable<Instructors> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get(ResourceEndpoints.SEARCH_INSTRUCTORS, paramMap);
  }

  searchAccountRequests(searchKey: string): Observable<AccountRequests> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get(ResourceEndpoints.SEARCH_ACCOUNT_REQUESTS, paramMap);
  }

  createStudentAccountSearchResults(
    students: Student[],
    distinctInstructorsMap: DistinctInstructorsMap,
    distinctCoursesMap: DistinctCoursesMap,
    distinctFeedbackSessionsMap: DistinctFeedbackSessionsMap,
    distinctInstructorPrivilegesMap: DistinctInstructorPrivilegesMap,
  ): StudentAccountSearchResult[] {
    return students.map((student: Student) => {
      const { courseId }: Student = student;
      return this.joinAdminStudent(
        student,
        distinctInstructorsMap[courseId],
        distinctCoursesMap[courseId],
        distinctFeedbackSessionsMap[courseId],
        distinctInstructorPrivilegesMap[courseId],
      );
    });
  }

  joinAdminStudent(
    student: Student,
    instructors: Instructors,
    course: Course,
    feedbackSessions: FeedbackSessions,
    instructorPrivileges: InstructorPrivilege[],
  ): StudentAccountSearchResult {
    let studentResult: StudentAccountSearchResult = {
      email: '',
      name: '',
      comments: '',
      team: '',
      section: '',
      awaitingSessions: {},
      openSessions: {},
      notOpenSessions: {},
      publishedSessions: {},
      courseId: '',
      courseName: '',
      isCourseDeleted: false,
      institute: '',
      manageAccountLink: '',
      homePageLink: '',
      profilePageLink: '',
      courseJoinLink: '',
      googleId: '',
      showLinks: false,
    };
    const {
      email,
      name,
      comments = '',
      teamName: team,
      sectionName: section,
      googleId = '',
      institute = '',
    }: Student = student;
    studentResult = { ...studentResult, email, name, comments, team, section, googleId, institute };

    const { courseId, courseName, deletionTimestamp }: Course = course;
    studentResult = { ...studentResult, courseId, courseName, isCourseDeleted: Boolean(deletionTimestamp) };

    let masqueradeGoogleId: string = '';
    for (const instructor of instructors.instructors) {
      if (instructor.googleId
          && instructor.role === InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER) {
        masqueradeGoogleId = instructor.googleId;
        break;
      }
    }
    // no instructor with co-owner privileges
    // there is usually at least one instructor with "modify instructor" permission
    if (masqueradeGoogleId === '') {
      for (const instructor of instructors.instructors) {
        const instructorPrivilege: InstructorPrivilege | undefined = instructorPrivileges.shift();
        if (instructor.googleId
            && (instructorPrivilege && instructorPrivilege.privileges.courseLevel.canModifyInstructor)) {
          masqueradeGoogleId = instructor.googleId;
          break;
        }
      }
    }

    // Generate feedback session urls
    const { awaitingSessions, openSessions, notOpenSessions, publishedSessions }: StudentFeedbackSessions =
      this.classifyFeedbackSessions(feedbackSessions, student, false);
    studentResult = { ...studentResult, awaitingSessions, openSessions, notOpenSessions, publishedSessions };

    // Generate links for students
    studentResult.courseJoinLink = this.linkService.generateCourseJoinLink(student, 'student');
    studentResult.homePageLink = this.linkService
      .generateHomePageLink(googleId, this.linkService.STUDENT_HOME_PAGE);
    studentResult.profilePageLink = this.linkService.generateProfilePageLink(student, masqueradeGoogleId);
    studentResult.manageAccountLink = this.linkService
      .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);

    return studentResult;
  }

  createInstructorAccountSearchResults(
    instructors: Instructor[],
    distinctCoursesMap: DistinctCoursesMap,
    distinctFeedbackSessionsMap: DistinctFeedbackSessionsMap,
  ): InstructorAccountSearchResult[] {
    return instructors.map((instructor: Instructor) =>
        this.joinAdminInstructor(instructor, distinctCoursesMap[instructor.courseId],
            distinctFeedbackSessionsMap[instructor.courseId]));
  }

  joinAdminInstructor(
    instructor: Instructor,
    course: Course,
    feedbackSessions: FeedbackSessions,
  ): InstructorAccountSearchResult {
    let instructorResult: InstructorAccountSearchResult = {
      email: '',
      name: '',
      courseId: '',
      courseName: '',
      isCourseDeleted: false,
      institute: '',
      manageAccountLink: '',
      homePageLink: '',
      courseJoinLink: '',
      googleId: '',
      showLinks: false,
      awaitingSessions: {},
      openSessions: {},
      notOpenSessions: {},
      publishedSessions: {},
    };
    const { email, name, googleId = '', institute = '' }: Instructor = instructor;
    instructorResult = { ...instructorResult, email, name, googleId, institute };

    const { courseId, courseName, deletionTimestamp }: Course = course;
    instructorResult = { ...instructorResult, courseId, courseName, isCourseDeleted: Boolean(deletionTimestamp) };

    // Generate feedback session urls
    const { awaitingSessions, openSessions, notOpenSessions, publishedSessions }: StudentFeedbackSessions =
        this.classifyFeedbackSessions(feedbackSessions, instructor, true);
    instructorResult = { ...instructorResult, awaitingSessions, openSessions, notOpenSessions, publishedSessions };

    // Generate links for instructors
    instructorResult.courseJoinLink = this.linkService.generateCourseJoinLink(instructor, 'instructor');
    instructorResult.homePageLink = this.linkService
      .generateHomePageLink(googleId, this.linkService.INSTRUCTOR_HOME_PAGE);
    instructorResult.manageAccountLink = this.linkService
      .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);

    return instructorResult;
  }

  classifyFeedbackSessions(feedbackSessions: FeedbackSessions, entity: Student | Instructor, isInstructor: boolean):
      StudentFeedbackSessions {
    const feedbackSessionLinks: StudentFeedbackSessions = {
      awaitingSessions: {},
      openSessions: {},
      notOpenSessions: {},
      publishedSessions: {},
    };
    for (const feedbackSession of feedbackSessions.feedbackSessions) {
      if (this.feedbackSessionService.isFeedbackSessionOpen(feedbackSession)) {
        feedbackSessionLinks.openSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateSubmitUrl(
              entity, feedbackSession.feedbackSessionName, isInstructor),
        };
      } else if (this.feedbackSessionService.isFeedbackSessionAwaiting(feedbackSession)) {
        feedbackSessionLinks.awaitingSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateSubmitUrl(
              entity, feedbackSession.feedbackSessionName, isInstructor),
        };
      } else {
        feedbackSessionLinks.notOpenSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateSubmitUrl(
              entity, feedbackSession.feedbackSessionName, isInstructor),
        };
      }

      if (this.feedbackSessionService.isFeedbackSessionPublished(feedbackSession)) {
        feedbackSessionLinks.publishedSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateResultUrl(
              entity, feedbackSession.feedbackSessionName, isInstructor),
        };
      }
    }
    return feedbackSessionLinks;
  }

  createAccountRequestSearchResults(
    accountRequests: AccountRequest[],
  ): AccountRequestSearchResult[] {
    return accountRequests.map((accountRequest: AccountRequest) => this.joinAdminAccountRequest(accountRequest));
  }

  joinAdminAccountRequest(accountRequest: AccountRequest): AccountRequestSearchResult {
    let accountRequestResult: AccountRequestSearchResult = {
      name: '',
      email: '',
      institute: '',
      createdAtText: '',
      registeredAtText: '',
      registrationLink: '',
      showLinks: false,
    };

    const { registrationKey, createdAt, registeredAt, name, institute, email }: AccountRequest = accountRequest;

    const timezone: string = this.timezoneService.guessTimezone() || 'UTC';
    accountRequestResult.createdAtText = this.formatTimestampAsString(createdAt, timezone);
    accountRequestResult.registeredAtText = registeredAt ? this.formatTimestampAsString(registeredAt, timezone) : null;

    const registrationLink: string = this.linkService.generateAccountRegistrationLink(registrationKey);
    accountRequestResult = { ...accountRequestResult, name, email, institute, registrationLink };

    return accountRequestResult;
  }

  private getDistinctFields(students: Student[], instructors: Instructor[]): Observable<DistinctFields> {
    const distinctCourseIds: string[] = Array.from(new Set([
      ...students.map((student: Student) => student.courseId),
      ...instructors.map((instructor: Instructor) => instructor.courseId),
    ]));
    if (distinctCourseIds.length === 0) {
      return forkJoin([of({}), of({}), of({}), of({})]);
    }
    return forkJoin([
      this.getDistinctInstructors(distinctCourseIds),
      this.getDistinctCourses(distinctCourseIds),
      this.getDistinctFeedbackSessions(distinctCourseIds),
    ]).pipe(
      mergeMap((value: [
        DistinctInstructorsMap,
        DistinctCoursesMap,
        DistinctFeedbackSessionsMap],
      ) => {
        return forkJoin([
          of(value[0]),
          of(value[1]),
          of(value[2]),
          this.getDistinctInstructorPrivileges(value[0]),
        ]);
      }),
    );
  }

  private getDistinctInstructors(distinctCourseIds: string[]): Observable<DistinctInstructorsMap> {
    return forkJoin(
      distinctCourseIds.map((courseId: string) =>
        this.instructorService.loadInstructors({ courseId, intent: Intent.FULL_DETAIL })),
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
      forkJoin(instructorsArray.map((instructors: Instructors) => {
        return forkJoin(
          instructors.instructors.map(
            (instructor: Instructor) => this.instructorService.loadInstructorPrivilege(
              {
                courseId: instructor.courseId,
                instructorEmail: instructor.email,
              },
            ),
          ),
        );
      })),
    ]).pipe(
      map(
        (value: [string[], InstructorPrivilege[][]]) => {
          const distinctInstructorPrivilegesMap: DistinctInstructorPrivilegesMap = {};
          value[1].forEach((instructorPrivilegesArray: InstructorPrivilege[], index: number) => {
            distinctInstructorPrivilegesMap[value[0][index]] = instructorPrivilegesArray;
          });
          return distinctInstructorPrivilegesMap;
        },
      ),
    );
  }

  private getDistinctCourses(distinctCourseIds: string[]): Observable<DistinctCoursesMap> {
    return forkJoin(
      distinctCourseIds.map((id: string) => this.courseService.getCourseAsInstructor(id)),
    ).pipe(
      map((courses: Course[]) => {
        const distinctCoursesMap: DistinctCoursesMap = {};
        courses.forEach((course: Course, index: number) => {
          distinctCoursesMap[distinctCourseIds[index]] = course;
        });
        return distinctCoursesMap;
      }),
    );
  }

  private getDistinctFeedbackSessions(distinctCourseIds: string[]): Observable<DistinctFeedbackSessionsMap> {
    return forkJoin(
      distinctCourseIds.map((id: string) =>
        this.feedbackSessionService.getFeedbackSessionsForStudent('admin', id)),
    )
    .pipe(
      map((feedbackSessionsArray: FeedbackSessions[]) => {
        const distinctFeedbackSessionsMap: DistinctFeedbackSessionsMap = {};
        feedbackSessionsArray.forEach(
          (feedbackSessions: FeedbackSessions, index: number) => {
            distinctFeedbackSessionsMap[distinctCourseIds[index]] = feedbackSessions;
          },
        );
        return distinctFeedbackSessionsMap;
      }),
    );
  }

  private formatProperties(feedbackSession: FeedbackSession): { startTime: string, endTime: string } {
    const startTime: string =
        this.formatTimestampAsString(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone);
    const endTime: string =
        this.formatTimestampAsString(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone);

    return { startTime, endTime };
  }

  private formatTimestampAsString(timestamp: number, timezone: string): string {
    const dateFormatWithZoneInfo: string = 'ddd, DD MMM YYYY, hh:mm A Z';

    return this.timezoneService
        .formatToString(timestamp, timezone, dateFormatWithZoneInfo);
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
  accountRequests: AccountRequestSearchResult[];
}

/**
 * Search results for account requests from the admin endpoint.
 */
export interface AccountRequestSearchResult {
  name: string;
  email: string;
  institute: string; // TODO: use institute with country here
  createdAtText: string;
  registeredAtText: string | null;
  registrationLink: string;
  showLinks: boolean;
}

/**
 * Search results for instructors for the admin endpoint
 */
export interface InstructorAccountSearchResult {
  name: string;
  email: string;
  googleId: string;
  courseId: string;
  courseName: string;
  isCourseDeleted: boolean;
  institute: string;
  courseJoinLink: string;
  homePageLink: string;
  manageAccountLink: string;
  showLinks: boolean;
  awaitingSessions: FeedbackSessionsGroup;
  openSessions: FeedbackSessionsGroup;
  notOpenSessions: FeedbackSessionsGroup;
  publishedSessions: FeedbackSessionsGroup;
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

/**
 * Feedback session information for search result.
 */
export interface FeedbackSessionsGroup {
  [name: string]: {
    startTime: string,
    endTime: string,
    feedbackSessionUrl: string,
  };
}

interface StudentFeedbackSessions {
  awaitingSessions: FeedbackSessionsGroup;
  openSessions: FeedbackSessionsGroup;
  notOpenSessions: FeedbackSessionsGroup;
  publishedSessions: FeedbackSessionsGroup;
}

interface DistinctInstructorsMap {
  [courseId: string]: Instructors;
}

interface DistinctFeedbackSessionsMap {
  [courseId: string]: FeedbackSessions;
}

interface DistinctCoursesMap {
  [courseId: string]: Course;
}

interface DistinctInstructorPrivilegesMap {
  [courseId: string]: InstructorPrivilege[];
}

type DistinctFields = [
  DistinctInstructorsMap,
  DistinctCoursesMap,
  DistinctFeedbackSessionsMap,
  DistinctInstructorPrivilegesMap,
];
