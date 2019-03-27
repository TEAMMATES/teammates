import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHelpPageComponent } from './instructor-help-page.component';

import { Component, Input } from '@angular/core';

@Component({ selector: 'tm-instructor-help-students-section', template: '' })
class InstructorHelpStudentsSectionStubComponent {
  @Input() key: string = '';
}
@Component({ selector: 'tm-instructor-help-courses-section', template: '' })
class InstructorHelpCoursesSectionStubComponent {
  @Input() key: string = '';
}
@Component({ selector: 'tm-instructor-help-sessions-section', template: '' })
class InstructorHelpSessionsSectionStubComponent {
  @Input() key: string = '';
}
@Component({ selector: 'tm-instructor-help-questions-section', template: '' })
class InstructorHelpQuestionsSectionStubComponent {
  @Input() key: string = '';
}

describe('InstructorHelpPageComponent', () => {
  let component: InstructorHelpPageComponent;
  let fixture: ComponentFixture<InstructorHelpPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpPageComponent, InstructorHelpCoursesSectionStubComponent,
        InstructorHelpStudentsSectionStubComponent, InstructorHelpSessionsSectionStubComponent,
        InstructorHelpQuestionsSectionStubComponent],
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
