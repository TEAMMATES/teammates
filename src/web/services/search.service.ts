import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchStudentsTable } from '../app/pages-instructor/instructor-search-page/instructor-search-page.component';
import { ResourceEndpoints } from '../types/api-endpoints';
import { InstructorPrivilege, Student, Students } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles the logic for search.
 */
@Injectable({
  providedIn: 'root',
})
export class SearchService {
  constructor(private httpRequestService: HttpRequestService) {}

  searchInstructor(searchKey: string): Observable<InstructorSearchResult> {
    return this.getStudents(searchKey).pipe(
      map((studentsRes: Students): SearchStudentsTable[] =>
        this.getCoursesWithSections(studentsRes),
      ),
      map(
        (coursesWithSections: SearchStudentsTable[]): InstructorSearchResult =>
          this.getPrivileges(coursesWithSections),
      ),
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
  ): InstructorSearchResult {
    for (const course of coursesWithSections) {
      for (const section of course.sections) {
        this.httpRequestService
          .get(ResourceEndpoints.INSTRUCTOR_PRIVILEGE, {
            courseid: course.courseId,
            sectionname: section.sectionName,
          })
          .subscribe((res: InstructorPrivilege): void => {
            section.isAllowedToViewStudentInSection =
              res.canViewStudentInSections;
            section.isAllowedToModifyStudent = res.canModifyStudent;
          });
      }
    }

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
