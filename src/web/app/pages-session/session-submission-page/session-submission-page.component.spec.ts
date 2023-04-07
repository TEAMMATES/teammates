import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo,
  CommentVisibilityType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleQuestionDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionRecipients,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsQuestionDetails,
  FeedbackResponse,
  FeedbackResponseComment,
  FeedbackResponses,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  Instructor,
  JoinState,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { Milliseconds } from '../../../types/datetime-const';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';
import { SessionSubmissionPageComponent } from './session-submission-page.component';

describe('SessionSubmissionPageComponent', () => {
  const deepCopy: <T>(obj: T) => T = <T>(obj: T) => JSON.parse(JSON.stringify(obj));

  const testStudent: Student = {
    email: 'alice@tmms.com',
    courseId: 'course-id',
    name: 'Alice Betsy',
    teamName: 'Team 1',
    sectionName: 'Section 1',
  };

  const testInstructor: Instructor = {
    courseId: 'course-id',
    email: 'test@example.com',
    name: 'Instructor Ho',
    joinState: JoinState.JOINED,
  };

  const testOpenFeedbackSession: FeedbackSession = {
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
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const testComment: FeedbackResponseComment = {
    commentGiver: 'comment giver',
    lastEditorEmail: 'last-editor@email.com',
    feedbackResponseCommentId: 1,
    commentText: 'comment text',
    createdAt: 10000000,
    lastEditedAt: 20000000,
    isVisibilityFollowingFeedbackQuestion: true,
    showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
    showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
  };

  const testMcqRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-1',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
    commentByGiver: {
      originalComment: testComment,
      originalRecipientIdentifier: 'barry-harris-id',
      commentEditFormModel: {
        commentText: 'comment text here',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testMcqRecipientSubmissionForm2: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-2',
    recipientIdentifier: 'recipient-identifier',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
    commentByGiver: {
      originalComment: testComment,
      originalRecipientIdentifier: 'recipient-identifier',
      commentEditFormModel: {
        commentText: '',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testTextRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-3',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'answer',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextResponseDetails,
    isValid: true,
    commentByGiver: {
      commentEditFormModel: {
        commentText: 'comment text here',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testMcqRecipientSubmissionForm3: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-4',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'barry-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
  };

  const testMcqRecipientSubmissionForm4: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-5',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'gene-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
    isValid: true,
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
    isValid: true,
    commentByGiver: {
      originalComment: testComment,
      originalRecipientIdentifier: 'barry-harris-id',
      commentEditFormModel: {
        commentText: 'comment text',
        isUsingCustomVisibilities: false,
        showCommentTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
        showGiverNameTo: [CommentVisibilityType.GIVER, CommentVisibilityType.RECIPIENT],
      },
      isEditing: false,
    },
  };

  const testNumscaleRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-7',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 5,
    } as FeedbackNumericalScaleResponseDetails,
    isValid: true,
  };

  const testConstsumRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-8',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [7, 13],
    } as FeedbackConstantSumResponseDetails,
    isValid: true,
  };

  const testContribRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-9',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 20,
    } as FeedbackContributionResponseDetails,
    isValid: true,
  };

  const testRubricRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-10',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: [3, 4],
    } as FeedbackRubricResponseDetails,
    isValid: true,
  };

  const testRankOptionsRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-11',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answers: [2, 1],
    } as FeedbackRankOptionsResponseDetails,
    isValid: true,
  };

  const testRankRecipientsRecipientSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = {
    responseId: 'response-id-12',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
    } as FeedbackRankRecipientsQuestionDetails,
    isValid: true,
  };

  const testResponse1: FeedbackResponse = {
    feedbackResponseId: 'response-id-4',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'barry-harris-id',
    responseDetails: {
      answer: 'barry-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
  };

  const testResponse2: FeedbackResponse = {
    feedbackResponseId: 'response-id-5',
    giverIdentifier: 'giver-identifier',
    recipientIdentifier: 'gene-harris-id',
    responseDetails: {
      answer: 'gene-harris-answer',
      questionType: FeedbackQuestionType.MCQ,
    } as FeedbackMcqResponseDetails,
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
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM,
    recipientList: [],
    recipientSubmissionForms: [testMcqRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.STUDENTS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
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
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.TEAMS,
    recipientList: [],
    recipientSubmissionForms: [],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: false,
    isTabExpanded: true,
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
    } as FeedbackTextQuestionDetails,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.INSTRUCTORS,
    recipientList: [],
    recipientSubmissionForms: [testTextRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testMsqQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-msq',
    questionNumber: 4,
    questionBrief: 'MSQ question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.MSQ,
    questionDetails: {
      msqChoices: ['first', 'second', 'third'],
      otherEnabled: false,
      hasAssignedWeights: true,
      msqWeights: [1, 2, 3],
      maxSelectableChoices: 2,
      minSelectableChoices: 1,
    } as FeedbackMsqQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testMsqRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testNumscaleQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-numscale',
    questionNumber: 5,
    questionBrief: 'numerical scale question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.NUMSCALE,
    questionDetails: {
      minScale: 1,
      maxScale: 10,
      step: 1,
    } as FeedbackNumericalScaleQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testNumscaleRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testConstsumQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-constsum',
    questionNumber: 6,
    questionBrief: 'constant sum question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
    questionDetails: {
      constSumOptions: ['option 1', 'option 2'],
      distributeToRecipients: true,
      pointsPerOption: true,
      forceUnevenDistribution: false,
      distributePointsFor: 'distribute points for',
      points: 20,
    } as FeedbackConstantSumQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testConstsumRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testContribQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-contrib',
    questionNumber: 7,
    questionBrief: 'contribution question',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.CONTRIB,
    questionDetails: {
      isNotSureAllowed: false,
    } as FeedbackContributionQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testContribRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testRubricQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rubric',
    questionNumber: 8,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RUBRIC,
    questionDetails: {
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [[1, 2], [2, 1]],
      rubricChoices: ['choice 1', 'choice 2'],
      rubricSubQuestions: ['subquestion 1', 'subquestion 2'],
      rubricDescriptions: [['description 1', 'description 2'], ['description 3', 'description 4']],
    } as FeedbackRubricQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRubricRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testRankOptionsQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rank-options',
    questionNumber: 9,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionDetails: {
      options: ['option 1', 'option 2'],
    } as FeedbackRankOptionsQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRankOptionsRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testRankRecipientsQuestionSubmissionForm: QuestionSubmissionFormModel = {
    feedbackQuestionId: 'feedback-question-id-rank-recipients',
    questionNumber: 10,
    questionBrief: 'question brief',
    questionDescription: 'question description',
    questionType: FeedbackQuestionType.RANK_RECIPIENTS,
    questionDetails: {
      minOptionsToBeRanked: 1,
      maxOptionsToBeRanked: 2,
      areDuplicatesAllowed: false,
    } as FeedbackRankRecipientsQuestionDetails,
    giverType: FeedbackParticipantType.INSTRUCTORS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [{ recipientName: 'Barry Harris', recipientIdentifier: 'barry-harris-id' }],
    recipientSubmissionForms: [testRankRecipientsRecipientSubmissionForm],
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 5,
    showResponsesTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.INSTRUCTORS],
    isLoading: false,
    isLoaded: true,
    isTabExpanded: true,
  };

  const testInfo: AuthInfo = {
    masquerade: false,
    user: {
      id: 'user-id',
      isAdmin: false,
      isInstructor: false,
      isStudent: true,
      isMaintainer: false,
    },
  };

  const testQueryParams: any = {
    courseid: 'CS3281',
    fsname: 'Feedback Session Name',
    key: 'reg-key',
  };

  const getFeedbackSessionArgs: any = {
    courseId: testQueryParams.courseid,
    feedbackSessionName: testQueryParams.fsname,
    intent: Intent.STUDENT_SUBMISSION,
    key: testQueryParams.key,
    moderatedPerson: '',
    previewAs: '',
  };

  let component: SessionSubmissionPageComponent;
  let fixture: ComponentFixture<SessionSubmissionPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let feedbackSessionsService: FeedbackSessionsService;
  let feedbackResponsesService: FeedbackResponsesService;
  let feedbackResponseCommentService: FeedbackResponseCommentService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let simpleModalService: SimpleModalService;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SessionSubmissionPageComponent, SavingCompleteModalComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        TeammatesCommonModule,
        FormsModule,
        AjaxLoadingModule,
        QuestionSubmissionFormModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        BrowserAnimationsModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: {
              intent: Intent.STUDENT_SUBMISSION,
              pipe: () => {
                return {
                  subscribe: (fn: (value: any) => void) => fn(testQueryParams),
                };
              },
            },
          },
        },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionSubmissionPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    studentService = TestBed.inject(StudentService);
    instructorService = TestBed.inject(InstructorService);
    feedbackQuestionsService = TestBed.inject(FeedbackQuestionsService);
    feedbackResponsesService = TestBed.inject(FeedbackResponsesService);
    feedbackResponseCommentService = TestBed.inject(FeedbackResponseCommentService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    simpleModalService = TestBed.inject(SimpleModalService);
    ngbModal = TestBed.inject(NgbModal);
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
    component.isCourseLoading = false;
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
    component.isCourseLoading = false;
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
    component.isCourseLoading = false;
    component.isFeedbackSessionLoading = false;
    component.isFeedbackSessionQuestionsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    component.ngOnInit();
    expect(component.intent).toEqual(Intent.STUDENT_SUBMISSION);
    expect(component.courseId).toEqual(testQueryParams.courseid);
    expect(component.feedbackSessionName).toEqual(testQueryParams.fsname);
    expect(component.regKey).toEqual(testQueryParams.key);
    expect(component.loggedInUser).toEqual(testInfo.user?.id);
  });

  it('should verify allowed access with used reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: true,
      isValid: false,
    };
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateByURLWithParamEncoding').mockImplementation();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/sessions/submission',
        { courseid: 'CS3281', fsname: 'Feedback Session Name' });
  });

  it('should deny unallowed access with valid reg key for logged in user', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: true,
    };
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateWithErrorMessage').mockImplementation();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/front',
        `You are trying to access TEAMMATES using the Google account user-id, which
                        is not linked to this TEAMMATES account. If you used a different Google account to
                        join/access TEAMMATES before, please use that Google account to access TEAMMATES. If you
                        cannot remember which Google account you used before, please email us at
                        ${environment.supportEmail} for help.`);
  });

  it('should deny unallowed access with invalid reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: false,
    };
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateWithErrorMessage').mockImplementation();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/front',
        'You are not authorized to view this page.');
  });

  it('should load a student name', () => {
    component.intent = Intent.STUDENT_SUBMISSION;
    jest.spyOn(studentService, 'getStudent').mockReturnValue(of(testStudent));
    component.loadPersonName();
    expect(component.personName).toEqual(testStudent.name);
    expect(component.personEmail).toEqual(testStudent.email);
  });

  it('should load an instructor name', () => {
    component.intent = Intent.INSTRUCTOR_SUBMISSION;
    jest.spyOn(instructorService, 'getInstructor').mockReturnValue(of(testInstructor));
    component.loadPersonName();
    expect(component.personName).toEqual(testInstructor.name);
    expect(component.personEmail).toEqual(testInstructor.email);
  });

  it('should join course for unregistered student', () => {
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateByURL').mockImplementation();
    component.joinCourseForUnregisteredEntity();
    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/join',
        { entitytype: 'student', key: testQueryParams.key });
  });

  it('should load an open feedback session', () => {
    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionsService, 'getFeedbackSession')
        .mockReturnValue(of(testOpenFeedbackSession));
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openInformationModal').mockImplementation();

    component.loadFeedbackSession(false, testInfo);

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith(getFeedbackSessionArgs);
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Feedback Session Will Be Closing Soon!',
        SimpleModalType.WARNING, 'Warning: you have less than 15 minutes before the submission deadline expires!');
    expect(component.feedbackSessionInstructions).toEqual(testOpenFeedbackSession.instructions);
    expect(component.feedbackSessionSubmissionStatus).toEqual(testOpenFeedbackSession.submissionStatus);
    expect(component.feedbackSessionTimezone).toEqual(testOpenFeedbackSession.timeZone);
    expect(component.isSubmissionFormsDisabled).toEqual(false);
  });

  it('should load a closed feedback session', () => {
    const testClosedFeedbackSession: FeedbackSession = deepCopy(testOpenFeedbackSession);
    testClosedFeedbackSession.submissionStatus = FeedbackSessionSubmissionStatus.CLOSED;
    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionsService, 'getFeedbackSession')
        .mockReturnValue(of(testClosedFeedbackSession));
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openInformationModal').mockImplementation();

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
    const testVisibleNotOpenFeedbackSession: FeedbackSession = deepCopy(testOpenFeedbackSession);
    testVisibleNotOpenFeedbackSession.submissionStatus = FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN;
    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionsService, 'getFeedbackSession')
        .mockReturnValue(of(testVisibleNotOpenFeedbackSession));
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openInformationModal').mockImplementation();

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
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(throwError(() => ({
      error: { message: 'This is an error' },
      status: 404,
    })));
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openInformationModal').mockImplementation();

    component.loadFeedbackSession(false, testInfo);

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Feedback Session Does Not Exist!', SimpleModalType.DANGER,
        'The session does not exist (most likely deleted by an instructor after the submission link was sent).',
        { onClosed: expect.any(Function) }, { backdrop: 'static' });
  });

  it('should show session not visible modal when loading non-viewable feedback session', () => {
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(throwError(() => ({
      error: { message: 'This is an error' },
      status: 403,
    })));
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openInformationModal').mockImplementation();

    component.loadFeedbackSession(false, testInfo);

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Not Authorised To Access!', SimpleModalType.DANGER, 'This is an error',
        { onClosed: expect.any(Function) }, { backdrop: 'static' });
  });

  it('should load feedback questions', () => {
    const testFeedbackQuestions: FeedbackQuestions = {
      questions: [
        {
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
          customNumberOfEntitiesToGiveFeedbackTo: testMcqQuestionSubmissionForm2.customNumberOfEntitiesToGiveFeedbackTo,
          showResponsesTo: testMcqQuestionSubmissionForm2.showResponsesTo,
          showGiverNameTo: testMcqQuestionSubmissionForm2.showGiverNameTo,
          showRecipientNameTo: testMcqQuestionSubmissionForm2.showRecipientNameTo,
        },
      ],
    };

    const getQuestionsSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions')
        .mockReturnValue(of(testFeedbackQuestions));

    component.loadFeedbackQuestions();

    expect(getQuestionsSpy).toHaveBeenLastCalledWith(getFeedbackSessionArgs);
    expect(component.questionSubmissionForms.length).toEqual(1);
    expect(component.questionSubmissionForms[0]).toEqual(testMcqQuestionSubmissionForm2);
    expect(component.questionsNeedingSubmission.length).toEqual(0);
  });

  it('should load the recipients and responses of a question if not yet loaded', () => {
    const testFeedbackQuestionRecipients: FeedbackQuestionRecipients = {
      recipients: [
        {
          name: 'Barry Harris',
          identifier: 'barry-harris-id',
        },
        {
          name: 'Gene Harris',
          identifier: 'gene-harris-id',
        },
      ],
    };
    const testExistingResponses: FeedbackResponses = {
      responses: [testResponse1, testResponse2],
    };

    const loadRecipientsSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'loadFeedbackQuestionRecipients')
        .mockReturnValue(of(testFeedbackQuestionRecipients));
    const getResponseSpy: SpyInstance = jest.spyOn(feedbackResponsesService, 'getFeedbackResponse')
        .mockReturnValue(of(testExistingResponses));

    component.questionSubmissionForms = [testMcqQuestionSubmissionForm2];
    component.loadRecipientsAndResponses({ visible: true }, testMcqQuestionSubmissionForm2);

    expect(loadRecipientsSpy).toHaveBeenLastCalledWith({
      intent: 'STUDENT_SUBMISSION',
      key: 'reg-key',
      moderatedPerson: '',
      previewAs: '',
      questionId: testMcqQuestionSubmissionForm2.feedbackQuestionId,
    });
    expect(getResponseSpy).toHaveBeenLastCalledWith({
      intent: 'STUDENT_SUBMISSION',
      key: 'reg-key',
      moderatedPerson: '',
      questionId: testMcqQuestionSubmissionForm2.feedbackQuestionId,
    });
    expect(testMcqQuestionSubmissionForm2.isLoading).toBe(false);
    expect(testMcqQuestionSubmissionForm2.isLoaded).toBe(true);
    expect(testMcqQuestionSubmissionForm2.recipientList).toEqual([
      {
        recipientName: 'Barry Harris',
        recipientIdentifier: 'barry-harris-id',
      },
      {
        recipientName: 'Gene Harris',
        recipientIdentifier: 'gene-harris-id',
      },
    ]);
    expect(testMcqQuestionSubmissionForm2.recipientSubmissionForms).toEqual([
      testMcqRecipientSubmissionForm3, testMcqRecipientSubmissionForm4,
    ]);
    expect(component.questionsNeedingSubmission.length).toEqual(1);
  });

  it('should not load the recipients and responses of a question if already loaded', () => {
    const loadRecipientsSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'loadFeedbackQuestionRecipients');
    const getResponseSpy: SpyInstance = jest.spyOn(feedbackResponsesService, 'getFeedbackResponse');

    testMcqQuestionSubmissionForm2.isLoaded = true;
    component.loadRecipientsAndResponses({ visible: true }, testMcqQuestionSubmissionForm2);

    testMsqQuestionSubmissionForm.isLoading = true;
    component.loadRecipientsAndResponses({ visible: true }, testMsqQuestionSubmissionForm);

    expect(loadRecipientsSpy).not.toHaveBeenCalled();
    expect(getResponseSpy).not.toHaveBeenCalled();
  });

  it('should not load the recipients and responses of a question if the event is not correct', () => {
    const loadRecipientsSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'loadFeedbackQuestionRecipients');
    const getResponseSpy: SpyInstance = jest.spyOn(feedbackResponsesService, 'getFeedbackResponse');

    component.loadRecipientsAndResponses(null, testMcqQuestionSubmissionForm2);
    component.loadRecipientsAndResponses({}, testMcqQuestionSubmissionForm2);
    component.loadRecipientsAndResponses({ visible: false }, testMsqQuestionSubmissionForm);

    expect(loadRecipientsSpy).not.toHaveBeenCalled();
    expect(getResponseSpy).not.toHaveBeenCalled();
  });

  it('should check that there are no responses to submit', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = deepCopy(testTextQuestionSubmissionForm);
    testSubmissionForm.recipientSubmissionForms = [];
    component.questionSubmissionForms = [testSubmissionForm];
    expect(component.questionsNeedingSubmission.length).toEqual(0);
  });

  it('should save feedback responses', () => {
    const mockModalRef: any = { componentInstance: {} };
    const testResponseDetails1: any = deepCopy(testMcqRecipientSubmissionForm.responseDetails);
    // leave question unanswered
    const testResponseDetails2: FeedbackTextResponseDetails = { answer: '', questionType: FeedbackQuestionType.TEXT };
    const testQuestionSubmissionForm1: QuestionSubmissionFormModel = deepCopy(testMcqQuestionSubmissionForm);
    const testQuestionSubmissionForm2: QuestionSubmissionFormModel = deepCopy(testTextQuestionSubmissionForm);
    testQuestionSubmissionForm1.recipientSubmissionForms[0].responseDetails = testResponseDetails1;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].responseDetails = testResponseDetails2;
    component.questionSubmissionForms = [testQuestionSubmissionForm1, testQuestionSubmissionForm2];

    const responseSpy: SpyInstance = jest.spyOn(feedbackResponsesService, 'submitFeedbackResponses')
        .mockImplementation((id: string) => {
          if (id === testQuestionSubmissionForm1.feedbackQuestionId) {
            return of({ responses: [testResponse1], requestId: '10' });
          }
          return of({ responses: [testResponse2], requestId: '20' });
        });
    jest.spyOn(feedbackResponseCommentService, 'createComment').mockReturnValue(of(testComment));
    jest.spyOn(feedbackResponseCommentService, 'updateComment').mockReturnValue(of(testComment));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(responseSpy).toHaveBeenCalledTimes(2);
    expect(responseSpy).toHaveBeenNthCalledWith(1, 'feedback-question-id-mcq', {
      responses: [{
        recipient: testMcqRecipientSubmissionForm.recipientIdentifier,
        responseDetails: testResponseDetails1,
      }],
    }, {
      intent: 'STUDENT_SUBMISSION',
      key: 'reg-key',
      moderatedperson: '',
    });
    expect(responseSpy).toHaveBeenLastCalledWith('feedback-question-id-text', {
      responses: [], // do not call for empty response details
    }, {
      intent: 'STUDENT_SUBMISSION',
      key: 'reg-key',
      moderatedperson: '',
    });

    expect(mockModalRef.componentInstance.requestIds).toEqual({
      'feedback-question-id-mcq': '10',
      'feedback-question-id-text': '20',
    });
    expect(mockModalRef.componentInstance.questions).toEqual([
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
    ]);
    expect(mockModalRef.componentInstance.answers).toEqual({
      'feedback-question-id-mcq': [testResponse1],
      'feedback-question-id-text': [testResponse2],
    });
    expect(mockModalRef.componentInstance.notYetAnsweredQuestions).toHaveLength(1);
    expect(mockModalRef.componentInstance.failToSaveQuestions).toEqual({});
  });

  it('should not save invalid feedback responses', () => {
    const mockModalRef: any = { componentInstance: {} };
    const testResponseDetails1: any = deepCopy(testMcqRecipientSubmissionForm.responseDetails);
    const testResponseDetails2: any = deepCopy(testConstsumRecipientSubmissionForm.responseDetails);
    const testQuestionSubmissionForm1: QuestionSubmissionFormModel = deepCopy(testMcqQuestionSubmissionForm);
    const testQuestionSubmissionForm2: QuestionSubmissionFormModel = deepCopy(testConstsumQuestionSubmissionForm);
    testQuestionSubmissionForm1.recipientSubmissionForms[0].responseDetails = testResponseDetails1;
    testQuestionSubmissionForm2.recipientSubmissionForms[0].responseDetails = testResponseDetails2;
    // invalid response
    testQuestionSubmissionForm2.recipientSubmissionForms[0].isValid = false;
    component.questionSubmissionForms = [testQuestionSubmissionForm1, testQuestionSubmissionForm2];

    const responseSpy: SpyInstance = jest.spyOn(feedbackResponsesService, 'submitFeedbackResponses')
        .mockImplementation(() => {
          return of({ responses: [testResponse1], requestId: '10' });
        });
    jest.spyOn(feedbackResponseCommentService, 'createComment').mockReturnValue(of(testComment));
    jest.spyOn(feedbackResponseCommentService, 'updateComment').mockReturnValue(of(testComment));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.saveFeedbackResponses(component.questionSubmissionForms);

    expect(responseSpy).toHaveBeenCalledTimes(1);
    expect(responseSpy).toHaveBeenNthCalledWith(1, testQuestionSubmissionForm1.feedbackQuestionId, {
      responses: [{
        recipient: testMcqRecipientSubmissionForm.recipientIdentifier,
        responseDetails: testResponseDetails1,
      }],
    }, {
      intent: 'STUDENT_SUBMISSION',
      key: 'reg-key',
      moderatedperson: '',
    });

    // only the valid response is saved
    expect(mockModalRef.componentInstance.requestIds).toEqual({
      [testQuestionSubmissionForm1.feedbackQuestionId]: '10',
    });
    expect(mockModalRef.componentInstance.questions).toEqual([
      testQuestionSubmissionForm1,
      testQuestionSubmissionForm2,
    ]);
    expect(mockModalRef.componentInstance.answers).toEqual({
      [testQuestionSubmissionForm1.feedbackQuestionId]: [testResponse1],
    });
    expect(mockModalRef.componentInstance.failToSaveQuestions).toEqual({
      [testQuestionSubmissionForm2.questionNumber]: 'Invalid responses provided. Please check question constraints.',
    });
  });

  it('should create comment request to create new comment when submission form has no original comment', () => {
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testTextRecipientSubmissionForm);
    const commentSpy: SpyInstance = jest.spyOn(feedbackResponseCommentService, 'createComment')
        .mockReturnValue(of(testComment));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(
          component.getCommentModel(testComment, testSubmissionForm.recipientIdentifier));
    });

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith({
      commentText: 'comment text here',
      showCommentTo: [],
      showGiverNameTo: [],
    }, testTextRecipientSubmissionForm.responseId, Intent.STUDENT_SUBMISSION,
        { key: testQueryParams.key, moderatedperson: '' });
  });

  it('should create comment request to create new comment when submission form has original comment'
      + 'with different original recipient', () => {
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testMcqRecipientSubmissionForm);
    testSubmissionForm.commentByGiver!.originalRecipientIdentifier = 'other-recipient-identifier';
    const commentSpy: SpyInstance = jest.spyOn(feedbackResponseCommentService, 'createComment')
        .mockReturnValue(of(testComment));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(
          component.getCommentModel(testComment, testSubmissionForm.recipientIdentifier));
    });

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith({
      commentText: 'comment text here',
      showCommentTo: [],
      showGiverNameTo: [],
    }, testMcqRecipientSubmissionForm.responseId, Intent.STUDENT_SUBMISSION,
        { key: testQueryParams.key, moderatedperson: '' });
  });

  it('should create comment request to update existing comment when submission form has original comment', () => {
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testMcqRecipientSubmissionForm);
    const expectedId: any = testMcqRecipientSubmissionForm.commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: SpyInstance = jest.spyOn(feedbackResponseCommentService, 'updateComment')
        .mockReturnValue(of(testComment));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(
          component.getCommentModel(testComment, testSubmissionForm.recipientIdentifier));
    });

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith({
      commentText: 'comment text here',
      showCommentTo: [],
      showGiverNameTo: [],
    }, expectedId, Intent.STUDENT_SUBMISSION, { key: testQueryParams.key, moderatedperson: '' });
  });

  it('should create comment request to delete existing comment when new comment text is empty', () => {
    const testSubmissionForm: FeedbackResponseRecipientSubmissionFormModel = deepCopy(testMcqRecipientSubmissionForm2);
    const expectedId: any = testMcqRecipientSubmissionForm2.commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: SpyInstance = jest.spyOn(feedbackResponseCommentService, 'deleteComment')
        .mockReturnValue(of({}));

    component.createCommentRequest(testSubmissionForm).subscribe(() => {
      expect(testSubmissionForm.commentByGiver).toEqual(undefined);
    });

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith(expectedId, Intent.STUDENT_SUBMISSION,
        { key: testQueryParams.key, moderatedperson: '' });
  });

  it('should delete participant comment', () => {
    const testSubmissionForm: QuestionSubmissionFormModel = deepCopy(testMsqQuestionSubmissionForm);
    const expectedId: any = testMsqQuestionSubmissionForm.recipientSubmissionForms[0]
        .commentByGiver?.originalComment?.feedbackResponseCommentId;
    const commentSpy: SpyInstance = jest.spyOn(feedbackResponseCommentService, 'deleteComment')
        .mockReturnValue(of(true));

    component.questionSubmissionForms = [testSubmissionForm];
    component.deleteParticipantComment(0, 0);

    expect(commentSpy).toHaveBeenCalledTimes(1);
    expect(commentSpy).toHaveBeenLastCalledWith(expectedId, Intent.STUDENT_SUBMISSION,
        { key: testQueryParams.key, moderatedperson: '' });
  });
});
