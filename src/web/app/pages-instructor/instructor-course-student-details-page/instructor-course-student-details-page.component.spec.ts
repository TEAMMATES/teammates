import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { InstructorCourseStudentDetailsPageComponent } from './instructor-course-student-details-page.component';
import { JoinState } from '../../../types/api-output';

describe('InstructorCourseStudentDetailsPageComponent', () => {
  let component: InstructorCourseStudentDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentDetailsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

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
      courseName: 'Test Course',
      institute: 'Test Institute',
      userId: 'student-details-1',
      name: 'firstName',
      comments: 'This is a comment',
      teamName: 'myTeam',
      teamId: 'team-a',
      sectionName: 'mySection',
      sectionId: 'section-a',
      joinState: JoinState.JOINED,
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
