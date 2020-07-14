import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { LoadingBarService } from '../../../services/loading-bar.service';
import { InstructorSearchResult, SearchService } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  StudentListSectionData,
  StudentListStudentData,
} from '../../components/student-list/student-list-section-data';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { SearchCommentsTable } from './comment-result-table/comment-result-table.component';
import { SearchParams } from './instructor-search-bar/instructor-search-bar.component';
import { SearchStudentsListRowTable, SearchStudentsTable } from './student-result-table/student-result-table.component';

/**
 * Instructor search page.
 */
@Component({
  selector: 'tm-instructor-search-page',
  templateUrl: './instructor-search-page.component.html',
  styleUrls: ['./instructor-search-page.component.scss'],
})
export class InstructorSearchPageComponent implements OnInit {

  searchParams: SearchParams = {
    searchKey: '',
    isSearchForStudents: true,
    isSearchForComments: false,
  };
  studentsListRowTables: SearchStudentsListRowTable[] = [];
  commentTables: SearchCommentsTable[] = [];

  constructor(
    private route: ActivatedRoute,
    private statusMessageService: StatusMessageService,
    private loadingBarService: LoadingBarService,
    private searchService: SearchService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.studentSearchkey) {
        this.searchParams.searchKey = queryParams.studentSearchkey;
      }
      if (this.searchParams.searchKey) {
        this.search();
      }
    });
  }

  /**
   * Searches for students and questions/responses/comments matching the search query.
   */
  search(): void {
    if (!(this.searchParams.isSearchForComments || this.searchParams.isSearchForStudents)
        || this.searchParams.searchKey === '') {
      return;
    }
    this.loadingBarService.showLoadingBar();
    forkJoin([
      this.searchParams.isSearchForComments
          ? this.searchService.searchComment(this.searchParams.searchKey)
          : of() as Observable<InstructorSearchResult>,
      this.searchParams.isSearchForStudents
          ? this.searchService.searchInstructor(this.searchParams.searchKey)
          : of() as Observable<InstructorSearchResult>,
    ]).pipe(
        finalize(() => this.loadingBarService.hideLoadingBar()),
    ).subscribe((resp: InstructorSearchResult[]) => {
      this.commentTables = resp[0].searchCommentsTables;
      const searchStudentsTable: SearchStudentsTable[] = resp[1].searchStudentsTables;
      const hasStudents: boolean = !!(
          searchStudentsTable && searchStudentsTable.length
      );
      const hasComments: boolean = !!(
          this.commentTables && this.commentTables.length
      );

      if (hasStudents) {
        this.studentsListRowTables = this.flattenStudentTable(searchStudentsTable);
      }
      if (!hasStudents && !hasComments) {
        this.statusMessageService.showWarningToast('No results found.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  private flattenStudentTable(searchStudentsTable: SearchStudentsTable[]): SearchStudentsListRowTable[] {
    return searchStudentsTable.map((course: SearchStudentsTable) => {
      const studentsList: StudentListRowModel[] = [];
      course.sections.forEach((section: StudentListSectionData) => {
        section.students.forEach((student: StudentListStudentData) => {
          studentsList.push({
            student: {
              courseId: course.courseId,
              name: student.name,
              email: student.email,
              teamName: student.team,
              sectionName: section.sectionName,
              joinState: student.status,
            },
            photoUrl: student.photoUrl,
            isAllowedToModifyStudent: section.isAllowedToModifyStudent,
            isAllowedToViewStudentInSection: section.isAllowedToViewStudentInSection,
          });
        });
      });

      return {
        courseId: course.courseId,
        students: studentsList,
      };
    });
  }
}
