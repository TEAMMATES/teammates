import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {
  Course, FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorPrivilege,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { InstructorHomePageComponent } from './instructor-home-page.component';
import { InstructorHomePageModule } from './instructor-home-page.module';

const instructorPrivilege: InstructorPrivilege = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
  canModifyInstructor: false,
  canViewStudentInSections: false,
  canModifySessionCommentsInSections: false,
  canViewSessionInSections: false,
};

const defaultCourse: Course = {
  courseId: 'CS3281',
  courseName: 'Thematic Systems',
  creationTimestamp: 4924740000,
  deletionTimestamp: 0,
  timeZone: 'Asia/Singapore',
};

const feedbackSession: FeedbackSession = {
  courseId: 'CS3281',
  timeZone: 'Asia/Singapore',
  feedbackSessionName: 'Feedback',
  instructions: 'Answer all questions',
  submissionStartTimestamp: 1552390757,
  submissionEndTimestamp: 1552590757,
  gracePeriod: 0,
  sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
  customSessionVisibleTimestamp: 0,
  responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
  customResponseVisibleTimestamp: 0,
  submissionStatus: FeedbackSessionSubmissionStatus.NOT_VISIBLE,
  publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,
  createdAtTimestamp: 0,
};

describe('InstructorHomePageComponent', () => {
  let component: InstructorHomePageComponent;
  let fixture: ComponentFixture<InstructorHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        InstructorHomePageModule,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course without feedback session', () => {
    const courseTabModels: any = {
      instructorPrivilege,
      course: {
        courseId: 'CS3243',
        courseName: 'Introduction to AI',
        creationTimestamp: 1546198490630,
        timeZone: 'Asia/Singapore',
      },
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with unpopulated feedback sessions', () => {
    const courseTabModels: any = {
      instructorPrivilege,
      course: defaultCourse,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with error loading feedback sessions', () => {
    const courseTabModels: any = {
      instructorPrivilege,
      course: defaultCourse,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: false,
      isTabExpanded: true,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with unexpanded course tab', () => {
    const courseTabModels: any = {
      instructorPrivilege,
      course: defaultCourse,
      sessionsTableRowModels: [],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: false,
      isAjaxSuccess: true,
      isTabExpanded: false,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with one feedback session with instructor privilege', () => {
    const sessionsTableRowModel: any = {
      feedbackSession,
      instructorPrivilege,
      responseRate: '0 / 6',
      isLoadingResponseRate: false,
    };
    const courseTabModels: any = {
      instructorPrivilege,
      course: defaultCourse,
      sessionsTableRowModels: [sessionsTableRowModel],
      sessionsTableRowModelsSortBy: SortBy.NONE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with one course with two feedback sessions with tutor privilege', () => {
    const feedbackSession1: any = {
      courseId: 'CS3281',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Feedback 1',
      instructions: 'Answer all questions',
      submissionStartTimestamp: 0,
      submissionEndTimestamp: 1,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      customSessionVisibleTimestamp: 0,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      customResponseVisibleTimestamp: 0,
      submissionStatus: FeedbackSessionSubmissionStatus.CLOSED,
      publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
    };
    const feedbackSession2: any = {
      courseId: 'CS3281',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Feedback 2',
      instructions: 'Answer all questions',
      submissionStartTimestamp: 10000,
      submissionEndTimestamp: 15000,
      gracePeriod: 100,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      customSessionVisibleTimestamp: 0,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      customResponseVisibleTimestamp: 0,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 1000,
    };
    const tutorPrivilege: any = {
      canModifyCourse: false,
      canModifySession: false,
      canModifyStudent: false,
      canSubmitSessionInSections: false,
    };
    const sessionsTableRowModel1: any = {
      feedbackSession: feedbackSession1,
      instructorPrivilege: tutorPrivilege,
      responseRate: '0 / 6',
      isLoadingResponseRate: false,
    };
    const sessionsTableRowModel2: any = {
      feedbackSession: feedbackSession2,
      instructorPrivilege: tutorPrivilege,
      responseRate: '5 / 6',
      isLoadingResponseRate: false,
    };
    const courseTabModels: any = {
      instructorPrivilege: tutorPrivilege,
      course: defaultCourse,
      sessionsTableRowModels: [sessionsTableRowModel1, sessionsTableRowModel2],
      sessionsTableRowModelsSortBy: SortBy.COURSE_CREATION_DATE,
      sessionsTableRowModelsSortOrder: SortOrder.ASC,
      hasPopulated: true,
      isAjaxSuccess: true,
      isTabExpanded: true,
    };
    component.courseTabModels = [courseTabModels];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
