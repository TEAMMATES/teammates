import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { FeedbackSessionStudentRemindRequest } from '../../../../types/api-request';
import { StudentStatusTableRowModel } from '../sessions-table-model';
import { StudentListInfoBaseModalComponent } from '../student-list-info-base-modal.component';

/**
 * Send reminders to students modal.
 */
@Component({
  selector: 'tm-send-reminders-to-student-modal',
  templateUrl: './send-reminders-to-student-modal.component.html',
  styleUrls: ['./send-reminders-to-student-modal.component.scss'],
})
export class SendRemindersToStudentModalComponent extends StudentListInfoBaseModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  checkAll: boolean = false;
  checkAllYetSubmitted: boolean = false;
  checkAllIns: boolean = false;
  checkAllYetSubmittedIns: boolean = false;

  constructor(public activeModal: NgbActiveModal, httpRequestService: HttpRequestService,
              statusMessageService: StatusMessageService) {
    super(httpRequestService, statusMessageService);
  }

  ngOnInit(): void {
    this.initializeStudentsStatusTable();
  }

  /**
   * Gets a list of students' response details.
   */
  initializeStudentsStatusTable(): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      fsname: this.feedbackSessionName,
    };

    this.getStudentStatusTableRowModel(paramMap, this.studentStatusTableRows);
  }

  /**
   * Check all students checkbox to all students.
   */
  checkAllStudentsHandler(): void {
    this.checkAllStudents(this.studentStatusTableRows, this.checkAll);
    this.checkAllYetSubmitted = this.checkAll;
  }

  /**
   * Check all yet to submit students checkbox to respective students.
   */
  checkAllYetSubmittedStudents(): void {
    for (const remindStudentRow of this.studentStatusTableRows) {
      if (!remindStudentRow.feedbackSessionStudentResponse.responseStatus) {
        remindStudentRow.isChecked = this.checkAllYetSubmitted;
      }
    }
  }

  /**
   * Check all instructors checkbox to all instructors.
   */
  checkAllInstructorsHandler(): void {
    this.checkAllInstructors(this.instructorStatusTableRows, this.checkAllIns);
    this.checkAllYetSubmittedIns = this.checkAllIns;
  }

  /**
   * Check all yet to submit instructors checkbox to respective instructors.
   */
  checkAllYetSubmittedInstructors(): void {
    for (const remindStudentRow of this.instructorStatusTableRows) {
      if (!remindStudentRow.feedbackSessionStudentResponse.responseStatus) {
        remindStudentRow.isChecked = this.checkAllYetSubmittedIns;
      }
    }
  }

  /**
   * Bind individual checkboxes to all submitted and all yet submitted students checkbox.
   */
  bindSelectedStudentsCheckboxes(): void {
    this.checkAll = this.studentStatusTableRows.every((tableRow: StudentStatusTableRowModel) => {
      return tableRow.isChecked;
    });

    this.checkAllYetSubmitted = this.studentStatusTableRows.filter(
        (tableRow: StudentStatusTableRowModel) => !tableRow.feedbackSessionStudentResponse.responseStatus,
    ).every((tableRow: StudentStatusTableRowModel) => {
      return tableRow.isChecked && !tableRow.feedbackSessionStudentResponse.responseStatus;
    });
  }

  /**
   * Bind individual checkboxes to all submitted and all yet submitted instructors checkbox.
   */
  bindSelectedInstructorsCheckboxes(): void {
    this.checkAllIns = this.instructorStatusTableRows.every((tableRow: StudentStatusTableRowModel) => {
      return tableRow.isChecked;
    });

    this.checkAllYetSubmittedIns = this.instructorStatusTableRows.filter(
        (tableRow: StudentStatusTableRowModel) => !tableRow.feedbackSessionStudentResponse.responseStatus,
    ).every((tableRow: StudentStatusTableRowModel) => {
      return tableRow.isChecked && !tableRow.feedbackSessionStudentResponse.responseStatus;
    });
  }

  /**
   * Collates a list of selected students with selected checkbox.
   */
  collateStudentsInstructorsToSendHandler(): FeedbackSessionStudentRemindRequest {
    return this.collateStudentsInstructorsToSend(this.studentStatusTableRows, this.instructorStatusTableRows);
  }
}
