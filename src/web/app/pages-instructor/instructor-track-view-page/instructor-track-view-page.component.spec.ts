import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';

import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionLog,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  LogType,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { ColumnData } from '../../components/sortable-table/sortable-table.component';

import { InstructorTrackViewPageComponent } from './instructor-track-view-page.component';
import { InstructorTrackViewPageModule } from './instructor-track-view-page.module';
import Spy = jasmine.Spy;

describe('InstructorTrackViewPageComponent', () => {
  let component: InstructorTrackViewPageComponent;
  let fixture: ComponentFixture<InstructorTrackViewPageComponent>;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let studentService: StudentService;
  let statusMessageService: StatusMessageService;
  let timezoneService: TimezoneService;
  let logService: LogService;

  const resultColumns: ColumnData[] = [
    { header: 'Status', sortBy: SortBy.RESULT_VIEW_STATUS },
    { header: 'Name', sortBy: SortBy.GIVER_NAME },
    { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
    { header: 'Section', sortBy: SortBy.SECTION_NAME },
    { header: 'Team', sortBy: SortBy.TEAM_NAME },
  ];

  const testCourse1: Course = {
    courseId: 'CS1234',
    courseName: 'test-course1',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };

  const testCourse2: Course = {
    courseId: 'MA1234',
    courseName: 'test-course2',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };

  const testFeedbackSessionPublished: FeedbackSession = {
    feedbackSessionName: 'First Session',
    courseId: 'CS1234',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1549095330000,
    resultVisibleFromTimestamp: Date.now(),
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testFeedbackSessionNotPublished: FeedbackSession = {
    feedbackSessionName: 'Second Session',
    courseId: 'MA1234',
    timeZone: 'Asia/Singapore',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 1549095330000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: false,
    createdAtTimestamp: 0,
  };

  const testStudent: Student = {
    email: 'doejohn@email.com',
    courseId: 'CS1234',
    name: 'Doe John',
    teamName: 'team 1',
    sectionName: 'section 1',
  };

  const testLogs: FeedbackSessionLog = {
    feedbackSessionData: testFeedbackSessionPublished,
    feedbackSessionLogEntries: [
      {
        studentData: testStudent,
        feedbackSessionLogType: LogType.FEEDBACK_SESSION_VIEW_RESULT,
        timestamp: 0,
      },
      {
        studentData: testStudent,
        feedbackSessionLogType: LogType.FEEDBACK_SESSION_VIEW_RESULT,
        timestamp: 1000,
      },
    ],
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorTrackViewPageModule,
        NgbModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorTrackViewPageComponent);
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    studentService = TestBed.inject(StudentService);
    statusMessageService = TestBed.inject(StatusMessageService);
    timezoneService = TestBed.inject(TimezoneService);
    logService = TestBed.inject(LogService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when page is still loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when searching for details in search form', () => {
    component.courses = [testCourse1, testCourse2];
    component.formModel = {
      courseId: 'CS1234',
      feedbackSessionName: 'First Session',
    };
    component.courseToFeedbackSession = {
      CS1234: [testFeedbackSessionPublished],
      MA1234: [],
    };
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with results of a search', () => {
    component.searchResult = {
      courseId: 'CS1234',
      feedbackSessionName: 'First Session',
      publishedDate: 'Thu, 27 May, 2021 12:00:00 PM',
      logColumnsData: resultColumns,
      logRowsData: [[
        { value: 'Sun, 30 May, 2021 12:30:00 PM' },
        { value: 'Doe John' },
        { value: 'doejohn@email.com' },
        { value: 'section 1' },
        { value: 'team 1' },
      ]],
    };
    component.isLoading = false;
    component.isSearching = false;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should load all courses and feedback sessions that instructor has on init', () => {
    const courseSpy: Spy = spyOn(courseService, 'getAllCoursesAsInstructor').and
        .returnValue(of({ courses: [testCourse1, testCourse2] }));
    const feedbackSessionSpy: Spy = spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor').and
        .returnValue(of({ feedbackSessions: [testFeedbackSessionPublished] }));

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
    expect(courseSpy).toBeCalledWith('active');
    expect(component.courses.length).toEqual(2);
    expect(component.courseToFeedbackSession[testCourse1.courseId][0]).toEqual(testFeedbackSessionPublished);
    expect(feedbackSessionSpy).toHaveBeenNthCalledWith(1, testCourse1.courseId);
    expect(feedbackSessionSpy).toHaveBeenNthCalledWith(2, testCourse2.courseId);
  });

  it('should load students and search for logs when view button is clicked', () => {
    spyOn(feedbackSessionsService, 'getFeedbackSession').and.returnValue(of(testFeedbackSessionPublished));
    spyOn(studentService, 'getStudentsFromCourse').and.returnValue(of({ students: [testStudent] }));
    const logSpy: Spy = spyOn(logService, 'searchFeedbackSessionLog').and
        .returnValue(of({ feedbackSessionLogs: [testLogs] }));
    const timezoneSpy: Spy = spyOn(timezoneService, 'getResolvedTimestamp').and
        .returnValue(of({ timestamp: 0, message: '' }));

    component.isLoading = false;
    component.isSearching = false;
    component.students = [];
    component.formModel = {
      courseId: testCourse1.courseId,
      feedbackSessionName: testFeedbackSessionPublished.feedbackSessionName,
    };
    component.courseToFeedbackSession = { CS1234: [testFeedbackSessionPublished] };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#view-button').click();

    expect(timezoneSpy).toHaveBeenCalledTimes(2);
    expect(component.students.length).toEqual(1);
    expect(component.students[0]).toEqual(testStudent);
    expect(logSpy).toHaveBeenCalled();
    expect(logSpy).toHaveBeenCalledWith({
      courseId: testCourse1.courseId,
      searchFrom: '0',
      searchUntil: '0',
      sessionName: testFeedbackSessionPublished.feedbackSessionName,
    });
  });

  it('should display error message if feedback session is not published', () => {
    spyOn(feedbackSessionsService, 'getFeedbackSession').and.returnValue(of(testFeedbackSessionNotPublished));
    const messageSpy: Spy = spyOn(statusMessageService, 'showErrorToast');

    component.isLoading = false;
    component.isSearching = false;
    component.students = [];
    component.formModel = {
      courseId: testCourse2.courseId,
      feedbackSessionName: testFeedbackSessionNotPublished.feedbackSessionName,
    };
    component.courseToFeedbackSession = { MA1234: [testFeedbackSessionNotPublished] };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#view-button').click();

    expect(messageSpy).lastCalledWith('This feedback session is not published');
  });
});
