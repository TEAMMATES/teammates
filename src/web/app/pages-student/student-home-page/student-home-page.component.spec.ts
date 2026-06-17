import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { StudentHomePageComponent } from './student-home-page.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import {
  Course,
  Courses,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  HasResponses,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { FormatDateDetailPipe } from '../../components/teammates-common/format-date-detail.pipe';

interface StudentSession {
  session: FeedbackSession;
  isOpened: boolean;
  isWaitingToOpen: boolean;
  isPublished: boolean;
  isSubmitted: boolean;
}

interface StudentCourse {
  course: Course;
  feedbackSessions: StudentSession[];
  isFeedbackSessionsLoading: boolean;
  hasFeedbackSessionsLoadingFailed: boolean;
  isTabExpanded: boolean;
  hasPopulated: boolean;
}

const studentCourseA: StudentCourse = {
  course: {
    courseId: 'CS1231',
    courseName: 'Discrete Structures',
    timeZone: 'Asia/Singapore',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    creationTimestamp: 1549095330000,
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-001',
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
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-002',
        feedbackSessionName: 'Second Session',
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
      },
      isOpened: false,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
  ],
  isFeedbackSessionsLoading: false,
  hasFeedbackSessionsLoadingFailed: false,
  isTabExpanded: true,
  hasPopulated: true,
};

const studentCourseB: StudentCourse = {
  course: {
    courseId: 'LSM1306',
    courseName: 'Forensic Science',
    timeZone: 'Asia/Singapore',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-003',
        feedbackSessionName: 'Third Session',
        courseId: 'LSM1306',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-004',
        feedbackSessionName: 'Fourth Session',
        courseId: 'LSM1306',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
      isOpened: false,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
  ],
  isFeedbackSessionsLoading: false,
  hasFeedbackSessionsLoadingFailed: false,
  isTabExpanded: true,
  hasPopulated: true,
};

const studentCourseC: StudentCourse = {
  course: {
    courseId: 'MA1521',
    courseName: 'Calculus for Computing',
    timeZone: 'Asia/Singapore',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-005',
        feedbackSessionName: 'Fifth Session',
        courseId: 'MA1521',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
        feedbackSessionId: 'test-feedback-session-id-006',
        feedbackSessionName: 'Sixth Session',
        courseId: 'MA1521',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
      isOpened: false,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
  ],
  isFeedbackSessionsLoading: false,
  hasFeedbackSessionsLoadingFailed: false,
  isTabExpanded: true,
  hasPopulated: true,
};

const studentCourses: Courses = {
  courses: [
    {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
    },
  ],
};

