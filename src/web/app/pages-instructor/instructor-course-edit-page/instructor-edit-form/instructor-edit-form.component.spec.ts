import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorEditFormComponent } from './instructor-edit-form.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TeammatesCommonModule} from "../../../components/teammates-common/teammates-common.module";
import {CommonModule} from "@angular/common";
import {AjaxLoadingModule} from "../../../components/ajax-loading/ajax-loading.module";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {InstructorEditSectionPrivilegesFormComponent} from "./instructor-edit-section-privileges-form/instructor-edit-section-privileges-form.component";
import {CourseEditFormComponent} from "../course-edit-form/course-edit-form.component";

describe('InstructorEditFormComponent', () => {
  let component: InstructorEditFormComponent;
  let fixture: ComponentFixture<InstructorEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorEditFormComponent,
        InstructorEditSectionPrivilegesFormComponent,
      ],
      imports: [
        AjaxLoadingModule,
        FormsModule,
        NgbModule,
        ReactiveFormsModule,
        TeammatesCommonModule,
        CommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
