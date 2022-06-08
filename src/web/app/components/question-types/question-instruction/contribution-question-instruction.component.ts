import { Component, Input } from '@angular/core';
import { FeedbackContributionQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_OLD_CONTRIBUTION_QUESTION_DETAILS } from '../../../../types/default-question-structs';

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
  questionDetails: FeedbackContributionQuestionDetails = DEFAULT_OLD_CONTRIBUTION_QUESTION_DETAILS();

  @Input()
  numOfRecipients: number = 0;

}
