import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { AddCourseFormModule } from './add-course-form/add-course-form.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  const date1: Date = new Date('2018-11-05T08:15:30');
  const date2: Date = new Date('2019-02-02T08:15:30');
  const date3: Date = new Date('2002-11-05T08:15:30');
  const date4: Date = new Date('2003-11-05T08:15:30');
  const date5: Date = new Date('2002-12-05T08:15:30');
  const date6: Date = new Date('2003-12-05T08:15:30');
  const activeCourses: any[] = [
    {
      course: {
        courseId: 'CS3281',
        courseName: 'Modifiable Students and Courses',
        timeZone: 'UTC',
        creationTimestamp: date1.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: true,
      canModifyStudent: true,
    },
    {
      course: {
        courseId: 'CS3282',
        courseName: 'Nothing modifiable',
        timeZone: 'UTC',
        creationTimestamp: date2.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: false,
      canModifyStudent: false,
    },
  ];

  const archivedCourses: any[] = [
    {
      course: {
        courseId: 'CS2104',
        courseName: 'Can modify archived',
        timeZone: 'UTC',
        creationTimestamp: date3.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: true,
    },
    {
      course: {
        courseId: 'CS2106',
        courseName: 'Cannot modify archived',
        timeZone: 'UTC',
        creationTimestamp: date3.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: false,
    },
  ];

  const deletedCourses: any[] = [
    {
      course: {
        courseId: 'CS1020',
        courseName: 'Can modify deleted',
        timeZone: 'UTC',
        creationTimestamp: date3.getTime(),
        deletionTimestamp: date4.getTime(),
      },
      canModifyCourse: true,
    },
    {
      course: {
        courseId: 'CS2010',
        courseName: 'Cannot modify deleted',
        timeZone: 'UTC',
        creationTimestamp: date5.getTime(),
        deletionTimestamp: date6.getTime(),
      },
      canModifyCourse: false,
    },
  ];

  const courseStats: Record<string, Record<string, number>> = {
    CS3281 : {
      sections: 1,
      teams: 1,
      students: 1,
      unregistered: 1,
    },
    CS3282: {
      sections: 2,
      teams: 2,
      students: 2,
      unregistered: 2,
    },
  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCoursesPageComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgbModule,
        BrowserAnimationsModule,
        LoadingSpinnerModule,
        AjaxLoadingModule,
        AddCourseFormModule,
        LoadingRetryModule,
        PanelChevronModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCoursesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all courses in course stats', () => {
    component.activeCourses = activeCourses;
    component.archivedCourses = archivedCourses;
    component.softDeletedCourses = deletedCourses;
    component.courseStats = courseStats;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it is undeletable and unrestorable', () => {
    component.activeCourses = activeCourses;
    component.archivedCourses = archivedCourses;
    component.softDeletedCourses = deletedCourses;
    component.courseStats = courseStats;
    component.canDeleteAll = false;
    component.canRestoreAll = false;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no courses in course stats', () => {
    component.activeCourses = activeCourses;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
