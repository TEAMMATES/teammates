import { Injectable } from '@angular/core';
import {
  FeedbackContributionResponseDetails,
  FeedbackQuestionType,
  FeedbackResponseDetails,
  FeedbackTextResponseDetails,
} from '../types/api-output';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
} from '../types/feedback-response-details';

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
          questionType,
          answer: '',
        } as FeedbackTextResponseDetails;
      case FeedbackQuestionType.CONTRIB:
        return {
          questionType,
          answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
        } as FeedbackContributionResponseDetails;
      default:
        throw new Error(`Unknown question type ${questionType}`);
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
