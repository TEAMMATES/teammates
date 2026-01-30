import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page.component';
import { JoinState } from '../../../types/api-output';

describe('InstructorCourseStudentDetailsPageComponent', () => {
  let component: InstructorCourseStudentDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentDetailsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
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
