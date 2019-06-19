import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StudentListInfoTableRowModel } from '../student-list-info-table/student-list-info-table-model';

/**
 * Re-send results link to students modal.
 */
@Component({
  selector: 'tm-resend-results-link-to-student-modal',
  templateUrl: './resend-results-link-to-student-modal.component.html',
  styleUrls: ['./resend-results-link-to-student-modal.component.scss'],
})
export class ResendResultsLinkToStudentModalComponent implements OnInit {

  // values below will be injected by other component
  courseId: string = '';
  feedbackSessionName: string = '';
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];

  constructor(public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
  }

  /**
   * Collates a list of selected students with selected status.
   */
  collateStudentsToSendHandler(): StudentListInfoTableRowModel[] {
    return this.studentListInfoTableRowModels
        .map((model: StudentListInfoTableRowModel) => Object.assign({}, model))
        .filter((model: StudentListInfoTableRowModel) => model.isSelected);
  }
}
