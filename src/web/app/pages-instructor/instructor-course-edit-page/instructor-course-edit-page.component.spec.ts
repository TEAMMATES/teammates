import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import { InstructorCourseEditPageModule } from './instructor-course-edit-page.module';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        RouterTestingModule,
        HttpClientTestingModule,
        InstructorCourseEditPageModule,
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
