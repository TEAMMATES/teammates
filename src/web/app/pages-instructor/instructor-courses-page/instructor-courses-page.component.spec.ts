import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of } from 'rxjs';
import { CourseModel, InstructorCoursesPageComponent } from './instructor-courses-page.component';
import { AuthService } from '../../../services/auth.service';
import { CourseService } from '../../../services/course.service';
import { InstituteService } from '../../../services/institute.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AuthInfo, Course, InstructorCourses, Institute, JoinState, Students } from '../../../types/api-output';

describe('InstructorCoursesPageComponent', () => {
  let component: InstructorCoursesPageComponent;
  let fixture: ComponentFixture<InstructorCoursesPageComponent>;
  let authService: AuthService;
  let courseService: CourseService;
  let instituteService: InstituteService;
  let studentService: StudentService;
  let timezoneService: TimezoneService;
  let simpleModalService: SimpleModalService;

  const date1: Date = new Date('2018-11-05T08:15:30');
  const date2: Date = new Date('2019-02-02T08:15:30');
  const date3: Date = new Date('2002-11-05T08:15:30');
  const date4: Date = new Date('2003-11-05T08:15:30');
  const date5: Date = new Date('2002-12-05T08:15:30');
  const date6: Date = new Date('2003-12-05T08:15:30');

  const activeCoursesSnap: CourseModel[] = [
    {
      course: {
        courseId: 'CS3281',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        courseName: 'Modifiable Students and Courses',
        timeZone: 'UTC',
        creationTimestamp: date1.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: true,
      canModifyStudent: true,
      canModifyInstructor: false,
      isLoadingCourseStats: false,
    },
    {
      course: {
        courseId: 'CS3282',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        courseName: 'Nothing modifiable',
        timeZone: 'UTC',
        creationTimestamp: date2.getTime(),
        deletionTimestamp: 0,
      },
      canModifyCourse: false,
      canModifyStudent: false,
      canModifyInstructor: false,
      isLoadingCourseStats: false,
    },
  ];

  const deletedCoursesSnap: CourseModel[] = [
    {
      course: {
        courseId: 'CS1020',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        courseName: 'Can modify deleted',
        timeZone: 'UTC',
        creationTimestamp: date3.getTime(),
        deletionTimestamp: date4.getTime(),
      },
      canModifyCourse: true,
      canModifyStudent: false,
      canModifyInstructor: false,
      isLoadingCourseStats: false,
    },
    {
      course: {
        courseId: 'CS2010',
        institute: 'Test Institute',
        country: 'SG',
        instituteId: 'test-institute-id',
        courseName: 'Cannot modify deleted',
        timeZone: 'UTC',
        creationTimestamp: date5.getTime(),
        deletionTimestamp: date6.getTime(),
      },
      canModifyCourse: false,
      canModifyStudent: false,
      canModifyInstructor: false,
      isLoadingCourseStats: false,
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
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    courseName: 'Discrete Structures',
    creationTimestamp: date1.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
  };

  const courseCS3281: Course = {
    courseId: 'CS3281',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    courseName: 'Thematic Systems Project I',
    creationTimestamp: date3.getTime(),
    deletionTimestamp: date4.getTime(),
    timeZone: 'UTC',
  };

  const courseCS3282: Course = {
    courseId: 'CS3282',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    courseName: 'Thematic Systems Project II',
    creationTimestamp: date5.getTime(),
    deletionTimestamp: date6.getTime(),
    timeZone: 'UTC',
  };

  const courseST4234: Course = {
    courseId: 'ST4234',
    institute: 'Test Institute',
    country: 'SG',
    instituteId: 'test-institute-id',
    courseName: 'Bayesian Statistics',
    creationTimestamp: date2.getTime(),
    deletionTimestamp: 0,
    timeZone: 'UTC',
  };

  const courseModelCS1231: CourseModel = {
    course: courseCS1231,
    canModifyCourse: true,
    canModifyStudent: true,
    canModifyInstructor: false,
    isLoadingCourseStats: false,
  };

  const courseModelCS3281: CourseModel = {
    course: courseCS3281,
    canModifyCourse: true,
    canModifyStudent: true,
    canModifyInstructor: false,
    isLoadingCourseStats: false,
  };

  const courseModelCS3282: CourseModel = {
    course: courseCS3282,
    canModifyCourse: true,
    canModifyStudent: false,
    canModifyInstructor: false,
    isLoadingCourseStats: false,
  };

  const courseModelST4234: CourseModel = {
    course: courseST4234,
    canModifyCourse: false,
    canModifyStudent: true,
    canModifyInstructor: false,
    isLoadingCourseStats: false,
  };

  const students: Students = {
    students: [
      {
        email: 'alice.b.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-a',
        name: 'Alice Betsy',
        comments: "This student's name is Alice Betsy",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Tutorial Group 1',
        sectionId: 'tutorial-group-1',
      },
      {
        email: 'benny.c.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-b',
        name: 'Benny Charles',
        comments: "This student's name is Benny Charles",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Tutorial Group 1',
        sectionId: 'tutorial-group-1',
      },
      {
        email: 'charlie.d.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-c',
        name: 'Charlie Davis',
        comments: "This student's name is Charlie Davis",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        teamId: 'team-2',
        sectionName: 'Tutorial Group 2',
        sectionId: 'tutorial-group-2',
      },
      {
        email: 'danny.e.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-d',
        name: 'Danny Engrid',
        comments: "This student's name is Danny Engrid",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Tutorial Group 1',
        sectionId: 'tutorial-group-1',
      },
      {
        email: 'emma.f.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-e',
        name: 'Emma Farrell',
        comments: "This student's name is Emma Farrell",
        joinState: JoinState.JOINED,
        teamName: 'Team 1',
        teamId: 'team-1',
        sectionName: 'Tutorial Group 1',
        sectionId: 'tutorial-group-1',
      },
      {
        email: 'francis.g.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-f',
        name: 'Francis Gabriel',
        comments: "This student's name is Francis Gabriel",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        teamId: 'team-2',
        sectionName: 'Tutorial Group 2',
        sectionId: 'tutorial-group-2',
      },
      {
        email: 'gene.h.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-g',
        name: 'Gene Hudson',
        comments: "This student's name is Gene Hudson",
        joinState: JoinState.JOINED,
        teamName: 'Team 2',
        teamId: 'team-2',
        sectionName: 'Tutorial Group 2',
        sectionId: 'tutorial-group-2',
      },
      {
        email: 'hugh.i.tmms@gmail.tmt',
        courseId: 'test.exa-demo',
        courseName: 'Test Course',
        institute: 'Test Institute',
        userId: 'student-h',
        name: 'Hugh Ivanov',
        comments: "This student's name is Hugh Ivanov",
        joinState: JoinState.NOT_JOINED,
        teamName: 'Team 3',
        teamId: 'team-3',
        sectionName: 'Tutorial Group 2',
        sectionId: 'tutorial-group-2',
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorCoursesPageComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    courseService = TestBed.inject(CourseService);
    instituteService = TestBed.inject(InstituteService);
    studentService = TestBed.inject(StudentService);
    timezoneService = TestBed.inject(TimezoneService);
    simpleModalService = TestBed.inject(SimpleModalService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all courses by the instructor', () => {
    const mockAuthInfo: AuthInfo = {
      loginUrl: '',
      masquerade: false,
      user: {
        accountId: 'test-account-id',
        accountEmail: 'test@test.com',
        isAdmin: false,
        isInstructor: true,
        isStudent: false,
        isMaintainer: false,
      },
    };
    const mockInstitute: Institute = { id: 'test-institute-id', name: 'Test Institute', country: 'SG' };
    vi.spyOn(authService, 'getAuthUser').mockReturnValue(of(mockAuthInfo));
    vi.spyOn(instituteService, 'getVerifiedInstitutes').mockReturnValue(of({ institutes: [mockInstitute] }));

    const courseSpy = vi
      .spyOn(courseService, 'getAllCoursesAsInstructor')
      .mockImplementation((courseStatus: string): Observable<InstructorCourses> => {
        if (courseStatus === 'active') {
          return of({ courses: [courseCS1231, courseCS3281, courseCS3282], instructorPermissions: {} });
        }

        // softDeleted
        return of({ courses: [courseST4234], instructorPermissions: {} });
      });

    component.loadInstructorCourses();

    expect(courseSpy).toHaveBeenCalledTimes(2);
    expect(courseSpy).toHaveBeenNthCalledWith(1, 'active');
    expect(courseSpy).toHaveBeenNthCalledWith(2, 'softDeleted');

    expect(component.activeCourses.length).toEqual(3);
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
    expect(component.activeCourses[0].course.courseName).toEqual('Discrete Structures');
    expect(component.activeCourses[1].course.courseId).toEqual('CS3282');
    expect(component.activeCourses[1].course.courseName).toEqual('Thematic Systems Project II');
    expect(component.activeCourses[2].course.courseId).toEqual('CS3281');
    expect(component.activeCourses[2].course.courseName).toEqual('Thematic Systems Project I');

    expect(component.softDeletedCourses.length).toEqual(1);
    expect(component.softDeletedCourses[0].course.courseId).toEqual('ST4234');
    expect(component.softDeletedCourses[0].course.courseName).toEqual('Bayesian Statistics');

    expect(component.courseFormModel.institutes).toEqual([mockInstitute]);
    expect(component.courseFormModel.course.institute).toEqual('Test Institute');
  });

  it('should get the course statistics', () => {
    component.activeCourses = [courseModelCS1231];
    const studentSpy = vi.spyOn(studentService, 'getStudents').mockReturnValue(of(students));
    component.getCourseStats(0);

    expect(studentSpy).toHaveBeenCalledTimes(1);
    expect(studentSpy).toHaveBeenLastCalledWith({ courseIds: ['CS1231'] });

    expect(component.courseStats['CS1231']['sections']).toEqual(2);
    expect(component.courseStats['CS1231']['teams']).toEqual(3);
    expect(component.courseStats['CS1231']['students']).toEqual(8);
    expect(component.courseStats['CS1231']['unregistered']).toEqual(1);
  });

  it('should restore a soft deleted course', () => {
    component.softDeletedCourses = [courseModelCS1231];
    expect(component.softDeletedCourses.length).toEqual(1);

    const courseSpy = vi.spyOn(courseService, 'restoreCourse').mockReturnValue(of({ message: 'Message' }));
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());

    component.onRestore('CS1231');

    expect(courseSpy).toHaveBeenCalledTimes(1);
    expect(courseSpy).toHaveBeenNthCalledWith(1, 'CS1231');

    expect(component.softDeletedCourses.length).toEqual(0);
  });

  it('should soft delete a course', async () => {
    component.activeCourses = [courseModelCS1231];
    const courseSpy = vi.spyOn(courseService, 'binCourse').mockReturnValue(of(courseCS1231));
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());

    await component.onDelete('CS1231').then(() => {
      expect(courseSpy).toHaveBeenCalledTimes(1);
      expect(courseSpy).toHaveBeenLastCalledWith('CS1231');

      expect(component.softDeletedCourses.length).toEqual(1);
      expect(component.activeCourses.length).toEqual(0);
    });
  });

  it('should permanently delete a course', async () => {
    component.softDeletedCourses = [courseModelCS1231];
    const courseSpy = vi.spyOn(courseService, 'deleteCourse').mockReturnValue(of({ message: 'Message' }));
    vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());

    await component.onDeletePermanently('CS1231').then(() => {
      expect(courseSpy).toHaveBeenCalledTimes(1);
      expect(courseSpy).toHaveBeenLastCalledWith('CS1231');

      expect(component.activeCourses.length).toEqual(0);
      expect(component.softDeletedCourses.length).toEqual(0);
    });
  });

  it('should show add course form and disable button when clicking on add new course', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#btn-add-course');
    button.click();
    fixture.detectChanges();

    const div = fixture.debugElement.nativeElement.querySelector('#add-course-section');
    expect(div).toBeTruthy();
    expect(button.disabled).toBeTruthy();
  });

  it('should disable enroll button when instructor cannot modify student', () => {
    component.activeCourses = [courseModelCS3282];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#btn-enroll-disabled-0');
    expect(button.textContent).toEqual('Enroll');
    expect(button.className).toContain('disabled');
  });

  it('should disable delete button when instructor cannot modify active course', () => {
    component.activeCourses = [courseModelST4234];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#btn-soft-delete-disabled-0');
    expect(button.textContent).toEqual(' Delete ');
    expect(button.className).toContain('disabled');
  });

  it('should disable restore and permanently delete buttons when instructor cannot modify deleted course', () => {
    component.softDeletedCourses = [courseModelST4234];
    component.isLoadingCourses = false;
    component.isRecycleBinExpanded = true;
    fixture.detectChanges();

    const restoreButton = fixture.debugElement.nativeElement.querySelector('#btn-restore-disabled-0');
    expect(restoreButton.textContent).toEqual('Restore');
    expect(restoreButton.className).toContain('disabled');

    const disableButton = fixture.debugElement.nativeElement.querySelector('#btn-delete-disabled-0');
    expect(disableButton.textContent).toEqual(' Delete Permanently ');
    expect(disableButton.className).toContain('disabled');
  });

  it('should sort courses by their IDs', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#sort-course-id');
    button.click();
    expect(component.activeCourses[0].course.courseId).toEqual('CS1231');
    expect(component.activeCourses[1].course.courseId).toEqual('CS3281');
    expect(component.activeCourses[2].course.courseId).toEqual('CS3282');
    expect(component.activeCourses[3].course.courseId).toEqual('ST4234');
  });

  it('should sort courses by their names', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#sort-course-name');
    button.click();
    expect(component.activeCourses[0].course.courseName).toEqual('Bayesian Statistics');
    expect(component.activeCourses[1].course.courseName).toEqual('Discrete Structures');
    expect(component.activeCourses[2].course.courseName).toEqual('Thematic Systems Project I');
    expect(component.activeCourses[3].course.courseName).toEqual('Thematic Systems Project II');
  });

  it('should sort courses by their creation dates', () => {
    component.activeCourses = [courseModelCS3282, courseModelST4234, courseModelCS1231, courseModelCS3281];
    component.isLoadingCourses = false;
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#sort-creation-date');
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
    component.softDeletedCourses = deletedCoursesSnap;
    component.courseStats = courseStatsSnap;
    component.isLoadingCourses = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it is undeletable and unrestorable', () => {
    component.activeCourses = activeCoursesSnap;
    component.softDeletedCourses = deletedCoursesSnap;
    component.courseStats = courseStatsSnap;
    component.canDeleteAll = false;
    component.canRestoreAll = false;
    component.isLoadingCourses = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no courses in course stats', () => {
    component.activeCourses = activeCoursesSnap;
    component.isLoadingCourses = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when courses are still loading', () => {
    component.isLoadingCourses = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when new course form is expanded', () => {
    component.isAddNewCourseFormExpanded = true;
    component.isLoadingCourses = false;
    // Mock the timezone service to prevent unexpected changes in time zones over time, such as daylight savings time
    const timezones: Record<string, number> = {
      Jamaica: -5 * 60,
      Portugal: 0,
      Singapore: 8 * 60,
      Turkey: 3 * 60,
    };
    vi.spyOn(timezoneService, 'getTzOffsets').mockReturnValue(timezones);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
