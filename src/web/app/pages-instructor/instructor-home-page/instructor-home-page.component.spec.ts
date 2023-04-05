import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course, CourseArchive, Courses,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  InstructorPermissionSet,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { CourseTabModel, InstructorHomePageComponent } from './instructor-home-page.component';
import { InstructorHomePageModule } from './instructor-home-page.module';

const testInstructorPrivilege: InstructorPermissionSet = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
  canModifyInstructor: false,
  canViewStudentInSections: false,
  canModifySessionCommentsInSections: false,
  canViewSessionInSections: false,
};

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
  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,
  createdAtTimestamp: 0,
  studentDeadlines: {},
  instructorDeadlines: {},
};

const testFeedbackSession2: FeedbackSession = {
  feedbackSessionName: 'Second Session',
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
  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,
  createdAtTimestamp: 0,
  studentDeadlines: {},
  instructorDeadlines: {},
};

const activeCourseTabModels: CourseTabModel[] = [
  {
    course: testCourse1,
    instructorPrivilege: testInstructorPrivilege,
    sessionsTableRowModels: [],
    sessionsTableRowModelsSortBy: SortBy.NONE,
    sessionsTableRowModelsSortOrder: SortOrder.ASC,

    hasPopulated: false,
    isAjaxSuccess: true,
    isTabExpanded: true,
    hasLoadingFailed: false,
  },
  {
    course: testCourse2,
    instructorPrivilege: testInstructorPrivilege,
    sessionsTableRowModels: [],
    sessionsTableRowModelsSortBy: SortBy.NONE,
    sessionsTableRowModelsSortOrder: SortOrder.ASC,

    hasPopulated: false,
    isAjaxSuccess: true,
    isTabExpanded: true,
    hasLoadingFailed: false,
  },
];

