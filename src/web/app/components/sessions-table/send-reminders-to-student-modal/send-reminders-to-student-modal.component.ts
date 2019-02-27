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
   * Bind individual checkboxes to all submitted and all yet submitted students checkbox.
   */
  bindSelectedCheckboxes(): void {
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
   * Collates a list of selected students with selected checkbox.
   */
  collateStudentsToSendHandler(): FeedbackSessionStudentRemindRequest {
    return this.collateStudentsToSend(this.studentStatusTableRows);
  }
}
