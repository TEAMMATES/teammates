import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinState, Student } from '../../../types/api-output';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page.component';

@Component({ selector: 'tm-course-related-info', template: '' })
class CourseRelatedInfoStubComponent {
  @Input() student: Student = {
    email: '',
    courseId: '',
    name: '',
    comments: '',
    teamName: '',
    sectionName: '',
    joinState: JoinState.JOINED,
  };
}

describe('InstructorCourseStudentDetailsPageComponent', () => {
  let component: InstructorCourseStudentDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentDetailsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseStudentDetailsPageComponent,
        CourseRelatedInfoStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
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
      courseId: 'CS3281',
      name: 'firstName',
      comments: 'This is a comment',
      teamName: 'myTeam',
      sectionName: 'mySection',
      joinState: JoinState.JOINED,
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
