import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { InstructorSessionsPageComponent } from './instructor-sessions-page.component';
import { InstructorSessionsPageModule } from './instructor-sessions-page.module';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course, Courses, FeedbackSession,
  FeedbackSessionPublishStatus, FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting, SessionVisibleSetting,
} from '../../../types/api-output';

describe('InstructorSessionsPageComponent', () => {
  let component: InstructorSessionsPageComponent;
  let courseService: CourseService;
  let fixture: ComponentFixture<InstructorSessionsPageComponent>;
  let sessionService: FeedbackSessionsService;
  let timezoneService: TimezoneService;
  let ngbModal: NgbModal;

  const testCourse1: Course = {
    courseId: 'CS1231',
    courseName: 'Discrete Structures',
    institute: 'Test Institute',
    creationTimestamp: 1549095330000, // Saturday, 2 February 2019 16:15:30 GMT+08:00
    deletionTimestamp: 0,
    timeZone: 'Asia/Singapore',
  };

  const testCourse2: Course = {
    courseId: 'CS3281',
    courseName: 'Thematic Systems I',
    institute: 'Test Institute',
    creationTimestamp: 1611580917000, // Monday, 25 January 2021 21:21:57 GMT+08:00
    deletionTimestamp: 0,
    timeZone: 'Asia/Singapore',
  };

  const testFeedbackSession1: FeedbackSession = {
    feedbackSessionName: 'First Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1610371317000, // Monday, 11 January 2021 21:21:57 GMT+08:00
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
  };

  const testFeedbackSession2: FeedbackSession = {
    feedbackSessionName: 'Second Session',
    courseId: 'CS3281',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1611148917000, // Wednesday, 20 January 2021 21:21:57 GMT+08:00
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
  };

  const testFeedbackSession3: FeedbackSession = {
    feedbackSessionName: 'Third Session',
    courseId: 'CS1231',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1611148917000, // Wednesday, 20 January 2021 21:21:57 GMT+08:00
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    deletedAtTimestamp: 1611580917000, // Monday, 25 January 2021 21:21:57 GMT+08:00
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const testFeedbackSession4: FeedbackSession = {
    feedbackSessionName: 'Fourth Session',
    courseId: 'CS3281',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1611148917000, // Wednesday, 20 January 2021 21:21:57 GMT+08:00
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    deletedAtTimestamp: 1612958400, // Wednesday, February 10, 2021 20:00:00 GMT+08:00
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const testTutorPrivilege: any = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canSubmitSessionInSections: false,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        InstructorSessionsPageModule,
        BrowserAnimationsModule,
      ],
      providers: [NgbModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionsPageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    timezoneService = TestBed.inject(TimezoneService);
    sessionService = TestBed.inject(FeedbackSessionsService);
    ngbModal = TestBed.inject(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load courses of the current instructor', () => {
    const activeCourses: Courses = {
      courses: [testCourse1, testCourse2],
    };

    jest.spyOn(courseService, 'getInstructorCoursesThatAreActive').mockReturnValue(of(activeCourses));
    component.loadCandidatesCourse();

    expect(component.courseCandidates[0].courseId).toEqual('CS1231');
    expect(component.courseCandidates[0].courseName).toEqual('Discrete Structures');
    expect(component.courseCandidates[1].courseId).toEqual('CS3281');
    expect(component.courseCandidates[1].courseName).toEqual('Thematic Systems I');
    expect(component.courseCandidates.length).toEqual(2);
  });

  it('should load default values for session edit form', () => {
    component.courseCandidates = [testCourse1, testCourse2];
    component.initDefaultValuesForSessionEditForm();

    expect(component.sessionEditFormModel.courseId).toEqual('CS1231');
    expect(component.sessionEditFormModel.courseName).toEqual('Discrete Structures');
    expect(component.sessionEditFormModel.timeZone).toEqual('Asia/Singapore');
  });

  it('should load all sessions by the instructor', () => {
    const courseSessions: FeedbackSessions = {
      feedbackSessions: [testFeedbackSession1, testFeedbackSession2],
    };
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'getFeedbackSessionsForInstructor')
      .mockReturnValue(of(courseSessions));

    component.loadFeedbackSessions();

    expect(sessionSpy).toHaveBeenCalledTimes(1);

    expect(component.sessionsTableRowModels.length).toEqual(2);
    expect(component.sessionsTableRowModels[0].feedbackSession.courseId).toEqual('CS1231');
    expect(component.sessionsTableRowModels[0].feedbackSession.feedbackSessionName).toEqual('First Session');
    expect(component.sessionsTableRowModels[1].feedbackSession.courseId).toEqual('CS3281');
    expect(component.sessionsTableRowModels[1].feedbackSession.feedbackSessionName).toEqual('Second Session');
  });

  it('should load all feedback sessions in recycle bin that can be accessed by instructor', () => {
    const recycleBinSessions: FeedbackSessions = {
      feedbackSessions: [testFeedbackSession3, testFeedbackSession4],
    };
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'getFeedbackSessionsInRecycleBinForInstructor')
      .mockReturnValue(of(recycleBinSessions));

    component.loadRecycleBinFeedbackSessions();

    expect(sessionSpy).toHaveBeenCalledTimes(1);

    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(2);
    expect(component.recycleBinFeedbackSessionRowModels[0].feedbackSession.courseId).toEqual('CS3281');
    expect(component.recycleBinFeedbackSessionRowModels[0].feedbackSession.feedbackSessionName)
      .toEqual('Fourth Session');
    expect(component.recycleBinFeedbackSessionRowModels[1].feedbackSession.courseId).toEqual('CS1231');
    expect(component.recycleBinFeedbackSessionRowModels[1].feedbackSession.feedbackSessionName)
      .toEqual('Third Session');
  });

  it('should recycle an active session', () => {
    const sessionsTableRowModel1: any = {
      feedbackSession: testFeedbackSession1,
      instructorPrivilege: testTutorPrivilege,
      isLoadingResponseRate: false,
    };
    const sessionsTableRowModel2: any = {
      feedbackSession: testFeedbackSession2,
      instructorPrivilege: testTutorPrivilege,
      isLoadingResponseRate: false,
    };
    component.sessionsTableRowModels = [sessionsTableRowModel1, sessionsTableRowModel2];
    component.recycleBinFeedbackSessionRowModels = [];
    const courseSpy: SpyInstance = jest.spyOn(sessionService, 'moveSessionToRecycleBin')
      .mockReturnValue(of(testFeedbackSession1));
    component.moveSessionToRecycleBinEventHandler(0);

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenLastCalledWith('CS1231', 'First Session');

    expect(component.sessionsTableRowModels.length).toEqual(1);
    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(1);
    expect(component.recycleBinFeedbackSessionRowModels[0].feedbackSession.courseId).toEqual('CS1231');
  });

  it('should restore a session', () => {
    const recycleBinFeedbackSessionRowModel1: any = {
      feedbackSession: testFeedbackSession3,
    };
    component.recycleBinFeedbackSessionRowModels = [recycleBinFeedbackSessionRowModel1];
    component.sessionsTableRowModels = [];
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'deleteSessionFromRecycleBin')
      .mockReturnValue(of(testFeedbackSession3));

    component.restoreRecycleBinFeedbackSession(recycleBinFeedbackSessionRowModel1);
    expect(sessionSpy).toHaveBeenCalledTimes(1);
    expect(sessionSpy).toHaveBeenLastCalledWith('CS1231', 'Third Session');
    expect(component.sessionsTableRowModels.length).toEqual(1);
    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(0);
  });

  it('should restore all sessions', () => {
    const recycleBinFeedbackSessionRowModel1: any = {
      feedbackSession: testFeedbackSession3,
    };
    const recycleBinFeedbackSessionRowModel2: any = {
      feedbackSession: testFeedbackSession4,
    };
    component.recycleBinFeedbackSessionRowModels =
      [recycleBinFeedbackSessionRowModel1, recycleBinFeedbackSessionRowModel2];
    component.sessionsTableRowModels = [];
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'deleteSessionFromRecycleBin')
    .mockImplementation((_courseId: string, feedbackSessionName: string) => {
      if (feedbackSessionName === testFeedbackSession3.feedbackSessionName) {
        return of(testFeedbackSession3);
      }
      return of(testFeedbackSession4);
    });

    component.restoreAllRecycleBinFeedbackSession();
    expect(sessionSpy).toHaveBeenCalledTimes(2);
    expect(component.sessionsTableRowModels.length).toEqual(2);
    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(0);
  });

  it('should permanently delete a session', async () => {
    const recycleBinFeedbackSessionRowModel1: any = {
      feedbackSession: testFeedbackSession3,
    };
    const promise: Promise<any> = Promise.resolve();
    const mockModalRef: any = createMockNgbModalRef({
      feedbackSessionName: 'Third Session',
      courseId: 'CS1231',
    }, promise);
    component.recycleBinFeedbackSessionRowModels = [recycleBinFeedbackSessionRowModel1];
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'deleteFeedbackSession')
      .mockReturnValue(of(testFeedbackSession3));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.permanentDeleteSession(recycleBinFeedbackSessionRowModel1);
    await promise;

    expect(ngbModal.open).toHaveBeenCalledWith(SessionPermanentDeletionConfirmModalComponent);
    expect(sessionSpy).toHaveBeenCalledTimes(1);
    expect(sessionSpy).toHaveBeenLastCalledWith('CS1231', 'Third Session');

    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(0);
  });

  it('should permanently delete all sessions', async () => {
    const recycleBinFeedbackSessionRowModel1: any = {
      feedbackSession: testFeedbackSession3,
    };
    const recycleBinFeedbackSessionRowModel2: any = {
      feedbackSession: testFeedbackSession4,
    };
    const promise: Promise<any> = Promise.resolve();
    const mockModalRef: any = createMockNgbModalRef({
      sessionsToDelete: [testFeedbackSession3, testFeedbackSession4],
    }, promise);
    component.recycleBinFeedbackSessionRowModels =
      [recycleBinFeedbackSessionRowModel1, recycleBinFeedbackSessionRowModel2];
    const sessionSpy: SpyInstance = jest.spyOn(sessionService, 'deleteFeedbackSession')
    .mockImplementation((_courseId: string, feedbackSessionName: string) => {
      if (feedbackSessionName === testFeedbackSession3.feedbackSessionName) {
        return of(testFeedbackSession3);
      }
      return of(testFeedbackSession4);
    });
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.permanentDeleteAllSessions();
    await promise;

    expect(ngbModal.open).toHaveBeenCalledWith(SessionsPermanentDeletionConfirmModalComponent);
    expect(sessionSpy).toHaveBeenCalledTimes(2);
    expect(sessionSpy).toHaveBeenLastCalledWith('CS3281', 'Fourth Session');
    expect(component.recycleBinFeedbackSessionRowModels.length).toEqual(0);
  });

  it('should show add session form and disable button when clicking on add new session', () => {
    component.courseCandidates = [testCourse1, testCourse2];
    component.isCoursesLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-add-session');
    button.click();
    fixture.detectChanges();

    const div: any = fixture.debugElement.nativeElement.querySelector('#add-session-section');
    expect(div).toBeTruthy();
    expect(button.disabled).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with active sessions', () => {
    component.sessionsTableRowModels = [
      {
        feedbackSession: testFeedbackSession1,
        instructorPrivilege: testTutorPrivilege,
        isLoadingResponseRate: false,
        responseRate: '',
      },
      {
        feedbackSession: testFeedbackSession2,
        instructorPrivilege: testTutorPrivilege,
        isLoadingResponseRate: false,
        responseRate: '',
      },
    ];
    component.isFeedbackSessionsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback sessions are loading', () => {
    component.isFeedbackSessionsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are loading', () => {
    component.isCoursesLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback sessions failed to load', () => {
    component.hasFeedbackSessionLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when new session form is expanded', () => {
    component.isSessionEditFormExpanded = true;
    component.isCoursesLoading = false;
    // Mock the timezone service to prevent unexpected changes in time zones over time, such as daylight savings time
    const timezones: Record<string, number> = {
      Jamaica: -5 * 60,
      Portugal: 0,
      Singapore: 8 * 60,
      Turkey: 3 * 60,
    };
    jest.spyOn(timezoneService, 'getTzOffsets').mockReturnValue(timezones);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when recycle bin section is expanded', () => {
    component.recycleBinFeedbackSessionRowModels = [
      {
        feedbackSession: testFeedbackSession3,
      },
      {
        feedbackSession: testFeedbackSession4,
      },
    ];
    component.isRecycleBinExpanded = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

});
