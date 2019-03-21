import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Component, Input } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Gender } from '../../../../types/gender';
import { SearchStudentsTable } from '../../../pages-instructor/instructor-search-page/instructor-search-page.component';
import { StudentAttributes } from '../../../pages-instructor/student-profile/student-attributes';
import { StudentProfile } from '../../../pages-instructor/student-profile/student-profile';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section.component';

@Component({ selector: 'tm-example-box', template: '' })
class ExampleBoxStubComponent {}
@Component({ selector: 'tm-instructor-search-bar', template: '' })
class InstructorSearchBarStubComponent {}
@Component({ selector: 'tm-instructor-course-student-edit-page', template: '' })
class InstructorCourseStudentEditPageStubComponent { @Input() isEnabled?: boolean; }
@Component({ selector: 'tm-student-result-table', template: '' })
class StudentResultTableStubComponent {
  @Input() studentTables: SearchStudentsTable[] = [];
}
@Component({ selector: 'tm-student-profile', template: '' })
class StudentProfileStubComponent {
  @Input() studentProfile: StudentProfile = {
    shortName: '',
    email: '',
    institute: '',
    nationality: '',
    gender: Gender.FEMALE,
    moreInfo: '',
    pictureKey: '',
  };
}
@Component({ selector: 'tm-course-related-info', template: '' })
class CourseRelatedInfoStubComponent {
  @Input() student: StudentAttributes = {
    email: '',
    course: '',
    name: '',
    lastName: '',
    comments: '',
    team: '',
    section: '',
  };
}
@Component({ selector: 'tm-more-info', template: '' })
class MoreInfoStubComponent {
  @Input() studentName: string = '';
  @Input() moreInfoText: string = '';
}

describe('InstructorHelpStudentsSectionComponent', () => {
  let component: InstructorHelpStudentsSectionComponent;
  let fixture: ComponentFixture<InstructorHelpStudentsSectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorHelpStudentsSectionComponent,
        ExampleBoxStubComponent,
        InstructorSearchBarStubComponent,
        InstructorCourseStudentEditPageStubComponent,
        StudentResultTableStubComponent,
        StudentProfileStubComponent,
        CourseRelatedInfoStubComponent,
        MoreInfoStubComponent,
      ],
      imports: [
        NgbModule,
        RouterTestingModule,
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
