import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbCollapseModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { StudentHomePageComponent } from './student-home-page.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import {
  Courses,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  HasResponses,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { ResponseStatusPipe } from '../../pipes/session-response-status.pipe';
import { SubmissionStatusPipe } from '../../pipes/session-submission-status.pipe';

const studentCourseA: any = {
  course: {
    courseId: 'CS1231',
    courseName: 'Discrete Structures',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 1549095330000,
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
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

const studentCourseB: any = {
  course: {
    courseId: 'LSM1306',
    courseName: 'Forensic Science',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
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

const studentCourseC: any = {
  course: {
    courseId: 'MA1521',
    courseName: 'Calculus for Computing',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
    deletionTimestamp: 0,
  },
  feedbackSessions: [
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
      },
      isOpened: true,
      isWaitingToOpen: false,
      isPublished: false,
      isSubmitted: true,
    },
    {
      session: {
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
        studentDeadlines: {},
        instructorDeadlines: {},
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
      courseId: 'CS1231',
      courseName: 'Discrete Structures',
      institute: 'Test Institute',
      timeZone: 'Asia/Singapore',
      creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
      deletionTimestamp: 0,
    },
  ],
};

const studentFeedbackSessions: FeedbackSessions = {
  feedbackSessions: [
    {
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
      studentDeadlines: {},
      instructorDeadlines: {},
    },
    {
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
      studentDeadlines: {},
      instructorDeadlines: {},
    },
    {
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
      studentDeadlines: {},
      instructorDeadlines: {},
    },
  ],
};

describe('StudentHomePageComponent', () => {
  let component: StudentHomePageComponent;
  let fixture: ComponentFixture<StudentHomePageComponent>;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [StudentHomePageComponent, ResponseStatusPipe, SubmissionStatusPipe],
      imports: [
        HttpClientTestingModule,
        NgbModule,
        RouterTestingModule,
        TeammatesCommonModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesRouterModule,
        NgbCollapseModule,
        BrowserAnimationsModule,
        PanelChevronModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
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
          studentDeadlines: {},
          instructorDeadlines: {},
        },
        {
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
          studentDeadlines: {},
          instructorDeadlines: {},
        },
      ],
    };

    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: { 'First Session': false, 'Second Session': true },
    };

    jest.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions1));
    jest.spyOn(feedbackSessionsService, 'hasStudentResponseForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

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
          studentDeadlines: {},
          instructorDeadlines: {},
        },
        {
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
          studentDeadlines: {},
          instructorDeadlines: {},
        },
      ],
    };

    const hasRes: HasResponses = {
      hasResponses: false,
      hasResponsesBySession: { 'First Session': false },
    };

    jest.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions1));
    jest.spyOn(feedbackSessionsService, 'hasStudentResponseForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

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

    jest.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions));
    jest.spyOn(feedbackSessionsService, 'hasStudentResponseForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

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

    jest.spyOn(courseService, 'getAllCoursesAsStudent').mockReturnValue(of(studentCourses));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForStudent').mockReturnValue(of(studentFeedbackSessions));
    jest.spyOn(feedbackSessionsService, 'hasStudentResponseForAllFeedbackSessionsInCourse').mockReturnValue(of(hasRes));

    component.loadStudentCourses();

    expect(component.courses.length).toEqual(1);
    expect(component.courses[0].feedbackSessions.length).toEqual(3);
    expect(component.courses[0].feedbackSessions[0].session.feedbackSessionName).toEqual('Orientation Session');
    expect(component.courses[0].feedbackSessions[1].session.feedbackSessionName).toEqual('Welcome Tea Session');
    expect(component.courses[0].feedbackSessions[2].session.feedbackSessionName).toEqual('Latest update Session');
  });

  it('should disable view response button when session is not published', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000,
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const button: any = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-0');
    expect(button.textContent).toEqual(' View Responses ');
    expect(button.className).toContain('disabled');
  });

  it('should disable start submission button when session is waiting to open', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const button: any = fixture.debugElement.nativeElement.querySelector('#disabled-start-submit-btn-0');
    expect(button.textContent).toEqual(' Start Submission ');
    expect(button.className).toContain('disabled');
  });

  it('should activate start submission button when session is open and response is not submitted', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const button: any = fixture.debugElement.nativeElement.querySelector('#start-submit-btn-0');
    expect(button.textContent).toEqual(' Start Submission ');
  });

  it('should activate edit submission button when session is open and response is submitted', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const button: any = fixture.debugElement.nativeElement.querySelector('#edit-submit-btn-0');
    expect(button.textContent).toEqual(' Edit Submission ');
  });

  it('should activate view submission button when session is not open', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const button: any = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-0');
    expect(button.textContent).toEqual(' View Submission ');
  });

  it('should navigate to student course page to view the corresponding team', () => {
    const studentCourse1: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    const studentCourse2: any = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    component.courses = [studentCourse1, studentCourse2];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-team-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-team-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/course?courseid=CS3281');
    expect(href2).toEqual('/web/student/course?courseid=CS3282');
  });

  it('should navigate to student session result page to view responses', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-responses-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/result?courseid=CS1231&fsname=First%20Session');
    expect(href2).toEqual('/web/student/sessions/result?courseid=CS1231&fsname=Second%20Session');
  });

  // start/edit/view submission button share the same router link and query params
  // here we only have to test one of them
  it('should navigate to student session submission page for viewing', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS1231',
        courseName: 'Discrete Structures',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
          },
          isOpened: false,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: true,
        },
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
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

    const href1: any = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-0').getAttribute('href');
    const href2: any = fixture.debugElement.nativeElement.querySelector('#view-submit-btn-1').getAttribute('href');
    expect(href1).toEqual('/web/student/sessions/submission?courseid=CS1231&fsname=First%20Session');
    expect(href2).toEqual('/web/student/sessions/submission?courseid=CS1231&fsname=Second%20Session');
  });

  it('should sort courses by their IDs', () => {
    component.courses = [studentCourseB, studentCourseC, studentCourseA];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-id-btn');
    button.click();

    expect(component.courses[0].course.courseId).toEqual(studentCourseA.course.courseId);
    expect(component.courses[1].course.courseId).toEqual(studentCourseB.course.courseId);
    expect(component.courses[2].course.courseId).toEqual(studentCourseC.course.courseId);
  });

  it('should sort courses by their names', () => {
    component.courses = [studentCourseA, studentCourseB, studentCourseC];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-name-btn');
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
    const studentCourse: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };
    component.courses = [studentCourse];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no feedback session over 2 courses', () => {
    const studentCourse1: any = {
      course: {
        courseId: 'CS3281',
        courseName: 'Thematic Systems I',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    const studentCourse2: any = {
      course: {
        courseId: 'CS3282',
        courseName: 'Thematic Systems II',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [],
    };

    component.courses = [studentCourse1, studentCourse2];
    component.isCoursesLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with feedback sessions', () => {
    const studentCourse: any = {
      course: {
        courseId: 'CS2103',
        courseName: 'Software Engineering',
        timeZone: 'Asia/Singapore',
        creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
        deletionTimestamp: 0,
      },
      feedbackSessions: [
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: true,
          isSubmitted: true,
        },
        {
          session: {
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
            studentDeadlines: {},
            instructorDeadlines: {},
          },
          isOpened: true,
          isWaitingToOpen: false,
          isPublished: false,
          isSubmitted: false,
        },
      ],
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
