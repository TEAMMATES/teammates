import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHelpPageComponent } from './instructor-help-page.component';

import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpQuestionsSectionComponent,
} from './instructor-help-questions-section/instructor-help-questions-section.component';
import {
  InstructorHelpSessionsSectionComponent,
} from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import {
  InstructorHelpStudentsSectionComponent,
} from './instructor-help-students-section/instructor-help-students-section.component';

describe('InstructorHelpPageComponent', () => {
  let component: InstructorHelpPageComponent;
  let fixture: ComponentFixture<InstructorHelpPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpPageComponent, InstructorHelpCoursesSectionComponent,
        InstructorHelpStudentsSectionComponent, InstructorHelpSessionsSectionComponent,
        InstructorHelpQuestionsSectionComponent],
      imports: [FormsModule, NgbModule, RouterTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
