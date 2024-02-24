import { Component, Input, OnInit } from '@angular/core';
import { QuestionResponse } from './question-response';
import {
  ContributionStatistics,
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

/**
 * Contribution question response.
 */
@Component({
  selector: 'tm-contribution-question-response',
  templateUrl: './contribution-question-response.component.html',
  styleUrls: ['./contribution-question-response.component.scss'],
})
export class ContributionQuestionResponseComponent
    extends QuestionResponse<FeedbackContributionResponseDetails, FeedbackContributionQuestionDetails>
    implements OnInit {

  @Input() statistics: string = '';
  @Input() giverEmail: string = '';
  @Input() recipientEmail: string = '';

  answer: number = 100;

  CONTRIBUTION_POINT_EQUAL_SHARE: number = CONTRIBUTION_POINT_EQUAL_SHARE;
  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  CONTRIBUTION_POINT_NOT_SURE: number = CONTRIBUTION_POINT_NOT_SURE;
  CONTRIBUTION_POINT_NOT_INITIALIZED: number = CONTRIBUTION_POINT_NOT_INITIALIZED;

  constructor() {
    super(DEFAULT_CONTRIBUTION_RESPONSE_DETAILS(), DEFAULT_CONTRIBUTION_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    this.answer = this.responseDetails.answer;
    if (this.statistics) {
      const statisticsObject: ContributionStatistics = JSON.parse(this.statistics);
      if (this.giverEmail === this.recipientEmail) {
        this.answer = statisticsObject.results[this.giverEmail].claimed;
      } else {
        this.answer = statisticsObject.results[this.giverEmail].claimedOthers[this.recipientEmail];
      }
    }
  }

}
