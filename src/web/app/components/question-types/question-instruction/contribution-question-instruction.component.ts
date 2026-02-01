import { Component, Input, TemplateRef } from '@angular/core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { SimpleModalType } from '../../simple-modal/simple-modal-type';

/**
 * Instruction of contribution question.
 */
@Component({
  selector: 'tm-contribution-question-instruction',
  templateUrl: './contribution-question-instruction.component.html',
  styleUrls: ['./contribution-question-instruction.component.scss'],
})
export class ContributionQuestionInstructionComponent {

  @Input()
  questionDetails: FeedbackContributionQuestionDetails = DEFAULT_CONTRIBUTION_QUESTION_DETAILS();

  @Input()
  numOfRecipients: number = 0;

  constructor(private simpleModalService: SimpleModalService) {}

  openHelpModal(modal: TemplateRef<any>): void {
    const modalHeader: string = 'More info about the <code>Equal Share</code> scale';
    this.simpleModalService.openInformationModal(modalHeader, SimpleModalType.NEUTRAL, modal);
  }
}
