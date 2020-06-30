import { Component } from '@angular/core';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONTRIBUTION_QUESTION_DETAILS,
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import {
  CONTRIBUTION_POINT_EQUAL_SHARE,
  CONTRIBUTION_POINT_NOT_INITIALIZED,
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
  CONTRIBUTION_POINT_NOT_INITIALIZED: number = CONTRIBUTION_POINT_NOT_INITIALIZED;

  constructor() {
    super(DEFAULT_CONTRIBUTION_RESPONSE_DETAILS(), DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

}
