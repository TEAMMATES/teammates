import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EventEmitter } from '@angular/core';
import {
  ComponentFixture,
  TestBed,
  waitForAsync,
} from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of, Observable } from 'rxjs';

import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import {
  CourseAddFormModel,
  CourseEditFormMode,
  DEFAULT_COURSE_ADD_FORM_MODEL,
  DEFAULT_COURSE_EDIT_FORM_MODEL,
} from './course-edit-form-model';
import { CourseEditFormComponent } from './course-edit-form.component';

describe('CourseEditFormComponent', () => {
  let component: CourseEditFormComponent;
  let fixture: ComponentFixture<CourseEditFormComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let ngbModal: NgbModal;

  const validCourseId: string = 'CS1101S';
  const validTimeZone: string = 'Asia/Singapore';
  const validInstitute: string = 'Test Institute';
  const testTimeZone: string = 'Australia/Adelaide';
  const timeZoneOffsets1: Record<string, number> = { GMT: 570 };
  const testCourse: Course = {
    courseId: 'testId',
    courseName: 'Test Course',
    timeZone: validTimeZone,
    institute: validInstitute,
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };
  const errorMssg = 'Error occured';

  const spyStatusMessageService: any = {
    showErrorToast: jest.fn(() => errorMssg),
    showSuccessToast: jest.fn(),
  };
  const timezoneServiceStub: any = {
    getTzOffsets: jest.fn(() => timeZoneOffsets1),
    guessTimezone: jest.fn(() => testTimeZone),
  };

  const spyCourseService: any = {
    createCourse: jest.fn(() => of({})),
    getAllCoursesAsInstructor: jest.fn(() => of({ courses: [testCourse] })),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CourseEditFormComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        NgbModule,
        AjaxLoadingModule,
        CommonModule,
        FormsModule,
        AjaxLoadingModule,
        LoadingRetryModule,
        LoadingSpinnerModule,
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
    fixture = TestBed.createComponent(CourseEditFormComponent);
    feedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    ngbModal = TestBed.inject(NgbModal);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap when in ADD Mode and is display only', () => {
    component.isDisplayOnly = true;
    component.formMode = CourseEditFormMode.ADD;
    fixture.detectChanges();

    const optionElement = fixture.debugElement.nativeElement.querySelector('#time-zone');
    const submitButton = fixture.debugElement.nativeElement.querySelector('#btn-submit-course');
    const copyButton = fixture.debugElement.nativeElement.querySelector('#btn-copy-course');

    expect(submitButton).toHaveProperty('disabled', true);
    expect(copyButton).toHaveProperty('disabled', true);
    expect(optionElement.options.length).toEqual(0);
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when in ADD Mode with default fields', () => {
    component.isDisplayOnly = false;
    component.formMode = CourseEditFormMode.ADD;
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    fixture.detectChanges();

    const optionElement = fixture.debugElement.nativeElement.querySelector('#time-zone');
    const submitButton = fixture.debugElement.nativeElement.querySelector('#btn-submit-course');
    const copyButton = fixture.debugElement.nativeElement.querySelector('#btn-copy-course');

    // submit button and copy button must be disabled since there is no valid input for the input fields
    expect(submitButton).toHaveProperty('disabled', true);
    expect(copyButton).toHaveProperty('disabled', true);

    expect(optionElement.options.length).toEqual(1);
    expect(component.model.course.timeZone).toEqual(testTimeZone);
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when in EDIT Mode and is display only', () => {
    component.isDisplayOnly = true;
    component.formMode = CourseEditFormMode.EDIT;
    fixture.detectChanges();

    const optionElement = fixture.debugElement.nativeElement.querySelector('#time-zone');
    const deleteButton = fixture.debugElement.nativeElement.querySelector('#btn-delete-course');
    const editButton = fixture.debugElement.nativeElement.querySelector('#btn-edit-course');

    expect(optionElement.options.length).toEqual(0);
    expect(deleteButton).toHaveProperty('disabled', true);
    expect(editButton).toHaveProperty('disabled', true);
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when in EDIT Mode with default fields', () => {
    component.isDisplayOnly = false;
    component.formMode = CourseEditFormMode.EDIT;
    component.formModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.nativeElement.querySelector('#btn-delete-course');
    const editButton = fixture.debugElement.nativeElement.querySelector('#btn-edit-course');

    expect(deleteButton).toHaveProperty('disabled', true);
    expect(editButton).toHaveProperty('disabled', true);
    expect(fixture).toMatchSnapshot();
  });

  it('should open copy course modal when in ADD Mode', () => {
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    component.formMode = CourseEditFormMode.ADD;
    fixture.detectChanges();

    const mockModalRef: any = createMockNgbModalRef({
      isCopyFromOtherSession: false,
      courses: [],
      courseToFeedbackSession: {},
      fetchFeedbackSessionsEvent: new EventEmitter<string>(),
    });

    const testFeedbackSession: FeedbackSession = {
      courseId: validCourseId,
      timeZone: validTimeZone,
      feedbackSessionName: 'Test Session',
      instructions: 'Instructions',
      submissionStartTimestamp: 1000000000000,
      submissionEndTimestamp: 1500000000000,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
      studentDeadlines: {},
      instructorDeadlines: {},
    };

    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor')
      .mockReturnValue(of({ feedbackSessions: [testFeedbackSession] }));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    const model: CourseAddFormModel = component.model as CourseAddFormModel;
    model.activeCourses = [testCourse];

    component.copyCourseHandler();
    mockModalRef.componentInstance.fetchFeedbackSessionsEvent.emit(testCourse.courseId);

    expect(ngbModal.open).toHaveBeenCalledWith(CopyCourseModalComponent);
    expect(mockModalRef.componentInstance.isCopyFromOtherSession).toEqual(true);
    expect(mockModalRef.componentInstance.activeCourses[0]).toEqual(testCourse);
    expect(mockModalRef.componentInstance.courseToFeedbackSession[testCourse.courseId]).toEqual([testFeedbackSession]);
  });

  it('should handle errors in copyCourseHandler when promise is rejected', () => {
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    component.formMode = CourseEditFormMode.ADD;
    fixture.detectChanges();

    const mockModalRef: any = createMockNgbModalRef({
      isCopyFromOtherSession: false,
      courses: [],
      courseToFeedbackSession: {},
      fetchFeedbackSessionsEvent: new EventEmitter<string>(),
    }, Promise.reject(new Error(errorMssg)));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.copyCourseHandler();

    expect(spyStatusMessageService.showErrorToast()).toBe(errorMssg);
  });

  it('should handle errors in copyCourseHandler when observable throws an error', () => {
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    component.formMode = CourseEditFormMode.ADD;
    fixture.detectChanges();

    const mockModalRef: any = createMockNgbModalRef({
      isCopyFromOtherSession: false,
      courses: [],
      courseToFeedbackSession: {},
      fetchFeedbackSessionsEvent: new Observable<number>((observer) => {
        observer.error({
          error: { message: errorMssg },
        });
      }),
    });
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.copyCourseHandler();

    expect(spyStatusMessageService.showErrorToast()).toBe(errorMssg);
  });

  it('should set isEditing to true when editModel exists', () => {
    component.editModel = DEFAULT_COURSE_EDIT_FORM_MODEL();

    component.setIsEditing(true);

    expect(component.editModel?.isEditing).toBe(true);
  });

  it('should set isEditing to false when editModel exists', () => {
    component.editModel = DEFAULT_COURSE_EDIT_FORM_MODEL();

    component.setIsEditing(false);

    expect(component.editModel?.isEditing).toBe(false);
  });

  it('should not modify isEditing when editModel is undefined', () => {
    component.editModel = undefined;

    component.setIsEditing(true);

    expect(component.editModel).toBeUndefined();
  });

  it('should update institutes when addModel is defined and when there is only one institute', () => {
    component.addModel = DEFAULT_COURSE_ADD_FORM_MODEL() as CourseAddFormModel;
    component.addModel.allCourses = [testCourse];
    component.addModel.course = testCourse;

    component.updateInstitutes();

    expect(component.addModel.institutes).toEqual([validInstitute]);
    expect(component.addModel.course.institute).toBe(validInstitute);
  });

  it('should update institutes when addModel is defined and when there is more than one institute', () => {
    component.addModel = DEFAULT_COURSE_ADD_FORM_MODEL() as CourseAddFormModel;
    component.addModel.allCourses = [testCourse, testCourse];
    component.addModel.course = testCourse;

    component.updateInstitutes();

    expect(component.addModel.institutes).toEqual([validInstitute]);
    expect(component.addModel.course.institute).toBe(validInstitute);
  });

  it('should not update institutes when addModel is not defined', () => {
    component.addModel = undefined;

    component.updateInstitutes();

    expect(component.addModel).toBeUndefined();
  });

  it('should set the course timeZone when not in display-only mode', () => {
    component.isDisplayOnly = false;

    component.detectTimezoneHandler();

    expect(component.model.course.timeZone).toBe(testTimeZone);
  });

  it('should not set the course timeZone when in display-only mode', () => {
    component.isDisplayOnly = true;

    component.detectTimezoneHandler();

    expect(component.model.course.timeZone).not.toBe(testTimeZone);
  });

  it('should do nothing when in display-only mode', () => {
    component.isDisplayOnly = true;
    const updateEmitSpy = jest.spyOn(component.updateCourseEvent, 'emit');
    const createEmitSpy = jest.spyOn(component.createNewCourseEvent, 'emit');

    component.submitHandler();

    expect(updateEmitSpy).not.toHaveBeenCalled();
    expect(createEmitSpy).not.toHaveBeenCalled();

  });

  it('should mark controls as touched and return when the form is invalid', () => {
    component.isDisplayOnly = false;
    component.formModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
    fixture.detectChanges();
    const formInvalidGetter = jest.spyOn(component.form, 'invalid', 'get');
    formInvalidGetter.mockReturnValue(true);

    const control1 = { markAsTouched: jest.fn() };
    const control2 = { markAsTouched: jest.fn() };

    jest.spyOn(Object, 'values').mockReturnValue([control1, control2]);

    component.submitHandler();

    expect(formInvalidGetter).toHaveBeenCalled();
    expect(Object.values).toHaveBeenCalledWith(component.form.controls);
    expect(control1.markAsTouched).toHaveBeenCalled();
    expect(control2.markAsTouched).toHaveBeenCalled();
  });

  it('should emit updateCourseEvent when the form is valid in EDIT mode', () => {
    component.isDisplayOnly = false;
    component.formMode = CourseEditFormMode.EDIT;
    component.formModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
    fixture.detectChanges();
    const emitSpy = jest.spyOn(component.updateCourseEvent, 'emit');

    component.submitHandler();

    expect(emitSpy).toHaveBeenCalled();
  });

  it('should emit createNewCourseEvent when the form is valid in ADD mode', () => {
    component.isDisplayOnly = false;
    component.formMode = CourseEditFormMode.ADD;
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    fixture.detectChanges();

    const emitSpy = jest.spyOn(component.createNewCourseEvent, 'emit');

    component.submitHandler();

    expect(emitSpy).toHaveBeenCalled();
  });

  it('should emit closeFormEvent when closeFormHandler is called', () => {
    const closeFormEventSpy = jest.spyOn(component.closeFormEvent, 'emit');

    component.closeFormHandler();

    expect(closeFormEventSpy).toHaveBeenCalled();
  });

  it('should emit deleteCourseEvent when deleteCourseHandler is called', () => {
    const deleteCourseEventSpy = jest.spyOn(component.deleteCourseEvent, 'emit');

    component.deleteCourseHandler();

    expect(deleteCourseEventSpy).toHaveBeenCalled();
  });

});
