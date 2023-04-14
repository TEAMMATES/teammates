import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails, FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails, FeedbackParticipantType,
  FeedbackQuestion, FeedbackQuestionType,
  FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails,
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails, FeedbackTextResponseDetails, FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting, ResponseVisibleSetting,
  SessionResults, SessionVisibleSetting,
} from '../../../types/api-output';
import { FeedbackQuestionModel } from '../../pages-session/session-result-page/session-result-page.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { SingleStatisticsModule } from '../question-responses/single-statistics/single-statistics.module';
import {
  StudentViewResponsesModule,
} from '../question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../question-text-with-info/question-text-with-info.module';
import { QuestionResponsePanelComponent } from './question-response-panel.component';

describe('QuestionResponsePanelComponent', () => {

  const testFeedbackSession: FeedbackSession = {
    feedbackSessionName: 'First Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1549095330000,
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

  const testQuestion1: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion1',
    questionNumber: 1,
    questionBrief: 'How well did team member perform?',
    questionDescription: '',
    questionDetails: {
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      mcqChoices: [
        '<p>Good</p>',
        '<p>Normal</p>',
        '<p>Bad</p>',
      ],
      otherEnabled: false,
      questionDropdownEnabled: false,
      generateOptionsFor: 'NONE',
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'How well did team member perform?',
    } as FeedbackMcqQuestionDetails,
    questionType: FeedbackQuestionType.MCQ,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestion2: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion2',
    questionNumber: 2,
    questionBrief: 'Rate your teammates in contribution',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.CONTRIB,
      questionText: 'Rate your teammates in contribution',
      isNotSureAllowed: false,
    } as FeedbackContributionQuestionDetails,
    questionType: FeedbackQuestionType.CONTRIB,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestion3: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion3',
    questionNumber: 3,
    questionBrief: 'Rate your teammates proficiency',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.RUBRIC,
      questionText: 'Rate your teammates proficiency',
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [[]],
      rubricChoices: ['Poor', 'Average', 'Good'],
      rubricSubQuestions: [],
      rubricDescriptions: [[]],
    } as FeedbackRubricQuestionDetails,
    questionType: FeedbackQuestionType.RUBRIC,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestion4: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestion4',
    questionNumber: 4,
    questionBrief: 'Do you have any feedback for the course?',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'Do you have any feedback for the course?',
      shouldAllowRichText: true,
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.NONE,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.STUDENTS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestionAnonymousResponse1: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestionAnonymousResponse1',
    questionNumber: 1,
    questionBrief: 'What comments do you have regarding each of your team members? '
        + '(response is confidential and will only be shown to the instructor).',
    questionDescription: '',
    questionDetails: {
      shouldAllowRichText: true,
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'What comments do you have regarding each of your team members? '
          + '(response is confidential and will only be shown to the instructor).',
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestionAnonymousResponse2: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestionAnonymousResponse2',
    questionNumber: 2,
    questionBrief: 'How are the team dynamics thus far? '
        + '(response is confidential and will only be shown to the instructor).',
    questionDescription: '',
    questionDetails: {
      shouldAllowRichText: true,
      questionType: FeedbackQuestionType.TEXT,
      questionText: 'How are the team dynamics thus far? '
          + '(response is confidential and will only be shown to the instructor).',
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.OWN_TEAM,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testFeedbackQuestionModel: FeedbackQuestionModel = {
    feedbackQuestion: testQuestion1,
    questionStatistics: '',
    allResponses: [],
    responsesToSelf: [],
    responsesFromSelf: [],
    otherResponses: [],
    isLoading: false,
    isLoaded: false,
    hasResponse: true,
    hasResponseButNotVisibleForPreview: false,
    hasCommentNotVisibleForPreview: false,
  };

  let component: QuestionResponsePanelComponent;
  let fixture: ComponentFixture<QuestionResponsePanelComponent>;
  let feedbackSessionsService: FeedbackSessionsService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionResponsePanelComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        SingleStatisticsModule,
        StudentViewResponsesModule,
        QuestionTextWithInfoModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
      providers: [FeedbackSessionsService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionResponsePanelComponent);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with feedback session with questions', () => {
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestion1,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-1',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: 'Good',
              isOther: false,
              otherFieldContent: '',
            } as FeedbackMcqResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion2,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-2',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'giver1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: 120,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
        ],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-3',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient2',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: 110,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-4',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: 100,
            } as FeedbackContributionResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-5',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: [1],
            } as FeedbackRubricResponseDetails,
            instructorComments: [
              {
                commentGiver: 'comment-giver-1',
                lastEditorEmail: 'comment@egeg.com',
                feedbackResponseCommentId: 1,
                commentText: 'this is a text',
                createdAt: 1402775804,
                lastEditedAt: 1402775804,
                isVisibilityFollowingFeedbackQuestion: true,
                showGiverNameTo: [],
                showCommentTo: [],
              },
            ],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session with questions of anonymous responses', () => {
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestionAnonymousResponse1,
        questionStatistics: '',
        allResponses: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-1',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-2',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient2',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-3',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-1',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-2',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient2',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
          {
            isMissingResponse: false,
            responseId: 'resp-id-3',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestionAnonymousResponse2,
        questionStatistics: '',
        allResponses: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-4',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'team1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-4',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'team1',
            recipientTeam: 'team1',
            recipientSection: 'section1',
            responseDetails: {
              answer: '<p>asdf</p>',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with questions and responses when previewing results', () => {
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestion1,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: true,
        hasCommentNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-5',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: 'recipient3',
            recipientTeam: 'team2',
            recipientSection: 'section2',
            responseDetails: {
              answer: [1],
            } as FeedbackRubricResponseDetails,
            instructorComments: [
              {
                commentGiver: 'comment-giver-1',
                lastEditorEmail: 'comment@egeg.com',
                feedbackResponseCommentId: 1,
                commentText: 'this is a text',
                createdAt: 1402775804,
                lastEditedAt: 1402775804,
                isVisibilityFollowingFeedbackQuestion: true,
                showGiverNameTo: [],
                showCommentTo: [],
              },
            ],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: true,
      },
      {
        feedbackQuestion: testQuestion4,
        questionStatistics: '',
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [
          {
            isMissingResponse: false,
            responseId: 'resp-id-7',
            giver: 'giver1',
            giverTeam: 'team1',
            giverSection: 'section1',
            recipient: '-',
            recipientTeam: 'None',
            recipientSection: '-',
            responseDetails: {
              answer: 'Yes',
              questionType: 'TEXT',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponse: true,
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should load the recipients and responses of a question if not yet loaded', () => {
    component.session = testFeedbackSession;
    component.questions = [testFeedbackQuestionModel];
    const testFeedbackSessionResult: SessionResults = {
      questions: [
        {
          feedbackQuestion: testQuestion1,
          questionStatistics: '',
          allResponses: [],
          hasResponseButNotVisibleForPreview: false,
          hasCommentNotVisibleForPreview: false,
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
      ],
    };

    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionsService, 'getFeedbackSessionResults')
        .mockReturnValue(of(testFeedbackSessionResult));
    component.loadQuestion({ visible: true }, testFeedbackQuestionModel);

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith({
      intent: 'STUDENT_RESULT',
      courseId: 'CS1231',
      feedbackSessionName: 'First Session',
      questionId: testQuestion1.feedbackQuestionId,
      key: '',
      previewAs: '',
    });
    expect(testFeedbackQuestionModel.isLoading).toBe(false);
    expect(testFeedbackQuestionModel.isLoaded).toBe(true);
    expect(testFeedbackQuestionModel.hasResponse).toBe(true);
  });

  it('should not load the recipients and responses of a question if already loaded', () => {
    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionsService, 'getFeedbackSessionResults');

    testFeedbackQuestionModel.isLoaded = true;
    component.loadQuestion({ visible: true }, testFeedbackQuestionModel);

    testFeedbackQuestionModel.isLoading = true;
    component.loadQuestion({ visible: true }, testFeedbackQuestionModel);

    expect(fsSpy).not.toHaveBeenCalled();
  });
});
