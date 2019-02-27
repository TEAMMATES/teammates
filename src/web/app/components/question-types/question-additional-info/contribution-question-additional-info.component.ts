import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for contribution questions.
 */
@Component({
  selector: 'tm-contribution-question-additional-info',
  templateUrl: './contribution-question-additional-info.component.html',
  styleUrls: ['./contribution-question-additional-info.component.scss'],
})
export class ContributionQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackContributionQuestionDetails> {

  constructor() {
    super({
      isNotSureAllowed: false,
      questionType: FeedbackQuestionType.CONTRIB,
      questionText: '',
    });
  }

}
