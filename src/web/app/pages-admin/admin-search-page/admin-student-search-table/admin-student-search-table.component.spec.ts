import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminStudentSearchTableComponent } from './admin-student-search-table.component';
import { AccountService } from '../../../../services/account.service';
import { EmailGenerationService } from '../../../../services/email-generation.service';
import { FeedbackSessionsGroup, StudentAccountSearchResult } from '../../../../services/search.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { StudentService } from '../../../../services/student.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';

const DEFAULT_SESSION_ID = '17681c09-f4e5-40c2-be77-eeccf0c221c2';
const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  [DEFAULT_SESSION_ID]: {
    name: 'sessionName',
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const DEFAULT_STUDENT_SEARCH_RESULT: StudentAccountSearchResult = {
  userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
  name: 'name',
  email: 'email',
  googleId: 'googleId',
  courseId: 'courseId',
  courseName: 'courseName',
  isCourseDeleted: false,
  institute: 'institute',
  courseJoinLink: 'courseJoinLink',
  homePageLink: 'homePageLink',
  manageAccountLink: 'manageAccountLink',
  showLinks: false,
  section: 'section',
  team: 'team',
  comments: 'comments',
  profilePageLink: 'profilePageLink',
  awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
};

describe('AdminStudentSearchTableComponent', () => {
  let component: AdminStudentSearchTableComponent;
  let fixture: ComponentFixture<AdminStudentSearchTableComponent>;
  let accountService: AccountService;
  let studentService: StudentService;
  let statusMessageService: StatusMessageService;
  let emailGenerationService: EmailGenerationService;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminStudentSearchTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    studentService = TestBed.inject(StudentService);
    statusMessageService = TestBed.inject(StatusMessageService);
    emailGenerationService = TestBed.inject(EmailGenerationService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with a deleted course', () => {
    component.students = [
      {
        userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
        name: 'student',
        email: 'student@gmail.tmt',
        googleId: 'student-google-id',
        courseId: 'deleted-course',
        courseName: 'deleted',
        isCourseDeleted: true,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: false,
        section: 'section',
        team: 'team',
        comments: 'comments',
        profilePageLink: 'profile-page-link',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded student table', () => {
    component.students = [
      {
        userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
        name: 'Alice Betsy',
        email: 'alice.b.tmms@gmail.tmt',
        googleId: 'student-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        isCourseDeleted: false,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
        section: 'section',
        team: 'team',
        comments: 'comments',
        profilePageLink: 'profile-page-link',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should show student links when expand all button clicked', () => {
    const studentResult: StudentAccountSearchResult = DEFAULT_STUDENT_SEARCH_RESULT;
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(true);
  });

  it('should show success message if successfully reset student google id', () => {
    const studentResult: StudentAccountSearchResult = DEFAULT_STUDENT_SEARCH_RESULT;
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy',
        course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetStudentAccount').mockReturnValue(
      of({
        message: 'success',
      }),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual("The student's Google ID has been reset.");
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
      userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      isCourseDeleted: false,
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
      section: 'section',
      team: 'team',
      comments: 'comments',
      profilePageLink: 'profilePageLink',
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy',
        course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetStudentAccount').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show success message and update all keys if successfully regenerated student registration key', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
      awaitingSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve({}),
      dismissed: {
        subscribe: jest.fn(),
      },
    };

    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

    jest.spyOn(studentService, 'regenerateStudentKey').mockReturnValue(
      of({
        message: 'success',
        newRegistrationKey: 'newKey',
      }),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('success');
      });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    expect(studentResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
    expect(studentResult.awaitingSessions[DEFAULT_SESSION_ID].feedbackSessionUrl).toEqual('awaitingSession?key=newKey');
    expect(studentResult.openSessions[DEFAULT_SESSION_ID].feedbackSessionUrl).toEqual('openSession?key=newKey');
    expect(studentResult.notOpenSessions[DEFAULT_SESSION_ID].feedbackSessionUrl).toEqual('notOpenSession?key=newKey');
    expect(studentResult.publishedSessions[DEFAULT_SESSION_ID].feedbackSessionUrl).toEqual(
      'publishedSession?key=newKey',
    );
  });

  it('should show error message if fail to regenerate registration key for student in a course', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
      awaitingSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        [DEFAULT_SESSION_ID]: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP[DEFAULT_SESSION_ID],
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve({}),
      dismissed: {
        subscribe: jest.fn(),
      },
    };

    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

    jest.spyOn(studentService, 'regenerateStudentKey').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to send course join email', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      showLinks: true,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(emailGenerationService, 'getCourseJoinEmail').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const sendButton: any = fixture.debugElement.nativeElement.querySelector('[data-testid="send-course-join-button"]');
    sendButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to send session reminder email', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      showLinks: true,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(emailGenerationService, 'getFeedbackSessionReminderEmail').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const sendAwaitingSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector(
      '[data-testid="send-awaiting-session-reminder-button"]',
    );
    sendAwaitingSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector(
      '[data-testid="send-open-session-reminder-button"]',
    );
    sendOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendNotOpenSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector(
      '[data-testid="send-not-open-session-reminder-button"]',
    );
    sendNotOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendPublishedSessionReminderButton: any = fixture.debugElement.nativeElement.querySelector(
      '[data-testid="send-published-session-reminder-button"]',
    );
    sendPublishedSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });
});
