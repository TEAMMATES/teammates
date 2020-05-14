import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseService } from '../../../../services/course.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course } from '../../../../types/api-output';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { of } from 'rxjs';
import { AddCourseFormComponent } from './add-course-form.component';

describe('AddCourseFormComponent', () => {
  let component: AddCourseFormComponent;
  let fixture: ComponentFixture<AddCourseFormComponent>;

  const date1: Date = new Date('2020-13-05T08:15:30');
  const courseId1: string = 'CS3281';
  const courseName1: string = 'Valid course';
  const timeZone1: string = 'UTC';
  const course1: Course =  {
    courseId: courseId1,
    courseName: courseName1,
    timeZone: timeZone1,
    creationTimestamp: date1.getTime(),
    deletionTimestamp: 0,
  };
  const timeZoneOffsets1: Record<string, number> = { GMT: 0 };

  const spyStatusMessageService: any = {
    showErrorMessage: jest.fn(),
    showSuccessMessageTemplate: jest.fn(),
  };
  const timezoneServiceStub: any = {
    getTzOffsets: jest.fn(() => timeZoneOffsets1),
  };
  const spyCourseService: any = {
    createCourse: jest.fn(() => of(course1)),
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddCourseFormComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NgbModule,
        MatSnackBarModule,
      ],
      providers: [
        { provide: StatusMessageService, useValue: spyStatusMessageService },
        { provide: CourseService, useValue: spyCourseService },
        { provide: TimezoneService, useValue: timezoneServiceStub },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCourseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    component.timezone = timeZone1;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when not enabled', () => {
    component.isEnabled = false;
    component.timezone = timeZone1;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should call showErrorMessage when courseId is blank', () => {
    component.newCourseId = '';
    component.onSubmit();
    fixture.detectChanges();
    expect(spyStatusMessageService.showErrorMessage).toHaveBeenCalled();
  });

  it('should hold added course with valid details', () => {
    component.newCourseId = courseId1;
    component.newCourseName = courseName1;
    component.onSubmit();
    fixture.detectChanges();
    expect(spyStatusMessageService.showSuccessMessageTemplate).toHaveBeenCalled();
    expect(component.course).toEqual(course1);
  });
});
