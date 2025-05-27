import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import {
  InstructorSessionIndividualExtensionPageComponent,
} from './instructor-session-individual-extension-page.component';
import {
  InstructorSessionIndividualExtensionPageModule,
} from './instructor-session-individual-extension-page.module';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  Instructor,
  Instructors,
  JoinState,
  Student,
  Students,
} from '../../../types/api-output';
import {
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-request';

describe('InstructorSessionIndividualExtensionPageComponent', () => {
  const testCourse: Course = {
    courseId: 'exampleId',
    courseName: 'Example Course',
    institute: 'Test Institute',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };

  const testFeedbackSession: FeedbackSession = {
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
    studentDeadlines: { 'alice@tmms.com': 1510000000000 },
    instructorDeadlines: { 'tester2@tester.com': 1510000000000 },
  };

  const testStudent1: Student = {
    email: 'alice@tmms.com',
    courseId: 'testId',
    name: 'AliceHasExtension',
    teamName: 'Team 1',
    sectionName: 'Section 1',
  };
  const testStudent2: Student = {
    email: 'bob@tmms.com',
    courseId: 'testId',
    name: 'Bob',
    teamName: 'Team 1',
    sectionName: 'Section 1',
  };
  const testStudent3: Student = {
    email: 'alex@tmms.com',
    courseId: 'testId',
    name: 'Alex',
    teamName: 'Team 1',
    sectionName: 'Section 1',
  };
  const students: Students = {
    students: [testStudent1, testStudent2, testStudent3],
  };

  const testInstructor1: Instructor = {
    name: 'tester1',
    email: 'tester1@tester.com',
    googleId: 'instructor-google-id',
    courseId: 'test-exa.demo',
    institute: 'institute',
    joinState: JoinState.JOINED,
  };
  const testInstructor2: Instructor = {
    name: 'tester2HasExtension',
    email: 'tester2@tester.com',
    googleId: 'instructor-google-id',
    courseId: 'test-exa.demo',
    institute: 'institute',
    joinState: JoinState.JOINED,
  };

  const instructors: Instructors = {
    instructors: [testInstructor1, testInstructor2],
  };

  const testFeedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet = {
    giverIdentifiers: [testStudent2.email],
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

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, RouterModule, InstructorSessionIndividualExtensionPageModule],
        providers: [
          StudentService,
          CourseService,
          TimezoneService,
          FeedbackSessionsService,
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
        ],
      }).compileComponents();
    }),
  );

  beforeEach(() => {
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
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingAllStudents = true;
    component.isLoadingAllInstructors = false;
    component.isLoadingFeedbackSession = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with instructor loading', () => {
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingAllInstructors = true;
    component.isLoadingFeedbackSession = false;
    component.isLoadingAllStudents = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when feedback session loading', () => {
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    component.isLoadingFeedbackSession = true;
    component.isLoadingAllStudents = false;
    component.isLoadingAllInstructors = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there are no students', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of({ students: [] }));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when there are no instructors', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of({ instructors: [] }));

    component.ngOnInit();

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should stop loading if student service returns 404', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'This is a test message' },
      })),
    );
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);
    const spyStatusMessageService: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast');

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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

    component.ngOnInit();
    fixture.detectChanges();

    expect(component.isLoadingAllInstructors).toBeFalsy();
    expect(component.isLoadingAllStudents).toBeFalsy();
    expect(component.isLoadingFeedbackSession).toBeFalsy();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when clicking the Select All Students button', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.ngOnInit();
    fixture.detectChanges();

    const selectAllButton = fixture.debugElement.query(By.css('#select-all-student-btn')).nativeElement;
    selectAllButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
    expect(component.isAllStudentsSelected).toBeTruthy();
  });

  it('should snap when clicking the Select All Instructors button', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    component.ngOnInit();
    fixture.detectChanges();

    const selectAllButton = fixture.debugElement.query(By.css('#select-all-instructor-btn')).nativeElement;
    selectAllButton.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
    expect(component.isAllInstructorsSelected).toBeTruthy();
  });

   it('should not select all students and instructors after unselecting', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
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
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    component.ngOnInit();
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual('Extend / Edit');
    expect(extendButton.disabled).toBeTruthy();
    expect(deleteButton.textContent).toEqual('Delete');
    expect(deleteButton.disabled).toBeTruthy();
  });

  it('should enable the extend button when a student is selected', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    component.ngOnInit();
    component.studentsOfCourse[1].isSelected = true; // Bob has no extension
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual('Extend / Edit');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual('Delete');
    expect(deleteButton.disabled).toBeTruthy();
  });

  it('should enable extend and delete button when student with extension selected', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    component.ngOnInit();
    component.studentsOfCourse[0].isSelected = true; // Alice has extension
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual('Extend / Edit');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual('Delete');
    expect(deleteButton.disabled).toBeFalsy();
  });

  it('should enable delete button even if one of selected students does not have extension', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    component.ngOnInit();
    component.studentsOfCourse[0].isSelected = true; // Alice has extension
    component.studentsOfCourse[1].isSelected = true; // Bob does not
    fixture.detectChanges();

    const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
    const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

    expect(extendButton.textContent).toEqual('Extend / Edit');
    expect(extendButton.disabled).toBeFalsy();
    expect(deleteButton.textContent).toEqual('Delete');
    expect(deleteButton.disabled).toBeFalsy();
  });

  it('should not automatically select students that have not submitted yet if preselectnonsubmitters is false', () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(testFeedbackSessionSubmittedGiverSet)); // Alice and Alex have not submitted yet
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

    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(testFeedbackSessionSubmittedGiverSet)); // Alice and Alex have not submitted yet
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

  it(
    'should select students that have not submitted yet if Select Not Submitted Student Button is checked',
    () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(testFeedbackSessionSubmittedGiverSet)); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const selectNotSubmittedButton = fixture.debugElement
     .query(By.css('#select-not-submitted-student-btn')).nativeElement;
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

  it(
    'should unselect only students that have not submitted yet if Select Not Submitted Student Button is unchecked',
    () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(testFeedbackSessionSubmittedGiverSet)); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const studentRow = fixture.debugElement.query(By.css('#student-row-1')).nativeElement;
    studentRow.click();
    fixture.detectChanges();

    const selectNotSubmittedButton = fixture.debugElement
      .query(By.css('#select-not-submitted-student-btn')).nativeElement;
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

  it(
    'should select those that have not submitted yet if Select Not Submitted Instructor and Student Button is checked',
    () => {
    jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

    const giverSet: FeedbackSessionSubmittedGiverSet = {
      giverIdentifiers: [testStudent2.email, testInstructor1.email],
    };
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(giverSet));
    component.ngOnInit();
    fixture.detectChanges();

    const selectNotSubmittedStudentButton = fixture.debugElement
      .query(By.css('#select-not-submitted-student-btn')).nativeElement;
    selectNotSubmittedStudentButton.click();
    fixture.detectChanges();

    const selectNotSubmittedInstructorButton = fixture.debugElement
      .query(By.css('#select-not-submitted-instructor-btn')).nativeElement;
    selectNotSubmittedInstructorButton.click();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();

    const instructorOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-0');
    const instructorTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#instructor-checkbox-1');
    const studentOneCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-0');
    const studentTwoCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-1');
    const studentThreeCheckBox: any = fixture.debugElement.nativeElement.querySelector('#student-checkbox-2');

    expect(component.isAllYetToSubmitInstructorsSelected).toBeTruthy();
    expect(component.isAllYetToSubmitStudentsSelected).toBeTruthy();

    expect(instructorOneCheckBox.checked).toBeTruthy();
    expect(instructorTwoCheckBox.checked).toBeFalsy();
    expect(studentOneCheckBox.checked).toBeTruthy();
    expect(studentTwoCheckBox.checked).toBeFalsy();
    expect(studentThreeCheckBox.checked).toBeTruthy();
  });

  it(
    'should unselect instructors that have not submitted yet if Select Not Submitted Instructor Button is unchecked',
    () => {
    jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
    jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
    jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

    const giverSet: FeedbackSessionSubmittedGiverSet = {
      giverIdentifiers: [testInstructor1.email],
    };
    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionSubmittedGiverSet')
      .mockReturnValue(of(giverSet)); // Alice and Alex have not submitted yet
    component.ngOnInit();
    fixture.detectChanges();

    const instructorRow = fixture.debugElement.query(By.css('#instructor-row-1')).nativeElement;
    instructorRow.click();
    fixture.detectChanges();

    const selectNotSubmittedButton = fixture.debugElement
      .query(By.css('#select-not-submitted-instructor-btn')).nativeElement;
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
});
