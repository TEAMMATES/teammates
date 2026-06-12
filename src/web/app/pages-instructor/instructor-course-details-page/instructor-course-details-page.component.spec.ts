import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';
import { CourseStatistics } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course, Instructor, InstructorPermissionRole, JoinState, Student } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';

const course: Course = {
  courseId: 'CS101',
  institute: 'Test Institute',
  country: 'SG',
  instituteId: 'test-institute-id',
  courseName: 'Introduction to CS',
  timeZone: '',
  creationTimestamp: 0,
  deletionTimestamp: 0,
};

const testStudent: Student = {
  name: 'Jamie',
  email: 'jamie@gmail.com',
  joinState: JoinState.NOT_JOINED,
  teamName: 'Team 1',
  teamId: 'team-1',
  sectionName: 'Tutorial Group 1',
  sectionId: 'tutorial-group-1',
  courseId: 'CS101',
  courseName: 'Test Course',
  institute: 'Test Institute',
  userId: 'student-jamie',
};

const testInstructor: Instructor = {
  courseId: course.courseId,
  courseName: 'Test Course',
  institute: 'Test Institute',
  userId: 'instructor-hock',
  joinState: JoinState.JOINED,
  name: 'Hock',
  email: 'hock@gmail.com',
  role: InstructorPermissionRole.COOWNER,
  displayedToStudentsAs: 'Hock',
  isDisplayedToStudents: false,
};

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;
  let studentService: StudentService;
  let simpleModalService: SimpleModalService;
  let statusMessageService: StatusMessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorCourseDetailsPageComponent);
    studentService = TestBed.inject(StudentService);
    simpleModalService = TestBed.inject(SimpleModalService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a course with one co-owner and no students, and populated course student list', () => {
    const stats: CourseStatistics = {
      numOfSections: 0,
      numOfTeams: 0,
      numOfStudents: 0,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      courseName: 'Test Course',
      institute: 'Test Institute',
      userId: 'instructor-hodor',
      joinState: JoinState.JOINED,
      name: 'Hodor',
      email: 'hodor@gmail.com',
      role: InstructorPermissionRole.COOWNER,
      displayedToStudentsAs: 'Hodor',
      isDisplayedToStudents: true,
    };
    const courseDetails: { course: Course; stats: CourseStatistics } = {
      course,
      stats,
    };
    component.courseDetails = courseDetails;
    component.instructors = [coOwner];
    component.isLoadingCsv = false;
    component.isStudentsLoading = false;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a course with one co-owner and one student, and ajax failure', () => {
    const stats: CourseStatistics = {
      numOfSections: 1,
      numOfTeams: 1,
      numOfStudents: 1,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      courseName: 'Test Course',
      institute: 'Test Institute',
      userId: 'instructor-bran',
      joinState: JoinState.JOINED,
      name: 'Bran',
      email: 'bran@gmail.com',
      role: InstructorPermissionRole.COOWNER,
      displayedToStudentsAs: 'Bran',
      isDisplayedToStudents: false,
    };
    const courseDetails: { course: Course; stats: CourseStatistics } = {
      course,
      stats,
    };
    const studentListRowModel: StudentListRowModel = {
      student: testStudent,
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    };
    component.students = [studentListRowModel];
    component.courseDetails = courseDetails;
    component.instructors = [coOwner];
    component.isLoadingCsv = false;
    component.isStudentsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when students are still loading', () => {
    component.isStudentsLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display confirmation modal if delete all students is requested', () => {
    const stats: CourseStatistics = {
      numOfSections: 1,
      numOfTeams: 1,
      numOfStudents: 1,
    };
    const courseDetails: { course: Course; stats: CourseStatistics } = {
      course,
      stats,
    };
    const studentListRowModel: StudentListRowModel = {
      student: testStudent,
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    };
    component.students = [studentListRowModel];
    component.courseDetails = courseDetails;
    component.instructors = [testInstructor];
    component.isLoadingCsv = false;
    component.isStudentsLoading = false;
    fixture.detectChanges();

    const promise: Promise<void> = Promise.resolve();

    const spySimpleModalService = vi
      .spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));

    const deleteAllButton = fixture.debugElement.nativeElement.querySelector('#btn-delete-all');
    deleteAllButton.click();
    fixture.detectChanges();

    expect(spySimpleModalService).toHaveBeenCalled();
  });

  it('should delete students and show success message upon completion', () => {
    const stats: CourseStatistics = {
      numOfSections: 10,
      numOfTeams: 10,
      numOfStudents: 350,
    };
    const courseDetails: { course: Course; stats: CourseStatistics } = {
      course,
      stats,
    };
    component.courseDetails = courseDetails;
    fixture.detectChanges();

    const spyStudentService = vi
      .spyOn(studentService, 'deleteStudentsFromCourse')
      .mockReturnValue(of({ message: 'Successful' }));

    vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation((args: string) => {
      expect(args).toEqual('All the students have been removed from the course');
    });

    component.deleteAllStudentsFromCourse(course.courseId);

    expect(spyStudentService).toHaveBeenCalledExactlyOnceWith({ courseId: course.courseId });
  });

  it('should show error message when delete fails', () => {
    const stats: CourseStatistics = {
      numOfSections: 1,
      numOfTeams: 1,
      numOfStudents: 1,
    };
    const courseDetails: { course: Course; stats: CourseStatistics } = {
      course,
      stats,
    };
    component.courseDetails = courseDetails;
    fixture.detectChanges();

    vi.spyOn(studentService, 'deleteStudentsFromCourse').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );

    const spy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation((args: string) => {
      expect(args).toEqual('This is the error message.');
    });

    component.deleteAllStudentsFromCourse(course.courseId);

    expect(spy).toHaveBeenCalled();
  });
});
