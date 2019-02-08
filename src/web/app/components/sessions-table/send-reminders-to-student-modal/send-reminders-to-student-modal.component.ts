import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { ErrorMessageOutput } from '../../../error-message-output';
import { StudentResponseStatus, StudentsResponseStatus } from '../../../student';

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

  checkAll: boolean = false;
  checkAllYetSubmitted: boolean = false;

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
