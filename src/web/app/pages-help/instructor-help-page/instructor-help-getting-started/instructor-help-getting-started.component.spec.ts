import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Component } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorHelpGettingStartedComponent } from './instructor-help-getting-started.component';

@Component({ selector: 'tm-example-box', template: '' })
class ExampleBoxStubComponent {}
@Component({ selector: 'tm-example-add-course-form', template: '' })
class ExampleAddCourseFormStubComponent {}

describe('InstructorHelpGettingStartedComponent', () => {
  let component: InstructorHelpGettingStartedComponent;
  let fixture: ComponentFixture<InstructorHelpGettingStartedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpGettingStartedComponent,
        ExampleBoxStubComponent,
        ExampleAddCourseFormStubComponent,
      ],
      imports: [
        RouterTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpGettingStartedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
