import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo, FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionType, FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, FeedbackTextQuestionDetails, FeedbackTextResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionOutput,
  RegkeyValidity,
  ResponseVisibleSetting, SessionResults,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { QuestionResponsePanelModule } from '../../components/question-response-panel/question-response-panel.module';
import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import {
  StudentViewResponsesModule,
} from '../../components/question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { SessionResultPageComponent } from './session-result-page.component';

describe('SessionResultPageComponent', () => {
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

  const testInfo: AuthInfo = {
    masquerade: false,
    user: {
      id: 'user-id',
      isAdmin: false,
      isInstructor: true,
      isStudent: false,
      isMaintainer: false,
    },
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

  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let feedbackSessionService: FeedbackSessionsService;
  let logService: LogService;

  const testQueryParams: Record<string, string> = {
    courseid: 'CS3281',
    fsname: 'Peer Feedback',
    key: 'reg-key',
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        StudentViewResponsesModule,
        QuestionTextWithInfoModule,
        QuestionResponsePanelModule,
        SingleStatisticsModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
      declarations: [SessionResultPageComponent],
      providers: [
        AuthService,
        NavigationService,
        StudentService,
        FeedbackSessionsService,
        LogService,
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of(testQueryParams),
            data: {
              intent: Intent.STUDENT_RESULT,
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
    fixture = TestBed.createComponent(SessionResultPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    studentService = TestBed.inject(StudentService);
    feedbackSessionService = TestBed.inject(FeedbackSessionsService);
    logService = TestBed.inject(LogService);
    component = fixture.componentInstance;
    // Set both loading flags to false initially for testing purposes only
    component.isCourseLoading = false;
    component.isFeedbackSessionDetailsLoading = false;
    component.isFeedbackSessionResultsLoading = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with session details and results are loading', () => {
    component.isCourseLoading = true;
    component.isFeedbackSessionDetailsLoading = true;
    component.isFeedbackSessionResultsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with session details loaded and results are loading', () => {
    component.isCourseLoading = false;
    component.isFeedbackSessionDetailsLoading = false;
    component.isFeedbackSessionResultsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when session results failed to load', () => {
    component.isCourseLoading = false;
    component.isFeedbackSessionDetailsLoading = false;
    component.isFeedbackSessionResultsLoading = false;
    component.hasFeedbackSessionResultsLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is logged in and using session link', () => {
    component.regKey = 'session-link-key';
    component.loggedInUser = 'alice';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is not logged in and using session link', () => {
    component.regKey = 'session-link-key';
    component.loggedInUser = '';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an open feedback session with no questions', () => {
    component.session = {
      courseId: 'CS3281',
      timeZone: 'UTC',
      feedbackSessionName: 'Peer Review 1',
      instructions: '',
      submissionStartTimestamp: 1555232400,
      submissionEndTimestamp: 1555233400,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 1555231400,
      studentDeadlines: {},
      instructorDeadlines: {},
    };
    component.questions = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session with questions', () => {
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestion1,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
      },
      {
        feedbackQuestion: testQuestion2,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback session with questions when previewing results', () => {
    component.intent = Intent.STUDENT_RESULT;
    component.regKey = '';
    component.previewAsPerson = 'alice@fan.tmt';
    component.personName = 'Alice';
    component.personEmail = 'alice@fan.tmt';
    component.session = testFeedbackSession;
    component.questions = [
      {
        feedbackQuestion: testQuestion1,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: true,
        hasCommentNotVisibleForPreview: false,
        responsesToSelf: [],
        responsesFromSelf: [],
        otherResponses: [[]],
      },
      {
        feedbackQuestion: testQuestion3,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: false,
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
      },
      {
        feedbackQuestion: testQuestion4,
        questionStatistics: '',
        allResponses: [],
        hasResponseButNotVisibleForPreview: false,
        hasCommentNotVisibleForPreview: true,
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
              answer: "Yes",
              questionType: 'TEXT',
            } as FeedbackTextResponseDetails,
            instructorComments: [],
          },
        ],
        otherResponses: [[]],
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));

    component.ngOnInit();

    expect(component.courseId).toEqual('CS3281');
    expect(component.feedbackSessionName).toEqual('Peer Feedback');
    expect(component.regKey).toEqual('reg-key');
    expect(component.loggedInUser).toEqual('user-id');
  });

  it('should verify allowed access and used reg key', () => {
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
    expect(navSpy).toHaveBeenLastCalledWith(expect.anything(), '/web/student/sessions/result',
        { courseid: 'CS3281', fsname: 'Peer Feedback' });
  });

  it('should load info and create log for unused reg key that is allowed', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    jest.spyOn(studentService, 'getStudent').mockReturnValue(of({
      name: 'student-name',
      email: '',
      courseId: '',
      sectionName: '',
      teamName: '',
    }));
    jest.spyOn(feedbackSessionService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    const logSpy: SpyInstance = jest.spyOn(logService, 'createFeedbackSessionLog').mockReturnValue(of('log created'));

    component.ngOnInit();

    expect(component.personName).toEqual('student-name');
    expect(component.session.courseId).toEqual('CS1231');
    expect(logSpy).toHaveBeenCalledTimes(1);
  });

  it('should fetch session results when loading feedback session', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };

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
        {
          feedbackQuestion: testQuestion3,
          questionStatistics: '',
          allResponses: [],
          hasResponseButNotVisibleForPreview: false,
          hasCommentNotVisibleForPreview: false,
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
        {
          feedbackQuestion: testQuestion2,
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

    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    jest.spyOn(feedbackSessionService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    const fsSpy: SpyInstance = jest.spyOn(feedbackSessionService, 'getFeedbackSessionResults')
        .mockReturnValue(of(testFeedbackSessionResult));

    component.ngOnInit();

    expect(fsSpy).toHaveBeenCalledTimes(1);
    expect(fsSpy).toHaveBeenLastCalledWith({
      courseId: 'CS3281',
      feedbackSessionName: 'Peer Feedback',
      intent: Intent.STUDENT_RESULT,
      key: 'reg-key',
      previewAs: '',
    });
    expect(component.questions.map((question: QuestionOutput) => question.feedbackQuestion.questionNumber))
        .toEqual([1, 2, 3]);
  });

  it('should deny access for reg key not belonging to logged in user', () => {
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
    expect(navSpy).toHaveBeenLastCalledWith(expect.anything(), '/web/front',
        `You are trying to access TEAMMATES using the Google account user-id, which
                    is not linked to this TEAMMATES account. If you used a different Google account to
                    join/access TEAMMATES before, please use that Google account to access TEAMMATES. If you
                    cannot remember which Google account you used before, please email us at
                    ${environment.supportEmail} for help.`);
  });

  it('should deny access for invalid reg key', () => {
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
    expect(navSpy).toHaveBeenLastCalledWith(expect.anything(), '/web/front',
        'You are not authorized to view this page.');
  });

  it('should navigate away when error occurs', () => {
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(throwError({
      error: { message: 'This is error' },
    }));
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateWithErrorMessage').mockImplementation();

    fixture.detectChanges();
    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(expect.anything(), '/web/front',
        'You are not authorized to view this page.');
  });

  it('should navigate to join course when user click on join course link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = 'user';
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateByURL').mockImplementation();

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement
        .querySelector('#join-course-btn');
    btn.click();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(expect.anything(), '/web/join', { entitytype: 'student', key: 'reg-key' });
  });
});
