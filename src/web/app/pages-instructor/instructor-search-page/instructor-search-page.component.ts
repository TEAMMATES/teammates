import { Component } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { finalize, map, mergeMap } from 'rxjs/operators';
import { SearchParams } from './instructor-search-bar/instructor-search-bar.component';
import { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { InstructorSearchResult, SearchService } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ApiConst } from '../../../types/api-const';
import { InstructorPermissionSet, InstructorPrivilege, Student } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Instructor search page.
 */
@Component({
  selector: 'tm-instructor-search-page',
  templateUrl: './instructor-search-page.component.html',
  styleUrls: ['./instructor-search-page.component.scss'],
})
export class InstructorSearchPageComponent {

  searchParams: SearchParams = {
    searchKey: '',
  };
  searchString: string = '';
  studentsListRowTables: SearchStudentsListRowTable[] = [];
  isSearching: boolean = false;

  constructor(
    private statusMessageService: StatusMessageService,
    private searchService: SearchService,
    private courseService: CourseService,
    private instructorService: InstructorService,
  ) {}

  /**
   * Searches for students matching the search query.
   */
  search(): void {
    if (this.searchParams.searchKey === '') {
      return;
    }
    this.searchString = this.searchParams.searchKey;
    this.isSearching = true;
    this.searchService.searchInstructor(this.searchParams.searchKey).pipe(
        map((res: InstructorSearchResult) => this.getCoursesWithStudents(res.students)),
        mergeMap((coursesWithStudents: SearchStudentsListRowTable[]) =>
            forkJoin([
              of(coursesWithStudents),
              this.getPrivileges(coursesWithStudents),
            ]),
        ),
        map((res: [SearchStudentsListRowTable[], InstructorPrivilege[]]) => this.combinePrivileges(res)),
        finalize(() => {
          this.isSearching = false;
        }),
    ).subscribe({
      next: (resp: TransformedInstructorSearchResult) => {
        const searchStudentsTable: SearchStudentsListRowTable[] = resp.searchStudentTables;
        const hasStudents: boolean = !!(
            searchStudentsTable && searchStudentsTable.length
        );

        if (hasStudents) {
          this.studentsListRowTables = searchStudentsTable;
          if (searchStudentsTable.length >= ApiConst.SEARCH_QUERY_SIZE_LIMIT) {
            this.statusMessageService.showWarningToast(
                `${ApiConst.SEARCH_QUERY_SIZE_LIMIT} results have been shown on this page
              but there may be more results not shown. Consider searching with more specific terms.`);
          }
        } else {
          this.studentsListRowTables = [];
        }
        if (!hasStudents) {
          this.statusMessageService.showWarningToast('No results found.');
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.studentsListRowTables = [];
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  getCoursesWithStudents(students: Student[]): SearchStudentsListRowTable[] {
    const distinctCourses: string[] = Array.from(
        new Set(students.map((s: Student) => s.courseId)),
    );
    const coursesWithStudents: SearchStudentsListRowTable[] = distinctCourses.map(
        (courseId: string) => ({
          courseId,
          students: Array.from(
              new Set(
                  students
                      .filter((s: Student) => s.courseId === courseId),
              ),
          ).map((s: Student) => ({
            student: s,
            isAllowedToViewStudentInSection: false,
            isAllowedToModifyStudent: false,
          })),
        }),
    );

    return coursesWithStudents;
  }

  getPrivileges(
      coursesWithStudents: SearchStudentsListRowTable[],
  ): Observable<InstructorPrivilege[]> {
    if (coursesWithStudents.length === 0) {
      return of([]);
    }
    const privileges: Observable<InstructorPrivilege>[] = [];
    coursesWithStudents.forEach((course: SearchStudentsListRowTable) => {
      const sectionToPrivileges: Record<string, Observable<InstructorPrivilege>> = {};
      Array.from(
          new Set(course.students.map((studentModel: StudentListRowModel) => studentModel.student.sectionName)),
      ).forEach((section: string) => {
        sectionToPrivileges[section] = this.instructorService.loadInstructorPrivilege({ courseId: course.courseId });
      });
      course.students.forEach((studentModel: StudentListRowModel) =>
          privileges.push(sectionToPrivileges[studentModel.student.sectionName]),
      );
    });
    return forkJoin(privileges);
  }

  combinePrivileges(
      [coursesWithStudents, privileges]: [SearchStudentsListRowTable[], InstructorPrivilege[]],
  ): TransformedInstructorSearchResult {
    /**
     * Pop the privilege objects one at a time and attach them to the results. This is possible
     * because `forkJoin` guarantees that the `InstructorPrivilege` results are returned in the
     * same order the requests were made.
     */
    for (const course of coursesWithStudents) {
      for (const studentModel of course.students) {
        const privilege: InstructorPrivilege | undefined = privileges.shift();
        if (!privilege) {
          continue;
        }
        const sectionName: string = studentModel.student.sectionName;
        const courseLevel: InstructorPermissionSet = privilege.privileges.courseLevel;
        const sectionLevel: InstructorPermissionSet = privilege.privileges.sectionLevel[sectionName]
            || courseLevel;

        studentModel.isAllowedToViewStudentInSection = sectionLevel.canViewStudentInSections;
        studentModel.isAllowedToModifyStudent = sectionLevel.canModifyStudent;
      }
    }

    return {
      searchStudentTables: coursesWithStudents,
    };
  }

  /**
   * Removes the student from course and updates the search table
   */
  removeStudentFromCourse(studentRow: StudentListRowModel): void {
    const courseId: string = studentRow.student.courseId;
    const studentEmail: string = studentRow.student.email;

    this.courseService.removeStudentFromCourse(courseId, studentEmail).subscribe({
      next: () => {
        const affectedTable: SearchStudentsListRowTable | undefined =
            this.studentsListRowTables.find((table: SearchStudentsListRowTable) => table.courseId === courseId);
        if (affectedTable) {
          affectedTable.students = affectedTable.students
              .filter((student: StudentListRowModel) => student.student.email !== studentEmail);
        }

        this.statusMessageService
            .showSuccessToast(`Student "${studentRow.student.name}" is successfully deleted from course "${courseId}"`);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

}

interface TransformedInstructorSearchResult {
  searchStudentTables: SearchStudentsListRowTable[];
}
