import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../respondent-list-info-table/respondent-list-info-table-model';

/**
 * Send reminders to respondents modal.
 */
@Component({
  selector: 'tm-send-reminders-to-respondents-modal',
  templateUrl: './send-reminders-to-respondents-modal.component.html',
  styleUrls: ['./send-reminders-to-respondents-modal.component.scss'],
})
export class SendRemindersToRespondentsModalComponent implements OnInit {

  // values below will be injected by other component
  courseId: string = '';
  feedbackSessionName: string = '';
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];
  instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [];

  constructor(public activeModal: NgbActiveModal) {
  }

  ngOnInit(): void {
  }

  /**
   * Changes selection state for all students.
   */
  changeSelectionStatusForAllStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels.forEach((model: StudentListInfoTableRowModel) => {
      model.isSelected = shouldSelect;
    });
  }

  /**
   * Changes selection state for all yet to submit students.
   */
  changeSelectionStatusForAllYetSubmittedStudentsHandler(shouldSelect: boolean): void {
    this.studentListInfoTableRowModels.forEach((model: StudentListInfoTableRowModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
  }

  /**
   * Changes selection state for all instructors.
   */
  changeSelectionStatusForAllInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels.forEach((model: InstructorListInfoTableRowModel) => {
      model.isSelected = shouldSelect;
    });
  }

  /**
   * Changes selection state for all yet to submit instructors.
   */
  changeSelectionStatusForAllYetSubmittedInstructorsHandler(shouldSelect: boolean): void {
    this.instructorListInfoTableRowModels.forEach((model: InstructorListInfoTableRowModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
  }

  /**
   * Collates a list of selected students with selected status.
   */
  collateRespondentsToSendHandler(): (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] {
    const studentsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
        this.studentListInfoTableRowModels.map(
            (model: StudentListInfoTableRowModel) => Object.assign({}, model))
            .filter((model: StudentListInfoTableRowModel) => model.isSelected);
    const instructorsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
        this.instructorListInfoTableRowModels.map(
            (model: InstructorListInfoTableRowModel) => Object.assign({}, model))
            .filter((model: InstructorListInfoTableRowModel) => model.isSelected);
    return studentsToSend.concat(instructorsToSend);
  }

  /**
   * Checks whether all students are selected.
   */
  get isAllStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels.every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all yet to submit students are selected.
   */
  get isAllYetToSubmitStudentsSelected(): boolean {
    return this.studentListInfoTableRowModels
        .filter((model: StudentListInfoTableRowModel) => !model.hasSubmittedSession)
        .every((model: StudentListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all instructors are selected.
   */
  get isAllInstructorsSelected(): boolean {
    return this.instructorListInfoTableRowModels.every((model: InstructorListInfoTableRowModel) => model.isSelected);
  }

  /**
   * Checks whether all yet to submit instructors are selected.
   */
  get isAllYetToSubmitInstructorsSelected(): boolean {
    return this.instructorListInfoTableRowModels
        .filter((model: InstructorListInfoTableRowModel) => !model.hasSubmittedSession)
        .every((model: InstructorListInfoTableRowModel) => model.isSelected);
  }
}
