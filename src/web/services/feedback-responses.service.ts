import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import {
  InstructorSessionResultSectionType,
} from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { ResourceEndpoints } from '../types/api-const';
import {
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponseDetails,
  FeedbackResponse,
  FeedbackResponses,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
  ResponseOutput,
} from '../types/api-output';
import { FeedbackResponsesRequest, Intent } from '../types/api-request';
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

/**
 * A collection of feedback responses.
 */
export interface FeedbackResponsesResponse {
  responses: FeedbackResponse[];
}

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
      case FeedbackQuestionType.TEXT: {
        const textDetails: FeedbackTextResponseDetails = details as FeedbackTextResponseDetails;
        return textDetails.answer.length === 0;
      }
      case FeedbackQuestionType.RANK_OPTIONS: {
        const rankOptionsDetails: FeedbackRankOptionsResponseDetails = details as FeedbackRankOptionsResponseDetails;
        const numberOfOptionsRanked: number = rankOptionsDetails.answers
            .filter((rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED && rank != null).length;
        return numberOfOptionsRanked === 0;
      }
      case FeedbackQuestionType.RANK_RECIPIENTS: {
        const rankRecipientsDetails: FeedbackRankRecipientsResponseDetails =
            details as FeedbackRankRecipientsResponseDetails;
        return rankRecipientsDetails.answer === RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED
            || rankRecipientsDetails.answer == null;
      }
      case FeedbackQuestionType.CONTRIB: {
        const contributionDetails: FeedbackContributionResponseDetails = details as FeedbackContributionResponseDetails;
        return contributionDetails.answer === CONTRIBUTION_POINT_NOT_SUBMITTED
            || contributionDetails.answer == null;
      }
      case FeedbackQuestionType.NUMSCALE: {
        const numScaleDetails: FeedbackNumericalScaleResponseDetails = details as FeedbackNumericalScaleResponseDetails;
        return numScaleDetails.answer === NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED
            || numScaleDetails.answer == null;
      }
      case FeedbackQuestionType.MCQ: {
        const mcqDetails: FeedbackMcqResponseDetails = details as FeedbackMcqResponseDetails;
        return mcqDetails.answer.length === 0 && !mcqDetails.isOther;
      }
      case FeedbackQuestionType.MSQ: {
        const msqDetails: FeedbackMsqResponseDetails = details as FeedbackMsqResponseDetails;
        return msqDetails.answers.length === 0 && !msqDetails.isOther;
      }
      case FeedbackQuestionType.RUBRIC: {
        const rubricDetails: FeedbackRubricResponseDetails = details as FeedbackRubricResponseDetails;
        return rubricDetails.answer.length === 0
            || rubricDetails.answer.every((val: number) => val === RUBRIC_ANSWER_NOT_CHOSEN);
      }
      case FeedbackQuestionType.CONSTSUM_OPTIONS: {
        const constsumDetails: FeedbackConstantSumResponseDetails = details as FeedbackConstantSumResponseDetails;
        return constsumDetails.answers.length === 0;
      }
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS: {
        const constsumRecipientsDetails: FeedbackConstantSumResponseDetails =
            details as FeedbackConstantSumResponseDetails;
        return constsumRecipientsDetails.answers.length === 0;
      }
      default:
        return true;
    }
  }

  /**
   * Determines whether responses should be displayed based on the selected section.
   */
  isFeedbackResponsesDisplayedOnSection(response: ResponseOutput, section: string,
      sectionType: InstructorSessionResultSectionType): boolean {

    let isDisplayed: boolean = true;

    if (section) {
      switch (sectionType) {
        case InstructorSessionResultSectionType.EITHER:
          isDisplayed = response.giverSection === section || response.recipientSection === section;
          break;
        case InstructorSessionResultSectionType.GIVER:
          isDisplayed = response.giverSection === section;
          break;
        case InstructorSessionResultSectionType.EVALUEE:
          isDisplayed = response.recipientSection === section;
          break;
        case InstructorSessionResultSectionType.BOTH:
          isDisplayed = response.giverSection === section && response.recipientSection === section;
          break;
        default:
      }
    }

    return isDisplayed;
  }

  /**
   * Retrieves a feedback response by calling API.
   */
  getFeedbackResponse(queryParams: {
    questionId: string,
    intent: Intent,
    key: string,
    moderatedPerson: string,
  }): Observable<FeedbackResponsesResponse> {
    const paramMap: Record<string, string> = {
      questionid: queryParams.questionId,
      intent: queryParams.intent,
      key: queryParams.key,
      moderatedperson: queryParams.moderatedPerson,
    };
    return this.httpRequestService.get(ResourceEndpoints.RESPONSES, paramMap);
  }

  /**
   * Submits a list of feedback responses for a feedback question by calling API.
   */
  submitFeedbackResponses(questionId: string, request: FeedbackResponsesRequest,
                          additionalParams: { [key: string]: string } = {}): Observable<FeedbackResponses> {
    return this.httpRequestService.put(ResourceEndpoints.RESPONSES, {
      questionid: questionId,
      ...additionalParams,
    }, request);
  }

}
