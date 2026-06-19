import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FeedbackQuestionModel } from './feedback-question.model';
import { SessionResultPageComponent } from './session-result-page.component';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { NavigationService } from '../../../services/navigation.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo,
  FeedbackMcqQuestionDetails,
  FeedbackQuestion,
  FeedbackQuestionType,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
  RegkeyValidity,
  UserSessionResults,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';

describe('SessionResultPageComponent', () => {
  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: 'test-session-id',
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
  const testFeedbackSessionView: FeedbackSessionView = {
    feedbackSession: testFeedbackSession,
  };

  const testInfo: AuthInfo = {
    loginUrl: '/login',
    masquerade: false,
    user: {
      accountEmail: 'account@teammates.tmt',
      isAdmin: false,
      isInstructor: true,
      isStudent: false,
      isMaintainer: false,
      accountId: 'account-id',
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

  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let feedbackSessionService: FeedbackSessionsService;
  let logService: LogService;

  const testQueryParams: Record<string, string> = {
    fsid: 'test-session-id',
    key: 'reg-key',
    previewAs: '',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SessionResultPageComponent);
    authService = TestBed.inject(AuthService);
    navService = TestBed.inject(NavigationService);
    studentService = TestBed.inject(StudentService);
    feedbackSessionService = TestBed.inject(FeedbackSessionsService);
    logService = TestBed.inject(LogService);
    component = fixture.componentInstance;
    component.feedbackSessionId = testQueryParams['fsid'];
    component.key = testQueryParams['key'];
    component.previewAs = testQueryParams['previewAs'];
    component.intent = Intent.STUDENT_RESULT;
    // Set both loading flags to false initially for testing purposes only
    component.isCourseLoading = false;
    component.isFeedbackSessionDetailsLoading = false;
    component.isFeedbackSessionResultsLoading = false;
    vi.spyOn(feedbackSessionService, 'getUserSessionResults').mockReturnValue(of({ questions: [] }));
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
    component.key = 'session-link-key';
    component.accountEmail = 'alice@example.com';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with user that is not logged in and using session link', () => {
    component.key = 'session-link-key';
    component.accountEmail = '';
    component.personName = 'alice';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an open feedback session with no questions', () => {
    component.session = {
      feedbackSessionId: 'test-session-id',
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
      isClosingSoonEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 1555231400,
    };
    component.questions = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when previewing results', () => {
    component.intent = Intent.STUDENT_RESULT;
    component.key = '';
    component.previewAs = 'alice2@tmt.tmt';
    component.personName = 'Alice2';
    component.personEmail = 'alice2@tmt.tmt';
    component.session = testFeedbackSession;
    component.questions = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));

    component.ngOnInit();

    expect(component.feedbackSessionId).toEqual('test-session-id');
    expect(component.key).toEqual('reg-key');
    expect(component.accountEmail).toEqual('account@teammates.tmt');
  });

  it('should verify allowed access and used reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: true,
      isValid: false,
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/student/sessions/test-session-id/result');
  });

  it('should load info and create log', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    vi.spyOn(studentService, 'getOwnStudent').mockReturnValue(
      of({
        name: 'student-name',
        email: 'student@tmt.tmt',
        courseId: '',
        courseName: '',
        institute: '',
        userId: 'student-name-id',
        sectionName: '',
        sectionId: '',
        teamName: '',
        teamId: '',
      }),
    );
    vi.spyOn(feedbackSessionService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    const logSpy = vi.spyOn(logService, 'createFeedbackSessionLog').mockReturnValue(of('log created'));

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
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    const navSpy = vi.spyOn(navService, 'navigateWithErrorMessage').mockResolvedValue();

    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith(
      '/web/front',
      `You are signed in as ${component.accountEmail}, but this course is linked to a different TEAMMATES account. If you used a different account to join/access TEAMMATES before, please use that account to access TEAMMATES. If you cannot remember which account you used before, please email us at ${environment.supportEmail} for help.`,
    );
  });

  it('should deny access for invalid reg key', () => {
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

  it('should navigate away when error occurs', () => {
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(
      throwError(() => ({
        error: { message: 'This is error' },
      })),
    );
    const navSpy = vi.spyOn(navService, 'navigateWithErrorMessage').mockResolvedValue();

    fixture.detectChanges();
    component.ngOnInit();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/front', 'You are not authorized to view this page.');
  });

  it('should navigate to join course when user click on join course link', () => {
    component.key = 'reg-key';
    component.accountEmail = 'account@example.com';
    const navSpy = vi.spyOn(navService, 'navigateByURL').mockResolvedValue(true);

    fixture.detectChanges();

    const btn = fixture.debugElement.nativeElement.querySelector('#join-course-btn');
    btn.click();

    expect(navSpy).toHaveBeenCalledTimes(1);
    expect(navSpy).toHaveBeenLastCalledWith('/web/join', { entityType: 'student', key: 'reg-key' });
  });

  it('should load session results and hydrate questions', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    const testFeedbackQuestionModel: FeedbackQuestionModel = {
      feedbackQuestion: testFeedbackQuestion,
      questionStatistics: undefined,
      allResponses: [],
      responsesToSelf: [],
      responsesFromSelf: [],
      otherResponses: [],
      isLoading: false,
      isLoaded: true,
      hasResponseButNotVisibleForPreview: false,
      hasCommentNotVisibleForPreview: false,
    };
    const testSessionResults: UserSessionResults = {
      questions: [
        {
          feedbackQuestion: testFeedbackQuestion,
          questionStatistics: undefined,
          allResponses: [],
          hasResponseButNotVisibleForPreview: false,
          hasCommentNotVisibleForPreview: false,
          responsesToSelf: [],
          responsesFromSelf: [],
          otherResponses: [],
        },
      ],
    };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(testInfo));
    vi.spyOn(authService, 'getAuthRegkeyValidity').mockReturnValue(of(testValidity));
    vi.spyOn(feedbackSessionService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    const getResultsSpy = vi
      .spyOn(feedbackSessionService, 'getUserSessionResults')
      .mockReturnValue(of(testSessionResults));

    component.ngOnInit();
    expect(getResultsSpy).toHaveBeenLastCalledWith({
      feedbackSessionId: testQueryParams['fsid'],
      intent: Intent.STUDENT_RESULT,
      key: testQueryParams['key'],
      previewAs: testQueryParams['previewAs'],
    });
    expect(component.questions.length).toEqual(1);
    expect(component.questions[0]).toEqual(testFeedbackQuestionModel);
  });
});
