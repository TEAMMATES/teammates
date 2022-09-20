import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CourseEditFormMode } from '../../../components/course-edit-form/course-edit-form-model';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';
import { InstructorHelpGettingStartedComponent } from './instructor-help-getting-started.component';

@Component({ selector: 'tm-example-box', template: '' })
class ExampleBoxStubComponent {}

@Component({ selector: 'tm-course-edit-form', template: '' })
class CourseEditFormStubComponent {
  @Input() isDisplayOnly?: boolean;
  @Input() formMode?: CourseEditFormMode;
}

describe('InstructorHelpGettingStartedComponent', () => {
  let component: InstructorHelpGettingStartedComponent;
  let fixture: ComponentFixture<InstructorHelpGettingStartedComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpGettingStartedComponent,
        ExampleBoxStubComponent,
        CourseEditFormStubComponent,
      ],
      imports: [
        RouterTestingModule,
        TeammatesRouterModule,
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
