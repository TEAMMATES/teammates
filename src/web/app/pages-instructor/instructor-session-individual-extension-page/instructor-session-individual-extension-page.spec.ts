import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus, Instructor, Instructors, JoinState, Student,
  Students,
} from '../../../types/api-output';
import { ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-request';
import {
  InstructorSessionIndividualExtensionPageComponent,
} from './instructor-session-individual-extension-page.component';
import { InstructorSessionIndividualExtensionPageModule } from './instructor-session-individual-extension-page.module';

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
      isClosingEmailEnabled: true,
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
      name: 'alex',
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

    const testTimeString = '5 Apr 2000 2:00:00';

    let component: InstructorSessionIndividualExtensionPageComponent;
    let fixture: ComponentFixture<InstructorSessionIndividualExtensionPageComponent>;
    let studentService: StudentService;
    let instructorService: InstructorService;
    let courseService: CourseService;
    let feedbackSessionsService: FeedbackSessionsService;
    let timezoneService: TimezoneService;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
          imports: [
            HttpClientTestingModule,
            RouterModule,
            InstructorSessionIndividualExtensionPageModule,
          ],
          providers: [StudentService, CourseService, TimezoneService, FeedbackSessionsService,
            {
              provide: ActivatedRoute,
              useValue: {
                queryParams: of({
                  courseid: testCourse.courseId,
                  feedbackSessionName: testFeedbackSession.feedbackSessionName,
                }),
              },
            },
          ],
        })
        .compileComponents();
      }));

    beforeEach(() => {
        fixture = TestBed.createComponent(InstructorSessionIndividualExtensionPageComponent);
        component = fixture.componentInstance;
        studentService = TestBed.inject(StudentService);
        instructorService = TestBed.inject(InstructorService);
        courseService = TestBed.inject(CourseService);
        feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
        timezoneService = TestBed.inject(TimezoneService);
        jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('UTC');
        fixture.detectChanges();
      });

    it('should create', () => {
        expect(component).toBeTruthy();
      });

    it('should snap with default fields', () => {
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
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(throwError({
        status: 404,
        error: { message: 'This is a test message' },
      }));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));

      component.ngOnInit();

      expect(component.isLoadingAllInstructors).toBeFalsy();
      expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
      expect(component.isLoadingAllStudents).toBeFalsy();
      expect(component.hasLoadedAllStudentsFailed).toBeTruthy();
      expect(component.isLoadingFeedbackSession).toBeFalsy();
      expect(component.hasLoadingFeedbackSessionFailed).toBeFalsy();
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should stop loading if instructor service returns 404', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(throwError({
        status: 404,
        error: { message: 'This is a test message' },
      }));

      component.ngOnInit();

      expect(component.isLoadingAllInstructors).toBeFalsy();
      expect(component.hasLoadedAllInstructorsFailed).toBeTruthy();
      expect(component.isLoadingAllStudents).toBeFalsy();
      expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
      expect(component.isLoadingFeedbackSession).toBeFalsy();
      expect(component.hasLoadingFeedbackSessionFailed).toBeFalsy();
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should stop loading if feedback session service returns 404', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(throwError({
        status: 404,
        error: { message: 'This is a test message' },
      }));
      jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);

      component.ngOnInit();

      expect(component.isLoadingAllInstructors).toBeFalsy();
      expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
      expect(component.isLoadingAllStudents).toBeFalsy();
      expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
      expect(component.isLoadingFeedbackSession).toBeFalsy();
      expect(component.hasLoadingFeedbackSessionFailed).toBeTruthy();
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should stop loading if course service returns 404', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(throwError({
        status: 404,
        error: { message: 'This is a test message' },
      }));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
      jest.spyOn(timezoneService, 'formatToString').mockReturnValue(testTimeString);

      component.ngOnInit();

      expect(component.isLoadingAllInstructors).toBeFalsy();
      expect(component.hasLoadedAllInstructorsFailed).toBeFalsy();
      expect(component.isLoadingAllStudents).toBeFalsy();
      expect(component.hasLoadedAllStudentsFailed).toBeFalsy();
      expect(component.isLoadingFeedbackSession).toBeFalsy();
      expect(component.hasLoadingFeedbackSessionFailed).toBeTruthy();
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should snap with details and extended students', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));

      component.ngOnInit();
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should snap when clicking the Select All Students button', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
      component.ngOnInit();
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();
      const selectAllButton = fixture.debugElement.query(By.css('#select-all-student-btn'));
      selectAllButton.triggerEventHandler('click', null);

      expect(fixture).toMatchSnapshot();
    });

    it('should snap when clicking the Select All Instructors button', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      jest.spyOn(instructorService, 'loadInstructors').mockReturnValue(of(instructors));
      component.ngOnInit();
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();
      const selectAllButton = fixture.debugElement.query(By.css('#select-all-instructor-btn'));
      selectAllButton.triggerEventHandler('click', null);

      expect(fixture).toMatchSnapshot();
    });

    it('should disable extend and delete button when no student selected', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      component.ngOnInit();
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();

      const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
      const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

      expect(extendButton.textContent).toEqual('Extend');
      expect(extendButton.disabled).toBeTruthy();
      expect(deleteButton.textContent).toEqual('Delete');
      expect(deleteButton.disabled).toBeTruthy();
    });

    it('should enable the extend button when a student is selected', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      component.ngOnInit();
      component.studentsOfCourse[1].selected = true; // Bob has no extension
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;
      fixture.detectChanges();

      const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
      const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

      expect(extendButton.textContent).toEqual('Extend');
      expect(extendButton.disabled).toBeFalsy();
      expect(deleteButton.textContent).toEqual('Delete');
      expect(deleteButton.disabled).toBeTruthy();
    });

    it('should enable extend and delete button when student with extension selected', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      component.ngOnInit();
      component.studentsOfCourse[0].selected = true; // Alice has extension
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();

      const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
      const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

      expect(extendButton.textContent).toEqual('Extend');
      expect(extendButton.disabled).toBeFalsy();
      expect(deleteButton.textContent).toEqual('Delete');
      expect(deleteButton.disabled).toBeFalsy();
    });

    it('should disable delete button if one of selected students does not have extension', () => {
      jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
      jest.spyOn(courseService, 'getCourseAsInstructor').mockReturnValue(of(testCourse));
      jest.spyOn(feedbackSessionsService, 'getFeedbackSession').mockReturnValue(of(testFeedbackSession));
      component.ngOnInit();
      component.studentsOfCourse[0].selected = true; // Alice has extension
      component.studentsOfCourse[1].selected = true; // Bob does not
      component.isLoadingAllInstructors = false;
      component.isLoadingAllStudents = false;
      component.isLoadingFeedbackSession = false;

      fixture.detectChanges();

      const extendButton: any = fixture.debugElement.nativeElement.querySelector('#extend-btn');
      const deleteButton: any = fixture.debugElement.nativeElement.querySelector('#delete-btn');

      expect(extendButton.textContent).toEqual('Extend');
      expect(extendButton.disabled).toBeFalsy();
      expect(deleteButton.textContent).toEqual('Delete');
      expect(deleteButton.disabled).toBeTruthy();
    });

});
