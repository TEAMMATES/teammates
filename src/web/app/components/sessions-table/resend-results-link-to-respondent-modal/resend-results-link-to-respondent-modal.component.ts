import { Component, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  InstructorListInfoTableRowModel,
  StudentListInfoTableRowModel,
} from '../respondent-list-info-table/respondent-list-info-table-model';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';

/**
 * Re-send results link to students modal.
 */
@Component({
  selector: 'tm-resend-results-link-to-respondent-modal',
  templateUrl: './resend-results-link-to-respondent-modal.component.html',
  styleUrls: ['./resend-results-link-to-respondent-modal.component.scss'],
  imports: [RespondentListInfoTableComponent],
})
export class ResendResultsLinkToRespondentModalComponent {
  activeModal = inject(NgbActiveModal);

  // values below will be injected by other component
  courseId = '';
  feedbackSessionName = '';
  studentListInfoTableRowModels: StudentListInfoTableRowModel[] = [];
  instructorListInfoTableRowModels: InstructorListInfoTableRowModel[] = [];

  /**
   * Collates a list of selected respondents with selected status.
   */
  collateRespondentsToSendHandler(): (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] {
    const studentsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
      this.studentListInfoTableRowModels
        .map((model: StudentListInfoTableRowModel) => ({ ...model }))
        .filter((model: StudentListInfoTableRowModel) => model.isSelected);
    const instructorsToSend: (StudentListInfoTableRowModel | InstructorListInfoTableRowModel)[] =
      this.instructorListInfoTableRowModels
        .map((model: InstructorListInfoTableRowModel) => ({ ...model }))
        .filter((model: InstructorListInfoTableRowModel) => model.isSelected);
    return studentsToSend.concat(instructorsToSend);
  }
}
