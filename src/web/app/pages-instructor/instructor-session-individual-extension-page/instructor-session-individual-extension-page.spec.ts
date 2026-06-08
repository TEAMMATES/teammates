import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { StudentExtensionTableColumnModel } from './extension-table-column-model';
import { InstructorSessionIndividualExtensionPageComponent } from './instructor-session-individual-extension-page.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  CourseView,
  DeadlineExtensions,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  Instructor,
  Instructors,
  JoinState,
  Student,
  Students,
} from '../../../types/api-output';
import { ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';

describe('InstructorSessionIndividualExtensionPageComponent', () => {
  const testCourse: Course = {
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'Test Institute',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testCourseView: CourseView = {
    course: testCourse,
  };

  const testFeedbackSession: FeedbackSession = {
    feedbackSessionId: '23901a20-48bb-4fcc-a3fb-0b2489b07886',
    courseId: 'testId1',
    timeZone: 'UTC',
    feedbackSessionName: 'Test Session',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000000000000,
    submissionEndTimestamp: 1500000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  const testFeedbackSessionView: FeedbackSessionView = {
    feedbackSession: testFeedbackSession,
  };

  const testDeadlineExtensions: DeadlineExtensions = {
    userDeadlines: {
      'alice-id': 1510000000000,
      'tester2-id': 1510000000000,
    },
  };

  const testStudent1: Student = {
    userId: 'alice-id',
    email: 'alice@tmms.com',
    courseId: 'testId',
    courseName: 'Test Course',
    institute: 'Test Institute',
    name: 'AliceHasExtension',
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Section 1',
    sectionId: 'section-1',
  };
  const testStudent2: Student = {
    userId: 'bob-id',
    email: 'bob@tmms.com',
    courseId: 'testId',
    courseName: 'Test Course',
    institute: 'Test Institute',
    name: 'Bob',
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Section 1',
    sectionId: 'section-1',
  };
  const testStudent3: Student = {
    userId: 'alex-id',
    email: 'alex@tmms.com',
    courseId: 'testId',
    courseName: 'Test Course',
    institute: 'Test Institute',
    name: 'Alex',
    teamName: 'Team 1',
    teamId: 'team-1',
    sectionName: 'Section 1',
    sectionId: 'section-1',
  };
  const students: Students = {
    students: [testStudent1, testStudent2, testStudent3],
  };

  const testInstructor1: Instructor = {
    userId: 'tester1-id',
    name: 'tester1',
    email: 'tester1@tester.com',
    googleId: 'instructor-google-id',
    courseId: 'test-exa.demo',
    courseName: 'Test Course',
    institute: 'institute',
    joinState: JoinState.JOINED,
  };
  const testInstructor2: Instructor = {
    userId: 'tester2-id',
    name: 'tester2HasExtension',
    email: 'tester2@tester.com',
    googleId: 'instructor-google-id',
    courseId: 'test-exa.demo',
    courseName: 'Test Course',
    institute: 'institute',
    joinState: JoinState.JOINED,
  };

  const instructors: Instructors = {
    instructors: [testInstructor1, testInstructor2],
  };

  const testFeedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet = {
    studentGivers: [testStudent2.userId],
    instructorGivers: [],
    studentNonGivers: [testStudent1.userId, testStudent3.userId],
    instructorNonGivers: [testInstructor1.userId, testInstructor2.userId],
  };

  const testTimeString = '5 Apr 2000 2:00:00';

  let component: InstructorSessionIndividualExtensionPageComponent;
  let fixture: ComponentFixture<InstructorSessionIndividualExtensionPageComponent>;
  let studentService: StudentService;
  let instructorService: InstructorService;
  let courseService: CourseService;
  let feedbackSessionsService: FeedbackSessionsService;
  let timezoneService: TimezoneService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({
              courseid: testCourse.courseId,
              feedbackSessionName: testFeedbackSession.feedbackSessionName,
              preselectnonsubmitters: 'false',
            }),
          },
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorSessionIndividualExtensionPageComponent);
    component = fixture.componentInstance;
    studentService = TestBed.inject(StudentService);
    instructorService = TestBed.inject(InstructorService);
    courseService = TestBed.inject(CourseService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    timezoneService = TestBed.inject(TimezoneService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with student loading', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingAllStudents = true;
    component.isLoadingAllInstructors = false;
    component.isLoadingFeedbackSession = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with instructor loading', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingAllInstructors = true;
    component.isLoadingFeedbackSession = false;
    component.isLoadingAllStudents = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session loading', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingFeedbackSession = true;
    component.isLoadingAllStudents = false;
    component.isLoadingAllInstructors = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there are no students', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of({ students: [] }));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there are no instructors', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({ instructors: [] }));

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if student service returns 404', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showErrorToast');

    component.ngOnInit();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.hasLoadedAllStudentsFailed).toBeTruthy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.hasLoadingFeedbackSessionFailed).toBeFalsy();
    fixture.detectChanges();
    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if instructor service returns 404', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showErrorToast');

    component.ngOnInit();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.hasLoadedAllInstructorsFailed).toBeTruthy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.hasLoadingFeedbackSessionFailed).toBeFalsy();
    fixture.detectChanges();
    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if feedback session service returns 404', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showErrorToast');

    component.ngOnInit();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.hasLoadingFeedbackSessionFailed).toBeTruthy();

    fixture.detectChanges();
    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if feedback session service get feedback session submitted giver set returns 404', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );

    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showErrorToast');

    component.ngOnInit();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.hasLoadedAllInstructorsFailed).toBeTruthy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.hasLoadedAllStudentsFailed).toBeTruthy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.hasLoadingFeedbackSessionFailed).toBeFalsy();
    fixture.detectChanges();
    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if course service returns 404', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService = vi.spyOn(statusMessageService, 'showErrorToast');

    component.ngOnInit();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(component.hasLoadingFeedbackSessionFailed).toBeTruthy();
    fixture.detectChanges();
    expect(spyStatusMessageService).toHaveBeenCalled();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with details and extended students', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when clicking the Select All Students button', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.ngOnInit();
    fixture.detectChanges();

    const selectAllButton = fixture.debugElement.query(By.css('#select-all-student-btn')).nativeElement;
    selectAllButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
    expect(component.isAllStudentsSelected).toBeTruthy();
  });

  it('should snap when clicking the Select All Instructors button', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.ngOnInit();
    fixture.detectChanges();

    const selectAllButton = fixture.debugElement.query(By.css('#select-all-instructor-btn')).nativeElement;
    selectAllButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
    expect(component.isAllInstructorsSelected).toBeTruthy();
  });

  it('should not select all students and instructors after unselecting', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.ngOnInit();
    component.isLoadingAllInstructors = false;
    component.isLoadingAllStudents = false;
    component.isLoadingFeedbackSession = false;
    fixture.detectChanges();

    const selectAllButtonStudents = fixture.debugElement.query(By.css('#select-all-student-btn')).nativeElement;
    selectAllButtonStudents.click();
    fixture.detectChanges();

    expect(component.isAllStudentsSelected).toBeTruthy();

    const studentRow = fixture.debugElement.query(By.css('#student-row-0')).nativeElement;
    studentRow.click();
    fixture.detectChanges();

    expect(component.isAllStudentsSelected).toBeFalsy();

    const selectAllInstructorButton = fixture.debugElement.query(By.css('#select-all-instructor-btn')).nativeElement;
    selectAllInstructorButton.click();
    fixture.detectChanges();

    expect(component.isAllInstructorsSelected).toBeTruthy();

    const instructorRow = fixture.debugElement.query(By.css('#instructor-row-0')).nativeElement;
    instructorRow.click();
    fixture.detectChanges();

    expect(component.isAllInstructorsSelected).toBeFalsy();
  });

  it('should disable extend and delete button when no student selected', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    component.ngOnInit();
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual(' Extend / Edit ');
    expect(extendButton.disabled).toBeTruthy();
    expect(deleteButton.textContent).toEqual(' Delete Extensions ');
    expect(deleteButton.disabled).toBeTruthy();
  });

  it('should enable the extend button when a student is selected', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    component.ngOnInit();
    component.studentsOfCourse[1].isSelected = true; // Bob has no extension
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual(' Extend / Edit ');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual(' Delete Extensions ');
    expect(deleteButton.disabled).toBeTruthy();
  });

  it('should enable extend and delete button when student with extension selected', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    component.ngOnInit();
    component.studentsOfCourse[0].isSelected = true; // Alice has extension
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual(' Extend / Edit ');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual(' Delete Extensions ');
    expect(deleteButton.disabled).toBeFalsy();
  });

  it('should enable delete button even if one of selected students does not have extension', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    component.ngOnInit();
    component.studentsOfCourse[0].isSelected = true; // Alice has extension
    component.studentsOfCourse[1].isSelected = true; // Bob does not
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual(' Extend / Edit ');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual(' Delete Extensions ');
    expect(deleteButton.disabled).toBeFalsy();
  });

  it('should not automatically select students that have not submitted yet if preselectnonsubmitters is false', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    ); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const studentOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
    expect(studentOneCheckBox.checked).toBeFalsy();
    expect(studentTwoCheckBox.checked).toBeFalsy();
    expect(studentThreeCheckBox.checked).toBeFalsy();
  });

  it('should automatically select students that have not submitted yet if preselectnonsubmitters is true', () => {
    const activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.queryParams = of({
      courseid: testCourse.courseId,
      feedbackSessionName: testFeedbackSession.feedbackSessionName,
      preselectnonsubmitters: 'true',
    });

    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    ); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const studentOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeTruthy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeTruthy();
    expect(studentOneCheckBox.checked).toBeTruthy();
    expect(studentTwoCheckBox.checked).toBeFalsy();
    expect(studentThreeCheckBox.checked).toBeTruthy();
  });

  it('should select students that have not submitted yet if Select Not Submitted Student Button is checked', async () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    ); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();
    await Promise.resolve();

    const selectNotSubmittedButton = fixture.debugElement.query(
      By.css('#select-not-submitted-student-btn'),
    ).nativeElement;
    selectNotSubmittedButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();

    const studentOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeTruthy();
    expect(studentOneCheckBox.checked).toBeTruthy();
    expect(studentTwoCheckBox.checked).toBeFalsy();
    expect(studentThreeCheckBox.checked).toBeTruthy();
  });

  it('should unselect only students that have not submitted yet if Select Not Submitted Student Button is unchecked', () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      of(testFeedbackSessionSubmittedGiverSet),
    ); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const studentRow = fixture.debugElement.query(By.css('#student-row-1')).nativeElement;
    studentRow.click();
    fixture.detectChanges();

    const selectNotSubmittedButton = fixture.debugElement.query(
      By.css('#select-not-submitted-student-btn'),
    ).nativeElement;
    selectNotSubmittedButton.click();
    selectNotSubmittedButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();

    const studentOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
    expect(studentOneCheckBox.checked).toBeFalsy();
    expect(studentTwoCheckBox.checked).toBeTruthy();
    expect(studentThreeCheckBox.checked).toBeFalsy();
  });

  it('should select those that have not submitted yet if Select Not Submitted Instructor and Student Button is checked', async () => {
    vi.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );

    const giverSet: FeedbackSessionSubmittedGiverSet = {
      studentGivers: [testStudent2.userId],
      instructorGivers: [testInstructor1.userId],
      studentNonGivers: [testStudent1.userId, testStudent3.userId],
      instructorNonGivers: [testInstructor2.userId],
    };
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(of(giverSet));
    component.ngOnInit();
    fixture.detectChanges();
    await Promise.resolve();

    const selectNotSubmittedStudentButton = fixture.debugElement.query(
      By.css('#select-not-submitted-student-btn'),
    ).nativeElement;
    selectNotSubmittedStudentButton.click();
    fixture.detectChanges();
    await Promise.resolve();

    const selectNotSubmittedInstructorButton = fixture.debugElement.query(
      By.css('#select-not-submitted-instructor-btn'),
    ).nativeElement;
    selectNotSubmittedInstructorButton.click();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();

    const instructorOneCheckBox = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-0');
    const instructorTwoCheckBox = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-1');
    const studentOneCheckBox = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeTruthy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeTruthy();

    expect(instructorOneCheckBox.checked).toBeTruthy();
    expect(instructorTwoCheckBox.checked).toBeFalsy();
    expect(studentOneCheckBox.checked).toBeTruthy();
    expect(studentTwoCheckBox.checked).toBeFalsy();
    expect(studentThreeCheckBox.checked).toBeTruthy();
  });

  it('should unselect instructors that have not submitted yet if Select Not Submitted Instructor Button is unchecked', () => {
    vi.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    vi.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourseView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSessionView));
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionDeadlineExtensions').mockReturnValue(
      of(testDeadlineExtensions),
    );

    const giverSet: FeedbackSessionSubmittedGiverSet = {
      studentGivers: [],
      instructorGivers: [testInstructor1.userId],
      studentNonGivers: [],
      instructorNonGivers: [testInstructor2.userId],
    };
    vi.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(of(giverSet)); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const instructorRow = fixture.debugElement.query(By.css('#instructor-row-1')).nativeElement;
    instructorRow.click();
    fixture.detectChanges();

    const selectNotSubmittedButton = fixture.debugElement.query(
      By.css('#select-not-submitted-instructor-btn'),
    ).nativeElement;
    selectNotSubmittedButton.click();
    selectNotSubmittedButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();

    const instructorOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-0');
    const instructorTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-1');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
    expect(instructorOneCheckBox.checked).toBeFalsy();
    expect(instructorTwoCheckBox.checked).toBeTruthy();
  });

  describe('sortStudentColumnsBy team name', () => {
    const makeStudent = (teamName: string, sectionName: string, name: string): StudentExtensionTableColumnModel => ({
      userId: `${name.toLowerCase()}-id`,
      teamName,
      sectionName,
      name,
      email: `${name.toLowerCase()}@test.com`,
      extensionDeadline: 0,
      hasExtension: false,
      isSelected: false,
    });

    beforeEach(() => {
      component.studentsOfCourse = [
        makeStudent('Team B', 'Section 1', 'Alice'),
        makeStudent('Team A', 'Section 1', 'Bob'),
        makeStudent('Team C', 'Section 1', 'Charlie'),
      ];
      component.sortStudentOrder = SortOrder.DESC;
    });

    it('should sort students by team name ascending on first click', () => {
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.studentsOfCourse.map((s: StudentExtensionTableColumnModel) => s.teamName)).toEqual([
        'Team A',
        'Team B',
        'Team C',
      ]);
    });

    it('should sort students by team name descending on second click', () => {
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.studentsOfCourse.map((s: StudentExtensionTableColumnModel) => s.teamName)).toEqual([
        'Team C',
        'Team B',
        'Team A',
      ]);
    });

    it('should sort by team name, not section name', () => {
      component.studentsOfCourse = [
        makeStudent('Team Z', 'Section A', 'Alice'),
        makeStudent('Team A', 'Section Z', 'Bob'),
      ];
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.studentsOfCourse[0].teamName).toBe('Team A');
      expect(component.studentsOfCourse[1].teamName).toBe('Team Z');
    });

    it('should set sortStudentsBy to TEAM_NAME', () => {
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.sortStudentsBy).toBe(SortBy.TEAM_NAME);
    });

    it('should toggle sortStudentOrder from DESC to ASC on first click', () => {
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.sortStudentOrder).toBe(SortOrder.ASC);
    });

    it('should toggle sortStudentOrder back to DESC on second click', () => {
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      component.sortStudentColumnsBy(SortBy.TEAM_NAME);
      expect(component.sortStudentOrder).toBe(SortOrder.DESC);
    });
  });
});
