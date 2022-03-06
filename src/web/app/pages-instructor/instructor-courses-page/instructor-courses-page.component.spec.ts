import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { CourseService } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course, CourseArchive, Courses, Students } from '../../../types/api-output';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { AddCourseFormModule } from './add-course-form/add-course-form.module';
import { InstructorCoursesPageComponent } from './instructor-courses-page.component';
import {default as courses} from '../../test-data/courses.json';
import {default as studentsJs} from '../../test-data/students.json';
import { Student} from '../../../types/api-output';

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  let courseService: CourseService;
  let studentService: StudentService;
  let timezoneService: TimezoneService;
  let simpleModalService: SimpleModalService;

  const activeCoursesSnap: any[] = [
    courses.CS3281_1,
    courses.CS3282
  ];

  const archivedCoursesSnap: any[] = [
   courses.CS2104,
   courses.CS2106
  ];

  const deletedCoursesSnap: any[] = [
    courses.CS1020,
    courses.CS2010
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

  const courseCS1231: Course = courses.CS1231_1;
  const courseCS3281: Course = courses.CS3281_deleted;
  const courseCS3282: Course = courses.CS3282_deleted;
  const courseST4234: Course = courses.ST4234;

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

  const tmp2: any = studentsJs.testStudent2;
  const tStudent2: Student = tmp2;
  const tmp3: any = studentsJs.testStudent3;
  const tStudent3: Student = tmp3;
  const tmp4: any = studentsJs.testStudent4;
  const tStudent4: Student = tmp4;
  const tmp5: any = studentsJs.testStudent5;
  const tStudent5: Student = tmp5;
  const tmp6: any = studentsJs.testStudent6;
  const tStudent6: Student = tmp6;
  const tmp7: any = studentsJs.testStudent7;
  const tStudent7: Student = tmp7;
  const tmp8: any = studentsJs.testStudent8;
  const tStudent8: Student = tmp8;
  const tmp9: any = studentsJs.testStudent9;
  const tStudent9: Student = tmp9;

  const students: Students = {
    students: [ 
      tStudent2,
      tStudent3,
      tStudent4,
      tStudent5,
      tStudent6,
      tStudent7,
      tStudent8,
      tStudent9
    ],
  };

  beforeEach(waitForAsync(() => {
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
        ProgressBarModule,
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
