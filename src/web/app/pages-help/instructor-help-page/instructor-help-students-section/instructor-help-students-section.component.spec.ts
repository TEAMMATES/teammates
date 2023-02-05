import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { PanelChevronModule } from '../../../components/panel-chevron/panel-chevron.module';
import {
  InstructorCourseStudentEditFormModule,
} from '../../../pages-instructor/instructor-course-student-edit-page/instructor-course-student-edit-form.module';
import {
  InstructorSearchComponentsModule,
} from '../../../pages-instructor/instructor-search-page/instructor-search-components.module';
import { ExampleBoxModule } from '../example-box/example-box.module';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section.component';

describe('InstructorHelpStudentsSectionComponent', () => {
  let component: InstructorHelpStudentsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpStudentsSectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpStudentsSectionComponent,
        InstructorHelpPanelComponent,
      ],
      imports: [
        NgbModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        ExampleBoxModule,
        InstructorSearchComponentsModule,
        InstructorCourseStudentEditFormModule,
        PanelChevronModule,
      ],
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
