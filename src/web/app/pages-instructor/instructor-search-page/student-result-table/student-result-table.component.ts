import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { JoinStatePipe } from '../../../components/student-list/join-state.pipe';
import { StudentListRowModel } from '../../../components/student-list/student-list.component';

/**
 * Search result for a list of students in a course
 */
export interface SearchStudentsListRowTable {
  courseId: string;
  students: StudentListRowModel[];
}

/**
 * Table to show student results, grouped by courses
 */
@Component({
  selector: 'tm-student-result-table',
  templateUrl: './student-result-table.component.html',
  styleUrls: ['./student-result-table.component.scss'],
})
export class StudentResultTableComponent implements OnInit {

  @Input() studentTables: SearchStudentsListRowTable[] = [];
  @Input() isActionButtonsEnabled: boolean = true;

  @Output() removeStudentFromCourseEvent: EventEmitter<StudentListRowModel> = new EventEmitter<StudentListRowModel>();

  studentSortBy: SortBy = SortBy.NONE;
  studentSortOrder: SortOrder = SortOrder.ASC;

  constructor(private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
  }

  /**
   * Sorts the student list.
   */
  sortStudentList(students: StudentListRowModel[], by: SortBy): void {
    this.studentSortBy = by;
    this.studentSortOrder =
        this.studentSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    students.sort(this.sortStudentBy(by, this.studentSortOrder));
  }

  /**
   * Returns a function to determine the order of sort for students.
   */
  sortStudentBy(by: SortBy, order: SortOrder):
      ((a: StudentListRowModel , b: StudentListRowModel) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();
    if (by === SortBy.NONE) {
      // Default order: section name > team name > student name
      return ((a: StudentListRowModel, b: StudentListRowModel): number => {
        return this.tableComparatorService
                .compare(SortBy.SECTION_NAME, order, a.student.sectionName, b.student.sectionName)
            || this.tableComparatorService.compare(SortBy.TEAM_NAME, order, a.student.teamName, b.student.teamName)
            || this.tableComparatorService.compare(SortBy.RESPONDENT_NAME, order, a.student.name, b.student.name);
      });
    }
    return (a: StudentListRowModel, b: StudentListRowModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.student.sectionName;
          strB = b.student.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.student.name;
          strB = b.student.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.student.teamName;
          strB = b.student.teamName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.student.email;
          strB = b.student.email;
          break;
        case SortBy.JOIN_STATUS:
          strA = joinStatePipe.transform(a.student.joinState);
          strB = joinStatePipe.transform(b.student.joinState);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }

  removeStudent(students: StudentListRowModel[], studentEmail: string): void {
    const studentToRemove: StudentListRowModel | undefined =
        students.find((student: StudentListRowModel) => student.student.email === studentEmail);
    if (studentToRemove) {
      this.removeStudentFromCourseEvent.emit(studentToRemove);
    }
  }

}
