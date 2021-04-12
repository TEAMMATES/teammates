import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Course, Instructor, InstructorPermissionRole, JoinState, Student } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';
import { InstructorCourseDetailsPageModule } from './instructor-course-details-page.module';

const course: Course = {
  courseId: 'CS101',
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
  sectionName: 'Tutorial Group 1',
  courseId: 'CS101',
};

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TeammatesCommonModule,
        RouterTestingModule,
        InstructorCourseDetailsPageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseDetailsPageComponent);
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
    const stats: any = {
      sectionsTotal: 0,
      teamsTotal: 0,
      studentsTotal: 0,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      joinState: JoinState.JOINED,
      googleId: 'Hodor',
      name: 'Hodor',
      email: 'hodor@gmail.com',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      displayedToStudentsAs: 'Hodor',
      isDisplayedToStudents: true,
    };
    const courseDetails: any = {
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
    const stats: any = {
      sectionsTotal: 1,
      teamsTotal: 1,
      studentsTotal: 1,
    };
    const coOwner: Instructor = {
      courseId: course.courseId,
      joinState: JoinState.JOINED,
      googleId: 'Bran',
      name: 'Bran',
      email: 'bran@gmail.com',
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      displayedToStudentsAs: 'Bran',
      isDisplayedToStudents: false,
    };
    const courseDetails: any = {
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
});
