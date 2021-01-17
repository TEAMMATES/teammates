import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { flatMap, map } from 'rxjs/operators';
import { ResourceEndpoints } from '../types/api-const';
import {
  CommentSearchResult,
  CommentSearchResults,
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

  /**
   * Search session, response, comments on response with {@code searchKey} and
   * parses the results
   */
  searchComment(searchKey: string): Observable<InstructorSearchResult> {
    return this.searchComments(searchKey).pipe(
      map((commentsRes: CommentSearchResults) => {
        return {
          students: [],
          comments: commentsRes.searchResults,
        };
      }),
    );
  }

  searchAdmin(searchKey: string): Observable<AdminSearchResult> {
    return forkJoin([
      this.searchStudents(searchKey, 'admin'),
      this.searchInstructors(searchKey),
    ]).pipe(
      map((value: [Students, Instructors]): [Student[], Instructor[]] =>
        [value[0].students, value[1].instructors],
      ),
      flatMap((value: [Student[], Instructor[]]) => {
        const [students, instructors]: [Student[], Instructor[]] = value;
        return forkJoin([
          of(students),
          of(instructors),
          this.getDistinctFields(students, instructors),
        ]);
      }),
      map((value: [Student[], Instructor[], DistinctFields]) => {
        return {
          students: this.createStudentAccountSearchResults(value[0], ...value[2]),
          instructors: this.createInstructorAccountSearchResults(value[1], value[2][1]),
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

  /**
   * Searches sessions, responses, and comments for any matches against the {@code searchKey}.
   * Only responses with comments will be searched.
   */
  searchComments(searchKey: string): Observable<CommentSearchResults> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get(ResourceEndpoints.SEARCH_COMMENTS, paramMap);
  }

  searchInstructorPrivilege(courseId: string, sectionName: string): Observable<InstructorPrivilege> {
    return this.instructorService.loadInstructorPrivilege(
        { courseId, sectionName },
    );
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
      openSessions: {},
      notOpenSessions: {},
      publishedSessions: {},
      courseId: '',
      courseName: '',
      institute: '',
      manageAccountLink: '',
      homePageLink: '',
      recordsPageLink: '',
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

    const { courseId, courseName }: Course = course;
    studentResult = { ...studentResult, courseId, courseName };

    let masqueradeGoogleId: string = '';
    for (const instructor of instructors.instructors) {
      const instructorPrivilege: InstructorPrivilege | undefined = instructorPrivileges.shift();
      if (instructor.googleId != null &&
          (instructorPrivilege != null && instructorPrivilege.canModifyInstructor ||
           instructor.role === InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER)) {
        masqueradeGoogleId = instructor.googleId;
        break;
      }
    }

    // Generate feedback session urls
    const { openSessions, notOpenSessions, publishedSessions }: StudentFeedbackSessions =
      this.classifyFeedbackSessions(feedbackSessions, student);
    studentResult = { ...studentResult, openSessions, notOpenSessions, publishedSessions };

    // Generate links for students
    studentResult.courseJoinLink = this.linkService.generateCourseJoinLinkStudent(student);
    studentResult.homePageLink = this.linkService
      .generateHomePageLink(googleId, this.linkService.STUDENT_HOME_PAGE);
    studentResult.recordsPageLink = this.linkService.generateRecordsPageLink(student, masqueradeGoogleId);
    studentResult.manageAccountLink = this.linkService
      .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);

    return studentResult;
  }

  createInstructorAccountSearchResults(
    instructors: Instructor[],
    distinctCoursesMap: DistinctCoursesMap,
  ): InstructorAccountSearchResult[] {
    return instructors.map((instructor: Instructor) =>
                           this.joinAdminInstructor(instructor, distinctCoursesMap[instructor.courseId]));
  }

  joinAdminInstructor(instructor: Instructor, course: Course): InstructorAccountSearchResult {
    let instructorResult: InstructorAccountSearchResult = {
      email: '',
      name: '',
      courseId: '',
      courseName: '',
      institute: '',
      manageAccountLink: '',
      homePageLink: '',
      courseJoinLink: '',
      googleId: '',
      showLinks: false,
    };
    const { email, name, googleId = '', institute = '' }: Instructor = instructor;
    instructorResult = { ...instructorResult, email, name, googleId, institute };

    const { courseId, courseName }: Course = course;
    instructorResult = { ...instructorResult, courseId, courseName };

    // Generate links for instructors
    instructorResult.courseJoinLink = this.linkService.generateCourseJoinLinkInstructor(instructor);
    instructorResult.homePageLink = this.linkService
      .generateHomePageLink(googleId, this.linkService.INSTRUCTOR_HOME_PAGE);
    instructorResult.manageAccountLink = this.linkService
      .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);

    return instructorResult;
  }

  classifyFeedbackSessions(feedbackSessions: FeedbackSessions, student: Student): StudentFeedbackSessions {
    const feedbackSessionLinks: StudentFeedbackSessions = {
      openSessions: {},
      notOpenSessions: {},
      publishedSessions: {},
    };
    for (const feedbackSession of feedbackSessions.feedbackSessions) {
      if (this.feedbackSessionService.isFeedbackSessionOpen(feedbackSession)) {
        feedbackSessionLinks.openSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateSubmitUrl(student, feedbackSession.feedbackSessionName),
        };
      } else {
        feedbackSessionLinks.notOpenSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateSubmitUrl(student, feedbackSession.feedbackSessionName),
        };
      }

      if (this.feedbackSessionService.isFeedbackSessionPublished(feedbackSession)) {
        feedbackSessionLinks.publishedSessions[feedbackSession.feedbackSessionName] = {
          ...this.formatProperties(feedbackSession),
          feedbackSessionUrl: this.linkService.generateResultUrl(student, feedbackSession.feedbackSessionName),
        };
      }
    }
    return feedbackSessionLinks;
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
      flatMap((value: [
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
        this.feedbackSessionService.getFeedbackSessionsForStudent(id)),
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
    const DATE_FORMAT_WITH_ZONE_INFO: string = 'ddd, DD MMM YYYY, hh:mm A Z';
    const startTime: string = this.timezoneService
        .formatToString(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone, DATE_FORMAT_WITH_ZONE_INFO);
    const endTime: string = this.timezoneService
        .formatToString(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone, DATE_FORMAT_WITH_ZONE_INFO);
    return { startTime, endTime };
  }
}

/**
 * The typings for the response object returned by the instructor search service.
 */
export interface InstructorSearchResult {
  students: Student[];
  comments: CommentSearchResult[];
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
  name: string;
  email: string;
  googleId: string;
  courseId: string;
  courseName: string;
  institute: string;
  courseJoinLink: string;
  homePageLink: string;
  manageAccountLink: string;
  showLinks: boolean;
}

/**
 * Search results for students from the Admin endpoint.
 */
export interface StudentAccountSearchResult extends InstructorAccountSearchResult {
  section: string;
  team: string;
  comments: string;
  recordsPageLink: string;
  openSessions: FeedbackSessionsGroup;
  notOpenSessions: FeedbackSessionsGroup;
  publishedSessions: FeedbackSessionsGroup;
}

/**
 * Feedback session inforamtion for search result.
 */
export interface FeedbackSessionsGroup {
  [name: string]: {
    startTime: string;
    endTime: string;
    feedbackSessionUrl: string;
  };
}

interface StudentFeedbackSessions {
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
  DistinctInstructorPrivilegesMap
];
