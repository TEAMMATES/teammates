import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import { Course, CourseArchive, Courses, JoinState, Students } from '../../../types/api-output';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { AddCourseFormModule } from './add-course-form/add-course-form.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';
import Spy = jasmine.Spy;

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  let courseService: CourseService;
  let studentService: StudentService;
  let simpleModalService: SimpleModalService;

  const date1: Date = new Date('2018-11-05T08:15:30');
  const date2: Date = new Date('2019-02-02T08:15:30');
  const date3: Date = new Date('2002-11-05T08:15:30');
  const date4: Date = new Date('2003-11-05T08:15:30');
  const date5: Date = new Date('2002-12-05T08:15:30');
  const date6: Date = new Date('2003-12-05T08:15:30');

  const activeCoursesSnap: any[] = [
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

  const archivedCoursesSnap: any[] = [
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

  const deletedCoursesSnap: any[] = [
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

  const courseStatsSnap: Record<string, Record<string, number>> = {
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

  const courseCS1231: Course = {
    courseId: 'CS1231',
    courseName: 'Discrete Structures',
    creationTimestamp: date1.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
  };

  const courseCS3281: Course = {
    courseId: 'CS3281',
    courseName: 'Thematic Systems Project I',
    creationTimestamp: date3.getTime(),
    deletionTimestamp: date4.getTime(),
    timeZone: 'UTC',
  };

  const courseCS3282: Course = {
    courseId: 'CS3282',
    courseName: 'Thematic Systems Project II',
    creationTimestamp: date5.getTime(),
    deletionTimestamp: date6.getTime(),
    timeZone: 'UTC',
  };

  const courseST4234: Course = {
    courseId: 'ST4234',
    courseName: 'Bayesian Statistics',
    creationTimestamp: date2.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
  };

  const courseModelCS1231: any = {
    course: courseCS1231,
    canModifyCourse: true,
    canModifyStudent: true,
    isLoadingCourseStats: false,
  };

  const courseModelCS3281: any = {
    course: courseCS3281,
    canModifyCourse: true,
    canModifyStudent: true,
    isLoadingCourseStats: false,
  };

  const courseModelCS3282: any = {
    course: courseCS3282,
    canModifyCourse: true,
    canModifyStudent: false,
    isLoadingCourseStats: false,
  };

  const courseModelST4234: any = {
    course: courseST4234,
    canModifyCourse: false,
    canModifyStudent: true,
    isLoadingCourseStats: false,
  };

  const students: Students = {
    students: [
      {
        email: 'alice.b.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Alice Betsy',
        lastName: 'Betsy',
        comments: "This student's name is Alice Betsy",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'benny.c.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Benny Charles',
        lastName: 'Charles',
        comments: "This student's name is Benny Charles",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'charlie.d.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Charlie Davis',
        lastName: 'Davis',
        comments: "This student's name is Charlie Davis",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'danny.e.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Danny Engrid',
        lastName: 'Engrid',
        comments: "This student's name is Danny Engrid",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'emma.f.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Emma Farrell',
        lastName: 'Farrell',
        comments: "This student's name is Emma Farrell",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'francis.g.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Francis Gabriel',
        lastName: 'Gabriel',
        comments: "This student's name is Francis Gabriel",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'gene.h.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Gene Hudson',
        lastName: 'Hudson',
        comments: "This student's name is Gene Hudson",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'hugh.i.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Hugh Ivanov',
        lastName: 'Ivanov',
        comments: "This student's name is Hugh Ivanov",
        joinState: JoinState.NOT_JOINED,
        teamName: 'Team 3',
        sectionName: 'Tutorial Group 2',
      },
    ],
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCoursesPageComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
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
    courseService = TestBed.inject(CourseService);
    studentService = TestBed.inject(StudentService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all courses by the instructor', () => {
    const courseSpy: Spy = spyOn(courseService, 'getAllCoursesAsInstructor').and.callFake(
      (courseStatus: string): Observable<Courses> => {
        if (courseStatus === 'active') {
          return of({ courses: [courseCS1231] });
        }
        if (courseStatus === 'archived') {
          return of({ courses: [courseCS3281, courseCS3282] });
        }
        // softDeleted
        return of({ courses: [courseST4234] });
      });

    component.loadInstructorCourses();

    expect(courseSpy.calls.count()).toEqual(3);
    expect(courseSpy.calls.all()[0].args[0]).toEqual('active');
    expect(courseSpy.calls.all()[1].args[0]).toEqual('archived');
    expect(courseSpy.calls.all()[2].args[0]).toEqual('softDeleted');

    expect(component.activeCourses.length).toEqual(1);
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
    expect(component.activeCourses[0].course.courseName).toEqual('Discrete Structures');

    expect(component.archivedCourses.length).toEqual(2);
    expect(component.archivedCourses[0].course.courseId).toEqual('CS3282');
    expect(component.archivedCourses[0].course.courseName).toEqual('Thematic Systems Project II');
    expect(component.archivedCourses[1].course.courseId).toEqual('CS3281');
    expect(component.archivedCourses[1].course.courseName).toEqual('Thematic Systems Project I');

    expect(component.softDeletedCourses.length).toEqual(1);
    expect(component.softDeletedCourses[0].course.courseId).toEqual('ST4234');
    expect(component.softDeletedCourses[0].course.courseName).toEqual('Bayesian Statistics');
  });

  it('should get the course statistics', () => {
    component.activeCourses = [courseModelCS1231];
    const studentSpy: Spy = spyOn(studentService, 'getStudentsFromCourse').and.returnValue(of(students));
    component.getCourseStats(0);

    expect(studentSpy.calls.count()).toEqual(1);
    expect(studentSpy.calls.mostRecent().args[0]).toEqual({ courseId: 'CS1231' });

    expect(component.courseStats.CS1231.sections).toEqual(2);
    expect(component.courseStats.CS1231.teams).toEqual(3);
    expect(component.courseStats.CS1231.students).toEqual(8);
    expect(component.courseStats.CS1231.unregistered).toEqual(1);
  });

  it('should archive an active course', () => {
    const courseArchiveCS1231: CourseArchive = {
      courseId: 'CS1231',
      isArchived: true,
    };
    component.activeCourses = [courseModelCS1231];
    const courseSpy: Spy = spyOn(courseService, 'changeArchiveStatus').and.returnValue(of(courseArchiveCS1231));
    component.changeArchiveStatus('CS1231', true);

    expect(courseSpy.calls.count()).toEqual(1);
    expect(courseSpy.calls.mostRecent().args[0]).toEqual('CS1231');
    expect(courseSpy.calls.mostRecent().args[1]).toEqual({ archiveStatus: true });

    expect(component.activeCourses.length).toEqual(0);
    expect(component.archivedCourses.length).toEqual(1);
    expect(component.archivedCourses[0].course.courseId).toEqual('CS1231');
  });

  it('should unarchive an archived course', () => {
    const courseArchiveCS1231: CourseArchive = {
      courseId: 'CS1231',
      isArchived: false,
    };
    component.archivedCourses = [courseModelCS1231];
    const courseSpy: Spy = spyOn(courseService, 'changeArchiveStatus').and.returnValue(of(courseArchiveCS1231));
    component.changeArchiveStatus('CS1231', false);

    expect(courseSpy.calls.count()).toEqual(1);
    expect(courseSpy.calls.mostRecent().args[0]).toEqual('CS1231');
    expect(courseSpy.calls.mostRecent().args[1]).toEqual({ archiveStatus: false });

    expect(component.archivedCourses.length).toEqual(0);
    expect(component.activeCourses.length).toEqual(1);
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
  });

  it('should soft delete a course', (done: any) => {
    component.activeCourses = [courseModelCS1231];
    const courseSpy: Spy = spyOn(courseService, 'binCourse').and.returnValue(of(courseCS1231));
    spyOn(simpleModalService, 'openConfirmationModal').and.returnValue({ result: Promise.resolve() });

    component.onDelete('CS1231').then(() => {
      expect(courseSpy.calls.count()).toEqual(1);
      expect(courseSpy.calls.mostRecent().args[0]).toEqual('CS1231');

      expect(component.softDeletedCourses.length).toEqual(1);
      expect(component.activeCourses.length).toEqual(0);
      done();
    });
  });

  it('should permanently delete a course', (done: any) => {
    component.archivedCourses = [courseModelCS1231];
    const courseSpy: Spy = spyOn(courseService, 'deleteCourse').and.returnValue(of(courseCS1231));
    spyOn(simpleModalService, 'openConfirmationModal').and.returnValue({
      componentInstance: {},
      result: Promise.resolve(),
    });

    component.onDeletePermanently('CS1231').then(() => {
      expect(courseSpy.calls.count()).toEqual(1);
      expect(courseSpy.calls.mostRecent().args[0]).toEqual('CS1231');

      expect(component.activeCourses.length).toEqual(0);
      expect(component.softDeletedCourses.length).toEqual(0);
      done();
    });
  });

  it('should show add course form and disable button when clicking on add new course', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoading = false;
    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-add-course');
    button.click();
    fixture.detectChanges();

    const div: any = fixture.debugElement.nativeElement.querySelector('#add-course-section');
    expect(div).toBeTruthy();
    expect(button.disabled).toBeTruthy();
  });

  it('should disable enroll button when instructor cannot modify student', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-enroll-disabled-0');
    expect(button.textContent).toEqual(' Enroll ');
    expect(button.className).toContain('disabled');
  });

  it('should disable delete button when instructor cannot modify active course', () => {
    component.activeCourses = [courseModelST4234];
    component.isLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-soft-delete-disabled-0');
    expect(button.textContent).toEqual(' Delete ');
    expect(button.className).toContain('disabled');
  });

  it('should disable delete button when instructor cannot modify archived course', () => {
    component.archivedCourses = [courseModelST4234];
    component.isLoading = false;
    component.isArchivedCourseExpanded = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-soft-delete-archived-disabled-0');
    expect(button.textContent).toEqual(' Delete ');
    expect(button.className).toContain('disabled');
  });

  it('should disable restore and permanently delete buttons when instructor cannot modify deleted course', () => {
    component.softDeletedCourses = [courseModelST4234];
    component.isLoading = false;
    component.isRecycleBinExpanded = true;
    fixture.detectChanges();

    const restoreButton: any = fixture.debugElement.nativeElement.querySelector('#btn-restore-disabled-0');
    expect(restoreButton.textContent).toEqual(' Restore ');
    expect(restoreButton.className).toContain('disabled');

    const disableButton: any = fixture.debugElement.nativeElement.querySelector('#btn-delete-disabled-0');
    expect(disableButton.textContent).toEqual(' Delete Permanently ');
    expect(disableButton.className).toContain('disabled');
  });

  it('should sort courses by their IDs', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-id');
    button.click();
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
    expect(component.activeCourses[1].course.courseId).toEqual('CS3281');
    expect(component.activeCourses[2].course.courseId).toEqual('CS3282');
    expect(component.activeCourses[3].course.courseId).toEqual('ST4234');
  });

  it('should sort courses by their names', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-course-name');
    button.click();
    expect(component.activeCourses[0].course.courseName).toEqual('Bayesian Statistics');
    expect(component.activeCourses[1].course.courseName).toEqual('Discrete Structures');
    expect(component.activeCourses[2].course.courseName).toEqual('Thematic Systems Project I');
    expect(component.activeCourses[3].course.courseName).toEqual('Thematic Systems Project II');
  });

  it('should sort courses by their creation dates', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoading = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#sort-creation-date');
    button.click();
    expect(component.activeCourses[0].course.courseId).toEqual('ST4234');
    expect(component.activeCourses[1].course.courseId).toEqual('CS1231');
    expect(component.activeCourses[2].course.courseId).toEqual('CS3282');
    expect(component.activeCourses[3].course.courseId).toEqual('CS3281');
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with all courses in course stats', () => {
    component.activeCourses = activeCoursesSnap;
    component.archivedCourses = archivedCoursesSnap;
    component.softDeletedCourses = deletedCoursesSnap;
    component.courseStats = courseStatsSnap;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it is undeletable and unrestorable', () => {
    component.activeCourses = activeCoursesSnap;
    component.archivedCourses = archivedCoursesSnap;
    component.softDeletedCourses = deletedCoursesSnap;
    component.courseStats = courseStatsSnap;
    component.canDeleteAll = false;
    component.canRestoreAll = false;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no courses in course stats', () => {
    component.activeCourses = activeCoursesSnap;
    component.isLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when new course form is expanded', () => {
    component.isAddNewCourseFormExpanded = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when archived courses are expanded', () => {
    component.archivedCourses = archivedCoursesSnap;
    component.isArchivedCourseExpanded = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
