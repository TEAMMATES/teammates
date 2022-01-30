import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
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
  FeedbackResponseDetails, FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
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
  CONTRIBUTION_POINT_NOT_SUBMITTED, NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
  RANK_OPTIONS_ANSWER_NOT_SUBMITTED,
  RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED, RUBRIC_ANSWER_NOT_CHOSEN,
} from '../types/feedback-response-details';
import { FeedbackResponsesService } from './feedback-responses.service';
import { HttpRequestService } from './http-request.service';

describe('FeedbackResponsesService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackResponsesService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(FeedbackResponsesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve the correct default essay/text response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.TEXT))
      .toStrictEqual(DEFAULT_TEXT_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default rank (options) response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.RANK_OPTIONS))
      .toStrictEqual(DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default rank (recipients) response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.RANK_RECIPIENTS))
      .toStrictEqual(DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default team contribution response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.CONTRIB))
      .toStrictEqual(DEFAULT_CONTRIBUTION_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default numerical scale response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.NUMSCALE))
      .toStrictEqual(DEFAULT_NUMSCALE_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default MCQ response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.MCQ))
      .toStrictEqual(DEFAULT_MCQ_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default MSQ response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.MSQ))
      .toStrictEqual(DEFAULT_MSQ_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default rubric response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.RUBRIC))
      .toStrictEqual(DEFAULT_RUBRIC_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default distribute points/constant sum (options) response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.CONSTSUM_OPTIONS))
      .toStrictEqual(DEFAULT_CONSTSUM_RESPONSE_DETAILS());
  });

  it('should retrieve the correct default distribute points/constant sum (recipients) response details', () => {
    expect(service.getDefaultFeedbackResponseDetails(FeedbackQuestionType.CONSTSUM_RECIPIENTS))
      .toStrictEqual(DEFAULT_CONSTSUM_RESPONSE_DETAILS());
  });

  it('should throw an error when trying to retrieve an unknown question type\'s response details', () => {
    const unknownFeedbackQuestionType: FeedbackQuestionType = 'UNKNOWN' as FeedbackQuestionType;
    expect(() => service.getDefaultFeedbackResponseDetails(unknownFeedbackQuestionType))
      .toThrow(`Unknown question type ${unknownFeedbackQuestionType}`);
  });

  it('should correctly indicate whether any text response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.TEXT;
    const feedbackResponseDetails: FeedbackTextResponseDetails = {
      questionType: feedbackQuestionType,
      answer: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = 'answer';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any rank (options) response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RANK_OPTIONS;
    const feedbackResponseDetails: FeedbackRankOptionsResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [null as unknown as number, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [1, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, 1, 2];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, 1, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [1, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [null as unknown as number, 2, 1];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [null as unknown as number, 1, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [null as unknown as number, 1, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [1];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [2, 1];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any rank (recipients) response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RANK_RECIPIENTS;
    const feedbackResponseDetails: FeedbackRankRecipientsResponseDetails = {
      questionType: feedbackQuestionType,
      answer: RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = 100;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any team contribution response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONTRIB;
    const feedbackResponseDetails: FeedbackContributionResponseDetails = {
      questionType: feedbackQuestionType,
      answer: CONTRIBUTION_POINT_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = 1;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any numerical scale response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;
    const feedbackResponseDetails: FeedbackNumericalScaleResponseDetails = {
      questionType: feedbackQuestionType,
      answer: NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED,
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = null as unknown as number;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = 3;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any MCQ response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;
    const feedbackResponseDetails: FeedbackMcqResponseDetails = {
      questionType: feedbackQuestionType,
      answer: '',
      isOther: false,
      otherFieldContent: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = '<p>Good</p>';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.isOther = true;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answer = '';
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any MSQ response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.MSQ;
    const feedbackResponseDetails: FeedbackMsqResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
      isOther: false,
      otherFieldContent: '',
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = ['answer 1'];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = ['answer 1', 'answer 2'];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.isOther = true;
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any rubric response details are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.RUBRIC;
    const feedbackResponseDetails: FeedbackRubricResponseDetails = {
      questionType: feedbackQuestionType,
      answer: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answer = [0, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, 2, 1];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, 3, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any distribute points/constant sum (options) response details'
    + ' are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_OPTIONS;
    const feedbackResponseDetails: FeedbackConstantSumResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [100];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [40, 60];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should correctly indicate whether any distribute points/constant sum (recipients) response details'
    + ' are empty or not', () => {
    const feedbackQuestionType: FeedbackQuestionType = FeedbackQuestionType.CONSTSUM_RECIPIENTS;
    const feedbackResponseDetails: FeedbackConstantSumResponseDetails = {
      questionType: feedbackQuestionType,
      answers: [],
    };
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [100];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();

    feedbackResponseDetails.answers = [70, 30];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeFalsy();
  });

  it('should always indicate an unknown question type\'s response details as empty', () => {
    const unknownFeedbackQuestionType: FeedbackQuestionType = 'UNKNOWN' as FeedbackQuestionType;
    const feedbackResponseDetails: FeedbackResponseDetails = { questionType: unknownFeedbackQuestionType };
    expect(service.isFeedbackResponseDetailsEmpty(unknownFeedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();
  });

  it('should call get when retrieving a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[auto-generated Datastore ID]',
      intent: dummyIntent,
      key: '[generated registration key]',
      moderatedperson: '',
    };
    service.getFeedbackResponse({
      questionId: paramMap.questionid,
      intent: dummyIntent,
      key: paramMap.key,
      moderatedPerson: paramMap.moderatedperson,
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap);
  });

  it('should call put when submitting a feedback response', () => {
    const paramMap: Record<string, string> = {
      questionid: '[auto-generated Datastore ID]',
    };
    const dummyAdditionalParams: { [key: string]: string } = {};
    const dummyRequest: FeedbackResponsesRequest = { responses: [] };
    service.submitFeedbackResponses(paramMap.questionid, dummyAdditionalParams, dummyRequest);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap, dummyRequest);
  });

  it('should include additional parameters when submitting a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[auto-generated Datastore ID]',
      intent: dummyIntent,
      key: '[generated registration key]',
      moderatedperson: '',
    };
    const dummyAdditionalParams: { [key: string]: string } = {
      intent: dummyIntent,
      key: paramMap.key,
      moderatedperson: paramMap.moderatedperson,
    };
    const dummyRequest: FeedbackResponsesRequest = { responses: [] };
    service.submitFeedbackResponses(paramMap.questionid, dummyAdditionalParams, dummyRequest);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap, dummyRequest);
  });
});
