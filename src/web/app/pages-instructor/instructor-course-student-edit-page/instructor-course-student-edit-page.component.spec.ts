import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { provideRouter } from '@angular/router';
import { InstructorCourseStudentEditPageComponent } from './instructor-course-student-edit-page.component';
import { JoinState } from '../../../types/api-output';

describe('InstructorCourseStudentEditPageComponent', () => {
  let component: InstructorCourseStudentEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentEditPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorCourseStudentEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with student details', () => {
    component.student = {
      email: 'jake@gmail.com',
      courseId: 'Crime101',
      courseName: 'Test Course',
      institute: 'Test Institute',
      userId: 'student-edit-1',
      name: 'Jake Peralta',
      comments: 'Cool cool cool.',
      teamName: 'Team A',
      teamId: 'team-a',
      sectionName: 'Section A',
      sectionId: 'section-a',
      joinState: JoinState.JOINED,
    };
    component.editForm = new UntypedFormGroup({
      'student-name': new UntypedFormControl('Jake Peralta'),
      'section-name': new UntypedFormControl('Section A'),
      'team-name': new UntypedFormControl('Team A'),
      'new-student-email': new UntypedFormControl('jake@gmail.com'),
      comments: new UntypedFormControl('Cool cool cool.'),
    });
    component.isStudentLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when student is still loading', () => {
    component.isStudentLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