describe('InstructorHomePageComponent', () => {
  let courseService: CourseService;
  let simpleModalService: SimpleModalService;
  let feedbackSessionsService: FeedbackSessionsService;
  let component: InstructorHomePageComponent;
  let fixture: ComponentFixture<InstructorHomePageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorHomePageModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
        BrowserAnimationsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHomePageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    simpleModalService = TestBed.inject(SimpleModalService);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get course candidates', () => {
    component.courseTabModels = activeCourseTabModels;
    fixture.detectChanges();

    const courses: Course[] = component.courseCandidates;

    expect(courses.length).toEqual(2);
    expect(courses[0].courseId).toEqual('CS1231');
    expect(courses[0].courseName).toEqual('Discrete Structures');
  });

  it('should expand the course tab model upon clicking', () => {
    component.courseTabModels = activeCourseTabModels;
    component.hasCoursesLoaded = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('.card-header');
    button.click();
    expect(component.courseTabModels[0].isTabExpanded).toBeFalsy();
    button.click();
    expect(component.courseTabModels[0].isTabExpanded).toBeTruthy();
  });

  it('should archive the entire course from the instructor', () => {
    const courseArchive: CourseArchive = {
      courseId: 'CS1231',
      isArchived: true,
    };

    component.courseTabModels = activeCourseTabModels;
    component.hasCoursesLoaded = true;
    fixture.detectChanges();

    expect(component.courseTabModels.length).toEqual(2);
    expect(component.courseTabModels[0].course.courseId).toEqual('CS1231');
    expect(component.courseTabModels[0].course.courseName).toEqual('Discrete Structures');

    jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(
        () => createMockNgbModalRef({
          header: 'mock header', content: 'mock content', type: SimpleModalType.INFO,
        }),
    );
    jest.spyOn(courseService, 'changeArchiveStatus').mockReturnValue(of(courseArchive));

    const courseButton: any = fixture.debugElement.nativeElement.querySelector('.btn-course');
    courseButton.click();
    const archiveButton: any = fixture.debugElement.nativeElement.querySelector('.btn-archive-course');
    archiveButton.click();

    expect(component.courseTabModels.length).toEqual(1);
    expect(component.courseTabModels[0].course.courseId).toEqual('CS3281');
    expect(component.courseTabModels[0].course.courseName).toEqual('Thematic Systems I');
  });

  it('should delete the entire course from the instructor', () => {
    const courseToDelete: Course = testCourse1;

    component.courseTabModels = activeCourseTabModels;
    component.hasCoursesLoaded = true;
    fixture.detectChanges();

    expect(component.courseTabModels.length).toEqual(2);
    expect(component.courseTabModels[0].course.courseId).toEqual('CS1231');
    expect(component.courseTabModels[0].course.courseName).toEqual('Discrete Structures');

    jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(
        () => createMockNgbModalRef({
          header: 'mock header', content: 'mock content', type: SimpleModalType.WARNING,
        }),
    );
    jest.spyOn(courseService, 'binCourse').mockReturnValue(of(courseToDelete));

    const courseButton: any = fixture.debugElement.nativeElement.querySelector('.btn-course');
    courseButton.click();
    const archiveButton: any = fixture.debugElement.nativeElement.querySelector('.btn-delete-course');
    archiveButton.click();

    expect(component.courseTabModels.length).toEqual(1);
    expect(component.courseTabModels[0].course.courseId).toEqual('CS3281');
    expect(component.courseTabModels[0].course.courseName).toEqual('Thematic Systems I');
  });

  it('should load courses of the current instructor', () => {
    const activeCourses: Courses = {
      courses: [testCourse1, testCourse2],
    };

    jest.spyOn(courseService, 'getInstructorCoursesThatAreActive').mockReturnValue(of(activeCourses));
    component.loadCourses();

    expect(component.hasCoursesLoaded).toBeTruthy();
    // panels are sorted in descending order by default
    expect(component.courseTabModels[0].course.courseId).toEqual('CS3281');
    expect(component.courseTabModels[0].course.courseName).toEqual('Thematic Systems I');
    expect(component.courseTabModels[1].course.courseId).toEqual('CS1231');
    expect(component.courseTabModels[1].course.courseName).toEqual('Discrete Structures');
    expect(component.courseTabModels.length).toEqual(2);
    expect(component.isNewUser).toBeFalsy();
  });

  it('should load feedbackSessions in the course', () => {
    const courseSessions: FeedbackSessions = {
      feedbackSessions: [testFeedbackSession1, testFeedbackSession2],
    };

    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor').mockReturnValue(of(courseSessions));
    component.courseTabModels = activeCourseTabModels;
    component.loadFeedbackSessions(0);
    fixture.detectChanges();

    expect(component.courseTabModels[0].hasLoadingFailed).toBeFalsy();
    expect(component.courseTabModels[0].hasPopulated).toBeTruthy();
    expect(component.courseTabModels[0].isAjaxSuccess).toBeTruthy();
    expect(component.courseTabModels[0].sessionsTableRowModels.length).toEqual(2);

    expect(component.courseTabModels[0].sessionsTableRowModels[0]
            .feedbackSession.feedbackSessionName).toEqual('Second Session');
    expect(component.courseTabModels[0].sessionsTableRowModels[1]
            .feedbackSession.feedbackSessionName).toEqual('First Session');
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course without feedback session', () => {
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse1,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with unpopulated feedback sessions', () => {
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse2,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with error loading feedback sessions', () => {
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse2,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: false,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with unexpanded course tab', () => {
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse2,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: true,
      isTabExpanded: false,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with one feedback session with instructor privilege', () => {
    const sessionsTableRowModel: any = {
      feedbackSession: testFeedbackSession1,
      instructorPrivilege: testInstructorPrivilege,
      responseRate: '0 / 6',
      isLoadingResponseRate: false,
    };
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse1,
      sessionsTableRowModels: [sessionsTableRowModel],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with two feedback sessions with tutor privilege', () => {
    const tutorPrivilege: any = {
      canModifyCourse: false,
      canModifySession: false,
      canModifyStudent: false,
      canSubmitSessionInSections: false,
    };
    const sessionsTableRowModel1: any = {
      feedbackSession: testFeedbackSession1,
      instructorPrivilege: tutorPrivilege,
      responseRate: '0 / 6',
      isLoadingResponseRate: false,
    };
    const sessionsTableRowModel2: any = {
      feedbackSession: testFeedbackSession2,
      instructorPrivilege: tutorPrivilege,
      responseRate: '5 / 6',
      isLoadingResponseRate: false,
    };
    const courseTabModels: any = {
      instructorPrivilege: tutorPrivilege,
      course: testCourse2,
      sessionsTableRowModels: [sessionsTableRowModel1, sessionsTableRowModel2],
      sessionsTableRowModelsSortBy: SortBy.COURSE_CREATION_DATE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = true;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    const courseTabModels: any = {
      instructorPrivilege: testInstructorPrivilege,
      course: testCourse2,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.hasCoursesLoaded = false;
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
