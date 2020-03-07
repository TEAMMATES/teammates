import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { SearchStudentsTable } from '../app/pages-instructor/instructor-search-page/instructor-search-page.component';
import { StudentListSectionData } from '../app/pages-instructor/student-list/student-list-section-data';
import { InstructorPrivilege, Student, Students } from '../types/api-output';
import { HttpRequestService } from './http-request.service';
import { InstructorService } from './instructor.service';

/**
 * Handles the logic for search.
 */
@Injectable({
  providedIn: 'root',
})
export class SearchService {
  constructor(
    private httpRequestService: HttpRequestService,
    private instructorService: InstructorService,
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
        this.getInstructors(searchKey),
        this.getStudents(searchKey),
        this.getSessions(searchKey),
        this.getLinks(searchKey),
        this.getCourses(searchKey),
    ).pipe(map((res: [Instructors, Students, SearchSessions, SearchLinks, SearchCourses]) => this.joinAdmin(res)));
  }

  private getStudents(searchKey: string): Observable<Students> {
    const paramMap: { [key: string]: string } = {
      searchkey: searchKey,
    };
    return this.httpRequestService.get('/search/students', paramMap);
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

  private joinAdminStudents(
    resp: [Students, SearchSessions, SearchLinks, SearchCourses],
  ): StudentAccountSearchResult[] {
    const [students, sessions, links, courses]
      : [Students, SearchSessions, SearchLinks, SearchCourses] = resp;
    const studentsData: StudentAccountSearchResult[] = [];
    for (const student of students.students) {
      let studentResult: StudentAccountSearchResult = {
        email: '',
        name: '',
        comments: '',
        team: '',
        section: '',
        openSessions: {},
        closedSessions: {},
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
      const { email, name, comments, teamName: team, sectionName: section, googleId = '' }: Student = student;
      studentResult = { ...studentResult, email, name, comments, team, section, googleId };

      // Join sessions
      const matchingSessions: StudentSessions = sessions.sessions[email];
      if (matchingSessions != null) {
        const { openSessions, closedSessions, publishedSessions }: StudentSessions = matchingSessions;
        studentResult = { ...studentResult, openSessions, closedSessions, publishedSessions };
      }

      // Join courses
      const matchingCourses: SearchCoursesCommon[] =
        courses.students.filter((el: SearchCoursesCommon) => el.email === email);
      if (matchingCourses.length !== 0) {
        const { courseId, courseName, institute }: SearchCoursesCommon = matchingCourses[0];
        studentResult = { ...studentResult, courseId, courseName, institute };
      }

      // Join links
      const matchingLinks: SearchLinksStudent[] = links.students.filter((el: SearchLinksStudent) => el.email === email);
      if (matchingLinks.length !== 0) {
        const { courseJoinLink, manageAccountLink, recordsPageLink, homePageLink }: SearchLinksStudent
          = matchingLinks[0];
        studentResult = { ...studentResult, courseJoinLink, manageAccountLink, recordsPageLink, homePageLink };
      }

      studentsData.push(studentResult);
    }

    return studentsData;
  }

  private joinAdminInstructors(resp: [Instructors, SearchLinks, SearchCourses ]): InstructorAccountSearchResult[] {
    const [instructors, links, courses]: [Instructors, SearchLinks, SearchCourses] = resp;
    const instructorsData: InstructorAccountSearchResult[] = [];
    for (const instructor of instructors.instructors) {
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

      // Join courses
      const matchingCourses: SearchCoursesCommon[]
        = courses.instructors.filter((el: SearchCoursesCommon) => el.email === email);
      if (matchingCourses.length !== 0) {
        const { courseId, courseName, institute }: SearchCoursesCommon = matchingCourses[0];
        instructorResult = { ...instructorResult, courseId, courseName, institute };
      }

      // Join links
      const matchingLinks: SearchLinksInstructor[]
        = links.instructors.filter((el: SearchLinksInstructor) => el.email === email);
      if (matchingLinks.length !== 0) {
        const { courseJoinLink, manageAccountLink, homePageLink }: SearchLinksInstructor = matchingLinks[0];
        instructorResult = { ...instructorResult, courseJoinLink, manageAccountLink, homePageLink };
      }

      instructorsData.push(instructorResult);
    }

    return instructorsData;
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

interface InstructorAccountSearchResult {
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
  closedSessions: { [index: string]: string };
  publishedSessions: { [index: string]: string };
}
