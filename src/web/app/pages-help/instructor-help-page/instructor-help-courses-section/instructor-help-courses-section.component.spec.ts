import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorHelpCoursesSectionComponent } from './instructor-help-courses-section.component';

describe('InstructorHelpCoursesSectionComponent', () => {
  let component: InstructorHelpCoursesSectionComponent;
  let fixture: ComponentFixture<InstructorHelpCoursesSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorHelpCoursesSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpCoursesSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
