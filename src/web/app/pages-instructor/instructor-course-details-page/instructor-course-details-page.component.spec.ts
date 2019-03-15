import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ClipboardModule } from 'ngx-clipboard';
import { InstructorCourseDetailsPageComponent } from './instructor-course-details-page.component';

@Component({ selector: 'tm-student-list', template: '' })
class StudentListStubComponent {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() sections: Object[] = [];
  @Input() enableRemindButton: boolean = true;
}
@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

const course: any = {
  id: 'CS101',
  name: 'Introduction to CS',
};

describe('InstructorCourseDetailsPageComponent', () => {
  let component: InstructorCourseDetailsPageComponent;
  let fixture: ComponentFixture<InstructorCourseDetailsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseDetailsPageComponent,
        StudentListStubComponent,
        AjaxPreloadComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        ClipboardModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseDetailsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a course with one co-owner and no students', () => {
    const stats: any = {
      sectionsTotal: 0,
      teamsTotal: 0,
      studentsTotal: 0,
    };
    const coOwner: any = {
      googleId: 'Hodor',
      name: 'Hodor',
      email: 'hodor@gmail.com',
      key: 'hodor@gmail.com%CS1012345',
      role: 'Co-owner',
      displayedName: 'Hodor',
      isArchived: false,
      isDisplayedToStudents: true,
    };
    const courseDetails: any = {
      course,
      stats,
    };
    component.courseDetails = courseDetails;
    component.instructors = [coOwner];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
