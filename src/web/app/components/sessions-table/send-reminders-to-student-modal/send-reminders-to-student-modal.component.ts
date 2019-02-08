import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { ErrorMessageOutput } from '../../../error-message-output';
import { StudentResponseStatus, StudentsResponseStatus } from '../../../student';
import { SortBy, SortOrder } from '../sessions-table-model';

interface RemindStudentsTableRow {
  studentResponseStatus: StudentResponseStatus;
  isChecked: boolean;
}

/**
 * Send reminders to students modal.
 */
@Component({
  selector: 'tm-send-reminders-to-student-modal',
  templateUrl: './send-reminders-to-student-modal.component.html',
  styleUrls: ['./send-reminders-to-student-modal.component.scss'],
})
export class SendRemindersToStudentModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  checkAll: boolean = false;
  checkAllYetSubmitted: boolean = false;

  remindStudentsTableRows: RemindStudentsTableRow[] = [];
  remindStudentsTableRowSortBy: SortBy = SortBy.NONE;
  remindStudentsTableRowSortOrder: SortOrder = SortOrder.DESC;

  constructor(public activeModal: NgbActiveModal, public httpRequestService: HttpRequestService,
              public statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    this.initializeRemindStudentsResponses();
  }

  /**
   * Gets a list of students' response details.
   */
  initializeRemindStudentsResponses(): void {
    this.loading = true;

    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      fsname: this.feedbackSessionName,
    };

    this.httpRequestService.get('/session/remind/submission', paramMap)
      .subscribe((studentsResponseStatus: StudentsResponseStatus) => {
        studentsResponseStatus.studentsResponseStatus.forEach((studentResponseStatus: StudentResponseStatus) => {
          const studentTableRow: RemindStudentsTableRow = {
            studentResponseStatus,
            isChecked: false,
          };

          this.remindStudentsTableRows.push(studentTableRow);
        });
        this.sortRemindStudentsTableRows(SortBy.SUBMIT_STATUS);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
        this.isAjaxSuccess = false;
      });
    this.loading = false;
  }

  /**
   * Sort the students in according to selection option.
   */
  sortRemindStudentsTableRows(by: SortBy): void {
    this.remindStudentsTableRowSortBy = by;
    // reverse the sort order
    this.remindStudentsTableRowSortOrder =
        this.remindStudentsTableRowSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.remindStudentsTableRows.sort(this.sortRowsBy(by, this.remindStudentsTableRowSortOrder));
  }

  /**
   * Sorts the rows of students in order.
   */
  sortRowsBy(by: SortBy, order: SortOrder):
      ((a: { studentResponseStatus: StudentResponseStatus },
        b: { studentResponseStatus: StudentResponseStatus }) => number) {
    return ((a: { studentResponseStatus: StudentResponseStatus },
             b: { studentResponseStatus: StudentResponseStatus }): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.studentResponseStatus.sectionName;
          strB = b.studentResponseStatus.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.studentResponseStatus.teamName;
          strB = b.studentResponseStatus.teamName;
          break;
        case SortBy.STUDENT_NAME:
          strA = a.studentResponseStatus.name;
          strB = b.studentResponseStatus.name;
          break;
        case SortBy.STUDENT_EMAIL:
          strA = a.studentResponseStatus.email;
          strB = b.studentResponseStatus.email;
          break;
        case SortBy.SUBMIT_STATUS:
          strA = a.studentResponseStatus.responseStatus.toString();
          strB = b.studentResponseStatus.responseStatus.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      if (order === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (order === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }
      return 0;
    });
  }

  /**
   * Check all students checkbox to all students.
   */
  checkAllStudents(): void {
    for (const remindStudentRow of this.remindStudentsTableRows) {
      remindStudentRow.isChecked = this.checkAll;
    }
    this.checkAllYetSubmitted = this.checkAll;
  }

  /**
   * Check all yet to submit students checkbox to respective students.
   */
  checkAllYetSubmittedStudents(): void {
    for (const remindStudentRow of this.remindStudentsTableRows) {
      if (!remindStudentRow.studentResponseStatus.responseStatus) {
        remindStudentRow.isChecked = this.checkAllYetSubmitted;
      }
    }
  }

  /**
   * Bind individual checkboxes to all submitted and all yet submitted students checkbox.
   */
  bindSelectedCheckboxes(): void {
    this.checkAll = this.remindStudentsTableRows.every((tableRow: RemindStudentsTableRow) => {
      return tableRow.isChecked;
    });

    this.checkAllYetSubmitted = this.remindStudentsTableRows.filter(
        (tableRow: RemindStudentsTableRow) => !tableRow.studentResponseStatus.responseStatus,
    ).every((tableRow: RemindStudentsTableRow) => {
      return tableRow.isChecked && !tableRow.studentResponseStatus.responseStatus;
    });
  }

}
