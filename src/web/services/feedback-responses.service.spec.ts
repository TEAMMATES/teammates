import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FeedbackResponsesService } from './feedback-responses.service';
import { HttpRequestService } from './http-request.service';
import { InstructorSessionResultSectionType }
  from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
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
  RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED,
  RUBRIC_ANSWER_NOT_CHOSEN,
} from '../types/feedback-response-details';

describe('FeedbackResponsesService', () => {
  let spyHttpRequestService: any;
  let service: FeedbackResponsesService;

  const questionTypeToResponseDetails: Map<FeedbackQuestionType, FeedbackResponseDetails> =
    new Map<FeedbackQuestionType, FeedbackResponseDetails>([
    [FeedbackQuestionType.TEXT, DEFAULT_TEXT_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RANK_OPTIONS, DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RANK_RECIPIENTS, DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONTRIB, DEFAULT_CONTRIBUTION_RESPONSE_DETAILS()],
    [FeedbackQuestionType.NUMSCALE, DEFAULT_NUMSCALE_RESPONSE_DETAILS()],
    [FeedbackQuestionType.MCQ, DEFAULT_MCQ_RESPONSE_DETAILS()],
    [FeedbackQuestionType.MSQ, DEFAULT_MSQ_RESPONSE_DETAILS()],
    [FeedbackQuestionType.RUBRIC, DEFAULT_RUBRIC_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONSTSUM_OPTIONS, DEFAULT_CONSTSUM_RESPONSE_DETAILS()],
    [FeedbackQuestionType.CONSTSUM_RECIPIENTS, DEFAULT_CONSTSUM_RESPONSE_DETAILS()],
  ]);

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
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

  it('should retrieve the correct default response details', () => {
    for (const [questionType, responseDetails] of questionTypeToResponseDetails) {
      expect(service.getDefaultFeedbackResponseDetails(questionType)).toStrictEqual(responseDetails);
    }
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

    feedbackResponseDetails.answers = [RANK_OPTIONS_ANSWER_NOT_SUBMITTED, null as unknown as number];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

    feedbackResponseDetails.answers = [null as unknown as number, 1, RANK_OPTIONS_ANSWER_NOT_SUBMITTED];
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

    feedbackResponseDetails.answer = [RUBRIC_ANSWER_NOT_CHOSEN, RUBRIC_ANSWER_NOT_CHOSEN];
    expect(service.isFeedbackResponseDetailsEmpty(feedbackQuestionType, feedbackResponseDetails))
      .toBeTruthy();

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

  it('should correctly display responses or not for an "either" section type', () => {
    const response: ResponseOutput = {
      giverSection: 'giver section',
      recipientSection: 'recipient section',
    } as ResponseOutput;
    let section: string = 'giver section';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = 'recipient section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = 'wrong section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();
  });

  it('should correctly display responses or not for a "giver" section type', () => {
    const response: ResponseOutput = {
      giverSection: 'giver section',
      recipientSection: 'recipient section',
    } as ResponseOutput;
    let section: string = 'giver section';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.GIVER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = 'recipient section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();

    section = 'wrong section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();
  });

  it('should correctly display responses or not for an "evaluee" section type', () => {
    const response: ResponseOutput = {
      giverSection: 'giver section',
      recipientSection: 'recipient section',
    } as ResponseOutput;
    let section: string = 'giver section';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EVALUEE;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();

    section = 'recipient section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = 'wrong section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();
  });

  it('should correctly display responses or not for a "both" section type', () => {
    const response: ResponseOutput = {
      giverSection: 'section',
      recipientSection: 'section',
    } as ResponseOutput;
    let section: string = 'section';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.BOTH;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = 'wrong section';
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeFalsy();
  });

  it('should display responses when no section is specified', () => {
    const response: ResponseOutput = {
      giverSection: 'giver section',
      recipientSection: 'recipient section',
    } as ResponseOutput;
    let section: string = '';
    const sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = null as unknown as string;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();

    section = undefined as unknown as string;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();
  });

  it('should display responses for an unknown section type', () => {
    const response: ResponseOutput = {
      giverSection: 'giver section',
      recipientSection: 'recipient section',
    } as ResponseOutput;
    const section: string = 'giver section';
    const sectionType: InstructorSessionResultSectionType = 'UNKNOWN' as InstructorSessionResultSectionType;
    expect(service.isFeedbackResponsesDisplayedOnSection(response, section, sectionType))
      .toBeTruthy();
  });

  it('should call get when retrieving a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[dummy question ID]',
      intent: dummyIntent,
      key: '[dummy registration key]',
      moderatedperson: '',
    };
    service.getFeedbackResponse({
      questionId: paramMap['questionid'],
      intent: dummyIntent,
      key: paramMap['key'],
      moderatedPerson: paramMap['moderatedperson'],
    });
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap);
  });

  it('should call put when submitting a feedback response', () => {
    const paramMap: Record<string, string> = {
      questionid: '[dummy question ID]',
    };
    const dummyRequest: FeedbackResponsesRequest = { responses: [] };
    const dummyAdditionalParams: { [key: string]: string } = {};
    service.submitFeedbackResponses(paramMap['questionid'], dummyRequest, dummyAdditionalParams);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap, dummyRequest);
  });

  it('should include additional parameters when submitting a feedback response', () => {
    const dummyIntent: Intent = Intent.STUDENT_SUBMISSION;
    const paramMap: Record<string, string> = {
      questionid: '[dummy question ID]',
      intent: dummyIntent,
      key: '[dummy registration key]',
      moderatedperson: '',
    };
    const dummyRequest: FeedbackResponsesRequest = { responses: [] };
    const dummyAdditionalParams: { [key: string]: string } = {
      intent: dummyIntent,
      key: paramMap['key'],
      moderatedperson: paramMap['moderatedperson'],
    };
    service.submitFeedbackResponses(paramMap['questionid'], dummyRequest, dummyAdditionalParams);
    expect(spyHttpRequestService.put).toHaveBeenCalledWith(ResourceEndpoints.RESPONSES, paramMap, dummyRequest);
  });
});
