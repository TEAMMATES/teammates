import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { CourseStatistics } from '../../../services/course.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Instructor, InstructorPermissionRole, JoinState } from '../../../types/api-output';
import { SimpleModalModule } from '../../components/simple-modal/simple-modal.module';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import TestCourses from '../../test-resources/courses';
import TestInstructors from '../../test-resources/instructors';
import TestStudents from '../../test-resources/students';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';
import { InstructorCourseDetailsPageModule } from './instructor-course-details-page.module';

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;
  let studentService: StudentService;
  let simpleModalService: SimpleModalService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TeammatesCommonModule,
        RouterTestingModule,
        InstructorCourseDetailsPageModule,
        SimpleModalModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
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
    const courseDetails: any = {
      course: TestCourses.cs101,
      stats,
    };
    component.courseDetails = courseDetails;
    component.instructors = [TestInstructors.hodor];
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
      courseId: TestCourses.cs101.courseId,
      joinState: JoinState.JOINED,
      googleId: 'Bran',
      name: 'Bran',
      email: 'bran@gmail.com',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      displayedToStudentsAs: 'Bran',
      isDisplayedToStudents: false,
    };
    const courseDetails: any = {
      course: TestCourses.cs101,
      stats,
    };
    const studentListRowModel: StudentListRowModel = {
      student: TestStudents.jamie,
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
    const courseDetails: any = {
      course: TestCourses.cs101,
      stats,
    };
    const studentListRowModel: StudentListRowModel = {
      student: TestStudents.jamie,
      isAllowedToViewStudentInSection: true,
      isAllowedToModifyStudent: true,
    };
    component.students = [studentListRowModel];
    component.courseDetails = courseDetails;
    component.instructors = [TestInstructors.hock];
    component.isLoadingCsv = false;
    component.isStudentsLoading = false;
    fixture.detectChanges();

    const promise: Promise<void> = Promise.resolve();

    const spySimpleModalService: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));

    const deleteAllButton: any = fixture.debugElement.nativeElement.querySelector('#btn-delete-all');
    deleteAllButton.click();
    fixture.detectChanges();

    expect(spySimpleModalService).toHaveBeenCalled();
  });

  it('should delete students in batches and show success message upon completion', () => {
    const stats: CourseStatistics = {
      numOfSections: 10,
      numOfTeams: 10,
      numOfStudents: 350,
    };
    const courseDetails: any = {
      course: TestCourses.cs101,
      stats,
    };
    component.courseDetails = courseDetails;
    fixture.detectChanges();

    const spyStudentService: SpyInstance = jest.spyOn(studentService, 'batchDeleteStudentsFromCourse')
        .mockReturnValue(of({ message: 'Successful' }));

    jest.spyOn(statusMessageService, 'showSuccessToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('All the students have been removed from the course');
        });

    component.deleteAllStudentsFromCourse(TestCourses.cs101.courseId);

    // given a limit of 100 students per call and 350 students,
    // there should be four calls in total
    expect(spyStudentService).toHaveBeenCalledTimes(4);
  });

  it('should show error message when delete fails', () => {
    const stats: CourseStatistics = {
      numOfSections: 1,
      numOfTeams: 1,
      numOfStudents: 1,
    };
    const courseDetails: any = {
      course: TestCourses.cs101,
      stats,
    };
    component.courseDetails = courseDetails;
    fixture.detectChanges();

    jest.spyOn(studentService, 'batchDeleteStudentsFromCourse').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));

    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast').mockImplementation((args: string) => {
      expect(args).toEqual('This is the error message.');
    });

    component.deleteAllStudentsFromCourse(TestCourses.cs101.courseId);

    expect(spy).toBeCalled();
  });
});
