import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { InstructorSessionResultSectionType } from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { NO_SPECIFIC_SECTION_ID } from '../app/pages-instructor/instructor-session-result-page/instructor-session-tab.model';
import { QueryParamKeys, ResourceEndpoints } from '../types/api-const';
import {
  FeedbackConstantSumOptionsResponseDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponseDetails,
  FeedbackResponse,
  FeedbackQuestionResponses,
  MessageOutput,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
  ResponseOutput,
} from '../types/api-output';
import { FeedbackResponsesRequest, Intent } from '../types/api-request';
import {
  DEFAULT_CONSTSUM_OPTIONS_RESPONSE_DETAILS,
  DEFAULT_CONSTSUM_RECIPIENTS_RESPONSE_DETAILS,
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
  RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED,
  RUBRIC_ANSWER_NOT_CHOSEN,
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
  private httpRequestService = inject(HttpRequestService);

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
        return DEFAULT_CONSTSUM_OPTIONS_RESPONSE_DETAILS();
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS:
        return DEFAULT_CONSTSUM_RECIPIENTS_RESPONSE_DETAILS();
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
        const numberOfOptionsRanked: number = rankOptionsDetails.answers.filter(
          (rank: number) => rank !== RANK_OPTIONS_ANSWER_NOT_SUBMITTED && rank != null,
        ).length;
        return numberOfOptionsRanked === 0;
      }
      case FeedbackQuestionType.RANK_RECIPIENTS: {
        const rankRecipientsDetails: FeedbackRankRecipientsResponseDetails =
          details as FeedbackRankRecipientsResponseDetails;
        return (
          rankRecipientsDetails.answer === RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED || rankRecipientsDetails.answer == null
        );
      }
      case FeedbackQuestionType.CONTRIB: {
        const contributionDetails: FeedbackContributionResponseDetails = details as FeedbackContributionResponseDetails;
        return contributionDetails.answer === CONTRIBUTION_POINT_NOT_SUBMITTED || contributionDetails.answer == null;
      }
      case FeedbackQuestionType.NUMSCALE: {
        const numScaleDetails: FeedbackNumericalScaleResponseDetails = details as FeedbackNumericalScaleResponseDetails;
        return numScaleDetails.answer === NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED || numScaleDetails.answer == null;
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
        return rubricDetails.answer.every((val: number) => val === RUBRIC_ANSWER_NOT_CHOSEN);
      }
      case FeedbackQuestionType.CONSTSUM_OPTIONS: {
        const constsumDetails: FeedbackConstantSumOptionsResponseDetails =
          details as FeedbackConstantSumOptionsResponseDetails;
        return constsumDetails.answers.length === 0;
      }
      case FeedbackQuestionType.CONSTSUM_RECIPIENTS: {
        const constsumRecipientsDetails: FeedbackConstantSumRecipientsResponseDetails =
          details as FeedbackConstantSumRecipientsResponseDetails;
        return constsumRecipientsDetails.answers.length === 0;
      }
      default:
        return true;
    }
  }

  /**
   * Determines whether responses should be displayed based on the selected section.
   */
  isFeedbackResponsesDisplayedOnSection(
    response: ResponseOutput,
    sectionId: string,
    sectionType: InstructorSessionResultSectionType,
  ): boolean {
    let isDisplayed = true;

    if (sectionId) {
      const isGiverInSection =
        sectionId === NO_SPECIFIC_SECTION_ID ? response.giverSectionId == null : response.giverSectionId === sectionId;
      const isRecipientInSection =
        sectionId === NO_SPECIFIC_SECTION_ID
          ? response.recipientSectionId == null
          : response.recipientSectionId === sectionId;

      switch (sectionType) {
        case InstructorSessionResultSectionType.EITHER:
          isDisplayed = isGiverInSection || isRecipientInSection;
          break;
        case InstructorSessionResultSectionType.GIVER:
          isDisplayed = isGiverInSection;
          break;
        case InstructorSessionResultSectionType.EVALUEE:
          isDisplayed = isRecipientInSection;
          break;
        case InstructorSessionResultSectionType.BOTH:
          isDisplayed = isGiverInSection && isRecipientInSection;
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
    questionId: string;
    intent: Intent;
    key: string;
    moderatedPerson: string;
  }): Observable<FeedbackResponsesResponse> {
    const paramMap: Record<string, string> = {
      questionid: queryParams.questionId,
      intent: queryParams.intent,
      key: queryParams.key,
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: queryParams.moderatedPerson,
    };
    return this.httpRequestService.get(ResourceEndpoints.RESPONSES, paramMap);
  }

  /**
   * Submits feedback responses for one or more feedback questions in a feedback session by calling API.
   */
  submitFeedbackResponses(
    feedbackSessionId: string,
    request: FeedbackResponsesRequest,
    params: {
      intent: Intent;
      key: string;
      moderatedPerson: string;
    },
  ): Observable<FeedbackQuestionResponses> {
    return this.httpRequestService.put(
      ResourceEndpoints.RESPONSES,
      {
        [QueryParamKeys.FEEDBACK_SESSION_ID]: feedbackSessionId,
        intent: params.intent,
        key: params.key,
        [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: params.moderatedPerson,
      },
      request,
    );
  }

  /**
   * Deletes a giver comment by clearing it from its feedback response.
   */
  deleteGiverComment(params: {
    responseId: string;
    intent: Intent;
    key: string;
    moderatedPerson: string;
  }): Observable<MessageOutput> {
    return this.httpRequestService.delete(ResourceEndpoints.RESPONSE_GIVER_COMMENT, {
      responseid: params.responseId,
      intent: params.intent,
      key: params.key,
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: params.moderatedPerson,
    });
  }
}
