import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AccountService } from '../../../services/account.service';
import { EmailGenerationService } from '../../../services/email-generation.service';
import { InstructorService } from '../../../services/instructor.service';
import {
  AccountRequestSearchResult,
  FeedbackSessionsGroup, InstructorAccountSearchResult,
  SearchService, StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AdminSearchPageComponent } from './admin-search-page.component';
import { default as searchResultJs } from '../../test-data/searchResult.json';

const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  sessionName: {
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const DEFAULT_STUDENT_SEARCH_RESULT: StudentAccountSearchResult = searchResultJs.searchResult1;
const DEFAULT_INSTRUCTOR_SEARCH_RESULT: InstructorAccountSearchResult = searchResultJs.searchResult2;
const DEFAULT_ACCOUNT_REQUEST_SEARCH_RESULT: AccountRequestSearchResult = searchResultJs.searchResult3;

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
      declarations: [AdminSearchPageComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
        NgbTooltipModule,
        BrowserAnimationsModule,
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

  it('should snap with an expanded instructor table', () => {
    component.instructors = [
      searchResultJs.searchResult4
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded student table', () => {
    component.students = [
      searchResultJs.searchResult5
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded account requests table', () => {
    component.accountRequests = [
      {
        name: 'name',
        email: 'email',
        institute: 'institute',
        registrationLink: 'registrationLink',
        createdAt: 'Tue, 08 Feb 2022, 08:23 AM +00:00',
        registeredAt: 'Not Registered Yet',
        showLinks: true,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display error message for invalid input', () => {
    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toBeCalled();
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

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should display instructor results', () => {
    const instructorResults: InstructorAccountSearchResult[] = [
      searchResultJs.searchResult6,
      searchResultJs.searchResult7
    ];

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
      searchResultJs.searchResult8,
      searchResultJs.searchResult9
    ];

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

  it('should display account request results', () => {
    const accountRequestResults: AccountRequestSearchResult[] = [
      searchResultJs.searchResult12,
      searchResultJs.searchResult13
    ];

    jest.spyOn(searchService, 'searchAdmin').mockReturnValue(of({
      students: [],
      instructors: [],
      accountRequests: accountRequestResults,
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(0);
    expect(component.accountRequests.length).toEqual(2);
    expect(component.accountRequests).toEqual(accountRequestResults);
    expect(component.accountRequests[0].showLinks).toEqual(false);
    expect(component.accountRequests[1].showLinks).toEqual(false);
  });

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = searchResultJs.searchResult10;
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

  it('should show account request links when expand all button clicked', () => {
    const accountRequestResult: AccountRequestSearchResult = DEFAULT_ACCOUNT_REQUEST_SEARCH_RESULT;
    component.accountRequests = [accountRequestResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-account-request-links');
    button.click();
    expect(component.accountRequests[0].showLinks).toEqual(true);
  });

  it('should show success message if successfully reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = searchResultJs.searchResult10;
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

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = searchResultJs.searchResult10;
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetInstructorAccount').mockReturnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
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

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset student google id', () => {
    const studentResult: StudentAccountSearchResult = searchResultJs.searchResult11;
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy', course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetStudentAccount').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show success message and update all keys if successfully regenerated student registration key', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
      awaitingSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

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

    expect(spyStatusMessageService).toBeCalled();

    expect(studentResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
    expect(studentResult.awaitingSessions.sessionName.feedbackSessionUrl).toEqual('awaitingSession?key=newKey');
    expect(studentResult.openSessions.sessionName.feedbackSessionUrl).toEqual('openSession?key=newKey');
    expect(studentResult.notOpenSessions.sessionName.feedbackSessionUrl).toEqual('notOpenSession?key=newKey');
    expect(studentResult.publishedSessions.sessionName.feedbackSessionUrl).toEqual('publishedSession?key=newKey');
  });

  it('should show error message if fail to regenerate registration key for student in a course', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
      awaitingSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'awaitingSession?key=oldKey',
        },
      },
      openSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'openSession?key=oldKey',
        },
      },
      notOpenSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'notOpenSession?key=oldKey',
        },
      },
      publishedSessions: {
        ...DEFAULT_FEEDBACK_SESSION_GROUP,
        sessionName: {
          ...DEFAULT_FEEDBACK_SESSION_GROUP.sessionName,
          feedbackSessionUrl: 'publishedSession?key=oldKey',
        },
      },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    jest.spyOn(studentService, 'regenerateStudentKey').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-student-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show success message and update all keys if successfully regenerated instructor registration key', () => {
    const instructorResult: InstructorAccountSearchResult = {
      ...DEFAULT_INSTRUCTOR_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

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

    expect(spyStatusMessageService).toBeCalled();

    expect(instructorResult.courseJoinLink).toEqual('courseJoinLink?key=newKey');
  });

  it('should show error message if fail to regenerate registration key for instructor in a course', () => {
    const instructorResult: InstructorAccountSearchResult = {
      ...DEFAULT_INSTRUCTOR_SEARCH_RESULT,
      courseJoinLink: 'courseJoinLink?key=oldKey',
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({});
    });

    jest.spyOn(instructorService, 'regenerateInstructorKey').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to send course join email', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      showLinks: true,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(emailGenerationService, 'getCourseJoinEmail').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const sendButton: any = fixture.debugElement.nativeElement.querySelector('#send-course-join-button');
    sendButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to send session reminder email', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      showLinks: true,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    jest.spyOn(emailGenerationService, 'getFeedbackSessionReminderEmail').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    const sendAwaitingSessionReminderButton: any =
      fixture.debugElement.nativeElement.querySelector('#send-awaiting-session-reminder-button');
    sendAwaitingSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();

    const sendOpenSessionReminderButton: any =
      fixture.debugElement.nativeElement.querySelector('#send-open-session-reminder-button');
    sendOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();

    const sendNotOpenSessionReminderButton: any =
      fixture.debugElement.nativeElement.querySelector('#send-not-open-session-reminder-button');
    sendNotOpenSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();

    const sendPublishedSessionReminderButton: any =
      fixture.debugElement.nativeElement.querySelector('#send-published-session-reminder-button');
    sendPublishedSessionReminderButton.click();

    expect(spyStatusMessageService).toBeCalled();
  });
});
