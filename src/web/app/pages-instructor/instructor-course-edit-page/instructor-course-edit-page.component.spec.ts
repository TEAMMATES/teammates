import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CourseEditFormComponent } from './course-edit-form/course-edit-form.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { InstructorEditFormComponent } from './instructor-edit-form/instructor-edit-form.component';
import {
  InstructorSectionPrivilegesFormFormComponent,
} from './instructor-edit-form/instructor-section-privileges-form/instructor-section-privileges-form.component';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CourseEditFormComponent,
        InstructorCourseEditPageComponent,
        InstructorSectionPrivilegesFormFormComponent,
        InstructorEditFormComponent,
      ],
      imports: [
        AjaxLoadingModule,
        NgbModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
