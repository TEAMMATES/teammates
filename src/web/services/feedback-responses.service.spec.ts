import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResourceEndpoints } from '../types/api-const';
import { FeedbackQuestionType } from '../types/api-output';
import { FeedbackResponsesRequest, Intent } from '../types/api-request';
import {
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
  DEFAULT_MSQ_RESPONSE_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
  DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS, DEFAULT_RUBRIC_RESPONSE_DETAILS,
  DEFAULT_TEXT_RESPONSE_DETAILS,
} from '../types/default-question-structs';
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
