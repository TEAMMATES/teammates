import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AdminSearchPageComponent } from './admin-search-page.component';
import {
  InstructorAccountSearchResult,
  SearchService,
  StudentAccountSearchResult,
} from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';

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
        manageAccountLink: 'manageAccountLink1',
      },
      {
        userId: '42aca1be-044d-48c8-a8c2-2bac0e287eb4',
        name: 'name2',
        email: 'email2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        manageAccountLink: 'manageAccountLink2',
      },
    ];

    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: [],
        instructors: instructorResults,
      }),
    );

    component.searchQuery = 'name';
    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(2);
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
        manageAccountLink: 'manageAccountLink1',
        section: 'section1',
        team: 'team1',
        comments: 'comments1',
        profilePageLink: 'profilePageLink1',
      },
      {
        userId: '81c1aaee-24f6-46f4-a8c2-2bac0e287eb4',
        name: 'name2',
        email: 'email2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        isCourseDeleted: false,
        institute: 'institute2',
        manageAccountLink: 'manageAccountLink2',
        section: 'section2',
        team: 'team2',
        comments: 'comments2',
        profilePageLink: 'profilePageLink2',
      },
    ];

    vi.spyOn(searchService, 'searchAdmin').mockReturnValue(
      of({
        students: studentResults,
        instructors: [],
      }),
    );

    component.searchQuery = 'name';
    const button: HTMLElement = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(2);
    expect(component.instructors.length).toEqual(0);
    expect(component.students).toEqual(studentResults);
  });
});
