import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseDetailsPageComponent],
      imports: [RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
