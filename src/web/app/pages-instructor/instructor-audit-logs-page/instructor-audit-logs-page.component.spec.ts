import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { CourseService } from '../../../services/course.service';
import { LogService } from '../../../services/log.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { SortBy } from '../../../types/sort-properties';
import { ColumnData } from '../../components/sortable-table/sortable-table.component';
import TestCourses from '../../test-resources/courses';
import TestFeedbackSessionLogs from '../../test-resources/feedback-session-logs';
import TestStudents from '../../test-resources/students';
import { InstructorAuditLogsPageComponent } from './instructor-audit-logs-page.component';
import { InstructorAuditLogsPageModule } from './instructor-audit-logs-page.module';

describe('InstructorAuditLogsPageComponent', () => {
  let component: InstructorAuditLogsPageComponent;
  let fixture: ComponentFixture<InstructorAuditLogsPageComponent>;
  let courseService: CourseService;
  let studentService: StudentService;
  let logService: LogService;
  let timezoneService: TimezoneService;

  const resultColumns: ColumnData[] = [
    { header: 'Time', sortBy: SortBy.LOG_DATE },
    { header: 'Name', sortBy: SortBy.GIVER_NAME },
    { header: 'Activity', sortBy: SortBy.LOG_TYPE },
    { header: 'Email', sortBy: SortBy.RESPONDENT_EMAIL },
    { header: 'Section', sortBy: SortBy.SECTION_NAME },
    { header: 'Team', sortBy: SortBy.TEAM_NAME },
  ];

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [InstructorAuditLogsPageModule, HttpClientTestingModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorAuditLogsPageComponent);
    courseService = TestBed.inject(CourseService);
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
    component.courses = [TestCourses.cs9999, TestCourses.ma1234];
    component.formModel = {
      logsDateFrom: { year: 1997, month: 9, day: 11 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 1998, month: 9, day: 11 },
      logsTimeTo: { hour: 15, minute: 0 },
      courseId: TestCourses.cs9999.courseName,
      studentEmail: TestStudents.johnDoe.email,
    };
    component.courseToStudents = {
      CS9999: [TestStudents.johnDoe],
      MA1234: [],
    };
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

  it('should load all courses that instructor has on init', () => {
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'getAllCoursesAsInstructor')
        .mockReturnValue(of({
          courses: [
            TestCourses.cs9999, TestCourses.ma1234, TestCourses.ee1111,
          ],
        }));

    component.ngOnInit();

    expect(component.isLoading).toBeFalsy();
    expect(courseSpy).toBeCalledWith('active');
    expect(component.courses.length).toEqual(2);
    expect(component.courses).toContainEqual(TestCourses.cs9999);
    expect(component.courses).toContainEqual(TestCourses.ma1234);
    expect(component.courses).not.toContainEqual(TestCourses.ee1111);

    // courseToStudents not loaded on init
    expect(component.courseToStudents).toMatchObject({});
  });

  it('should load all students of selected course has on select', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            TestStudents.johnDoe,
          ],
        }));

    component.formModel.courseId = TestCourses.cs9999.courseId;
    component.loadStudents();

    expect(component.courseToStudents[TestCourses.cs9999.courseId][0]).toEqual(TestStudents.emptyStudent);
    expect(component.courseToStudents[TestCourses.cs9999.courseId][1]).toEqual(TestStudents.johnDoe);
    expect(studentSpy).toHaveBeenNthCalledWith(1, { courseId: TestCourses.cs9999.courseId });
  });

  it('should load students from cache if present', () => {
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse')
        .mockReturnValue(of({
          students: [
            TestStudents.johnDoe,
          ],
        }));

    component.formModel.courseId = TestCourses.cs9999.courseId;
    component.courseToStudents[TestCourses.cs9999.courseId] = [TestStudents.emptyStudent];
    component.loadStudents();

    expect(component.courseToStudents[TestCourses.cs9999.courseId].length).toEqual(1);
    expect(component.courseToStudents[TestCourses.cs9999.courseId][0]).toEqual(TestStudents.emptyStudent);
    expect(studentSpy).not.toHaveBeenCalled();
  });

  it('should search for logs using feedback course timezone when search button is clicked', () => {
    const logSpy: SpyInstance = jest.spyOn(logService, 'searchFeedbackSessionLog')
        .mockReturnValue(of(
            {
              feedbackSessionLogs: [
                    TestFeedbackSessionLogs.testLogs1,
                    TestFeedbackSessionLogs.testLogs2,
              ],
            }));
    const timeSpy: SpyInstance = jest.spyOn(timezoneService, 'resolveLocalDateTime');
    const tzOffset: number = timezoneService.getTzOffsets()[TestCourses.cs9999.timeZone];

    component.isLoading = false;
    component.isSearching = false;
    component.formModel = {
      logsDateFrom: { year: 2020, month: 12, day: 30 },
      logsTimeFrom: { hour: 23, minute: 59 },
      logsDateTo: { year: 2020, month: 12, day: 31 },
      logsTimeTo: { hour: 23, minute: 59 },
      courseId: TestCourses.cs9999.courseId,
      studentEmail: TestStudents.johnDoe.email,
    };
    component.courses = [TestCourses.cs9999];
    component.courseToStudents = { CS9999: [TestStudents.johnDoe] };
    fixture.detectChanges();

    fixture.debugElement.nativeElement.querySelector('#search-button').click();

    expect(timeSpy).toHaveBeenCalledTimes(2);
    expect(timeSpy).toHaveBeenCalledWith(
      component.formModel.logsDateFrom,
      component.formModel.logsTimeFrom,
      TestCourses.cs9999.timeZone,
      true,
    );
    expect(logSpy).toHaveBeenCalled();
    expect(logSpy).toHaveBeenCalledWith({
      courseId: TestCourses.cs9999.courseId,
      searchFrom: (new Date('2020-12-31T00:00+00:00').getTime() - tzOffset * 60 * 1000).toString(),
      searchUntil: (new Date('2021-01-01T00:00+00:00').getTime() - tzOffset * 60 * 1000).toString(),
      studentEmail: TestStudents.johnDoe.email,
    });

    expect(component.searchResults.length).toEqual(2);

    for (let i: number = 0; i < 2; i += 1) {
      expect(component.searchResults[i].isTabExpanded).toBeFalsy();
      expect(component.searchResults[i].logColumnsData).toEqual(resultColumns);
      // Testing that the LogType is converted correctly.
      expect(component.searchResults[i].logRowsData[0][2].value).toEqual('Submitted responses');
    }
  });
});
