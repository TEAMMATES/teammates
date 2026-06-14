import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { InstructorStudentActivityLogsComponent } from './instructor-student-activity-logs.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { LogService } from '../../../services/log.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  CourseView,
  FeedbackSession,
  FeedbackSessionLogType,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { Milliseconds } from '../../../types/datetime-const';
import { SortBy } from '../../../types/sort-properties';
import { ColumnData } from '../../components/sortable-table/sortable-table.component';

describe('InstructorStudentActivityLogsComponent', () => {
  let component: InstructorStudentActivityLogsComponent;
  let fixture: ComponentFixture<InstructorStudentActivityLogsComponent>;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let studentService: StudentService;
  let logService: LogService;
  let timezoneService: TimezoneService;

  const LOGS_DATE_TIME_FORMAT = 'ddd, DD MMM YYYY hh:mm:ss A';
  const resultColumns: ColumnData[] = [
    { header: 'Status', sortBy: SortBy.RESULT_VIEW_STATUS },
    { header: 'Name', sortBy: SortBy.GIVER_NAME },
    { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
    { header: 'Section', sortBy: SortBy.SECTION_NAME },
    { header: 'Team', sortBy: SortBy.TEAM_NAME },
  ];
  const testCourse1: Course = {
    courseId: 'CS9999',
    courseName: 'CS9999',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };
  const testCourseView1: CourseView = {
    course: testCourse1,
  };
  const emptyStudent: Student = {
    courseId: '',
    email: '',
    name: '',
    sectionName: '',
    sectionId: '',
    teamName: '',
    teamId: '',
    userId: '',
    institute: '',
    courseName: '',
  };
  const testStudent: Student = {
    userId: '00000000-0000-4000-8000-000000000001',
    email: 'doejohn@email.com',
    courseId: 'CS9999',
    name: 'Doe John',
    teamName: 'team 1',
    teamId: 'team-1',
    sectionName: 'section 1',
    sectionId: 'section-1',
    institute: 'Test Institute',
    courseName: 'CS9999',
  };
  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: '00000000-0000-4000-8000-000000000001',
    feedbackSessionName: 'Feedback Session 1',
    courseId: 'CS9999',
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
  const testLogs1 = [
    {
      feedbackSessionLogId: '00000000-0000-4000-8000-000000000001',
      user: testStudent,
      feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
      timestamp: 0,
    },
  ];
  const testLogs2 = [
    {
      feedbackSessionLogId: '00000000-0000-4000-8000-000000000002',
      user: testStudent,
      feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
      timestamp: 0,
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ courseid: testCourse1.courseId }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorStudentActivityLogsComponent);
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    studentService = TestBed.inject(StudentService);
    logService = TestBed.inject(LogService);
    timezoneService = TestBed.inject(TimezoneService);

    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView1));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor').mockReturnValue(
      of({ feedbackSessions: [{ feedbackSession: testFeedbackSession }] }),
    );
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(
      of({
        students: [testStudent],
      }),
    );

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load form data after initialization', () => {
    const searchButton: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('#search-button');

    expect(component.isLoading).toBe(false);
    expect(component.course.courseId).toEqual(testCourse1.courseId);
    expect(component.students).toHaveLength(2);
    expect(component.feedbackSessions.size).toEqual(1);
    expect(searchButton.disabled).toBe(false);
  });

  it('should show loading indicator when page is loading', () => {
    component.isLoading = true;
    fixture.detectChanges();

    const loadingContainer = fixture.debugElement.nativeElement.querySelector('.loading-container');
    expect(loadingContainer).toBeTruthy();
  });

  it('should disable search button while loading', () => {
    component.course = testCourse1;
    component.formModel = {
      logsDateFrom: { year: 1997, month: 9, day: 11 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 1998, month: 9, day: 11 },
      logsTimeTo: { hour: 15, minute: 0 },
      selectedUserId: 'doe-john',
      logTypes: [FeedbackSessionLogType.SUBMISSION, FeedbackSessionLogType.ACCESS],
      selectedSessionId: '',
      showActions: false,
      showInactions: false,
    };
    component.students = [testStudent];
    component.isLoading = true;
    fixture.detectChanges();

    const searchButton: HTMLButtonElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    expect(searchButton).toBeFalsy();
  });

  it('should render search results', () => {
    component.searchResults = [
      {
        feedbackSessionName: 'Feedback session 1',
        logColumnsData: resultColumns,
        logRowsData: [
          [
            { value: '15 January 2021' },
            { value: 'Doe John' },
            { value: 'Viewed the submission page' },
            { value: 'doejohn@email.com' },
            { value: 'section 1' },
            { value: 'team 1' },
          ],
        ],
        isTabExpanded: true,
      },
      {
        feedbackSessionName: 'Feedback session 2',
        logColumnsData: resultColumns,
        logRowsData: [],
        isTabExpanded: true,
      },
    ];
    component.isLoading = false;
    fixture.detectChanges();

    const cards = fixture.debugElement.nativeElement.querySelectorAll('#logs-output .card');
    expect(cards.length).toEqual(2);
  });

  it('should load course, feedback sessions, and students together on init', () => {
    const courseSpy = vi.spyOn(courseService, 'getCourseAsInstructor');
    const feedbackSessionSpy = vi.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor');
    const studentSpy = vi.spyOn(studentService, 'getStudentsFromCourse');

    expect(courseSpy).toHaveBeenCalledWith(testCourse1.courseId);
    expect(feedbackSessionSpy).toHaveBeenCalledWith(testCourse1.courseId);
    expect(studentSpy).toHaveBeenCalledWith({ courseId: testCourse1.courseId });
    expect(component.course).toEqual(testCourse1);
    expect(component.feedbackSessions.get(testFeedbackSession.feedbackSessionId)).toEqual(testFeedbackSession);
    expect(component.students[0]).toEqual(emptyStudent);
    expect(component.students[1]).toEqual(testStudent);
    expect(component.isLoading).toBe(false);
  });

  it('should search for logs using feedback course timezone when search button is clicked', () => {
    const logSpy = vi.spyOn(logService, 'searchFeedbackSessionLog').mockReturnValue(
      of({
        feedbackSessionLogs: {
          [testFeedbackSession.feedbackSessionId]: [...testLogs1, ...testLogs2],
        },
      }),
    );
    const timeSpy = vi.spyOn(timezoneService, 'resolveLocalDateTime');
    const tzOffset: number = timezoneService.getTzOffsets()[testCourse1.timeZone];

    component.formModel = {
      logsDateFrom: { year: 2020, month: 12, day: 30 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2020, month: 12, day: 31 },
      logsTimeTo: { hour: 23, minute: 59 },
      selectedUserId: testStudent.userId,
      logTypes: [FeedbackSessionLogType.SUBMISSION],
      selectedSessionId: '',
      showActions: true,
      showInactions: false,
    };
    component.course = testCourse1;
    component.students = [testStudent];
    component.feedbackSessions.set(testFeedbackSession.feedbackSessionId, testFeedbackSession);
    component.isLoading = false;
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#search-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(timeSpy).toHaveBeenCalledWith(
      component.formModel.logsDateFrom,
      component.formModel.logsTimeFrom,
      testCourse1.timeZone,
      true,
    );
    expect(logSpy).toHaveBeenCalled();
    expect(logSpy).toHaveBeenCalledWith({
      courseId: testCourse1.courseId,
      searchFrom: new Date('2020-12-31T00:00+00:00').getTime() - tzOffset * Milliseconds.IN_ONE_MINUTE,
      searchUntil: new Date('2021-01-01T00:00+00:00').getTime() - tzOffset * Milliseconds.IN_ONE_MINUTE,
      logTypes: [FeedbackSessionLogType.SUBMISSION],
      userId: testStudent.userId,
      sessionId: '',
    });

    expect(component.searchResults.length).toEqual(1);

    const timestamp: string = timezoneService.formatToString(0, testFeedbackSession.timeZone, LOGS_DATE_TIME_FORMAT);

    expect(component.searchResults[0].isTabExpanded).toBeTruthy();
    expect(component.searchResults[0].logColumnsData).toEqual(resultColumns);
    // Testing that the LogType is converted correctly.
    expect(component.searchResults[0].logRowsData[0][0].value).toEqual(`Submitted responses at ${timestamp}`);
  });

  it('should include selected feedback sessions even when they have no log entries', () => {
    vi.spyOn(logService, 'searchFeedbackSessionLog').mockReturnValue(
      of({
        feedbackSessionLogs: {},
      }),
    );

    component.formModel = {
      logsDateFrom: { year: 2020, month: 12, day: 30 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2020, month: 12, day: 31 },
      logsTimeTo: { hour: 23, minute: 59 },
      selectedUserId: '',
      logTypes: [FeedbackSessionLogType.SUBMISSION],
      selectedSessionId: '',
      showActions: true,
      showInactions: false,
    };
    component.course = testCourse1;
    component.students = [testStudent];
    component.feedbackSessions.set(testFeedbackSession.feedbackSessionId, testFeedbackSession);
    fixture.detectChanges();

    component.search();

    expect(component.searchResults.length).toEqual(1);
    expect(component.searchResults[0].feedbackSessionName).toEqual(testFeedbackSession.feedbackSessionName);
    expect(component.searchResults[0].logRowsData.length).toEqual(0);
  });
});
