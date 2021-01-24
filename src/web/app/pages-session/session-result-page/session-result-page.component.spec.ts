import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../services/auth.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { NavigationService } from '../../../services/navigation.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo, FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, RegkeyValidity,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SingleStatisticsModule } from '../../components/question-responses/single-statistics/single-statistics.module';
import { StudentViewResponsesModule } from '../../components/question-responses/student-view-responses/student-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { SessionResultPageComponent } from './session-result-page.component';
import Spy = jasmine.Spy;

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
  };
  const testInfo: AuthInfo = {
    masquerade: false,
    user: {
      id: 'user-id',
      isAdmin: false,
      isInstructor: true,
      isStudent: false,
    },
  };

  let component: SessionResultPageComponent;
  let fixture: ComponentFixture<SessionResultPageComponent>;
  let authService: AuthService;
  let navService: NavigationService;
  let studentService: StudentService;
  let feedbackSessionService: FeedbackSessionsService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        StudentViewResponsesModule,
        QuestionTextWithInfoModule,
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
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              courseid: 'CS3281',
              fsname: 'Peer Feedback',
              key: 'reg-key',
            }),
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
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with session results are loading', () => {
    component.isFeedbackSessionResultsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when session results failed to load', () => {
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
    };
    component.questions = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should fetch auth info on init', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));

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
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateByURLWithParamEncoding');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1])
        .toEqual('/web/student/sessions/result');
  });

  it('should load info for unused reg key that is allowed', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: true,
      isUsed: false,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    spyOn(studentService, 'getStudent').and.returnValue(of({ name: 'student-name' }));
    spyOn(feedbackSessionService, 'getFeedbackSession').and.returnValue(of(testFeedbackSession));

    component.ngOnInit();

    expect(component.personName).toEqual('student-name');
    expect(component.session.courseId).toEqual('CS1231');
  });

  it('should deny access for reg key not belonging to logged in user', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: true,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2]).toEqual('You are not authorized to view this page.');
  });

  it('should deny access for invalid reg key', () => {
    const testValidity: RegkeyValidity = {
      isAllowedAccess: false,
      isUsed: false,
      isValid: false,
    };
    spyOn(authService, 'getAuthUser').and.returnValue(of(testInfo));
    spyOn(authService, 'getAuthRegkeyValidity').and.returnValue(of(testValidity));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    component.ngOnInit();

    expect(navSpy.calls.count()).toEqual(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2])
        .toEqual('You are not authorized to view this page.');
  });

  it('should navigate away when error occurs', () => {
    spyOn(authService, 'getAuthUser').and.returnValue(throwError({
      error: { message: 'This is error' },
    }));
    const navSpy: Spy = spyOn(navService, 'navigateWithErrorMessage');

    fixture.detectChanges();
    component.ngOnInit();

    expect(navSpy.calls.count()).toBe(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/front');
    expect(navSpy.calls.mostRecent().args[2])
        .toEqual('You are not authorized to view this page.');
  });

  it('should navigate to join course when user click on join course link', () => {
    component.regKey = 'reg-key';
    component.loggedInUser = 'user';
    const navSpy: Spy = spyOn(navService, 'navigateByURL');

    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement
        .querySelector('#join-course-btn');
    btn.click();

    expect(navSpy.calls.count()).toBe(1);
    expect(navSpy.calls.mostRecent().args[1]).toEqual('/web/join');
  });
});
