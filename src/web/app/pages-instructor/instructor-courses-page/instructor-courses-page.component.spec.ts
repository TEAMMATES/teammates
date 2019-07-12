import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';

@Component({ selector: 'tm-add-course-form', template: '' })
class AddCourseFormStubComponent {}

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  const activeCourses: any[] = [
    {
      courseId: 'CS3281',
      courseName: 'Modifiable Students and Courses',
      timeZone: 'UTC',
      creationTimestamp: 1541376930000,
      deletionTimestamp: 0,
      canModifyCourse: true,
      canModifyStudent: true,
    },
    {
      courseId: 'CS3282',
      courseName: 'Nothing modifiable',
      timeZone: 'UTC',
      creationTimestamp: 1549066530000,
      deletionTimestamp: 0,
      canModifyCourse: false,
      canModifyStudent: false,
    },
  ];

  const archivedCourses: any[] = [
    {
      courseId: 'CS2104',
      courseName: 'Can modify archived',
      timeZone: 'UTC',
      creationTimestamp: 1036455330000,
      deletionTimestamp: 0,
      canModifyCourse: true,
    },
    {
      courseId: 'CS2106',
      courseName: 'Cannot modify archived',
      timeZone: 'UTC',
      creationTimestamp: 1036455330000,
      deletionTimestamp: 0,
      canModifyCourse: false,
    },
  ];

  const deletedCourses: any[] = [
    {
      courseId: 'CS1020',
      courseName: 'Can modify deleted',
      timeZone: 'UTC',
      creationTimestamp: 1036455330000,
      deletionTimestamp: 1067991330000,
      canModifyCourse: true,
    },
    {
      courseId: 'CS2010',
      courseName: 'Cannot modify deleted',
      timeZone: 'UTC',
      creationTimestamp: 1036455330000,
      deletionTimestamp: 1067991330000,
      canModifyCourse: false,
    },
  ];

  const courseStats: {[key: string]: { [key: string]: number }} = {
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
        AddCourseFormStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgbModule,
        MatSnackBarModule,
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
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no courses in course stats', () => {
    component.activeCourses = activeCourses;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
