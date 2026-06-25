import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { QuestionResponsePanelComponent } from './question-response-panel.component';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackQuestion,
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackTextQuestionDetails,
  FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FeedbackQuestionModel } from '../../pages-session/session-result-page/feedback-question.model';

describe('QuestionResponsePanelComponent', () => {
  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: 'c64aa0ca-beba-412d-94c3-58134feb6822',
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
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
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
      mcqChoices: ['<p>Good</p>', '<p>Normal</p>', '<p>Bad</p>'],
      otherEnabled: false,
      questionDropdownEnabled: false,
      generateOptionsFor: 'NONE',
      questionType: FeedbackQuestionType.MCQ,
      questionText: 'How well did team member perform?',
    } as FeedbackMcqQuestionDetails,
    questionType: FeedbackQuestionType.MCQ,
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
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
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS,
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
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS,
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
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.NONE,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.STUDENTS],
    showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestionAnonymousResponse1: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestionAnonymousResponse1',
    questionNumber: 1,
    questionBrief:
      'What comments do you have regarding each of your team members? ' +
      '(response is confidential and will only be shown to the instructor).',
    questionDescription: '',
    questionDetails: {
      shouldAllowRichText: true,
      questionType: FeedbackQuestionType.TEXT,
      questionText:
        'What comments do you have regarding each of your team members? ' +
        '(response is confidential and will only be shown to the instructor).',
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testQuestionAnonymousResponse2: FeedbackQuestion = {
    feedbackQuestionId: 'feedbackQuestionAnonymousResponse2',
    questionNumber: 2,
    questionBrief:
      'How are the team dynamics thus far? ' + '(response is confidential and will only be shown to the instructor).',
    questionDescription: '',
    questionDetails: {
      shouldAllowRichText: true,
      questionType: FeedbackQuestionType.TEXT,
      questionText:
        'How are the team dynamics thus far? ' + '(response is confidential and will only be shown to the instructor).',
    } as FeedbackTextQuestionDetails,
    questionType: FeedbackQuestionType.TEXT,
    giverType: QuestionGiverType.STUDENTS,
    recipientType: QuestionRecipientType.OWN_TEAM,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
    showGiverNameTo: [],
    showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
    customNumberOfEntitiesToGiveFeedbackTo: 0,
  };

  const testFeedbackQuestionModel: FeedbackQuestionModel = {
    feedbackQuestion: testQuestion1,
    questionStatistics: undefined,
    allResponses: [],
    responsesToSelf: [],
    responsesFromSelf: [],
    otherResponses: [],
    isLoading: false,
    isLoaded: false,
    hasResponseButNotVisibleForPreview: false,
  };

  let component: QuestionResponsePanelComponent;
  let fixture: ComponentFixture<QuestionResponsePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(QuestionResponsePanelComponent);
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
        questionStatistics: undefined,
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
        hasResponseButNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion2,
        questionStatistics: undefined,
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
        hasResponseButNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: undefined,
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
                giverId: 'comment-giver-id-1',
                commentGiverName: 'comment-giver-name-1',
                responseInstructorCommentId: '00000000-0000-4000-8000-000000000001',
                commentText: 'this is a text',
                createdAt: 1402775804,
              },
            ],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponseButNotVisibleForPreview: false,
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
        questionStatistics: undefined,
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
        hasResponseButNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestionAnonymousResponse2,
        questionStatistics: undefined,
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
        hasResponseButNotVisibleForPreview: false,
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
        questionStatistics: undefined,
        allResponses: [],
        responsesToSelf: [],
        responsesFromSelf: [],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponseButNotVisibleForPreview: true,
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: undefined,
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
                giverId: 'comment-giver-id-1',
                commentGiverName: 'comment-giver-name-1',
                responseInstructorCommentId: '00000000-0000-4000-8000-000000000001',
                commentText: 'this is a text',
                createdAt: 1402775804,
              },
            ],
          },
        ],
        otherResponses: [[]],
        isLoading: false,
        isLoaded: true,
        hasResponseButNotVisibleForPreview: false,
      },
      {
        feedbackQuestion: testQuestion4,
        questionStatistics: undefined,
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
        hasResponseButNotVisibleForPreview: false,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('canUserSeeResponses: should allow instructors to see responses when entityType is instructor', () => {
    component.entityType = 'instructor';
    testFeedbackQuestionModel.feedbackQuestion.showResponsesTo = [FeedbackVisibilityType.INSTRUCTORS];
    const canSee = component.canUserSeeResponses(testFeedbackQuestionModel);

    expect(canSee).toBe(true);
  });

  it('canUserSeeResponses: should return false when no responses are visible to instructors', () => {
    component.entityType = 'instructor';
    // Empty showResponsesTo should make the instructor branch return false.
    testFeedbackQuestionModel.feedbackQuestion.showResponsesTo = [];
    const canSee = component.canUserSeeResponses(testFeedbackQuestionModel);

    expect(canSee).toBe(false);
  });

  it('canUserSeeResponses: should allow students when entityType is student and responses are visible to recipients and instructors', () => {
    component.entityType = 'student';
    testFeedbackQuestionModel.feedbackQuestion.showResponsesTo = [
      FeedbackVisibilityType.RECIPIENT,
      FeedbackVisibilityType.INSTRUCTORS,
    ];

    const canSee = component.canUserSeeResponses(testFeedbackQuestionModel);

    expect(canSee).toBe(true);
  });

  it('should render a preloaded question response card', () => {
    component.session = testFeedbackSession;
    component.questions = [{ ...testFeedbackQuestionModel, isLoaded: true }];

    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#question-1-responses')).toBeTruthy();
  });
});
