import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {
  InstructorCourseStudentEditPageComponent,
} from './instructor-course-student-edit-page.component';

describe('InstructorCourseStudentEditPageComponent', () => {
  let component: InstructorCourseStudentEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseStudentEditPageComponent],
      imports: [RouterTestingModule],
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
});
