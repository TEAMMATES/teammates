import { Injectable } from '@angular/core';
import { FeedbackQuestionType } from '../app/feedback-question';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED, FeedbackContributionResponseDetails,
  FeedbackResponseDetails,
  FeedbackTextResponseDetails,
} from '../app/feedback-response';

/**
 * Handles feedback response settings provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackResponsesService {

  constructor() { }

  /**
   * Gets the default feedback response details based on {@code questionType}.
   */
  getDefaultFeedbackResponseDetails(questionType: FeedbackQuestionType): FeedbackResponseDetails {
    switch (questionType) {
      case FeedbackQuestionType.TEXT:
        return {
          answer: '',
        };
      case FeedbackQuestionType.CONTRIB:
        return {
          answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
        };
      default:
        return {};
    }
  }

  /**
   * Checks whether a feedback response details is empty.
   */
  isFeedbackResponseDetailsEmpty(questionType: FeedbackQuestionType, details: FeedbackResponseDetails): boolean {
    switch (questionType) {
      case FeedbackQuestionType.TEXT:
        const textDetails: FeedbackTextResponseDetails = details as FeedbackTextResponseDetails;
        return textDetails.answer.length === 0;
      case FeedbackQuestionType.CONTRIB:
        const contributionDetails: FeedbackContributionResponseDetails = details as FeedbackContributionResponseDetails;
        return contributionDetails.answer === CONTRIBUTION_POINT_NOT_SUBMITTED;
      default:
        return true;
    }
  }
}
