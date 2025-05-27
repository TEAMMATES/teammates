import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { InstructorStudentActivityLogsComponent } from './instructor-student-activity-logs.component';
import { InstructorStudentActivityLogsModule } from './instructor-student-activity-logs.module';
import { LogService } from '../../../services/log.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionLog,
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
  let studentService: StudentService;
  let logService: LogService;
  let timezoneService: TimezoneService;

  const LOGS_DATE_TIME_FORMAT: string = 'ddd, DD MMM YYYY hh:mm:ss A';
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
    timeZone: 'Asia/Singapore',
    creationTimestamp: 0,
    deletionTimestamp: 0,
    privileges: {
      canModifyCourse: true,
      canModifySession: true,
      canModifyStudent: true,
      canModifyInstructor: true,
      canViewStudentInSections: true,
      canModifySessionCommentsInSections: true,
      canViewSessionInSections: true,
      canSubmitSessionInSections: true,
    },
  };
  const emptyStudent: Student = {
    courseId: '', email: '', name: '', sectionName: '', teamName: '',
  };
  const testStudent: Student = {
    email: 'doejohn@email.com',
    courseId: 'CS9999',
    name: 'Doe John',
    teamName: 'team 1',
    sectionName: 'section 1',
  };
  const testFeedbackSession: FeedbackSession = {
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
    studentDeadlines: {},
    instructorDeadlines: {},
  };
  const testLogs1: FeedbackSessionLog = {
    feedbackSessionData: testFeedbackSession,
    feedbackSessionLogEntries: [
      {
        studentData: testStudent,
        feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
        timestamp: 0,
      },
    ],
  };
  const testLogs2: FeedbackSessionLog = {
    feedbackSessionData: testFeedbackSession,
    feedbackSessionLogEntries: [
      {
        studentData: testStudent,
        feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
        timestamp: 0,
      },
    ],
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorStudentActivityLogsModule,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentActivityLogsComponent);
    studentService = TestBed.inject(StudentService);
    logService = TestBed.inject(LogService);
    timezoneService = TestBed.inject(TimezoneService);
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
    component.course = testCourse1;
    component.formModel = {
      logsDateFrom: { year: 1997, month: 9, day: 11 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 1998, month: 9, day: 11 },
      logsTimeTo: { hour: 15, minute: 0 },
      selectedStudent: { studentEmail: 'doejohn@email.com', studentId: undefined },
      logType: 'session access',
      selectedSession: { feedbackSessionName: undefined, sessionId: undefined },
      showActions: false,
      showInactions: false,
    };
    component.students = [testStudent];
    component.isLoading = false;
    component.isSearching = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with results of a search', () => {
    component.searchResults = [
      {
        feedbackSessionName: 'Feedback session 1',
        logColumnsData: resultColumns,
        logRowsData: [[
          { value: '15 January 2021' },
          { value: 'Doe John' },
          { value: 'Viewed the submission page' },
          { value: 'doejohn@email.com' },
          { value: 'section 1' },
          { value: 'team 1' },
        ]],
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
    component.isSearching = false;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should load all students of selected course has on select', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            testStudent,
          ],
        }));

    component.loadStudents(testCourse1.courseId);

    expect(component.students[0]).toEqual(emptyStudent);
    expect(component.students[1]).toEqual(testStudent);
    expect(studentSpy).toHaveBeenNthCalledWith(1, { courseId: testCourse1.courseId });
  });

  it('should load students from cache if present', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            testStudent,
          ],
        }));

    component.students = [emptyStudent];
    component.loadStudents(testCourse1.courseId);

    expect(component.students.length).toEqual(1);
    expect(component.students[0]).toEqual(emptyStudent);
    expect(studentSpy).not.toHaveBeenCalled();
  });

  it('should search for logs using feedback course timezone when search button is clicked', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchFeedbackSessionLog')
        .mockReturnValue(of({ feedbackSessionLogs: [testLogs1, testLogs2] }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime');
    const tzOffset: number = timezoneService.getTzOffsets()[testCourse1.timeZone];

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsDateFrom: { year: 2020, month: 12, day: 30 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2020, month: 12, day: 31 },
      logsTimeTo: { hour: 23, minute: 59 },
      selectedStudent: { studentEmail: testStudent.email, studentId: '' },
      logType: 'submission',
      selectedSession: { feedbackSessionName: '', sessionId: '' },
      showActions: true,
      showInactions: false,
    };
    component.course = testCourse1;
    component.students = [testStudent];
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
      searchFrom: (new Date('2020-12-31T00:00+00:00').getTime()
        - tzOffset * Milliseconds.IN_ONE_MINUTE).toString(),
      searchUntil: (new Date('2021-01-01T00:00+00:00').getTime()
        - tzOffset * Milliseconds.IN_ONE_MINUTE).toString(),
      studentEmail: testStudent.email,
      sessionName: '',
      logType: 'submission',
      studentId: '',
      sessionId: '',
    });

    expect(component.searchResults.length).toEqual(2);

    const timestamp: string = timezoneService.formatToString(
        0, testFeedbackSession.timeZone, LOGS_DATE_TIME_FORMAT);

    for (let i: number = 0; i < 2; i += 1) {
      expect(component.searchResults[i].isTabExpanded).toBeTruthy();
      expect(component.searchResults[i].logColumnsData).toEqual(resultColumns);
      // Testing that the LogType is converted correctly.
      expect(component.searchResults[i].logRowsData[0][0].value).toEqual(`Submitted responses at ${timestamp}`);
    }
  });
});
