import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponse,
  FeedbackResponseDetails, FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
} from '../types/api-output';
import { FeedbackResponseCreateRequest, FeedbackResponseUpdateRequest } from '../types/api-request';
import {
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
  DEFAULT_MSQ_RESPONSE_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
  DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS,
  DEFAULT_RUBRIC_RESPONSE_DETAILS,
  DEFAULT_TEXT_RESPONSE_DETAILS,
} from '../types/default-question-structs';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
  RANK_OPTIONS_ANSWER_NOT_SUBMITTED,
  RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED, RUBRIC_ANSWER_NOT_CHOSEN,
} from '../types/feedback-response-details';
import { HttpRequestService } from './http-request.service';

/**
 * Handles feedback response settings provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackResponsesService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Gets the default feedback response details based on {@code questionType}.
   */
  getDefaultFeedbackResponseDetails(questionType: FeedbackQuestionType): FeedbackResponseDetails {
    switch (questionType) {
      case FeedbackQuestionType.TEXT:
        return DEFAULT_TEXT_RESPONSE_DETAILS();
      case FeedbackQuestionType.RANK_OPTIONS:
        return DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS();
      case FeedbackQuestionType.RANK_RECIPIENTS:
        return DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS();
      case FeedbackQuestionType.CONTRIB:
        return DEFAULT_CONTRIBUTION_RESPONSE_DETAILS();
      case FeedbackQuestionType.NUMSCALE:
        return DEFAULT_NUMSCALE_RESPONSE_DETAILS();
      case FeedbackQuestionType.MCQ:
        return DEFAULT_MCQ_RESPONSE_DETAILS();
      case FeedbackQuestionType.MSQ:
        return DEFAULT_MSQ_RESPONSE_DETAILS();
      case FeedbackQuestionType.RUBRIC:
        return DEFAULT_RUBRIC_RESPONSE_DETAILS();
      case FeedbackQuestionType.CONSTSUM_OPTIONS:
        return DEFAULT_CONSTSUM_RESPONSE_DETAILS();
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
        return DEFAULT_CONSTSUM_RESPONSE_DETAILS();
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
      case FeedbackQuestionType.RANK_OPTIONS:
        const rankOptionsDetails: FeedbackRankOptionsResponseDetails = details as FeedbackRankOptionsResponseDetails;
        const numberOfOptionsRanked: number = rankOptionsDetails.answers
            .filter((rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED).length;
        return numberOfOptionsRanked === 0;
      case FeedbackQuestionType.RANK_RECIPIENTS:
        const rankRecipientsDetails: FeedbackRankRecipientsResponseDetails =
            details as FeedbackRankRecipientsResponseDetails;
        return rankRecipientsDetails.answer === RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED;
      case FeedbackQuestionType.CONTRIB:
        const contributionDetails: FeedbackContributionResponseDetails = details as FeedbackContributionResponseDetails;
        return contributionDetails.answer === CONTRIBUTION_POINT_NOT_SUBMITTED;
      case FeedbackQuestionType.NUMSCALE:
        const numScaleDetails: FeedbackNumericalScaleResponseDetails = details as FeedbackNumericalScaleResponseDetails;
        return numScaleDetails.answer === NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED;
      case FeedbackQuestionType.MCQ:
        const mcqDetails: FeedbackMcqResponseDetails = details as FeedbackMcqResponseDetails;
        return mcqDetails.answer.length === 0 && !mcqDetails.isOther;
      case FeedbackQuestionType.MSQ:
        const msqDetails: FeedbackMsqResponseDetails = details as FeedbackMsqResponseDetails;
        return msqDetails.answers.length === 0 && !msqDetails.isOther;
      case FeedbackQuestionType.RUBRIC:
        const rubricDetails: FeedbackRubricResponseDetails = details as FeedbackRubricResponseDetails;
        return rubricDetails.answer.length === 0
            || rubricDetails.answer.every((val: number) => val === RUBRIC_ANSWER_NOT_CHOSEN);
      case FeedbackQuestionType.CONSTSUM_OPTIONS:
        const constumDetails: FeedbackConstantSumResponseDetails = details as FeedbackConstantSumResponseDetails;
        return constumDetails.answers.length === 0;
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
        const constumRecipientsDetails: FeedbackConstantSumResponseDetails =
            details as FeedbackConstantSumResponseDetails;
        return constumRecipientsDetails.answers.length === 0;
      default:
        return true;
    }
  }

  /**
   * Creates a feedback response by calling API.
   */
  createFeedbackResponse(questionId: string, additionalParams: { [key: string]: string } = {},
                         request: FeedbackResponseCreateRequest): Observable<FeedbackResponse> {
    return this.httpRequestService.post('/response', {
      questionid: questionId,
      ...additionalParams,
    }, request);
  }

  /**
   * Updates a feedback response by calling API.
   */
  updateFeedbackResponse(responseId: string, additionalParams: { [key: string]: string } = {},
                         request: FeedbackResponseUpdateRequest): Observable<FeedbackResponse> {
    return this.httpRequestService.put('/response', {
      responseid: responseId,
      ...additionalParams,
    }, request);
  }

}
