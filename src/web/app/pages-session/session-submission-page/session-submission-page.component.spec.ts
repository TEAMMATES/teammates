import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { of, throwError } from 'rxjs';
import { SessionSubmissionPageComponent } from './session-submission-page.component';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { FileSaveService } from '../../../services/file-save.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  AuthInfo,
  FeedbackConstantSumRecipientsQuestionDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponse,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionLogType,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
  RegkeyValidity,
  ResponseVisibleSetting,
  SessionSubmission,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { Milliseconds } from '../../../types/datetime-const';
import {
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormModel,
  ResponseSubmissionStatus,
} from '../../components/question-submission-form/question-submission-form-model';
import type { GiverCommentRowModel } from '../../components/comment-box/comment.model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';

describe('SessionSubmissionPageComponent', () => {
  const testOpenFeedbackSession: FeedbackSession = {
    feedbackSessionId: '00000000-0000-4000-8000-000000000001',
    feedbackSessionName: 'First Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000000000000,
    submissionEndTimestamp: Date.now() + Milliseconds.IN_TEN_MINUTES, // 10 minutes before closing
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  const toFeedbackSessionView = (feedbackSession: FeedbackSession): FeedbackSessionView => ({
    feedbackSession,
  });

  const createGiverComment = (commentText: string): GiverCommentRowModel => {
    const originalCommentFormModel = {
      commentText,
      showCommentTo: [],
      showGiverNameTo: [],
    };

    return {
      commentType: 'giver',
      originalCommentFormModel,
      commentEditFormModel: structuredClone(originalCommentFormModel),
      isEditing: false,
    };
  };

  const testMcqRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-1',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
    commentByGiver: createGiverComment('comment text here'),
  };

  const testTextRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-3',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
    commentByGiver: createGiverComment('comment text here'),
  };

  const testMsqRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-6',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: ['answer 1', 'answer 2'],
      isOther: false,
      otherFieldContent: 'other field content',
      questionType: FeedbackQuestionType.MSQ,
    } as FeedbackMsqResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
    commentByGiver: createGiverComment('comment text'),
  };

  const testNumscaleRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-7',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 5,
      minScale: 1,
      maxScale: 10,
      step: 1,
      questionText: 'question text',
      questionType: FeedbackQuestionType.NUMSCALE,
    } as FeedbackNumericalScaleQuestionDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testConstsumRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-8',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [7, 13],
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    } as FeedbackConstantSumRecipientsResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testContribRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-9',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 20,
      questionType: FeedbackQuestionType.CONTRIB,
    } as FeedbackContributionResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testRubricRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-10',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: [3, 4],
      questionType: FeedbackQuestionType.RUBRIC,
    } as FeedbackRubricResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testRankOptionsRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-11',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [2, 1],
      questionType: FeedbackQuestionType.RANK_OPTIONS,
    } as FeedbackRankOptionsResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testRankRecipientsRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-12',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      answer: 1,
    } as FeedbackRankRecipientsResponseDetails,
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
  };

  const testResponse1: FeedbackResponse = {
    feedbackResponseId: 'response-id-4',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'barry-harris-answer',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails,
  };

  const testResponse2: FeedbackResponse = {
    feedbackResponseId: 'response-id-5',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'gene-harris-answer',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails,
  };

  const testMcqQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-mcq',
    questionNumber: 1,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'question text',
      mcqChoices: ['choice 1', 'choice 2', 'choice 3'],
    } as FeedbackMcqQuestionDetails,
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM,
    recipientList: [{ recipientName: 'Gene Harris', recipientIdentifier: 'gene-harris-id' }],
    recipientSubmissionForms: [testMcqRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.STUDENTS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  const testMcqQuestionSubmissionForm2: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-mcq-2',
    questionNumber: 2,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MCQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'question text',
      mcqChoices: ['choice 1', 'choice 2', 'choice 3'],
    } as FeedbackMcqQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.TEAMS,
    recipientList: [],
    recipientSubmissionForms: [],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testTextQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-text',
    questionNumber: 3,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'question text',
    },
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.INSTRUCTORS,
    recipientList: [{ recipientName: 'Gene Harris', recipientIdentifier: 'gene-harris-id' }],
    recipientSubmissionForms: [testTextRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  const testMsqQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-msq',
    questionNumber: 4,
    questionBrief: 'MSQ question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MSQ,
    questionDetails: {
      questionType: FeedbackQuestionType.MSQ,
      msqChoices: ['first', 'second', 'third'],
      otherEnabled: false,
      hasAssignedWeights: true,
      msqWeights: [1, 2, 3],
      maxSelectableChoices: 2,
      minSelectableChoices: 1,
    } as FeedbackMsqQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testMsqRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testNumscaleQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-numscale',
    questionNumber: 5,
    questionBrief: 'numerical scale question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.NUMSCALE,
    questionDetails: {
      questionType: FeedbackQuestionType.NUMSCALE,
      minScale: 1,
      maxScale: 10,
      step: 1,
    } as FeedbackNumericalScaleQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testNumscaleRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testConstsumQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-constsum',
    questionNumber: 6,
    questionBrief: 'constant sum question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    questionDetails: {
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      pointsPerOption: true,
      forceUnevenDistribution: false,
      distributePointsFor: 'distribute points for',
      points: 20,
    } as FeedbackConstantSumRecipientsQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testConstsumRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testContribQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-contrib',
    questionNumber: 7,
    questionBrief: 'contribution question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONTRIB,
    questionDetails: {
      questionType: FeedbackQuestionType.CONTRIB,
      isNotSureAllowed: false,
    } as FeedbackContributionQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testContribRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testRubricQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rubric',
    questionNumber: 8,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RUBRIC,
    questionDetails: {
      questionType: FeedbackQuestionType.RUBRIC,
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [
        [1, 2],
        [2, 1],
      ],
      rubricChoices: ['choice 1', 'choice 2'],
      rubricSubQuestions: ['subquestion 1', 'subquestion 2'],
      rubricDescriptions: [
        ['description 1', 'description 2'],
        ['description 3', 'description 4'],
      ],
    } as FeedbackRubricQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRubricRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testRankOptionsQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rank-options',
    questionNumber: 9,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionDetails: {
      questionType: FeedbackQuestionType.RANK_OPTIONS,
      options: ['option 1', 'option 2'],
    } as FeedbackRankOptionsQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRankOptionsRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testRankRecipientsQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rank-recipients',
    questionNumber: 10,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: {
      questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
    } as FeedbackRankRecipientsQuestionDetails,
    giverType: QuestionGiverType.INSTRUCTORS,
    recipientType: QuestionRecipientType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRankRecipientsRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
  };

  const testInfo: AuthInfo = {
    masquerade: false,
    loginUrl: 'http://localhost:8080/auth',
    user: {
      id: 'user-id',
      accountId: 'account-id',
      isAdmin: false,
      isInstructor: false,
      isStudent: true,
      isMaintainer: false,
    },
  };

  const testQueryParams = {
    fsid: '00000000-0000-4000-8000-000000000001',
    key: 'reg-key',
  };

  const getFeedbackSessionArgs = {
    feedbackSessionId: '00000000-0000-4000-8000-000000000001',
    intent: Intent.STUDENT_SUBMISSION,
    key: testQueryParams.key,
    moderatedPerson: '',
    previewAs: '',
  };

  const getSessionSubmissionDataArgs = {
    feedbackSessionId: testQueryParams.fsid,
    intent: Intent.STUDENT_SUBMISSION,
    key: testQueryParams.key,
    moderatedPerson: '',
    previewAs: '',
  };

  let component: SessionSubmissionPageComponent;
  let fixture: ComponentFixture<SessionSubmissionPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackResponsesService: FeedbackResponsesService;
  let simpleModalService: SimpleModalService;
  let statusMessageService: StatusMessageService;
  let ngbModal: NgbModal;
  let logService: LogService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: {
              intent: Intent.STUDENT_SUBMISSION,
              pipe: () => {
                return {
                  subscribe: (fn: (_value: unknown) => void) => fn(testQueryParams),
                };
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SessionSubmissionPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    feedbackResponsesService = TestBed.inject(FeedbackResponsesService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    simpleModalService = TestBed.inject(SimpleModalService);
    statusMessageService = TestBed.inject(StatusMessageService);
    ngbModal = TestBed.inject(NgbModal);
    logService = TestBed.inject(LogService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session questions have failed to load', () => {
    component.retryAttempts = 0;
    component.hasFeedbackSessionQuestionsLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when saving responses', () => {
    component.isSavingResponses = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is logged in and using session link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = 'alice';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is not logged in and using session link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = '';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session and user details', () => {
    component.courseId = 'test.exa-demo';
    component.feedbackSessionName = 'First team feedback session';
    component.regKey = 'reg-key';
    component.loggedInUser = 'logged-in-user';
    component.personName = 'person name';
    component.personEmail = 'person@email.com';
    component.courseName = 'Course name';
    component.courseInstitute = 'Test institute';
    component.formattedSessionOpeningTime = 'Sun, 01 Apr, 2012, 11:59 PM +08';
    component.formattedSessionClosingTime = 'Mon, 02 Apr, 2012, 11:59 PM +08';
    component.feedbackSessionInstructions = 'Please give your feedback based on the following questions.';
    component.isFeedbackSessionLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session question submission forms', () => {
    component.questionSubmissionForms = [
      testMcqQuestionSubmissionForm,
      testTextQuestionSubmissionForm,
      testMcqQuestionSubmissionForm2,
      testMsqQuestionSubmissionForm,
      testNumscaleQuestionSubmissionForm,
      testConstsumQuestionSubmissionForm,
      testContribQuestionSubmissionForm,
      testRubricQuestionSubmissionForm,
      testRankOptionsQuestionSubmissionForm,
      testRankRecipientsQuestionSubmissionForm,
    ];
    component.isFeedbackSessionLoading = false;
    component.isFeedbackSessionQuestionsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session question submission forms when disabled', () => {
    component.questionSubmissionForms = [
      testMcqQuestionSubmissionForm,
      testTextQuestionSubmissionForm,
      testMcqQuestionSubmissionForm2,
      testMsqQuestionSubmissionForm,
      testNumscaleQuestionSubmissionForm,
      testConstsumQuestionSubmissionForm,
      testContribQuestionSubmissionForm,
      testRubricQuestionSubmissionForm,
      testRankOptionsQuestionSubmissionForm,
      testRankRecipientsQuestionSubmissionForm,
    ];
    component.isSubmissionFormsDisabled = true;
    component.isFeedbackSessionLoading = false;
    component.isFeedbackSessionQuestionsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    component.ngOnInit();
    expect(component.intent).toEqual(Intent.STUDENT_SUBMISSION);
    expect(component.feedbackSessionId).toEqual(testQueryParams.fsid);
    expect(component.regKey).toEqual(testQueryParams.key);
    expect(component.loggedInUser).toEqual(testInfo.user?.id);
  });

  it('should verify allowed access with used reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: true,
      isValid: false,
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy = vi.spyOn(navService, 'navigateByURLWithParamEncoding').mockResolvedValue(true);

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/sessions/submission', {
      fsid: '00000000-0000-4000-8000-000000000001',
    });
  });

  it('should deny unallowed access with valid reg key for logged in user', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: true,
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy = vi.spyOn(navService, 'navigateWithErrorMessage').mockResolvedValue();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(
      '/web/front',
      `You are trying to access TEAMMATES using the Google account user-id, which
                        is not linked to this TEAMMATES account. If you used a different Google account to
                        join/access TEAMMATES before, please use that Google account to access TEAMMATES. If you
                        cannot remember which Google account you used before, please email us at
                        ${environment.supportEmail} for help.`,
    );
  });

  it('should deny unallowed access with invalid reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: false,
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy = vi.spyOn(navService, 'navigateWithErrorMessage').mockResolvedValue();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/front', 'You are not authorized to view this page.');
  });

  it('should join course for unregistered student', () => {
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);
    component.joinCourseForUnregisteredEntity();
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/join', { entitytype: 'student', key: testQueryParams.key });
  });

  it('should load an open feedback session', () => {
    const fsSpy = vi
      .spyOn(feedbackSessionsService, 'getFeedbackSession')
      .mockReturnValue(of(toFeedbackSessionView(testOpenFeedbackSession)));
    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.loadFeedbackSession(false, testInfo);

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith(getFeedbackSessionArgs);
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Feedback Session Will Be Closing Soon!',
      SimpleModalType.WARNING,
      'Warning: you have less than 15 minutes before the submission deadline expires!',
    );
    expect(component.feedbackSessionInstructions).toEqual(testOpenFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testOpenFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testOpenFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(false);
  });

  it('should load a closed feedback session', () => {
    const testClosedFeedbackSession: FeedbackSession = structuredClone(testOpenFeedbackSession);
    testClosedFeedbackSession.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
    const fsSpy = vi
      .spyOn(feedbackSessionsService, 'getFeedbackSession')
      .mockReturnValue(of(toFeedbackSessionView(testClosedFeedbackSession)));
    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.loadFeedbackSession(false, testInfo);

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith(getFeedbackSessionArgs);
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Feedback Session Closed', SimpleModalType.WARNING, expect.anything());
    expect(component.feedbackSessionInstructions).toEqual(testClosedFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testClosedFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testClosedFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(true);
  });

  it('should load a visible not open feedback session', () => {
    const testVisibleNotOpenFeedbackSession: FeedbackSession = structuredClone(testOpenFeedbackSession);
    testVisibleNotOpenFeedbackSession.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
    const fsSpy = vi
      .spyOn(feedbackSessionsService, 'getFeedbackSession')
      .mockReturnValue(of(toFeedbackSessionView(testVisibleNotOpenFeedbackSession)));
    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.loadFeedbackSession(false, testInfo);

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith(getFeedbackSessionArgs);
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Feedback Session Not Open', SimpleModalType.WARNING, expect.anything());
    expect(component.feedbackSessionInstructions).toEqual(testVisibleNotOpenFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testVisibleNotOpenFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testVisibleNotOpenFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(true);
  });

  it('should show session not found modal when loading non-existent feedback session', () => {
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({
        error: { message: 'This is an error' },
        status: 404,
      })),
    );
    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.loadFeedbackSession(false, testInfo);

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Feedback Session Does Not Exist!',
      SimpleModalType.DANGER,
      'The session does not exist (most likely deleted by an instructor after the submission link was sent).',
      { onClosed: expect.any(Function) },
      { backdrop: 'static' },
    );
  });

  it('should show session not visible modal when loading non-viewable feedback session', () => {
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({
        error: { message: 'This is an error' },
        status: 403,
      })),
    );
    const modalSpy = vi.spyOn(simpleModalService, 'openInformationModal').mockReturnValue(createMockNgbModalRef());

    component.loadFeedbackSession(false, testInfo);

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Not Authorised To Access!',
      SimpleModalType.DANGER,
      'This is an error',
      { onClosed: expect.any(Function) },
      { backdrop: 'static' },
    );
  });

  it('should load feedback questions', () => {
    const testSessionSubmissionData: SessionSubmission = {
      questions: [
        {
          question: {
            feedbackQuestionId: testMcqQuestionSubmissionForm2.feedbackQuestionId,
            questionNumber: testMcqQuestionSubmissionForm2.questionNumber,
            questionBrief: testMcqQuestionSubmissionForm2.questionBrief,
            questionDescription: testMcqQuestionSubmissionForm2.questionDescription,
            questionDetails: testMcqQuestionSubmissionForm2.questionDetails,
            questionType: testMcqQuestionSubmissionForm2.questionType,
            giverType: testMcqQuestionSubmissionForm2.giverType,
            recipientType: testMcqQuestionSubmissionForm2.recipientType,
            numberOfEntitiesToGiveFeedbackToSetting:
              testMcqQuestionSubmissionForm2.numberOfEntitiesToGiveFeedbackToSetting,
            customNumberOfEntitiesToGiveFeedbackTo:
              testMcqQuestionSubmissionForm2.customNumberOfEntitiesToGiveFeedbackTo,
            showResponsesTo: testMcqQuestionSubmissionForm2.showResponsesTo,
            showGiverNameTo: testMcqQuestionSubmissionForm2.showGiverNameTo,
            showRecipientNameTo: testMcqQuestionSubmissionForm2.showRecipientNameTo,
          },
          recipients: [],
          responses: [],
        },
      ],
    };

    const getSessionSubmissionDataSpy = vi
      .spyOn(feedbackSessionsService, 'getSessionSubmissionData')
      .mockReturnValue(of(testSessionSubmissionData));
    const getResponseSpy = vi.spyOn(feedbackResponsesService, 'getFeedbackResponse');

    component.loadFeedbackQuestions();

    expect(getSessionSubmissionDataSpy).toHaveBeenLastCalledWith(getSessionSubmissionDataArgs);
    expect(getResponseSpy).not.toHaveBeenCalled();
    expect(component.questionSubmissionForms.length).toEqual(1);
    expect(component.questionSubmissionForms[0]).toEqual(testMcqQuestionSubmissionForm2);
    expect(component.questionsNeedingSubmission.length).toEqual(0);
  });

  it('should classify a question for nobody specific as ungrouped instead of grouping it by recipient', () => {
    const testSessionSubmissionData: SessionSubmission = {
      questions: [
        {
          question: {
            feedbackQuestionId: testTextQuestionSubmissionForm.feedbackQuestionId,
            questionNumber: testTextQuestionSubmissionForm.questionNumber,
            questionBrief: testTextQuestionSubmissionForm.questionBrief,
            questionDescription: testTextQuestionSubmissionForm.questionDescription,
            questionDetails: testTextQuestionSubmissionForm.questionDetails,
            questionType: testTextQuestionSubmissionForm.questionType,
            giverType: testTextQuestionSubmissionForm.giverType,
            recipientType: QuestionRecipientType.STUDENTS,
            numberOfEntitiesToGiveFeedbackToSetting:
              testTextQuestionSubmissionForm.numberOfEntitiesToGiveFeedbackToSetting,
            customNumberOfEntitiesToGiveFeedbackTo:
              testTextQuestionSubmissionForm.customNumberOfEntitiesToGiveFeedbackTo,
            showResponsesTo: testTextQuestionSubmissionForm.showResponsesTo,
            showGiverNameTo: testTextQuestionSubmissionForm.showGiverNameTo,
            showRecipientNameTo: testTextQuestionSubmissionForm.showRecipientNameTo,
          },
          recipients: [{ identifier: 'barry-harris-id', name: 'Barry Harris', section: 'Section A', team: 'Team 1' }],
          responses: [],
        },
        {
          question: {
            feedbackQuestionId: 'feedback-question-id-general',
            questionNumber: 11,
            questionBrief: testTextQuestionSubmissionForm.questionBrief,
            questionDescription: testTextQuestionSubmissionForm.questionDescription,
            questionDetails: testTextQuestionSubmissionForm.questionDetails,
            questionType: testTextQuestionSubmissionForm.questionType,
            giverType: testTextQuestionSubmissionForm.giverType,
            recipientType: QuestionRecipientType.NONE,
            numberOfEntitiesToGiveFeedbackToSetting:
              testTextQuestionSubmissionForm.numberOfEntitiesToGiveFeedbackToSetting,
            customNumberOfEntitiesToGiveFeedbackTo:
              testTextQuestionSubmissionForm.customNumberOfEntitiesToGiveFeedbackTo,
            showResponsesTo: testTextQuestionSubmissionForm.showResponsesTo,
            showGiverNameTo: testTextQuestionSubmissionForm.showGiverNameTo,
            showRecipientNameTo: testTextQuestionSubmissionForm.showRecipientNameTo,
          },
          recipients: [{ identifier: '%GENERAL%', name: '-', section: '', team: '' }],
          responses: [],
        },
      ],
    };

    vi.spyOn(feedbackSessionsService, 'getSessionSubmissionData').mockReturnValue(of(testSessionSubmissionData));

    component.loadFeedbackQuestions();

    // The question for nobody specific is treated as ungroupable
    expect(component.ungroupableQuestionsSorted).toContain(11);
    expect(component.recipientQuestionMap.has('%GENERAL%')).toBe(false);
    // The question with a specific recipient is still grouped by that recipient
    expect(component.ungroupableQuestionsSorted).not.toContain(testTextQuestionSubmissionForm.questionNumber);
    expect(
      component.recipientQuestionMap.get('barry-harris-id')?.has(testTextQuestionSubmissionForm.questionNumber),
    ).toBe(true);
  });

  it('should check that there are no responses to submit', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = structuredClone(testTextQuestionSubmissionForm);
    testSubmissionForm.recipientSubmissionForms = [];
    component.questionSubmissionForms = [testSubmissionForm];
    expect(component.questionsNeedingSubmission.length).toEqual(0);
  });

  it('should save feedback responses', () => {
    const mockModalRef = createMockNgbModalRef();
    const testResponseDetails1 = structuredClone(testMcqRecipientSubmissionForm.responseDetails);
    // leave question unanswered
    const testResponseDetails2: FeedbackTextResponseDetails = { answer: '', questionType: FeedbackQuestionType.TEXT };
    const testQuestionSubmissionForm1: QuestionSubmissionFormModel = structuredClone(testMcqQuestionSubmissionForm);
    const testQuestionSubmissionForm2: QuestionSubmissionFormModel = structuredClone(testTextQuestionSubmissionForm);
    testQuestionSubmissionForm1.recipientSubmissionForms[0].status = ResponseSubmissionStatus.MODIFIED;
    testQuestionSubmissionForm1.recipientSubmissionForms[0].responseDetails = testResponseDetails1;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].responseDetails = testResponseDetails2;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].responseId = '';
    component.questionSubmissionForms = [testQuestionSubmissionForm1, testQuestionSubmissionForm2];

    const responseSpy = vi.spyOn(feedbackResponsesService, 'submitFeedbackResponses').mockReturnValueOnce(
      of({
        questionResponses: {
          'feedback-question-id-mcq': [testResponse1],
          'feedback-question-id-text': [testResponse2],
        },
        requestId: '10',
      }),
    );
    const logSpy = vi.spyOn(logService, 'createFeedbackSessionLog').mockReturnValue(of('Successful'));
    vi.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(responseSpy).toHaveBeenCalledTimes(1);
    expect(responseSpy).toHaveBeenCalledWith(
      component.feedbackSessionId,
      {
        questionResponses: {
          'feedback-question-id-mcq': [
            {
              responseId: testMcqRecipientSubmissionForm.responseId,
              recipient: testMcqRecipientSubmissionForm.recipientIdentifier,
              responseDetails: testResponseDetails1,
              giverComment: 'comment text here',
            },
          ],
          'feedback-question-id-text': [], // empty response details are submitted as empty array to delete any existing responses
        },
      },
      {
        intent: 'STUDENT_SUBMISSION',
        key: 'reg-key',
        moderatedperson: '',
      },
    );

    expect(mockModalRef.componentInstance.questions).toEqual([
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
    ]);
    expect(mockModalRef.componentInstance.submittedQuestions).toEqual([testQuestionSubmissionForm1.questionNumber]);
    expect(mockModalRef.componentInstance.notYetAnsweredQuestions).toHaveLength(1);
    expect(mockModalRef.componentInstance.failToSaveQuestions).toEqual({});
    expect(logSpy).toHaveBeenCalledTimes(1);
    expect(logSpy).toHaveBeenCalledWith({
      logType: FeedbackSessionLogType.SUBMISSION,
      key: 'reg-key',
      feedbackSessionId: '00000000-0000-4000-8000-000000000001',
    });
  });

  it('should submit empty question responses to delete saved responses', () => {
    const testQuestionSubmissionForm: QuestionSubmissionFormModel = structuredClone(testTextQuestionSubmissionForm);
    testQuestionSubmissionForm.recipientSubmissionForms[0].status = ResponseSubmissionStatus.MODIFIED;
    testQuestionSubmissionForm.recipientSubmissionForms[0].responseDetails = {
      answer: '',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails;
    component.questionSubmissionForms = [testQuestionSubmissionForm];

    const responseSpy = vi.spyOn(feedbackResponsesService, 'submitFeedbackResponses').mockReturnValueOnce(
      of({
        questionResponses: {
          [testQuestionSubmissionForm.feedbackQuestionId]: [],
        },
        requestId: '10',
      }),
    );
    const toastSpy = vi.spyOn(statusMessageService, 'showSuccessToast');
    const modalSpy = vi.spyOn(ngbModal, 'open');

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(responseSpy).toHaveBeenCalledTimes(1);
    expect(responseSpy).toHaveBeenCalledWith(
      component.feedbackSessionId,
      {
        questionResponses: {
          [testQuestionSubmissionForm.feedbackQuestionId]: [],
        },
      },
      {
        intent: 'STUDENT_SUBMISSION',
        key: 'reg-key',
        moderatedperson: '',
      },
    );
    expect(toastSpy).toHaveBeenCalledWith(
      `Response to question ${testQuestionSubmissionForm.questionNumber} submitted successfully.`,
    );
    expect(modalSpy).not.toHaveBeenCalled();
    expect(component.questionSubmissionForms[0].recipientSubmissionForms[0].responseId).toBe('');
    expect(component.questionSubmissionForms[0].recipientSubmissionForms[0].status).toBe(ResponseSubmissionStatus.NEW);
  });

  it('should not save invalid feedback responses', () => {
    const mockModalRef = createMockNgbModalRef();
    const testResponseDetails1 = structuredClone(testMcqRecipientSubmissionForm.responseDetails);
    const testResponseDetails2 = structuredClone(testConstsumRecipientSubmissionForm.responseDetails);
    const testQuestionSubmissionForm1: QuestionSubmissionFormModel = structuredClone(testMcqQuestionSubmissionForm);
    const testQuestionSubmissionForm2: QuestionSubmissionFormModel = structuredClone(
      testConstsumQuestionSubmissionForm,
    );
    testQuestionSubmissionForm1.recipientSubmissionForms[0].responseDetails = testResponseDetails1;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].status = ResponseSubmissionStatus.MODIFIED;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].responseDetails = testResponseDetails2;
    // invalid response
    testQuestionSubmissionForm2.recipientSubmissionForms[0].isValid = false;
    component.questionSubmissionForms = [testQuestionSubmissionForm1, testQuestionSubmissionForm2];

    const responseSpy = vi.spyOn(feedbackResponsesService, 'submitFeedbackResponses').mockImplementation(() => {
      return of({
        questionResponses: {
          [testQuestionSubmissionForm1.feedbackQuestionId]: [testResponse1],
        },
        requestId: '10',
      });
    });
    vi.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(responseSpy).toHaveBeenCalledTimes(1);
    expect(responseSpy).toHaveBeenNthCalledWith(
      1,
      component.feedbackSessionId,
      {
        questionResponses: {
          [testQuestionSubmissionForm1.feedbackQuestionId]: [
            {
              responseId: testMcqRecipientSubmissionForm.responseId,
              recipient: testMcqRecipientSubmissionForm.recipientIdentifier,
              responseDetails: testResponseDetails1,
              giverComment: 'comment text here',
            },
          ],
        },
      },
      {
        intent: 'STUDENT_SUBMISSION',
        key: 'reg-key',
        moderatedperson: '',
      },
    );

    // valid questions are submitted; invalid ones are shown in the completion modal
    expect(mockModalRef.componentInstance.questions).toEqual([
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
    ]);
    expect(mockModalRef.componentInstance.submittedQuestions).toEqual([testQuestionSubmissionForm1.questionNumber]);
    expect(mockModalRef.componentInstance.failToSaveQuestions).toEqual({
      [testQuestionSubmissionForm2.questionNumber]: 'Invalid responses provided. Please check question constraints.',
    });
    expect(component.questionSubmissionForms[1].recipientSubmissionForms[0].status).toBe(
      ResponseSubmissionStatus.ERROR,
    );
  });

  it('should show one backend error modal when batch save fails', () => {
    const testResponseDetails1 = structuredClone(testMcqRecipientSubmissionForm.responseDetails);
    const testQuestionSubmissionForm1: QuestionSubmissionFormModel = structuredClone(testMcqQuestionSubmissionForm);
    testQuestionSubmissionForm1.recipientSubmissionForms[0].responseDetails = testResponseDetails1;
    component.questionSubmissionForms = [testQuestionSubmissionForm1];

    vi.spyOn(feedbackResponsesService, 'submitFeedbackResponses').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'backend error',
        },
      })),
    );
    const simpleModalSpy = vi.spyOn(simpleModalService, 'openInformationModal');
    const ngbModalSpy = vi.spyOn(ngbModal, 'open');

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(simpleModalSpy).toHaveBeenCalledWith(
      'Saving Failed',
      SimpleModalType.DANGER,
      'An error occurred and your responses could not be saved. Error details: backend error',
    );
    expect(ngbModalSpy).toHaveBeenCalledTimes(1);
  });

  it('should delete participant comment', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = structuredClone(testMsqQuestionSubmissionForm);
    const commentSpy = vi.spyOn(feedbackResponsesService, 'deleteGiverComment').mockReturnValue(
      of({
        message: 'Successfully deleted feedback response giver comment.',
      }),
    );

    component.questionSubmissionForms = [testSubmissionForm];
    component.deleteParticipantComment(0, 0);

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith({
      responseId: 'response-id-6',
      intent: Intent.STUDENT_SUBMISSION,
      key: testQueryParams.key,
      moderatedPerson: '',
    });
    expect(component.questionSubmissionForms[0].recipientSubmissionForms[0].commentByGiver).toBeUndefined();
  });

  it('should download submission receipt using latest fetched responses', async () => {
    const saveFileSpy = vi.spyOn(TestBed.inject(FileSaveService), 'saveFile');
    const getResponseSpy = vi
      .spyOn(feedbackResponsesService, 'getFeedbackResponse')
      .mockImplementation((queryParams: { questionId: string }) => {
        if (queryParams.questionId === 'feedback-question-id-mcq') {
          return of({ responses: [] });
        }
        return of({
          responses: [
            {
              feedbackResponseId: 'response-id-3',
              giverIdentifier: 'giver-identifier',
              recipientIdentifier: 'gene-harris-id',
              responseDetails: {
                answer: 'answer',
                questionType: FeedbackQuestionType.TEXT,
              },
            },
          ],
        });
      });

    component.courseId = 'course-id';
    component.courseName = 'Test Course';
    component.feedbackSessionName = 'First Session';
    component.feedbackSessionTimezone = 'Asia/Singapore';
    component.personName = 'Alice Betsy';
    component.personEmail = 'alice@tmms.com';
    component.questionSubmissionForms = [
      structuredClone(testMcqQuestionSubmissionForm),
      structuredClone(testTextQuestionSubmissionForm),
    ];
    component.questionSubmissionForms[0].recipientSubmissionForms[0].status = ResponseSubmissionStatus.MODIFIED;
    component.questionSubmissionForms[1].recipientSubmissionForms[0].status = ResponseSubmissionStatus.NEW;

    component.downloadSubmissionReceipt();

    expect(getResponseSpy).toHaveBeenCalledTimes(2);
    expect(getResponseSpy).toHaveBeenNthCalledWith(1, {
      questionId: 'feedback-question-id-mcq',
      intent: Intent.STUDENT_SUBMISSION,
      key: 'reg-key',
      moderatedPerson: '',
    });
    expect(getResponseSpy).toHaveBeenNthCalledWith(2, {
      questionId: 'feedback-question-id-text',
      intent: Intent.STUDENT_SUBMISSION,
      key: 'reg-key',
      moderatedPerson: '',
    });
    expect(saveFileSpy).toHaveBeenCalledTimes(1);

    const [blob, fileName] = saveFileSpy.mock.calls[0];
    expect(fileName).toContain('TEAMMATES Submission Receipt - ');

    const content: string = await blob.text();
    expect(content).toContain('Questions Answered: 1 of 2');
    expect(content).toContain('Question 1\nquestion brief');
    expect(content).toContain('No submitted responses for this question.');
    expect(content).toContain('Question 3\nquestion brief');
    expect(content).toContain('Response ID: response-id-3');
    expect(content).not.toContain('Response ID: response-id-1');
  });
});
