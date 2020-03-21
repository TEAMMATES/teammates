import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { flatMap, map, mergeMap } from 'rxjs/operators';
import { SearchStudentsTable } from '../app/pages-instructor/instructor-search-page/instructor-search-page.component';
import { StudentListSectionData } from '../app/pages-instructor/student-list/student-list-section-data';
import {
  Course,
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
    private feedbackSessionsService: FeedbackSessionsService,
    private courseService: CourseService,
    private linkService: LinkService
  ) {}

  searchInstructor(searchKey: string): Observable<InstructorSearchResult> {
    return this.getStudents(searchKey).pipe(
      map((studentsRes: Students) => this.getCoursesWithSections(studentsRes)),
      mergeMap((coursesWithSections: SearchStudentsTable[]) =>
        forkJoin([
      mergeMap((coursesWithSections: SearchStudentsTable[]) =>
          of(coursesWithSections),
          this.getPrivileges(coursesWithSections),
        ]),
      ),
      map((res: [SearchStudentsTable[], InstructorPrivilege[]]) => this.combinePrivileges(res)),
    );
  }

  searchAdmin(searchKey: string): Observable<AdminSearchResult> {
    return forkJoin(
      this.getStudents(searchKey).pipe(
        map((students: Students) => students.students),
        flatMap((studentsArray: Student[]) =>
                studentsArray.length !== 0
                  ? forkJoin(studentsArray.map((student: Student) => this.createStudentAccountSearchResult(student)))
                  : of([])),
      ),
      this.getInstructors(searchKey).pipe(
        map((instructors: Instructors) => instructors.instructors),
        flatMap((instructorsArray: Instructor[]) =>
                instructorsArray.length !== 0
                  ? forkJoin(instructorsArray.map((instructor: Instructor) =>
                                                  this.createInstructorAccountSearchResult(instructor)))
                  : of([])),
      ),
    ).pipe(
      map((resp: [StudentAccountSearchResult[], InstructorAccountSearchResult[]]) => ({
        students: resp[0],
        instructors: resp[1],
      })),
    );
  }

  getStudents(searchKey: string): Observable<Students> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get('/search/students', paramMap);
  }

  getInstructors(searchKey: string): Observable<Instructors> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get('/search/instructors', paramMap);
  }

  getCoursesWithSections(studentsRes: Students): SearchStudentsTable[] {
    const { students }: { students: Student[] } = studentsRes;

    const distinctCourses: string[] = Array.from(
      new Set(students.map((s: Student) => s.courseId)),
    );
    const coursesWithSections: SearchStudentsTable[] = distinctCourses.map(
      (courseId: string) => ({
        courseId,
        sections: Array.from(
          new Set(
            students
              .filter((s: Student) => s.courseId === courseId)
              .map((s: Student) => s.sectionName),
          ),
        ).map((sectionName: string) => ({
          sectionName,
          isAllowedToViewStudentInSection: false,
          isAllowedToModifyStudent: false,
          students: students
            .filter(
              (s: Student) =>
                s.courseId === courseId && s.sectionName === sectionName,
            )
            .map((s: Student) => ({
              name: s.name,
              email: s.email,
              status: s.joinState,
              team: s.teamName,
            })),
        })),
      }),
    );

    return coursesWithSections;
  }

  getPrivileges(
    coursesWithSections: SearchStudentsTable[],
  ): Observable<InstructorPrivilege[]> {
    return forkJoin(
      coursesWithSections.map((course: SearchStudentsTable) => {
        return course.sections.map((section: StudentListSectionData) => {
          return this.instructorService.loadInstructorPrivilege({
            courseId: course.courseId,
            sectionName: section.sectionName,
          });
        });
      }).reduce(
        (acc: Observable<InstructorPrivilege>[], val: Observable<InstructorPrivilege>[]) =>
        acc.concat(val),
        [],
      ),
    );
  }

  combinePrivileges(
    [coursesWithSections, privileges]: [SearchStudentsTable[], InstructorPrivilege[]],
  ): InstructorSearchResult {
    /**
     * Pop the privilege objects one at a time and attach them to the results. This is possible
     * because `forkJoin` guarantees that the `InstructorPrivilege` results are returned in the
     * same order the requests were made.
     */
    for (const course of coursesWithSections) {
      for (const section of course.sections) {
        const sectionPrivileges: InstructorPrivilege | undefined = privileges.shift();
        if (!sectionPrivileges) { continue; }

        section.isAllowedToViewStudentInSection = sectionPrivileges.canViewStudentInSections;
        section.isAllowedToModifyStudent = sectionPrivileges.canModifyStudent;
    return {
      searchStudentsTables: coursesWithSections,
    };
  }

  createStudentAccountSearchResult(student: Student): Observable<StudentAccountSearchResult> {
    const { courseId }: {courseId: string} = student;
    return forkJoin(
      this.feedbackSessionService.getFeedbackSessionsForStudent(courseId),
      this.courseService.getCourseAsStudent(courseId),
      this.instructorService.getInstructorsFromCourse(courseId, Intent.FULL_DETAIL),
      this.instructorService.loadInstructorPrivilege({ courseId }),
    ).pipe(
      map((resp: [FeedbackSessions, Course, Instructors, InstructorPrivilege]) => this.joinAdminStudent(resp, student)),
    );
  }

  joinAdminStudent(
    resp: [FeedbackSessions, Course, Instructors, InstructorPrivilege], student: Student,
  ): StudentAccountSearchResult {
    const [feedbackSessions, course, instructors, instructorPrivilege]: [
      FeedbackSessions, Course, Instructors, InstructorPrivilege
    ] = resp;
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

    let instructorGoogleId: string = '';
    // Get instructors with a vlaid google id.
    const instructorsWithGoogleIds: Instructor[] = instructors.instructors
      .filter((instructor: Instructor) => instructor.googleId != null);
    const isAllowedToModifyInstructor: boolean = instructorPrivilege.canModifyInstructor;
    
    // If allowed to modify instructor for course, just pick the first valid instructor.
    if (isAllowedToModifyInstructor && instructorsWithGoogleIds.length > 0) {
      instructorGoogleId = instructorsWithGoogleIds[0].googleId;
    } else {
      // Search for instructors with coowner privileges and selects the first eligible one.
      const instructorsWithCoownerPrivileges: Instructor[] = instructorsWithGoogleIds
        .filter((instructor: Instructor) =>
                instructor.role
                ? instructor.role === InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER
                : false,
        );
      if (instructorsWithCoownerPrivileges.length > 0) {
        const { googleId: instructorGoogleIdResult = '' }: { googleId: string } = instructorsWithCoownerPrivileges[0];
        instructorGoogleId = instructorGoogleIdResult;
      }
    }

    // Generate feedback session urls
    const { openSessions, notOpenSessions, publishedSessions }: StudentFeedbackSessions =
      this.classifyFeedbackSessions(feedbackSessions, student);
    studentResult = { ...studentResult, openSessions, notOpenSessions, publishedSessions };

    // Generate links for students
    studentResult.courseJoinLink = this.linkService.generateCourseJoinLinkStudent(student);
    studentResult.homePageLink = this.linkService.generateHomePageLink(googleId, this.linkService.STUDENT_HOME_PAGE);
    studentResult.recordsPageLink = this.linkService.generateRecordsPageLink(student, instructorGoogleId);
    studentResult.manageAccountLink = this.linkService
      .generateManageAccountLink(googleId, this.linkService.ADMIN_ACCOUNTS_PAGE);

    return studentResult;
  }

  createInstructorAccountSearchResult(instructor: Instructor): Observable<InstructorAccountSearchResult> {
    const { courseId }: {courseId: string} = instructor;
    return this.courseService.getCourseAsInstructor(courseId).pipe(
      map((resp: Course) => this.joinAdminInstructor(resp, instructor)),
    );
  }

  joinAdminInstructor(course: Course, instructor: Instructor): InstructorAccountSearchResult {
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
    const { email, name, googleId }: Instructor = instructor;
    instructorResult = { ...instructorResult, email, name, googleId };

    const { courseId, courseName }: Course = course;
    instructorResult = { ...instructorResult, courseId, courseName };

    // Generate links for instructors
    instructorResult.courseJoinLink = this.linkService.generateCourseJoinLinkInstructor(instructor);
    instructorResult.homePageLink = this.linkService.generateHomePageLink(googleId, this.linkService.INSTRUCTOR_HOME_PAGE);
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
        feedbackSessionLinks.openSessions[this.feedbackSessionService.generateNameFragment(feedbackSession).toString()]
          = this.linkService.generateSubmitUrl(student, feedbackSession.feedbackSessionName);
      } else {
        feedbackSessionLinks.notOpenSessions[this.feedbackSessionService.generateNameFragment(feedbackSession)]
          = this.linkService.generateSubmitUrl(student, feedbackSession.feedbackSessionName);
      }

      if (this.feedbackSessionService.isFeedbackSessionPublished(feedbackSession)) {
        feedbackSessionLinks.publishedSessions[this.feedbackSessionService.generateNameFragment(feedbackSession)]
           = this.linkService.generateResultUrl(student, feedbackSession.feedbackSessionName);
      }
    }
    return feedbackSessionLinks;
  }
}

/**
 * The typings for the response object returned by the instructor search service.
 */
export interface InstructorSearchResult {
  searchStudentsTables: SearchStudentsTable[];
}

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
  openSessions: { [index: string]: string };
  notOpenSessions: { [index: string]: string };
  publishedSessions: { [index: string]: string };
}

// Private interfaces
interface FeedbackSessionsGroup {
  [key: string]: string;
}

interface StudentFeedbackSessions {
  openSessions: FeedbackSessionsGroup;
  notOpenSessions: FeedbackSessionsGroup;
  publishedSessions: FeedbackSessionsGroup;
}
