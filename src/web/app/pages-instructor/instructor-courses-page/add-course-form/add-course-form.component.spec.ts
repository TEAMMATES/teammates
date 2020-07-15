import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseService } from '../../../../services/course.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course } from '../../../../types/api-output';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { of } from 'rxjs';
import { AddCourseFormComponent } from './add-course-form.component';

describe('AddCourseFormComponent', () => {
  let component: AddCourseFormComponent;
  let fixture: ComponentFixture<AddCourseFormComponent>;

  const testDate: Date = new Date('2020-13-05T08:15:30');
  const testCourseId: string = 'CS3281';
  const testCourseName: string = 'Valid course';
  const testTimeZone: string = 'UTC';
  const testCourse: Course =  {
    courseId: testCourseId,
    courseName: testCourseName,
    timeZone: testTimeZone,
    creationTimestamp: testDate.getTime(),
    deletionTimestamp: 0,
  };
  const timeZoneOffsets1: Record<string, number> = { GMT: 0 };

  const spyStatusMessageService: any = {
    showErrorToast: jest.fn(),
    showSuccessToastTemplate: jest.fn(),
  };
  const timezoneServiceStub: any = {
    getTzOffsets: jest.fn(() => timeZoneOffsets1),
    guessTimezone: jest.fn(() => 'UTC'),
  };
  const spyCourseService: any = {
    createCourse: jest.fn(() => of(testCourse)),
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
    // Unable to leave timezone as default field, otherwise the field defaults to the
    // timezone the system is on. This will differ from
    // place to place causing the snapshot to constantly be mismatched.
    component.timezone = testTimeZone;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when not enabled', () => {
    component.isEnabled = false;
    component.timezone = testTimeZone;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should call showErrorToast when courseId is blank', () => {
    component.newCourseId = '';
    component.onSubmit();
    fixture.detectChanges();
    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalled();
  });

  it('should hold added course with valid details', () => {
    component.newCourseId = testCourseId;
    component.newCourseName = testCourseName;
    component.onSubmit();
    fixture.detectChanges();
    expect(component.course).toEqual(testCourse);
  });
});
