import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StudentListInfoTableRowModel } from '../student-list-info-table/student-list-info-table-model';

/**
 * Send reminders to students modal.
 */
@Component({
  selector: 'tm-send-reminders-to-student-modal',
  templateUrl: './send-reminders-to-student-modal.component.html',
  styleUrls: ['./send-reminders-to-student-modal.component.scss'],
})
export class SendRemindersToStudentModalComponent implements OnInit {

  // values below will be injected by other component
  courseId: string = '';
  feedbackSessionName: string = '';
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];

  constructor(public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    for (const model of this.studentListInfoTableRowModels) {
      model.isSelected = shouldSelect;
    }
  }

  /**
   * Changes selection state for all yet to submit students.
   */
  changeSelectionStatusForAllYetSubmittedStudentsHandler(shouldSelect: boolean): void {
    for (const model of this.studentListInfoTableRowModels) {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    }
  }

  /**
   * Collates a list of selected students with selected status.
   */
  collateStudentsToSendHandler(): StudentListInfoTableRowModel[] {
    return this.studentListInfoTableRowModels
        .map((model: StudentListInfoTableRowModel) => Object.assign({}, model))
        .filter((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all students are selected.
   */
  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all students are slected.
   */
  get isAllYetToSubmitStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels
        .filter((model: StudentListInfoTableRowModel) => !model.hasSubmittedSession)
        .every((model: StudentListInfoTableRowModel) => model.isSelected);
  }
}
