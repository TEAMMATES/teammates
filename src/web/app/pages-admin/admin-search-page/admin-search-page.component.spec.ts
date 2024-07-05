import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminSearchPageComponent } from './admin-search-page.component';
import { AdminSearchPageModule } from './admin-search-page.module';
import { AccountService } from '../../../services/account.service';
import { EmailGenerationService } from '../../../services/email-generation.service';
import { InstructorService } from '../../../services/instructor.service';
import {
  FeedbackSessionsGroup, InstructorAccountSearchResult,
  SearchService, StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';

const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  sessionName: {
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const DEFAULT_STUDENT_SEARCH_RESULT: StudentAccountSearchResult = {
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

const DEFAULT_INSTRUCTOR_SEARCH_RESULT: InstructorAccountSearchResult = {
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
  awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
};

describe('AdminSearchPageComponent', () => {
  let component: AdminSearchPageComponent;
  let fixture: ComponentFixture<AdminSearchPageComponent>;
  let accountService: AccountService;
  let searchService: SearchService;
  let instructorService: InstructorService;
  let studentService: StudentService;
  let statusMessageService: StatusMessageService;
  let emailGenerationService: EmailGenerationService;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        HttpClientTestingModule,
        NgbTooltipModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        AdminSearchPageModule,
      ],
      providers: [AccountService, SearchService, StatusMessageService, NgbModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSearchPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    searchService = TestBed.inject(SearchService);
    instructorService = TestBed.inject(InstructorService);
    studentService = TestBed.inject(StudentService);
    statusMessageService = TestBed.inject(StatusMessageService);
    emailGenerationService = TestBed.inject(EmailGenerationService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', (() => {
    expect(fixture).toMatchSnapshot();
  }));

  it('should snap with a search key', () => {
    component.searchQuery = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a deleted course', () => {
    component.instructors = [
      {
        name: 'instructor',
        email: 'instructor@tester.com',
        googleId: 'ins-google-id',
        courseId: 'deleted-course',
        courseName: 'deleted',
        isCourseDeleted: true,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: false,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];
    component.students = [
      {
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

  it('should snap with an expanded instructor table', () => {
    component.instructors = [
      {
        name: 'tester',
        email: 'tester@tester.com',
        googleId: 'instructor-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        isCourseDeleted: false,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
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

  it('should display error message for invalid input', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message');
        });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display warning message for no results', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: [],
      instructors: [],
      accountRequests: [],
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showWarningToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('No results found.');
        });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display instructor results', () => {
    const instructorResults: InstructorAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        isCourseDeleted: false,
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
      {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      }];

    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: [],
      instructors: instructorResults,
      accountRequests: [],
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(2);
    expect(component.accountRequests.length).toEqual(0);
    expect(component.instructors).toEqual(instructorResults);
    expect(component.instructors[0].showLinks).toEqual(false);
    expect(component.instructors[1].showLinks).toEqual(false);
  });

  it('should display student results', () => {
    const studentResults: StudentAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        isCourseDeleted: false,
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        section: 'section1',
        team: 'team1',
        comments: 'comments1',
        profilePageLink: 'profilePageLink1',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      }, {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        section: 'section2',
        team: 'team2',
        comments: 'comments2',
        profilePageLink: 'profilePageLink2',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      }];

    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: studentResults,
      instructors: [],
      accountRequests: [],
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(2);
    expect(component.instructors.length).toEqual(0);
    expect(component.accountRequests.length).toEqual(0);
    expect(component.students).toEqual(studentResults);
    expect(component.students[0].showLinks).toEqual(false);
    expect(component.students[1].showLinks).toEqual(false);
  });

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = {
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
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-instructor-links');
    button.click();
    expect(component.instructors[0].showLinks).toEqual(true);
  });

  it('should show student links when expand all button clicked', () => {
    const studentResult: StudentAccountSearchResult = DEFAULT_STUDENT_SEARCH_RESULT;
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(true);
  });

  it('should show success message if successfully reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
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
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetInstructorAccount').mockReturnValue(of({
      message: 'Success',
    }));
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('The instructor\'s Google ID has been reset.');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
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
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetInstructorAccount').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show success message if successfully reset student google id', () => {
    const studentResult: StudentAccountSearchResult = DEFAULT_STUDENT_SEARCH_RESULT;
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetStudentAccount').mockReturnValue(of({
      message: 'success',
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('The student\'s Google ID has been reset.');
        });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
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
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetStudentAccount').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
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
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
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

    jest.spyOn(studentService, 'regenerateStudentKey').mockReturnValue(of({
      message: 'success',
      newRegistrationKey: 'newKey',
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('success');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    expect(studentResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
    expect(studentResult.awaitingSessions['sessionName'].feedbackSessionUrl).toEqual('awaitingSession?key=newKey');
    expect(studentResult.openSessions['sessionName'].feedbackSessionUrl).toEqual('openSession?key=newKey');
    expect(studentResult.notOpenSessions['sessionName'].feedbackSessionUrl).toEqual('notOpenSession?key=newKey');
    expect(studentResult.publishedSessions['sessionName'].feedbackSessionUrl).toEqual('publishedSession?key=newKey');
  });

  it('should show error message if fail to regenerate registration key for student in a course', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
      awaitingSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP['sessionName'],
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

    jest.spyOn(studentService, 'regenerateStudentKey').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message.');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show success message and update all keys if successfully regenerated instructor registration key', () => {
    const instructorResult: InstructorAccountSearchResult = {
      ...DEFAULT_INSTRUCTOR_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve({}),
      dismissed: {
        subscribe: jest.fn(),
      },
    };

    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

    jest.spyOn(instructorService, 'regenerateInstructorKey').mockReturnValue(of({
      message: 'success',
      newRegistrationKey: 'newKey',
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('success');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    expect(instructorResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
  });

  it('should show error message if fail to regenerate registration key for instructor in a course', () => {
    const instructorResult: InstructorAccountSearchResult = {
      ...DEFAULT_INSTRUCTOR_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve({}),
      dismissed: {
        subscribe: jest.fn(),
      },
    };

    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);

    jest.spyOn(instructorService, 'regenerateInstructorKey').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message.');
        });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
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

    jest.spyOn(emailGenerationService, 'getCourseJoinEmail').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message.');
        });

    const sendButton: any = fixture.debugElement.nativeElement.querySelector('#send-course-join-button');
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

    jest.spyOn(emailGenerationService, 'getFeedbackSessionReminderEmail').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message.');
        });

    const sendAwaitingSessionReminderButton: any =
        fixture.debugElement.nativeElement.querySelector('#send-awaiting-session-reminder-button');
    sendAwaitingSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendOpenSessionReminderButton: any =
        fixture.debugElement.nativeElement.querySelector('#send-open-session-reminder-button');
    sendOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendNotOpenSessionReminderButton: any =
        fixture.debugElement.nativeElement.querySelector('#send-not-open-session-reminder-button');
    sendNotOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();

    const sendPublishedSessionReminderButton: any =
        fixture.debugElement.nativeElement.querySelector('#send-published-session-reminder-button');
    sendPublishedSessionReminderButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });
});
