import { DOCUMENT } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { PageScrollService, NGXPS_CONFIG } from 'ngx-page-scroll-core';
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
      providers: [
        {
          provide: DomSanitizer,
                   useValue: {
                     bypassSecurityTrustHtml: () => '',
                     sanitize: () => '',
                   },
        },
        { provide: DOCUMENT, useValue: document },
        PageScrollService,
        { provide: NGXPS_CONFIG, useValue: {} },
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
