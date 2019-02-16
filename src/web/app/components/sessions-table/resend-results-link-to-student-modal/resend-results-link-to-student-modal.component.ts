import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { FeedbackSessionStudentRemindRequest } from '../../../../types/api-request';
import { StudentStatusTableRowModel } from '../sessions-table-model';
import { StudentListInfoBaseModalComponent } from '../student-list-info-base-modal.component';

/**
 * Re-send results link to students modal.
 */
@Component({
  selector: 'tm-resend-results-link-to-student-modal',
  templateUrl: './resend-results-link-to-student-modal.component.html',
  styleUrls: ['./resend-results-link-to-student-modal.component.scss'],
})
export class ResendResultsLinkToStudentModalComponent extends StudentListInfoBaseModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  checkAll: boolean = false;

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
   * Bind individual checkboxes to all submitted and all yet submitted students checkbox.
   */
  bindSelectedCheckboxes(): void {
    this.checkAll = this.studentStatusTableRows.every((tableRow: StudentStatusTableRowModel) => {
      return tableRow.isChecked;
    });
  }

  /**
   * Check all students checkbox to all students.
   */
  checkAllStudentsHandler(): void {
    this.checkAllStudents(this.studentStatusTableRows, this.checkAll);
  }

  /**
   * Collates a list of selected students with selected checkbox.
   */
  collateStudentsToSendHandler(): FeedbackSessionStudentRemindRequest {
    return this.collateStudentsToSend(this.studentStatusTableRows);
  }
}
