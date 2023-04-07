import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo,
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestions,
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  ResponseVisibleSetting,
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
import { FeedbackQuestionModel, SessionResultPageComponent } from './session-result-page.component';

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

  const testFeedbackQuestion: FeedbackQuestion = {
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

  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let feedbackQuestionsService: FeedbackQuestionsService;
  let feedbackSessionService: FeedbackSessionsService;
  let logService: LogService;

  const testQueryParams: Record<string, string> = {
    courseid: 'CS3281',
    fsname: 'Peer Feedback',
    key: 'reg-key',
    previewas: '',
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
    feedbackQuestionsService = TestBed.inject(FeedbackQuestionsService);
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

  it('should snap when previewing results', () => {
    component.intent = Intent.STUDENT_RESULT;
    component.regKey = '';
    component.previewAsPerson = 'alice2@tmt.tmt';
    component.personName = 'Alice2';
    component.personEmail = 'alice2@tmt.tmt';
    component.session = testFeedbackSession;
    component.questions = [];
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
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/sessions/result',
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
    expect(navSpy).toHaveBeenLastCalledWith('/web/front',
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
    expect(navSpy).toHaveBeenLastCalledWith('/web/front',
        'You are not authorized to view this page.');
  });

  it('should navigate away when error occurs', () => {
    jest.spyOn(authService, 'getAuthUser').mockReturnValue(throwError(() => ({
      error: { message: 'This is error' },
    })));
    const navSpy: SpyInstance = jest.spyOn(navService, 'navigateWithErrorMessage').mockImplementation();

    fixture.detectChanges();
    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/front',
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
    expect(navSpy).toHaveBeenLastCalledWith('/web/join', { entitytype: 'student', key: 'reg-key' });
  });

  it('should load feedback questions', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    const testFeedbackQuestions: FeedbackQuestions = {
      questions: [testFeedbackQuestion],
    };
    const testFeedbackQuestionModel: FeedbackQuestionModel = {
      feedbackQuestion: testFeedbackQuestion,
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

    jest.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    jest.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    jest.spyOn(feedbackSessionService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    const getQuestionsSpy: SpyInstance = jest.spyOn(feedbackQuestionsService, 'getFeedbackQuestions')
        .mockReturnValue(of(testFeedbackQuestions));

    component.ngOnInit();
    expect(getQuestionsSpy).toHaveBeenLastCalledWith({
      courseId: testQueryParams['courseid'],
      feedbackSessionName: testQueryParams['fsname'],
      intent: Intent.STUDENT_RESULT,
      key: testQueryParams['key'],
      previewAs: testQueryParams['previewas'],
    });
    expect(component.questions.length).toEqual(1);
    expect(component.questions[0]).toEqual(testFeedbackQuestionModel);
  });
});
