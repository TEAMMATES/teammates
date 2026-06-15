import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

describe('InstructorStudentRecordsPageComponent', () => {
  let component: InstructorStudentRecordsPageComponent;
  let fixture: ComponentFixture<InstructorStudentRecordsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorStudentRecordsPageComponent);
    component = fixture.componentInstance;
    component.courseId = 'su1337';
    component.userId = '00000000-0000-4000-9000-000000000001';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    component.studentName = 'Not John Doe';
    component.courseId = 'su1337';
    component.isStudentResultsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when student results are still loading', () => {
    component.studentName = 'John Doe';
    component.courseId = 'CS1111';
    component.isStudentResultsLoading = true;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
