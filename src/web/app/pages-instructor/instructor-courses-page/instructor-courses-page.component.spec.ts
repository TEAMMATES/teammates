import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course, CourseArchive, Courses, JoinState, Students } from '../../../types/api-output';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CourseEditFormComponent } from '../../components/course-edit-form/course-edit-form.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  let courseService: CourseService;
  let studentService: StudentService;
  let timezoneService: TimezoneService;
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
    CS3281: {
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
    institute: 'Test Institute',
  };

  const courseCS3281: Course = {
    courseId: 'CS3281',
    courseName: 'Thematic Systems Project I',
    creationTimestamp: date3.getTime(),
    deletionTimestamp: date4.getTime(),
    timeZone: 'UTC',
    institute: 'Test Institute',
  };

  const courseCS3282: Course = {
    courseId: 'CS3282',
    courseName: 'Thematic Systems Project II',
    creationTimestamp: date5.getTime(),
    deletionTimestamp: date6.getTime(),
    timeZone: 'UTC',
    institute: 'Test Institute',
  };

  const courseST4234: Course = {
    courseId: 'ST4234',
    courseName: 'Bayesian Statistics',
    creationTimestamp: date2.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
    institute: 'Test Institute',
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
        comments: "This student's name is Alice Betsy",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'benny.c.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Benny Charles',
        comments: "This student's name is Benny Charles",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'charlie.d.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Charlie Davis',
        comments: "This student's name is Charlie Davis",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'danny.e.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Danny Engrid',
        comments: "This student's name is Danny Engrid",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'emma.f.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Emma Farrell',
        comments: "This student's name is Emma Farrell",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        sectionName: 'Tutorial Group 1',
      },
      {
        email: 'francis.g.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Francis Gabriel',
        comments: "This student's name is Francis Gabriel",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'gene.h.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Gene Hudson',
        comments: "This student's name is Gene Hudson",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        sectionName: 'Tutorial Group 2',
      },
      {
        email: 'hugh.i.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        name: 'Hugh Ivanov',
        comments: "This student's name is Hugh Ivanov",
        joinState: JoinState.NOT_JOINED,
        teamName: 'Team 3',
        sectionName: 'Tutorial Group 2',
      },
    ],
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCoursesPageComponent,
        CourseEditFormComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
        NgbModule,
        BrowserAnimationsModule,
        LoadingSpinnerModule,
        AjaxLoadingModule,
        LoadingRetryModule,
        PanelChevronModule,
        ProgressBarModule,
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCoursesPageComponent);
    component = fixture.componentInstance;
    courseService = TestBed.inject(CourseService);
    studentService = TestBed.inject(StudentService);
    timezoneService = TestBed.inject(TimezoneService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all courses by the instructor', () => {
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'getAllCoursesAsInstructor').mockImplementation(
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

    expect(courseSpy).toHaveBeenCalledTimes(3);
    expect(courseSpy).toHaveBeenNthCalledWith(1, 'active');
    expect(courseSpy).toHaveBeenNthCalledWith(2, 'archived');
    expect(courseSpy).toHaveBeenNthCalledWith(3, 'softDeleted');

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
    const studentSpy: SpyInstance = jest.spyOn(studentService, 'getStudentsFromCourse').mockReturnValue(of(students));
    component.getCourseStats(0);

    expect(studentSpy).toHaveBeenCalledTimes(1);
    expect(studentSpy).toHaveBeenLastCalledWith({ courseId: 'CS1231' });

    expect(component.courseStats['CS1231']['sections']).toEqual(2);
    expect(component.courseStats['CS1231']['teams']).toEqual(3);
    expect(component.courseStats['CS1231']['students']).toEqual(8);
    expect(component.courseStats['CS1231']['unregistered']).toEqual(1);
  });

  it('should archive an active course', () => {
    const courseArchiveCS1231: CourseArchive = {
      courseId: 'CS1231',
      isArchived: true,
    };
    component.activeCourses = [courseModelCS1231];
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'changeArchiveStatus')
        .mockReturnValue(of(courseArchiveCS1231));
    component.changeArchiveStatus('CS1231', true);

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenLastCalledWith('CS1231', { archiveStatus: true });

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
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'changeArchiveStatus')
        .mockReturnValue(of(courseArchiveCS1231));
    component.changeArchiveStatus('CS1231', false);

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenNthCalledWith(1, 'CS1231', { archiveStatus: false });

    expect(component.archivedCourses.length).toEqual(0);
    expect(component.activeCourses.length).toEqual(1);
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
  });

  it('should restore a soft deleted course', () => {
    component.softDeletedCourses = [courseModelCS1231];
    expect(component.softDeletedCourses.length).toEqual(1);

    const courseSpy: SpyInstance = jest.spyOn(courseService, 'restoreCourse')
      .mockReturnValue(of(courseModelCS1231));
    jest.spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef());

    component.onRestore('CS1231');

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenNthCalledWith(1, 'CS1231');

    expect(component.archivedCourses.length).toEqual(0);
    expect(component.softDeletedCourses.length).toEqual(0);
});

  it('should soft delete a course', async () => {
    component.activeCourses = [courseModelCS1231];
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'binCourse').mockReturnValue(of(courseCS1231));
    jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef());

    await component.onDelete('CS1231').then(() => {
      expect(courseSpy).toHaveBeenCalledTimes(1);
      expect(courseSpy).toHaveBeenLastCalledWith('CS1231');

      expect(component.softDeletedCourses.length).toEqual(1);
      expect(component.activeCourses.length).toEqual(0);
    });
  });

  it('should permanently delete a course', async () => {
    component.archivedCourses = [courseModelCS1231];
    const courseSpy: SpyInstance = jest.spyOn(courseService, 'deleteCourse')
        .mockReturnValue(of({ message: 'Message' }));
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(
        createMockNgbModalRef());

    await component.onDeletePermanently('CS1231').then(() => {
      expect(courseSpy).toHaveBeenCalledTimes(1);
      expect(courseSpy).toHaveBeenLastCalledWith('CS1231');

      expect(component.activeCourses.length).toEqual(0);
      expect(component.softDeletedCourses.length).toEqual(0);
    });
  });

  it('should show add course form and disable button when clicking on add new course', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoadingActiveCourses = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-add-course');
    button.click();
    fixture.detectChanges();

    const div: any = fixture.debugElement.nativeElement.querySelector('#add-course-section');
    expect(div).toBeTruthy();
    expect(button.disabled).toBeTruthy();
  });

  it('should disable enroll button when instructor cannot modify student', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoadingActiveCourses = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-enroll-disabled-0');
    expect(button.textContent).toEqual(' Enroll ');
    expect(button.className).toContain('disabled');
  });

  it('should disable delete button when instructor cannot modify active course', () => {
    component.activeCourses = [courseModelST4234];
    component.isLoadingActiveCourses = false;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-soft-delete-disabled-0');
    expect(button.textContent).toEqual(' Delete ');
    expect(button.className).toContain('disabled');
  });

  it('should disable delete button when instructor cannot modify archived course', () => {
    component.archivedCourses = [courseModelST4234];
    component.isLoadingArchivedCourses = false;
    component.isArchivedCourseExpanded = true;
    fixture.detectChanges();

    const button: any = fixture.debugElement.nativeElement.querySelector('#btn-soft-delete-archived-disabled-0');
    expect(button.textContent).toEqual(' Delete ');
    expect(button.className).toContain('disabled');
  });

  it('should disable restore and permanently delete buttons when instructor cannot modify deleted course', () => {
    component.softDeletedCourses = [courseModelST4234];
    component.isLoadingSoftDeletedCourses = false;
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
    component.isLoadingActiveCourses = false;
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
    component.isLoadingActiveCourses = false;
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
    component.isLoadingActiveCourses = false;
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
    component.isLoadingActiveCourses = false;
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
    component.isLoadingActiveCourses = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no courses in course stats', () => {
    component.activeCourses = activeCoursesSnap;
    component.isLoadingActiveCourses = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isLoadingActiveCourses = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when new course form is expanded', () => {
    component.isAddNewCourseFormExpanded = true;
    component.isLoadingActiveCourses = false;
    // Mock the timezone service to prevent unexpected changes in time zones over time, such as daylight savings time
    const timezones: Record<string, number> = {
      Jamaica: -5 * 60,
      Portugal: 0,
      Singapore: 8 * 60,
      Turkey: 3 * 60,
    };
    jest.spyOn(timezoneService, 'getTzOffsets').mockReturnValue(timezones);
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
