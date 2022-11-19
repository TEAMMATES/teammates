import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Course, Gender, Instructor, InstructorPermissionRole, JoinState, Student } from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentCourseDetailsPageComponent, StudentProfileWithPicture } from './student-course-details-page.component';

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

  it('should sort teammate profiles by name after clicking on "name"', () => {
    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'iam2@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'billy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'iam3@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'amy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'iam4@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'dawson',
        nationality: 'Andorran',
        shortName: 'I am 2',
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
    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'cam2@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'billy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'bam3@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'amy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'aam4@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'dawson',
        nationality: 'Andorran',
        shortName: 'I am 2',
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

  it('should sort teammate profiles by gender correctly', () => {
    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'cam2@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'billy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'bam3@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'amy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'aam4@hello.com',
        gender: Gender.FEMALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'dawson',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    component.sortTeammatesBy(SortBy.STUDENT_GENDER);

    expect(component.teammateProfiles.length).toEqual(3);
    expect(component.teammateProfiles[0].gender).toEqual(Gender.FEMALE);
    expect(component.teammateProfiles[1].gender).toEqual(Gender.MALE);
    expect(component.teammateProfiles[2].gender).toEqual(Gender.MALE);
  });

  it('should sort teammate profiles by institute correctly', () => {
    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'cam2@hello.com',
        gender: Gender.MALE,
        institute: 'nyu',
        moreInfo: 'Misc',
        name: 'billy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'bam3@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'amy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'aam4@hello.com',
        gender: Gender.FEMALE,
        institute: 'ntu',
        moreInfo: 'Misc',
        name: 'dawson',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    component.sortTeammatesBy(SortBy.INSTITUTION);

    expect(component.teammateProfiles.length).toEqual(3);
    expect(component.teammateProfiles[0].institute).toEqual('ntu');
    expect(component.teammateProfiles[1].institute).toEqual('nus');
    expect(component.teammateProfiles[2].institute).toEqual('nyu');
  });

  it('should sort teammate profiles by nationality', () => {
    const teammateProfiles: StudentProfileWithPicture[] = [
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'cam2@hello.com',
        gender: Gender.MALE,
        institute: 'nyu',
        moreInfo: 'Misc',
        name: 'billy',
        nationality: 'Andorran',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'bam3@hello.com',
        gender: Gender.MALE,
        institute: 'nus',
        moreInfo: 'Misc',
        name: 'amy',
        nationality: 'Chinese',
        shortName: 'I am 2',
      },
      {
        photoUrl: '/assets/images/profile_picture_default.png',
        email: 'aam4@hello.com',
        gender: Gender.FEMALE,
        institute: 'ntu',
        moreInfo: 'Misc',
        name: 'dawson',
        nationality: 'British',
        shortName: 'I am 2',
      },
    ];

    component.course = course;
    component.instructorDetails = instructorDetails;
    component.student = student;
    component.teammateProfiles = teammateProfiles;
    component.sortTeammatesBy(SortBy.NATIONALITY);

    expect(component.teammateProfiles.length).toEqual(3);
    expect(component.teammateProfiles[0].nationality).toEqual('Andorran');
    expect(component.teammateProfiles[1].nationality).toEqual('British');
    expect(component.teammateProfiles[2].nationality).toEqual('Chinese');
  });

});
