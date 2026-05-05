import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminInstructorSearchTableComponent } from './admin-instructor-search-table.component';
import { AccountService } from '../../../../services/account.service';
import { InstructorService } from '../../../../services/instructor.service';
import { FeedbackSessionsGroup, InstructorAccountSearchResult } from '../../../../services/search.service';
import { StatusMessageService } from '../../../../services/status-message.service';
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

const DEFAULT_INSTRUCTOR_SEARCH_RESULT: InstructorAccountSearchResult = {
  userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
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

describe('AdminInstructorSearchTableComponent', () => {
  let component: AdminInstructorSearchTableComponent;
  let fixture: ComponentFixture<AdminInstructorSearchTableComponent>;
  let accountService: AccountService;
  let instructorService: InstructorService;
  let statusMessageService: StatusMessageService;
  let ngbModal: NgbModal;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminInstructorSearchTableComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    instructorService = TestBed.inject(InstructorService);
    statusMessageService = TestBed.inject(StatusMessageService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with a deleted course', () => {
    component.instructors = [
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
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

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded instructor table', () => {
    component.instructors = [
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
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

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = {
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

  it('should show success message if successfully reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
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
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy',
        course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetInstructorAccount').mockReturnValue(
      of({
        message: 'Success',
      }),
    );
    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual("The instructor's Google ID has been reset.");
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should show error message if fail to reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
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
      awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    jest.spyOn(ngbModal, 'open').mockImplementation(() => {
      return createMockNgbModalRef({
        name: 'dummy',
        course: 'dummy',
      });
    });

    jest.spyOn(accountService, 'resetInstructorAccount').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message',
        },
      })),
    );

    const spyStatusMessageService: SpyInstance = jest
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

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

    jest.spyOn(instructorService, 'regenerateInstructorKey').mockReturnValue(
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

    jest.spyOn(instructorService, 'regenerateInstructorKey').mockReturnValue(
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

    const regenerateButton: any = fixture.debugElement.nativeElement.querySelector('#regenerate-instructor-key-0');
    regenerateButton.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });
});