const studentFeedbackSessions: FeedbackSessions = {
  feedbackSessions: [
    {
      feedbackSession: {
        feedbackSessionId: 'test-feedback-session-id-007',
        feedbackSessionName: 'Latest update Session',
        courseId: 'CS1231',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1611392191000, // Saturday, 23 January 2021 16:56:31 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
    },
    {
      feedbackSession: {
        feedbackSessionId: 'test-feedback-session-id-008',
        feedbackSessionName: 'Orientation Session',
        courseId: 'CS1231',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
    },
    {
      feedbackSession: {
        feedbackSessionId: 'test-feedback-session-id-009',
        feedbackSessionName: 'Welcome Tea Session',
        courseId: 'CS1231',
        timeZone: 'Asia/Singapore',
        instructions: '',
        submissionStartTimestamp: 0,
        submissionEndTimestamp: 1579769791000, // Thursday, 23 January 2020 16:56:31 GMT+08:00
        gracePeriod: 0,
        sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
        responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
        submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
        publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
        isClosingSoonEmailEnabled: true,
        isPublishedEmailEnabled: true,
        createdAtTimestamp: 0,
      },
    },
  ],
};

describe('StudentHomePageComponent', () => {
  let component: StudentHomePageComponent;
  let fixture: ComponentFixture<StudentHomePageComponent>;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [FormatDateDetailPipe, provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentHomePageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the courses and feedback sessions involving the student', () => {
    const studentFeedbackSessions1: FeedbackSessions = {
      feedbackSessions: [
        {
          feedbackSession: {
            feedbackSessionId: 'test-feedback-session-id-010',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
        },
        {
          feedbackSession: {
            feedbackSessionId: 'test-feedback-session-id-011',
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 1,
            submissionEndTimestamp: 1549095331000, // Saturday, 2 February 2019 16:15:31 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
        },
      ],
    };

    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: { 'First Session': false, 'Second Session': true },
    };

    vi.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions1));
    vi.spyOn(feedbackSessionsService, 'hasResponsesForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

    component.loadStudentCourses();

    expect(component.courses.length).toEqual(1);
    expect(component.courses[0].course.courseId).toEqual('CS1231');
    expect(component.courses[0].course.courseName).toEqual('Discrete Structures');
    expect(component.courses[0].feedbackSessions[0].session.feedbackSessionName).toEqual('First Session');
    expect(component.isCoursesLoading).toBeFalsy();
  });

  it('should load the courses and feedback sessions but fail if sessions are not loaded correctly', () => {
    const studentFeedbackSessions1: FeedbackSessions = {
      feedbackSessions: [
        {
          feedbackSession: {
            feedbackSessionId: 'test-feedback-session-id-012',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
        },
        {
          feedbackSession: {
            feedbackSessionId: 'test-feedback-session-id-013',
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 1,
            submissionEndTimestamp: 1549095331000, // Saturday, 2 February 2019 16:15:31 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
        },
      ],
    };

    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: { 'First Session': false },
    };

    vi.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions1));
    vi.spyOn(feedbackSessionsService, 'hasResponsesForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

    component.loadStudentCourses();

    expect(component.courses[0].hasFeedbackSessionsLoadingFailed).toBeTruthy();
  });

  it('should sort feedback sessions first by createdAtTimestamp upon loading', () => {
    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: {
        'Orientation Session': false,
        'Welcome Tea Session': false,
        'Latest update Session': false,
      },
    };

    vi.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions));
    vi.spyOn(feedbackSessionsService, 'hasResponsesForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

    component.loadStudentCourses();

    expect(component.courses.length).toEqual(1);
    expect(component.courses[0].feedbackSessions.length).toEqual(3);
    expect(component.courses[0].feedbackSessions[0].session.feedbackSessionName).toEqual('Orientation Session');
    expect(component.courses[0].feedbackSessions[1].session.feedbackSessionName).toEqual('Welcome Tea Session');
    expect(component.courses[0].feedbackSessions[2].session.feedbackSessionName).toEqual('Latest update Session');
  });

  it('should sort feedback sessions by submissionEndTimestamp, when createdAtTimestamps are equal', () => {
    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: {
        'Orientation Session': false,
        'Welcome Tea Session': false,
        'Latest update Session': false,
      },
    };

    vi.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions));
    vi.spyOn(feedbackSessionsService, 'hasResponsesForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

    component.loadStudentCourses();

    expect(component.courses.length).toEqual(1);
    expect(component.courses[0].feedbackSessions.length).toEqual(3);
    expect(component.courses[0].feedbackSessions[0].session.feedbackSessionName).toEqual('Orientation Session');
    expect(component.courses[0].feedbackSessions[1].session.feedbackSessionName).toEqual('Welcome Tea Session');
    expect(component.courses[0].feedbackSessions[2].session.feedbackSessionName).toEqual('Latest update Session');
  });

  it('should disable view response button when session is not published', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-014',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-0');
    expect(button.textContent).toEqual(' View Responses ');
    expect(button.className).toContain('disabled');
  });

  it('should disable start submission button when session is waiting to open', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-015',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: true,
          isPublished: true,
          isSubmitted: false,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#disabled-start-submit-btn-0');
    expect(button.textContent).toEqual(' Start Submission ');
    expect(button.className).toContain('disabled');
  });

  it('should activate start submission button when session is open and response is not submitted', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-016',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: false,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#start-submit-btn-0');
    expect(button.textContent).toEqual(' Start Submission ');
  });

  it('should activate edit submission button when session is open and response is submitted', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-017',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#edit-submit-btn-0');
    expect(button.textContent).toEqual(' Edit Submission ');
  });

  it('should activate view submission button when session is not open', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-018',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-0');
    expect(button.textContent).toEqual(' View Submission ');
  });

  it('should navigate to student course page to view the corresponding team', () => {
    const studentCourse1: StudentCourse = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };

    const studentCourse2: StudentCourse = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };

    component.courses = [studentCourse1, studentCourse2];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-team-btn-0')
      .getAttribute('href');
    const href2: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-team-btn-1')
      .getAttribute('href');
    expect(href1).toEqual('/web/student/courses/CS3281');
    expect(href2).toEqual('/web/student/courses/CS3282');
  });

  it('should navigate to student session result page to view responses', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'fs-id-1',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionId: 'fs-id-2',
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: true,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-responses-btn-0')
      .getAttribute('href');
    const href2: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-responses-btn-1')
      .getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/fs-id-1/result');
    expect(href2).toEqual('/web/student/sessions/fs-id-2/result');
  });

  // start/edit/view submission button share the same router link and query params
  // here we only have to test one of them
  it('should navigate to student session submission page for viewing', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'fs-id-1',
            feedbackSessionName: 'First Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionId: 'fs-id-2',
            feedbackSessionName: 'Second Session',
            courseId: 'CS1231',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: true,
      hasPopulated: true,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-submit-btn-0')
      .getAttribute('href');
    const href2: HTMLElement = fixture.debugElement.nativeElement
      .querySelector('#view-submit-btn-1')
      .getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/fs-id-1/submission');
    expect(href2).toEqual('/web/student/sessions/fs-id-2/submission');
  });

  it('should sort courses by their IDs', () => {
    component.courses = [studentCourseB, studentCourseC, studentCourseA];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#sort-course-id-btn');
    button.click();

    expect(component.courses[0].course.courseId).toEqual(studentCourseA.course.courseId);
    expect(component.courses[1].course.courseId).toEqual(studentCourseB.course.courseId);
    expect(component.courses[2].course.courseId).toEqual(studentCourseC.course.courseId);
  });

  it('should sort courses by their names', () => {
    component.courses = [studentCourseA, studentCourseB, studentCourseC];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#sort-course-name-btn');
    button.click();

    expect(component.courses[0].course.courseId).toEqual(studentCourseC.course.courseId);
    expect(component.courses[1].course.courseId).toEqual(studentCourseA.course.courseId);
    expect(component.courses[2].course.courseId).toEqual(studentCourseB.course.courseId);
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no course', () => {
    component.courses = [];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback sessions', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };
    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback session over 2 courses', () => {
    const studentCourse1: StudentCourse = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };

    const studentCourse2: StudentCourse = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };

    component.courses = [studentCourse1, studentCourse2];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback sessions', () => {
    const studentCourse: StudentCourse = {
      course: {
        courseId: 'CS2103',
        courseName: 'Software Engineering',
        timeZone: 'Asia/Singapore',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-019',
            feedbackSessionName: 'First Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
            feedbackSessionId: 'test-feedback-session-id-020',
            feedbackSessionName: 'Second Session',
            courseId: 'CS2103',
            timeZone: 'Asia/Singapore',
            instructions: '',
            submissionStartTimestamp: 0,
            submissionEndTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
            gracePeriod: 0,
            sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
            responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
            submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
            publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
            isClosingSoonEmailEnabled: true,
            isPublishedEmailEnabled: true,
            createdAtTimestamp: 0,
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: false,
        },
      ],
      isFeedbackSessionsLoading: false,
      hasFeedbackSessionsLoadingFailed: false,
      isTabExpanded: false,
      hasPopulated: false,
    };

    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all feedback sessions over 2 courses', () => {
    component.courses = [studentCourseA, studentCourseB];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isCoursesLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there is course loading failed', () => {
    component.isCoursesLoading = false;
    component.hasCoursesLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
