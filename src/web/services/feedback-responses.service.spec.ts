import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackResponsesService } from './feedback-responses.service';
import { HttpRequestService } from './http-request.service';
import { createMockHttpRequestService, type MockHttpRequestService } from '../test-helpers/mock-http-request';
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

describe('FeedbackResponsesService', () => {
  let spyHttpRequestService: MockHttpRequestService;
  let service: FeedbackResponsesService;

  const questionTypeToResponseDetails: Map<FeedbackQuestionType, FeedbackResponseDetails> = new Map<
    FeedbackQuestionType,
    FeedbackResponseDetails
  >([
    [FeedbackQuestionType.TEXT, DEFAULT_TEXT_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RANK_OPTIONS, DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RANK_RECIPIENTS, DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONTRIB, DEFAULT_CONTRIBUTION_RESPONSE_DETAILS()],
    [FeedbackQuestionType.NUMSCALE, DEFAULT_NUMSCALE_RESPONSE_DETAILS()],
    [FeedbackQuestionType.MCQ, DEFAULT_MCQ_RESPONSE_DETAILS()],
    [FeedbackQuestionType.MSQ, DEFAULT_MSQ_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RUBRIC, DEFAULT_RUBRIC_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONSTSUM_OPTIONS, DEFAULT_CONSTSUM_OPTIONS_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONSTSUM_RECIPIENTS, DEFAULT_CONSTSUM_RECIPIENTS_RESPONSE_DETAILS()],
  ]);

  beforeEach(() => {
    spyHttpRequestService = createMockHttpRequestService();
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(FeedbackResponsesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve the correct default response details', () => {
    for (const [questionType, responseDetails] of questionTypeToResponseDetails) {
      expect(service.getDefaultFeedbackResponseDetails(questionType)).toStrictEqual(responseDetails);
    }
  });

  it('should correctly indicate whether any text response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.TEXT;
    const feedbackResponseDetails: FeedbackTextResponseDetails = {
      questionType: feedbackQuestionType,
      answer: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = 'answer';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any rank (options) response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RANK_OPTIONS;
    const feedbackResponseDetails: FeedbackRankOptionsResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answers = [null as unknown as number, 1, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any rank (recipients) response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RANK_RECIPIENTS;
    const feedbackResponseDetails: FeedbackRankRecipientsResponseDetails = {
      questionType: feedbackQuestionType,
      answer: RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = 100;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any team contribution response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONTRIB;
    const feedbackResponseDetails: FeedbackContributionResponseDetails = {
      questionType: feedbackQuestionType,
      answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = 1;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any numerical scale response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;
    const feedbackResponseDetails: FeedbackNumericalScaleResponseDetails = {
      questionType: feedbackQuestionType,
      answer: NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = 3;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any MCQ response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;
    const feedbackResponseDetails: FeedbackMcqResponseDetails = {
      questionType: feedbackQuestionType,
      answer: '',
      isOther: false,
      otherFieldContent: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = '<p>Good</p>';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();

    feedbackResponseDetails.isOther = true;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();

    feedbackResponseDetails.answer = '';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any MSQ response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.MSQ;
    const feedbackResponseDetails: FeedbackMsqResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
      isOther: false,
      otherFieldContent: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answers = ['answer 1'];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();

    feedbackResponseDetails.isOther = true;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();

    feedbackResponseDetails.answers = [];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it('should correctly indicate whether any rubric response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RUBRIC;
    const feedbackResponseDetails: FeedbackRubricResponseDetails = {
      questionType: feedbackQuestionType,
      answer: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, 3, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
  });

  it(
    'should correctly indicate whether any distribute points/constant sum (options) response details' +
      ' are empty or not',
    () => {
      const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
      const feedbackResponseDetails: FeedbackConstantSumOptionsResponseDetails = {
        questionType: feedbackQuestionType,
        answers: [],
      };
      expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

      feedbackResponseDetails.answers = [40, 60];
      expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
    },
  );

  it(
    'should correctly indicate whether any distribute points/constant sum (recipients) response details' +
      ' are empty or not',
    () => {
      const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
      const feedbackResponseDetails: FeedbackConstantSumRecipientsResponseDetails = {
        questionType: feedbackQuestionType,
        answers: [],
      };
      expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeTruthy();

      feedbackResponseDetails.answers = [70, 30];
      expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails)).toBeFalsy();
    },
  );

  it("should always indicate an unknown question type's response details as empty", () => {
    const unknownFeedbackQuestionType: FeedbackQuestionType = 'UNKNOWN' as FeedbackQuestionType;
    const feedbackResponseDetails: FeedbackResponseDetails = { questionType: unknownFeedbackQuestionType };
    expect(service.isFeedbackResponseDetailsEmpty(unknownFeedbackQuestionType, feedbackResponseDetails)).toBeTruthy();
  });

  it('should correctly display responses or not for an "either" section type', () => {
    const response: ResponseOutput = {
      giverSectionId: 'giver-section-id',
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;
    let section = 'giver-section-id';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = 'recipient-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = 'wrong-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();
  });

  it('should correctly display responses or not for a "giver" section type', () => {
    const response: ResponseOutput = {
      giverSectionId: 'giver-section-id',
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;
    let section = 'giver-section-id';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.GIVER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = 'recipient-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();

    section = 'wrong-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();
  });

  it('should correctly display responses or not for an "evaluee" section type', () => {
    const response: ResponseOutput = {
      giverSectionId: 'giver-section-id',
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;
    let section = 'giver-section-id';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EVALUEE;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();

    section = 'recipient-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = 'wrong-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();
  });

  it('should correctly display responses or not for a "both" section type', () => {
    const response: ResponseOutput = {
      giverSectionId: 'section-id',
      recipientSectionId: 'section-id',
    } as ResponseOutput;
    let section = 'section-id';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.BOTH;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = 'wrong-section-id';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeFalsy();
  });

  it('should display responses when no section is specified', () => {
    const response: ResponseOutput = {
      giverSectionId: 'giver-section-id',
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;
    let section = '';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = null as unknown as string;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();

    section = undefined as unknown as string;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();
  });

  it('should display responses for an unknown section type', () => {
    const response: ResponseOutput = {
      giverSectionId: 'giver-section-id',
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;
    const section = 'giver-section-id';
    const sectionType: InstructorSessionResultSectionType = 'UNKNOWN' as InstructorSessionResultSectionType;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType)).toBeTruthy();
  });

  it('should display responses for the fake no-specific section when section IDs are absent', () => {
    const response: ResponseOutput = {
      giverSectionId: undefined,
      recipientSectionId: 'recipient-section-id',
    } as ResponseOutput;

    expect(
      service.isFeedbackResponsesDisplayedOnSection(
        response,
        NO_SPECIFIC_SECTION_ID,
        InstructorSessionResultSectionType.EITHER,
      ),
    ).toBeTruthy();
    expect(
      service.isFeedbackResponsesDisplayedOnSection(
        response,
        NO_SPECIFIC_SECTION_ID,
        InstructorSessionResultSectionType.GIVER,
      ),
    ).toBeTruthy();
    expect(
      service.isFeedbackResponsesDisplayedOnSection(
        response,
        NO_SPECIFIC_SECTION_ID,
        InstructorSessionResultSectionType.EVALUEE,
      ),
    ).toBeFalsy();
  });

  it('should call get when retrieving a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[dummy question ID]',
      intent: dummyIntent,
      key: '[dummy key]',
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: '',
    };
    service.getFeedbackResponse({
      questionId: paramMap['questionid'],
      intent: dummyIntent,
      key: paramMap['key'],
      moderatedPerson: paramMap[QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON],
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap);
  });

  it('should call put when submitting a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      [QueryParamKeys.FEEDBACK_SESSION_ID]: '[dummy session ID]',
      intent: dummyIntent,
      key: '[dummy key]',
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: '',
    };
    const dummyRequest: FeedbackResponsesRequest = { questionResponses: {} };
    const dummyParams = {
      intent: dummyIntent,
      key: paramMap['key'],
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: paramMap[QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON],
    };
    service.submitFeedbackResponses(paramMap[QueryParamKeys.FEEDBACK_SESSION_ID], dummyRequest, dummyParams);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap, dummyRequest);
  });

  it('should call delete when deleting a giver comment', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      responseid: '[dummy response ID]',
      intent: dummyIntent,
      key: '[dummy key]',
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: '',
    };
    service.deleteGiverComment({
      responseId: paramMap['responseid'],
      intent: dummyIntent,
      key: paramMap['key'],
      [QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON]: paramMap[QueryParamKeys.FEEDBACK_SESSION_MODERATED_PERSON],
    });

    expect(spyHttpRequestService.delete).toHaveBeenCalledWith(ResourceEndpoints.RESPONSE_GIVER_COMMENT, paramMap);
  });
});
