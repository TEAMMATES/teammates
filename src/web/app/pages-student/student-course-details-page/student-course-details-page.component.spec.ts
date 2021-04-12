import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Course, Gender, Instructor, InstructorPermissionRole, JoinState, Student } from '../../../types/api-output';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentCourseDetailsPageComponent, StudentProfileWithPicture } from './student-course-details-page.component';

describe('StudentCourseDetailsPageComponent', () => {
  let component: StudentCourseDetailsPageComponent;
  let fixture: ComponentFixture<StudentCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentCourseDetailsPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesCommonModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentCourseDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    const student: Student = {
      courseId: '1.1.c-demo2',
      email: '1@1.com',
      lastName: '1',
      name: '1',
      comments: '',
      joinState: JoinState.NOT_JOINED,
      sectionName: 'Tutorial Group 2',
      teamName: 'Team 2',
    };

    const instructorDetails: Instructor[] = [{
      googleId: '',
      courseId: '1.1.c-demo2',
      displayedToStudentsAs: 'Instructor',
      isDisplayedToStudents: true,
      email: '1@1.com',
      name: '1',
      joinState: JoinState.JOINED,
      role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    }];

    const course: Course = {
      courseId: '1.1.c-demo2',
      courseName: 'Sample Course 101',
      creationTimestamp: 1552472130000,
      deletionTimestamp: 0,
      timeZone: 'UTC',
    };

    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'iam2@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: '2',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    component.isLoadingStudent = false;
    component.isLoadingInstructor = false;
    component.isLoadingTeammates = false;
    component.isLoadingCourse = false;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when all data are still loading', () => {
    component.isLoadingStudent = true;
    component.isLoadingInstructor = true;
    component.isLoadingTeammates = true;
    component.isLoadingCourse = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
