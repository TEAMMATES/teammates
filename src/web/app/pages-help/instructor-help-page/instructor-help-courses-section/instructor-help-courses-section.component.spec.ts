import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { InstructorHelpCoursesSectionComponent } from './instructor-help-courses-section.component';

describe('InstructorHelpCoursesSectionComponent', () => {
  let component: InstructorHelpCoursesSectionComponent;
  let fixture: ComponentFixture<InstructorHelpCoursesSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorHelpCoursesSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
