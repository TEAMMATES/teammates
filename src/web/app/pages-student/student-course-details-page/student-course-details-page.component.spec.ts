import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Course, Instructor, InstructorPermissionRole, JoinState, Student } from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentCourseDetailsPageComponent } from './student-course-details-page.component';

const student: Student = {
  courseId: '1.1.c-demo2',
  email: '1@1.com',
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
  institute: 'Test Institute',
  creationTimestamp: 1552472130000,
  deletionTimestamp: 0,
  timeZone: 'UTC',
};

describe('StudentCourseDetailsPageComponent', () => {
  let component: StudentCourseDetailsPageComponent;
  let fixture: ComponentFixture<StudentCourseDetailsPageComponent>;

  beforeEach(waitForAsync(() => {
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

  it('should snap when it fails to load', () => {
    component.hasLoadingFailed = true;
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    const teammateProfiles: Student[] = [
      {
        courseId: '1.1.c-demo2',
        email: 'iam2@hello.com',
        name: '2',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
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

  it('should sort teammate profiles by name after clicking on "name"', () => {
    const teammateProfiles: Student[] = [
      {
        courseId: '1.1.c-demo2',
        email: 'iam2@hello.com',
        name: 'billy',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
      {
        courseId: '1.1.c-demo2',
        email: 'iam3@hello.com',
        name: 'amy',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
      {
        courseId: '1.1.c-demo2',
        email: 'iam4@hello.com',
        name: 'dawson',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    fixture.detectChanges();

    const sortButton: any = fixture.debugElement.nativeElement.querySelector('#sort-name');
    sortButton.click();

    expect(component.teammateProfiles.length).toEqual(3);
    expect(component.teammateProfiles[0].name).toEqual('amy');
    expect(component.teammateProfiles[1].name).toEqual('billy');
    expect(component.teammateProfiles[2].name).toEqual('dawson');
  });

  it('should sort teammate profiles by email correctly', () => {
    const teammateProfiles: Student[] = [
      {
        courseId: '1.1.c-demo2',
        email: 'cam2@hello.com',
        name: 'billy',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
      {
        courseId: '1.1.c-demo2',
        email: 'bam3@hello.com',
        name: 'amy',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
      {
        courseId: '1.1.c-demo2',
        email: 'aam4@hello.com',
        name: 'dawson',
        sectionName: 'Tutorial Group 2',
        teamName: 'Team 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    component.sortTeammatesBy(SortBy.RESPONDENT_EMAIL);

    expect(component.teammateProfiles.length).toEqual(3);
    expect(component.teammateProfiles[0].email).toEqual('aam4@hello.com');
    expect(component.teammateProfiles[1].email).toEqual('bam3@hello.com');
    expect(component.teammateProfiles[2].email).toEqual('cam2@hello.com');
  });

});
