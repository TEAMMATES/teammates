import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminSearchPageComponent } from './admin-search-page.component';
import {
  FeedbackSessionsGroup,
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';

const DEFAULT_SESSION_ID = '17681c09-f4e5-40c2-be77-eeccf0c221c2';
const DEFAULT_FEEDBACK_SESSION_GROUP: FeedbackSessionsGroup = {
  [DEFAULT_SESSION_ID]: {
    name: 'sessionName',
    feedbackSessionUrl: 'sessionUrl',
    startTime: 'startTime',
    endTime: 'endTime',
  },
};

describe('AdminSearchPageComponent', () => {
  let component: AdminSearchPageComponent;
  let fixture: ComponentFixture<AdminSearchPageComponent>;
  let searchService: SearchService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminSearchPageComponent);
    component = fixture.componentInstance;
    searchService = TestBed.inject(SearchService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a search key', () => {
    component.searchQuery = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display error message for invalid input', () => {
    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message',
        },
      })),
    );

    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message');
      });

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display warning message for no results', () => {
    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: [],
        instructors: [],
        accountRequests: [],
      }),
    );

    const spyStatusMessageService = vi
      .spyOn(statusMessageService, 'showWarningToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('No results found.');
      });

    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toHaveBeenCalled();
  });

  it('should display instructor results', () => {
    const instructorResults: InstructorAccountSearchResult[] = [
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
        name: 'name1',
        email: 'email1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        isCourseDeleted: false,
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
      {
        userId: '42aca1be-044d-48c8-a8c2-2bac0e287eb4',
        name: 'name2',
        email: 'email2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: [],
        instructors: instructorResults,
        accountRequests: [],
      }),
    );

    component.searchQuery = 'name';
    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(2);
    expect(component.accountRequests.length).toEqual(0);
    expect(component.instructors).toEqual(instructorResults);
  });

  it('should display student results', () => {
    const studentResults: StudentAccountSearchResult[] = [
      {
        userId: '42aca1be-044d-48c8-b27c-26c29daf512c',
        name: 'name1',
        email: 'email1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        isCourseDeleted: false,
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        section: 'section1',
        team: 'team1',
        comments: 'comments1',
        profilePageLink: 'profilePageLink1',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
        name: 'name2',
        email: 'email2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        section: 'section2',
        team: 'team2',
        comments: 'comments2',
        profilePageLink: 'profilePageLink2',
        awaitingSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        openSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        notOpenSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
        publishedSessions: DEFAULT_FEEDBACK_SESSION_GROUP,
      },
    ];

    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: studentResults,
        instructors: [],
        accountRequests: [],
      }),
    );

    component.searchQuery = 'name';
    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(2);
    expect(component.instructors.length).toEqual(0);
    expect(component.accountRequests.length).toEqual(0);
    expect(component.students).toEqual(studentResults);
  });
});
