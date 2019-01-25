import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section.component';

describe('InstructorHelpStudentsSectionComponent', () => {
  let component: InstructorHelpStudentsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpStudentsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorHelpStudentsSectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpStudentsSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
