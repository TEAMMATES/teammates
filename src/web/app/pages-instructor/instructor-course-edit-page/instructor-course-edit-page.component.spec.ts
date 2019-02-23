import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {CourseEditFormComponent} from "./course-edit-form/course-edit-form.component";
import {InstructorEditSectionPrivilegesFormComponent} from "./instructor-edit-form/instructor-edit-section-privileges-form/instructor-edit-section-privileges-form.component";
import {InstructorEditFormComponent} from "./instructor-edit-form/instructor-edit-form.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AjaxLoadingModule} from '../../components/ajax-loading/ajax-loading.module';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CourseEditFormComponent,
        InstructorCourseEditPageComponent,
        InstructorEditSectionPrivilegesFormComponent,
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
