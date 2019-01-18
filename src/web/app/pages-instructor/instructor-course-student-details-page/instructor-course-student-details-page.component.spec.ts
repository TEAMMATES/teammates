import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {
  InstructorCourseStudentDetailsPageComponent,
} from './instructor-course-student-details-page.component';

describe('InstructorCourseStudentDetailsPageComponent', () => {
  let component: InstructorCourseStudentDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseStudentDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseStudentDetailsPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
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
});
