import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinState } from '../../../../types/api-output';
import { LoadingRetryModule } from '../../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../../components/loading-spinner/loading-spinner.module';
import { InstructorCourseStudentEditPageComponent } from './process-account-request-panel.component';

describe('InstructorCourseStudentEditPageComponent', () => {
  let component: InstructorCourseStudentEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentEditPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseStudentEditPageComponent],
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
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
      name: 'Jake Peralta',
      comments: 'Cool cool cool.',
      teamName: 'Team A',
      sectionName: 'Section A',
      joinState: JoinState.JOINED,
    };
    component.editForm = new FormGroup({
      studentname: new FormControl('Jake Peralta'),
      sectionname: new FormControl('Section A'),
      teamname: new FormControl('Team A'),
      newstudentemail: new FormControl('jake@gmail.com'),
      comments: new FormControl('Cool cool cool.'),
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
