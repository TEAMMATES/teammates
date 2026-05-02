import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AdminStudentSearchTableComponent } from './admin-student-search-table.component';
import { FeedbackSessionsGroup, StudentAccountSearchResult } from '../../../../services/search.service';

const DEFAULT_SESSION_ID = '17681c09-f4e5-40c2-be77-eeccf0c221c2';
const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  [DEFAULT_SESSION_ID]: {
    name: 'sessionName',
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

const DEFAULT_STUDENT_SEARCH_RESULT: StudentAccountSearchResult = {
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
  section: 'section',
  team: 'team',
  comments: 'comments',
  profilePageLink: 'profilePageLink',
  awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
  publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
};

describe('AdminStudentSearchTableComponent', () => {
  let component: AdminStudentSearchTableComponent;
  let fixture: ComponentFixture<AdminStudentSearchTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminStudentSearchTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with a deleted course', () => {
    component.students = [
      {
        userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
        name: 'student',
        email: 'student@gmail.tmt',
        googleId: 'student-google-id',
        courseId: 'deleted-course',
        courseName: 'deleted',
        isCourseDeleted: true,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: false,
        section: 'section',
        team: 'team',
        comments: 'comments',
        profilePageLink: 'profile-page-link',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with an expanded student table', () => {
    component.students = [
      {
        userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
        name: 'Alice Betsy',
        email: 'alice.b.tmms@gmail.tmt',
        googleId: 'student-google-id',
        courseId: 'test-exa.demo',
        courseName: 'demo',
        isCourseDeleted: false,
        institute: 'institute',
        courseJoinLink: 'course-join-link',
        homePageLink: 'home-page-link',
        manageAccountLink: 'manage-account-link',
        showLinks: true,
        section: 'section',
        team: 'team',
        comments: 'comments',
        profilePageLink: 'profile-page-link',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should show student links when expand all button clicked', () => {
    const studentResult: StudentAccountSearchResult = DEFAULT_STUDENT_SEARCH_RESULT;
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(true);
  });

  it('should hide student links when collapse all button clicked', () => {
    const studentResult: StudentAccountSearchResult = {
      ...DEFAULT_STUDENT_SEARCH_RESULT,
      showLinks: true,
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#hide-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(false);
  });
});
