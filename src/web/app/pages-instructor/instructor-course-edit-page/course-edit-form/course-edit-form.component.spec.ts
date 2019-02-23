import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseEditFormComponent } from './course-edit-form.component';
import {AjaxLoadingModule} from "../../../components/ajax-loading/ajax-loading.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TeammatesCommonModule} from "../../../components/teammates-common/teammates-common.module";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {CommonModule} from "@angular/common";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('CourseEditFormComponent', () => {
  let component: CourseEditFormComponent;
  let fixture: ComponentFixture<CourseEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CourseEditFormComponent ],
      imports: [
        AjaxLoadingModule,
        FormsModule,
        NgbModule,
        ReactiveFormsModule,
        TeammatesCommonModule,
        CommonModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
