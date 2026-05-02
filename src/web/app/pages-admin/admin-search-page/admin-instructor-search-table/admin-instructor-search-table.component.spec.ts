import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AdminInstructorSearchTableComponent } from './admin-instructor-search-table.component';
import { FeedbackSessionsGroup, InstructorAccountSearchResult } from '../../../../services/search.service';

const DEFAULT_SESSION_ID = '17681c09-f4e5-40c2-be77-eeccf0c221c2';
const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  [DEFAULT_SESSION_ID]: {
    name: 'sessionName',
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const DEFAULT_INSTRUCTOR_SEARCH_RESULT: InstructorAccountSearchResult = {
  userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
  name: 'name',
  email: 'email',
  googleId: 'googleId',
  courseId: 'courseId',
  courseName: 'courseName',
  isCourseDeleted: false,
  institute: 'institute',
  courseJoinLink: 'courseJoinLink',
  homePageLink: 'homePageLink',
  manageAccountLink: 'manageAccountLink',
  showLinks: false,
  awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
};

describe('AdminInstructorSearchTableComponent', () => {
  let component: AdminInstructorSearchTableComponent;
  let fixture: ComponentFixture<AdminInstructorSearchTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminInstructorSearchTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with a deleted course', () => {
    component.instructors = [
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
        name: 'instructor',
        email: 'instructor@tester.com',
        googleId: 'ins-google-id',
        courseId: 'deleted-course',
        courseName: 'deleted',
        isCourseDeleted: true,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: false,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded instructor table', () => {
    component.instructors = [
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
        name: 'tester',
        email: 'tester@tester.com',
        googleId: 'instructor-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        isCourseDeleted: false,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = DEFAULT_INSTRUCTOR_SEARCH_RESULT;
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-instructor-links');
    button.click();
    expect(component.instructors[0].showLinks).toEqual(true);
  });

  it('should hide instructor links when collapse all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = {
      ...DEFAULT_INSTRUCTOR_SEARCH_RESULT,
      showLinks: true,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#hide-instructor-links');
    button.click();
    expect(component.instructors[0].showLinks).toEqual(false);
  });
});
