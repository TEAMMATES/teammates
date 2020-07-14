import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  InstructorCourseStudentEditFormModule,
} from '../../../pages-instructor/instructor-course-student-edit-page/instructor-course-student-edit-form.module';
import {
  InstructorSearchComponentsModule,
} from '../../../pages-instructor/instructor-search-page/instructor-search-components.module';
import { StudentProfileModule } from '../../../pages-instructor/student-profile/student-profile.module';
import { ExampleBoxModule } from '../example-box/example-box.module';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section.component';

describe('InstructorHelpStudentsSectionComponent', () => {
  let component: InstructorHelpStudentsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpStudentsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpStudentsSectionComponent],
      imports: [
        NgbModule,
        RouterTestingModule,
        HttpClientTestingModule,
        ExampleBoxModule,
        StudentProfileModule,
        InstructorSearchComponentsModule,
        InstructorCourseStudentEditFormModule,
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
