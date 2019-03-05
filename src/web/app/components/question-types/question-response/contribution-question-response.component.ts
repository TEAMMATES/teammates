import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import {
  CONTRIBUTION_POINT_EQUAL_SHARE,
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  CONTRIBUTION_POINT_NOT_SURE,
} from '../../../../types/feedback-response-details';
import { QuestionResponse } from './question-response';

/**
 * Contribution question response.
 */
@Component({
  selector: 'tm-contribution-question-response',
  templateUrl: './contribution-question-response.component.html',
  styleUrls: ['./contribution-question-response.component.scss'],
})
export class ContributionQuestionResponseComponent
    extends QuestionResponse<FeedbackContributionResponseDetails, FeedbackContributionQuestionDetails> {

  CONTRIBUTION_POINT_EQUAL_SHARE: number = CONTRIBUTION_POINT_EQUAL_SHARE;
  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  CONTRIBUTION_POINT_NOT_SURE: number = CONTRIBUTION_POINT_NOT_SURE;

  constructor() {
    super({
      answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
      questionType: FeedbackQuestionType.CONTRIB,
    }, {
      isNotSureAllowed: true,
      questionType: FeedbackQuestionType.CONTRIB,
      questionText: '',
    });
  }

}
