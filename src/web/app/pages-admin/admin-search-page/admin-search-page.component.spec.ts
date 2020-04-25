import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { InstructorAccountSearchResult,
  SearchService, StudentAccountSearchResult } from '../../../services/search.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AdminSearchPageComponent } from './admin-search-page.component';

describe('AdminSearchPageComponent', () => {
  let component: AdminSearchPageComponent;
  let fixture: ComponentFixture<AdminSearchPageComponent>;
  let accountService: AccountService;
  let searchService: SearchService;
  let statusMessageService: StatusMessageService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminSearchPageComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
        MatSnackBarModule,
      ],
      providers: [AccountService, SearchService, StatusMessageService],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSearchPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.get(AccountService);
    searchService = TestBed.get(SearchService);
    statusMessageService = TestBed.get(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display error message for invalid input', () => {
    spyOn(searchService, 'searchAdmin').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('This is the error message');
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should display warning message for no results', () => {
    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: [],
      instructors: [],
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showWarningMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('No results found.');
      });

    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should display instructor results', () => {
    const instructorResults: InstructorAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
      },
      {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
      }];

    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: [],
      instructors: instructorResults,
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(0);
    expect(component.instructors.length).toEqual(2);
    expect(component.instructors).toEqual(instructorResults);
    expect(component.instructors[0].showLinks).toEqual(false);
    expect(component.instructors[1].showLinks).toEqual(false);
  });

  it('should display student results', () => {
    const studentResults: StudentAccountSearchResult[] = [
      {
        name: 'name1',
        email: 'email1',
        googleId: 'googleId1',
        courseId: 'courseId1',
        courseName: 'courseName1',
        institute: 'institute1',
        courseJoinLink: 'courseJoinLink1',
        homePageLink: 'homePageLink1',
        manageAccountLink: 'manageAccountLink1',
        showLinks: true,
        section: 'section1',
        team: 'team1',
        comments: 'comments1',
        recordsPageLink: 'recordsPageLink1',
        openSessions: { ['index']: 'session' },
        notOpenSessions: { ['index']: 'session' },
        publishedSessions: { ['index']: 'session' },
      }, {
        name: 'name2',
        email: 'email2',
        googleId: 'googleId2',
        courseId: 'courseId2',
        courseName: 'courseName2',
        institute: 'institute2',
        courseJoinLink: 'courseJoinLink2',
        homePageLink: 'homePageLink2',
        manageAccountLink: 'manageAccountLink2',
        showLinks: true,
        section: 'section2',
        team: 'team2',
        comments: 'comments2',
        recordsPageLink: 'recordsPageLink2',
        openSessions: { ['index']: 'session' },
        notOpenSessions: { ['index']: 'session' },
        publishedSessions: { ['index']: 'session' },
      }];

    spyOn(searchService, 'searchAdmin').and.returnValue(of({
      students: studentResults,
      instructors: [],
    }));

    component.searchQuery = 'name';
    const button: any = fixture.debugElement.nativeElement.querySelector('#search-button');
    button.click();

    expect(component.students.length).toEqual(2);
    expect(component.instructors.length).toEqual(0);
    expect(component.students).toEqual(studentResults);
    expect(component.students[0].showLinks).toEqual(false);
    expect(component.students[1].showLinks).toEqual(false);
  });

  it('should show instructor links when expand all button clicked', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-instructor-links');
    button.click();
    expect(component.instructors[0].showLinks).toEqual(true);
  });

  it('should show student links when expand all button clicked', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: { ['index']: 'session' },
      notOpenSessions: { ['index']: 'session' },
      publishedSessions: { ['index']: 'session' },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#show-student-links');
    button.click();
    expect(component.students[0].showLinks).toEqual(true);
  });

  it('should show success message if successfully reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    spyOn(accountService, 'resetInstructorAccount').and.returnValue(of('Success'));
    const spyStatusMessageService: any = spyOn(statusMessageService, 'showSuccessMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('The instructor\'s Google ID has been reset.');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset instructor google id', () => {
    const instructorResult: InstructorAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,
    };
    component.instructors = [instructorResult];
    fixture.detectChanges();

    spyOn(accountService, 'resetInstructorAccount').and.returnValue(throwError({
      error: {
        message: 'This is the error message',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('This is the error message');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-instructor-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show success message if successfully reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: { ['index']: 'session' },
      notOpenSessions: { ['index']: 'session' },
      publishedSessions: { ['index']: 'session' },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(accountService, 'resetStudentAccount').and.returnValue(of('success'));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showSuccessMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('The student\'s Google ID has been reset.');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });

  it('should show error message if fail to reset student google id', () => {
    const studentResult: StudentAccountSearchResult = {
      name: 'name',
      email: 'email',
      googleId: 'googleId',
      courseId: 'courseId',
      courseName: 'courseName',
      institute: 'institute',
      courseJoinLink: 'courseJoinLink',
      homePageLink: 'homePageLink',
      manageAccountLink: 'manageAccountLink',
      showLinks: false,

      section: 'section',
      team: 'team',
      comments: 'comments',
      recordsPageLink: 'recordsPageLink',
      openSessions: { ['index']: 'session' },
      notOpenSessions: { ['index']: 'session' },
      publishedSessions: { ['index']: 'session' },
    };
    component.students = [studentResult];
    fixture.detectChanges();

    spyOn(accountService, 'resetStudentAccount').and.returnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spyStatusMessageService: any = spyOn(statusMessageService, 'showErrorMessage').and.callFake(
      (args: string): void => {
        expect(args).toEqual('This is the error message.');
      });

    const link: any = fixture.debugElement.nativeElement.querySelector('#reset-student-id-0');
    link.click();

    expect(spyStatusMessageService).toBeCalled();
  });
});
