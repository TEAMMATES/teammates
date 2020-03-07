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

  getStudents(searchKey: string): Observable<Students> {
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
