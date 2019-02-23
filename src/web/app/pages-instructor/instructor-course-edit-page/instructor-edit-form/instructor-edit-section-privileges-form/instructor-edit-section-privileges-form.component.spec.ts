import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorEditSectionPrivilegesFormComponent } from './instructor-edit-section-privileges-form.component';
import {AjaxLoadingModule} from "../../../../components/ajax-loading/ajax-loading.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TeammatesCommonModule} from "../../../../components/teammates-common/teammates-common.module";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {CommonModule} from "@angular/common";

describe('InstructorEditSectionPrivilegesFormComponent', () => {
  let component: InstructorEditSectionPrivilegesFormComponent;
  let fixture: ComponentFixture<InstructorEditSectionPrivilegesFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstructorEditSectionPrivilegesFormComponent ],
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
    fixture = TestBed.createComponent(InstructorEditSectionPrivilegesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
