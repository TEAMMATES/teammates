import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import moment from 'moment-timezone';
import SpyInstance = jest.SpyInstance;
import { SessionEditFormMode } from './session-edit-form-model';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';
import { DateTimeService } from '../../../services/datetime.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course, ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-output';
import { DateFormat, TimeFormat, getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;
  let simpleModalService: SimpleModalService;
  let dateTimeService: DateTimeService;

  const submissionStartDateField = 'submissionStartDate';
  const submissionStartTimeField = 'submissionStartTime';

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        SessionEditFormModule,
        HttpClientTestingModule,
        RouterTestingModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionEditFormComponent);
    simpleModalService = TestBed.inject(SimpleModalService);
    dateTimeService = TestBed.inject(DateTimeService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should configure the time to be 23:59 if the hour is 23 and minute is greater than 0', () => {
    const time : TimeFormat = { hour: 23, minute: 5 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(23);
    expect(time.minute).toEqual(59);
  });

  it('should configure the time correctly if the hour is less than 23 and minute is greater than 0', () => {
    const time : TimeFormat = { hour: 22, minute: 5 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(23);
    expect(time.minute).toEqual(0);
  });

  it('should configure the time correctly if the minute is 0', () => {
    const time : TimeFormat = { hour: 21, minute: 0 };
    component.configureSubmissionOpeningTime(time);
    expect(time.hour).toEqual(21);
    expect(time.minute).toEqual(0);
  });

  it('should set the submission start time correctly when the date is same as'
      + ' earliest date and time is earlier than earliest possible time', () => {
    const date: DateFormat = component.minDateForSubmissionStart;
    const minTime: TimeFormat = component.minTimeForSubmissionStart;
    const time: TimeFormat = { hour: minTime.hour - 1, minute: minTime.minute };
    const configureSubmissionOpeningTimeSpy = jest.spyOn(component, 'configureSubmissionOpeningTime');
    const triggerModelChangeSpy = jest.spyOn(component, 'triggerModelChange');
    component.model.submissionStartTime = time;
    component.triggerSubmissionOpeningDateModelChange(submissionStartDateField, date);
    component.configureSubmissionOpeningTime(minTime);
    expect(component.model.submissionStartTime).toStrictEqual(minTime);
    expect(configureSubmissionOpeningTimeSpy).toHaveBeenCalledWith(minTime);
    expect(triggerModelChangeSpy).toHaveBeenCalledWith(submissionStartDateField, date);
  });

  it('should trigger the change of the model when the submission opening date changes', () => {
    const date: DateFormat = component.minDateForSubmissionStart;
    const minTime: TimeFormat = component.minTimeForSubmissionStart;
    const time: TimeFormat = { hour: minTime.hour + 1, minute: minTime.minute };
    const configureSubmissionOpeningTimeSpy = jest.spyOn(component, 'configureSubmissionOpeningTime');
    const triggerModelChangeSpy = jest.spyOn(component, 'triggerModelChange');
    component.model.submissionStartTime = time;
    component.triggerSubmissionOpeningDateModelChange(submissionStartDateField, date);
    expect(triggerModelChangeSpy).toHaveBeenCalledWith(submissionStartDateField, date);
    expect(configureSubmissionOpeningTimeSpy).not.toHaveBeenCalled();
  });

  it('should trigger the change of the model when the submission opening time '
    + 'changes to before the visibility time', () => {
    const date: DateFormat = { day: 12, month: 7, year: 2024 };
    const time: TimeFormat = { hour: 4, minute: 0 };
    const visibilityTime: TimeFormat = { hour: 14, minute: 0 };
    const triggerModelChangeSpy = jest.spyOn(component, 'triggerModelChange');
    const configureSessionVisibleDateTimeSpy = jest.spyOn(component, 'configureSessionVisibleDateTime');
    component.model.customSessionVisibleDate = date;
    component.model.submissionStartDate = date;
    component.model.customSessionVisibleTime = visibilityTime;
    component.triggerSubmissionOpeningTimeModelChange(submissionStartTimeField, time);
    expect(triggerModelChangeSpy).toHaveBeenCalledWith(submissionStartTimeField, time);
    expect(configureSessionVisibleDateTimeSpy).toHaveBeenCalledWith(date, time);
  });

  it('should adjust the session visibility date if submission opening date is earlier', () => {
    const date: DateFormat = { day: 12, month: 7, year: 2024 };
    const time: TimeFormat = { hour: 14, minute: 0 };
    component.model.customSessionVisibleDate = { day: 13, month: 7, year: 2024 };
    component.model.customSessionVisibleTime = time;
    component.configureSessionVisibleDateTime(date, time);
    expect(component.model.customSessionVisibleDate).toEqual(date);
    expect(component.model.customSessionVisibleTime).toEqual(time);
  });

  it('should not adjust the session visibility date and time if submission opening date and time are later', () => {
    const date: DateFormat = component.minDateForSubmissionStart;
    const time: TimeFormat = component.minTimeForSubmissionStart;
    component.model.customSessionVisibleDate = dateTimeService
      .getDateInstance(moment().tz(component.model.timeZone).subtract(1, 'days'));
    component.model.customSessionVisibleTime = dateTimeService
      .getTimeInstance(moment().tz(component.model.timeZone).subtract(1, 'hours'));
    component.configureSessionVisibleDateTime(date, time);
    expect(component.model.customSessionVisibleDate).not.toEqual(date);
    expect(component.model.customSessionVisibleTime).not.toEqual(time);
  });

  it('should emit a modelChange event with the updated field when triggerModelChange is called', () => {
    const field = 'courseId';
    const data = 'testId';
    const modelChangeSpy = jest.spyOn(component.modelChange, 'emit');
    component.triggerModelChange(field, data);
    expect(modelChangeSpy).toHaveBeenCalledWith({
      ...component.model,
      [field]: data,
    });
  });

  it('should emit modelChange event when a valid course ID is provided', () => {
    const newCourseId = 'testId1';
    const courseCandidates: Course[] = [
      {
        courseId: 'testId1',
        courseName: 'testCourse1',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 1',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
    ];
    component.courseCandidates = courseCandidates;
    const modelChangeSpy = jest.spyOn(component.modelChange, 'emit');
    component.courseIdChangeHandler(newCourseId);
    expect(modelChangeSpy).toHaveBeenCalledWith({
      ...component.model,
      courseId: newCourseId,
      courseName: 'testCourse1',
      timeZone: 'Asia/Singapore',
    });
  });

  it('should not emit a modelChange event when no candidates are found', () => {
    const newCourseId = 'testId1';
    const courseCandidates: Course[] = [];
    component.courseCandidates = courseCandidates;
    const modelChangeSpy = jest.spyOn(component.modelChange, 'emit');
    component.courseIdChangeHandler(newCourseId);
    expect(modelChangeSpy).not.toHaveBeenCalled();
  });

  it('should return the minimum session closing datetime as the session opening datetime '
      + 'if it is later than one hour before now', () => {
    const now = moment().tz(component.model.timeZone);
    const date = dateTimeService.getDateInstance(now.add(1, 'days'));
    const time = dateTimeService.getTimeInstance(now);
    component.model.submissionStartDate = date;
    component.model.submissionStartTime = time;
    expect(component.minTimeForSubmissionEnd).toStrictEqual(time);
  });

  it('should return the minimum session closing datetime as one hour before now '
      + 'if it is later than the session opening datetime', () => {
    const now = moment().tz(component.model.timeZone);
    const date = dateTimeService.getDateInstance(now.subtract(1, 'days'));
    const time = dateTimeService.getTimeInstance(now);
    const oneHourBeforeNow = dateTimeService.getTimeInstance(now.subtract(1, 'hours'));
    component.model.submissionStartDate = date;
    component.model.submissionStartTime = time;
    expect(component.minTimeForSubmissionEnd).toStrictEqual(oneHourBeforeNow);
  });

  it('should return the minimum date for session visibility as 30 days before session opening datetime', () => {
    const expectedMinDateForSessionVisible = dateTimeService.getDateInstance(
      dateTimeService.getMomentInstanceFromDate(component.model.submissionStartDate).subtract(30, 'days'),
    );
    expect(component.minDateForSessionVisible).toEqual(expectedMinDateForSessionVisible);
  });

  it('should return the minimum time for session visibility as 30 days before session opening datetime', () => {
    const expectedMinTimeForSessionVisible = dateTimeService.getTimeInstance(
      dateTimeService.getMomentInstanceFromDate(component.model.submissionStartDate).subtract(30, 'days'),
    );
    expect(component.minTimeForSessionVisible).toEqual(expectedMinTimeForSessionVisible);
  });

  it('should return the submissionStartDate as the maximum date for session visibility '
      + 'when response visible setting is LATER', () => {
    component.model.responseVisibleSetting = ResponseVisibleSetting.LATER;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(moment().tz(component.model.timeZone));
    expect(component.maxDateForSessionVisible).toEqual(component.model.submissionStartDate);
  });

  it('should return the submissionStartDate as the maximum date for session visibility '
      + 'when response visible setting is AT_VISIBLE', () => {
    component.model.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(moment().tz(component.model.timeZone));
    expect(component.maxDateForSessionVisible).toEqual(component.model.submissionStartDate);
  });

  it('should return the submissionStartDate as the maximum date for session visibility '
      + 'when response visible setting is CUSTOM and submissionStartDate is before customResponseVisibleDate', () => {
    const now = moment().tz(component.model.timeZone);
    component.model.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(now);
    component.model.customResponseVisibleDate =
        dateTimeService.getDateInstance(now.add(1, 'days'));
    expect(component.maxDateForSessionVisible).toEqual(component.model.submissionStartDate);
  });

  it('should return the customResponseVisibleDate as the maximum date for session visibility '
      + 'when response visible setting is CUSTOM and submissionStartDate is after customResponseVisibleDate', () => {
    const now = moment().tz(component.model.timeZone);
    component.model.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(now.add(1, 'days'));
    component.model.customResponseVisibleDate =
        dateTimeService.getDateInstance(now);
    expect(component.maxDateForSessionVisible).toEqual(component.model.customResponseVisibleDate);
  });

  it('should return the default date format if response visible setting is not defined', () => {
    expect(component.maxDateForSessionVisible).toEqual(getDefaultDateFormat());
  });

  it('should return the submissionStartTime as the maximum time for session visibility '
      + 'when response visible setting is LATER', () => {
    component.model.responseVisibleSetting = ResponseVisibleSetting.LATER;
    component.model.submissionStartTime = dateTimeService.getTimeInstance(moment());
    expect(component.maxTimeForSessionVisible).toEqual(component.model.submissionStartTime);
  });

  it('should return the submissionStartTime as the maximum time for session visibility '
      + 'when response visible setting is AT_VISIBLE', () => {
    component.model.responseVisibleSetting = ResponseVisibleSetting.AT_VISIBLE;
    component.model.submissionStartTime = dateTimeService.getTimeInstance(moment());
    expect(component.maxTimeForSessionVisible).toEqual(component.model.submissionStartTime);
  });

  it('should return submissionStartTime as the maximum time for session visibility '
      + 'when response visible setting is CUSTOM and submissionStartDate is before customResponseVisibleDate', () => {
    const dateTime = moment();
    component.model.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
    component.model.submissionStartTime =
        dateTimeService.getTimeInstance(dateTime);
    component.model.customResponseVisibleTime =
        dateTimeService.getTimeInstance(dateTime);
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(dateTime.tz(component.model.timeZone));
    component.model.customResponseVisibleDate =
        dateTimeService.getDateInstance(dateTime.tz(component.model.timeZone).add(1, 'days'));
    expect(component.maxTimeForSessionVisible).toEqual(component.model.submissionStartTime);
  });

  it('should return customResponseVisibleTime as the maximum time for session visibility '
      + 'when response visible setting is CUSTOM and submissionStartDate is after customResponseVisibleDate', () => {
    const dateTime = moment();
    component.model.responseVisibleSetting = ResponseVisibleSetting.CUSTOM;
    component.model.submissionStartTime =
        dateTimeService.getTimeInstance(dateTime);
    component.model.customResponseVisibleTime =
        dateTimeService.getTimeInstance(dateTime);
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(dateTime.tz(component.model.timeZone).add(1, 'days'));
    component.model.customResponseVisibleDate =
        dateTimeService.getDateInstance(dateTime.tz(component.model.timeZone));
    expect(component.maxTimeForSessionVisible).toEqual(component.model.customResponseVisibleTime);
  });

  it('should return the default time format if response visible setting is not recognized', () => {
    expect(component.maxTimeForSessionVisible).toEqual(getDefaultTimeFormat());
  });

  it('should return submissionStartDate as the minimum date for response visibility'
    + ' when session visible setting is AT_OPEN', () => {
    component.model.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(moment().tz(component.model.timeZone));
    expect(component.minDateForResponseVisible).toEqual(component.model.submissionStartDate);
  });

  it('should return customSessionVisibleDate as the minimum date for response visibility '
      + 'when session visible setting is CUSTOM', () => {
    component.model.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
    component.model.submissionStartDate =
        dateTimeService.getDateInstance(moment().tz(component.model.timeZone));
    component.model.customSessionVisibleDate =
        dateTimeService.getDateInstance(moment().tz(component.model.timeZone).add(1, 'days'));
    expect(component.minDateForResponseVisible).toEqual(component.model.customSessionVisibleDate);
  });

  it('should return the default date format if session visible setting is not recognized', () => {
    expect(component.minDateForResponseVisible).toEqual(getDefaultDateFormat());
  });

  it('should return submissionStartTime as the minimum time for response visibility '
      + 'when session visible setting is AT_OPEN', () => {
    component.model.sessionVisibleSetting = SessionVisibleSetting.AT_OPEN;
    component.model.submissionStartTime =
        dateTimeService.getTimeInstance(moment().tz(component.model.timeZone));
    expect(component.minTimeForResponseVisible).toEqual(component.model.submissionStartTime);
  });

  it('should return customSessionVisibleTime as the minimum time for response visibility '
      + 'when session visible setting is CUSTOM', () => {
    component.model.sessionVisibleSetting = SessionVisibleSetting.CUSTOM;
    component.model.submissionStartTime =
        dateTimeService.getTimeInstance(moment().tz(component.model.timeZone));
    component.model.customSessionVisibleTime =
        dateTimeService.getTimeInstance(moment().tz(component.model.timeZone).add(1, 'days'));
    expect(component.minTimeForResponseVisible).toEqual(component.model.customSessionVisibleTime);
  });

  it('should return the default time format if session visible setting is not defined', () => {
    expect(component.minTimeForResponseVisible).toEqual(getDefaultTimeFormat());
  });

  it('should emit addNewSessionEvent when session edit form mode is ADD', () => {
    component.formMode = SessionEditFormMode.ADD;
    const addNewSessionSpy = jest.spyOn(component.addNewSessionEvent, 'emit');
    component.submitFormHandler();
    expect(addNewSessionSpy).toHaveBeenCalled();
  });

  it('should emit editExistingSessionEvent when session edit form Mode is EDIT', () => {
    component.formMode = SessionEditFormMode.EDIT;
    const editExistingSessionSpy = jest.spyOn(component.editExistingSessionEvent, 'emit');
    component.submitFormHandler();
    expect(editExistingSessionSpy).toHaveBeenCalled();
  });

  it('should display warning when discarding edit to current feedback session', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.cancelHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Discard unsaved edit?',
        SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost.');
  });

  it('should display warning when deleting the current feedback session', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.deleteHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy)
      .toHaveBeenLastCalledWith(`Delete the session <strong>${component.model.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING, 'The session will be moved to the recycle bin. This action can be reverted '
        + 'by going to the "Sessions" tab and restoring the desired session(s).');
  });

  it('should emit copyCurrentSessionEvent when copyHandler is called', () => {
    const copyCurrentSessionSpy = jest.spyOn(component.copyCurrentSessionEvent, 'emit');
    component.copyHandler();
    expect(copyCurrentSessionSpy).toHaveBeenCalled();
  });

  it('should emit copyOtherSessionsEvent when copyOthersHandler is called', () => {
    const copyOtherSessionsSpy = jest.spyOn(component.copyOtherSessionsEvent, 'emit');
    component.copyOthersHandler();
    expect(copyOtherSessionsSpy).toHaveBeenCalled();
  });

  it('should emit closeEditFormEvent when closeEditFormHandler is called', () => {
    const closeEditFormSpy = jest.spyOn(component.closeEditFormEvent, 'emit');
    component.closeEditFormHandler();
    expect(closeEditFormSpy).toHaveBeenCalled();
  });
});
