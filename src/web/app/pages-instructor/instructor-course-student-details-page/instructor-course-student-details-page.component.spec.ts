import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { Gender } from '../../../types/gender';
import { StudentAttributes } from '../student-profile/student-attributes';
import { StudentProfile } from '../student-profile/student-profile';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page.component';

@Component({ selector: 'tm-student-profile', template: '' })
class StudentProfileStubComponent {
  @Input() studentProfile: StudentProfile | undefined;
  @Input() studentName: string = '';
  @Input() photoUrl: string = '/assets/images/profile_picture_default.png';
  @Input() hideMoreInfo: boolean = false;
}
@Component({ selector: 'tm-course-related-info', template: '' })
class CourseRelatedInfoStubComponent {
  @Input() student: StudentAttributes = {
    email: '',
    course: '',
    name: '',
    lastName: '',
    comments: '',
    team: '',
    section: '',
  };
}
@Component({ selector: 'tm-more-info', template: '' })
class MoreInfoStubComponent {
  @Input() studentName: string = '';
  @Input() moreInfoText: string = '';
}

describe('InstructorCourseStudentDetailsPageComponent', () => {
  let component: InstructorCourseStudentDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseStudentDetailsPageComponent,
        StudentProfileStubComponent,
        CourseRelatedInfoStubComponent,
        MoreInfoStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseStudentDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated student', () => {
    component.student = {
      email: 'studentEmail@email.com',
      course: 'CS3281',
      name: 'firstName',
      lastName: 'lastName',
      comments: 'This is a comment',
      team: 'myTeam',
      section: 'mySection',
    };
    component.studentProfile = {
      shortName: 'shortName',
      email: 'profileEmail@email.com',
      institute: 'NUS',
      nationality: 'Indian',
      gender: Gender.MALE,
      moreInfo: 'I have more info here',
      pictureKey: 'pictureKey',
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
