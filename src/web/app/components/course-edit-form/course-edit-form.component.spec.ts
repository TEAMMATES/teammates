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

import {
  CourseAddFormModel,
  CourseEditFormMode,
  DEFAULT_COURSE_ADD_FORM_MODEL,
  DEFAULT_COURSE_EDIT_FORM_MODEL,
} from './course-edit-form-model';
import { CourseEditFormComponent } from './course-edit-form.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import createSpyFromClass from '../../../test-helpers/create-spy-from-class';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import {
  Course,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';

describe('CourseEditFormComponent', () => {
  let component: CourseEditFormComponent;
  let fixture: ComponentFixture<CourseEditFormComponent>;
  let feedbackSessionsService: FeedbackSessionsService;
  let ngbModal: NgbModal;

  const validCourseId: string = 'CS1101S';
  const validTimeZone: string = 'Asia/Singapore';
  const validInstitute1: string = 'Test Institute1';
  const validInstitute2: string = 'Test Institute2';
  const testTimeZone: string = 'Australia/Adelaide';
  const timeZoneOffsets1: Record<string, number> = { GMT: 570 };
  const testCourse1: Course = {
    courseId: 'testId1',
    courseName: 'Test Course1',
    timeZone: validTimeZone,
    institute: validInstitute1,
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };
  const testCourse2: Course = {
    courseId: 'testId2',
    courseName: 'Test Course2',
    timeZone: validTimeZone,
    institute: validInstitute2,
    creationTimestamp: 0,
    deletionTimestamp: 1000,
  };
  const errorMssg = 'Error occured';

  const customError: ErrorMessageOutput = {
    error: {
      message: errorMssg,
    },
    status: 0,
  };

  const spyStatusMessageService = createSpyFromClass(StatusMessageService);
  spyStatusMessageService.showErrorToast.mockReturnValue(errorMssg);

  const timezoneServiceStub = createSpyFromClass(TimezoneService);
  timezoneServiceStub.getTzOffsets.mockReturnValue(timeZoneOffsets1);
  timezoneServiceStub.guessTimezone.mockReturnValue(testTimeZone);

  const spyCourseService = createSpyFromClass(CourseService);
  spyCourseService.createCourse.mockReturnValue(of({}));
  spyCourseService.getAllCoursesAsInstructor(of({ courses: [testCourse1, testCourse1] }));

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
      isClosingSoonEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
      studentDeadlines: {},
      instructorDeadlines: {},
    };

    jest.spyOn(feedbackSessionsService, 'getFeedbackSessionsForInstructor')
      .mockReturnValue(of({ feedbackSessions: [testFeedbackSession] }));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    const model: CourseAddFormModel = component.model as CourseAddFormModel;
    model.activeCourses = [testCourse1];

    component.copyCourseHandler();
    mockModalRef.componentInstance.fetchFeedbackSessionsEvent.emit(testCourse1.courseId);

    expect(ngbModal.open).toHaveBeenCalledWith(CopyCourseModalComponent);
    expect(mockModalRef.componentInstance.isCopyFromOtherSession).toEqual(true);
    expect(mockModalRef.componentInstance.activeCourses[0]).toEqual(testCourse1);
    expect(mockModalRef.componentInstance.courseToFeedbackSession[testCourse1.courseId]).toEqual([testFeedbackSession]);
  });

  it('should handle errors in copyCourseHandler when promise is rejected', async () => {
    component.formModel = DEFAULT_COURSE_ADD_FORM_MODEL();
    component.formMode = CourseEditFormMode.ADD;
    fixture.detectChanges();

    const mockModalRef: any = createMockNgbModalRef({
      isCopyFromOtherSession: false,
      courses: [],
      courseToFeedbackSession: {},
      fetchFeedbackSessionsEvent: new EventEmitter<string>(),
    }, Promise.reject(customError));
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.copyCourseHandler();

    expect(await spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMssg);
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
        observer.error(customError);
      }),
    });
    jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef);

    component.copyCourseHandler();

    expect(spyStatusMessageService.showErrorToast).toHaveBeenCalledWith(errorMssg);
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

  it('should update isEditing to true when editModel exists and isEditing is set as false', () => {
    const defaultCourseEditFormModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
    defaultCourseEditFormModel.isEditing = false;
    component.editModel = defaultCourseEditFormModel;

    component.setIsEditing(true);

    expect(component.editModel?.isEditing).toBe(true);
  });

  it('should update institutes when addModel is defined and when there is only one institute', () => {
    component.addModel = DEFAULT_COURSE_ADD_FORM_MODEL() as CourseAddFormModel;
    component.addModel.allCourses = [testCourse1];
    component.addModel.course = testCourse1;

    component.updateInstitutes();

    expect(component.addModel.institutes).toEqual([validInstitute1]);
    expect(component.addModel.course.institute).toBe(validInstitute1);
  });

  it('should update institutes when addModel is defined and when there is more than one institute', () => {
    component.addModel = DEFAULT_COURSE_ADD_FORM_MODEL() as CourseAddFormModel;
    component.addModel.allCourses = [testCourse1, testCourse2];
    component.addModel.course = testCourse1;

    component.updateInstitutes();

    expect(component.addModel.institutes).toEqual([validInstitute1, validInstitute2]);
    expect(component.addModel.course.institute).toBe(validInstitute1);
  });

  it('should not update institutes when allCourses of the component is empty', () => {
    component.addModel = DEFAULT_COURSE_ADD_FORM_MODEL() as CourseAddFormModel;
    component.addModel.allCourses = [];
    component.addModel.course = testCourse2;

    component.updateInstitutes();

    expect(component.addModel.course.institute).toBe(validInstitute2);
  });

  it('should set the course timeZone when not in display-only mode', () => {
    component.isDisplayOnly = false;

    component.detectTimezoneHandler();

    expect(component.model.course.timeZone).toBe(testTimeZone);
  });

  it('should not update the course timeZone when in display-only mode', () => {
    component.isDisplayOnly = true;
    component.model.course.timeZone = 'Asia/Singapore';

    component.detectTimezoneHandler();

    expect(component.model.course.timeZone).toBe('Asia/Singapore');
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
