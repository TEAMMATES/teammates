import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Course } from 'src/web/types/api-output';
import { DateFormat, TimeFormat } from 'src/web/types/datetime-const';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SessionEditFormMode } from './session-edit-form-model';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;
  const mockModal = { result: Promise.resolve(true) };
  const submissionStartDateField = 'submissionStartDate';

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
  + 'earliest date and time is earlier than earliest possible time', () => {
    const date: DateFormat = component.minDateForSubmissionStart;
    const minTime: TimeFormat = component.minTimeForSubmissionStart;
    const time: TimeFormat = { hour: minTime.hour - 1, minute: minTime.minute };
    const configureSubmissionOpeningTimeSpy = jest.spyOn(component, 'configureSubmissionOpeningTime');
    component.model.submissionStartTime = time;
    component.triggerSubmissionOpeningDateModelChange(submissionStartDateField, date);
    component.configureSubmissionOpeningTime(minTime);
    expect(component.model.submissionStartTime).toStrictEqual(minTime);
    expect(configureSubmissionOpeningTimeSpy).toHaveBeenCalledWith(minTime);
  });

  it('should trigger the change of the model when the submission opening date changes', () => {
    const date: DateFormat = component.minDateForSubmissionStart;
    const minTime: TimeFormat = component.minTimeForSubmissionStart;
    const time: TimeFormat = { hour: minTime.hour + 1, minute: minTime.minute };
    const triggerModelChangeSpy = jest.spyOn(component, 'triggerModelChange');
    component.model.submissionStartTime = time;
    component.triggerSubmissionOpeningDateModelChange(submissionStartDateField, date);
    expect(triggerModelChangeSpy).toHaveBeenCalledWith(submissionStartDateField, date);
  });

  it('should emit a model change event with the updated field when triggerModelChange is called', () => {
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

  it('should not emit modelChange event when no candidates are found', () => {
    const newCourseId = 'testId1';
    const courseCandidates: Course[] = [];
    component.courseCandidates = courseCandidates;
    const modelChangeSpy = jest.spyOn(component.modelChange, 'emit');
    component.courseIdChangeHandler(newCourseId);
    expect(modelChangeSpy).not.toHaveBeenCalled();
  });

  it('should emit addNewSessionEvent when session edit form mode is ADD', () => {
    component.formMode = SessionEditFormMode.ADD;
    const addNewSessionSpy = jest.spyOn(component.addNewSessionEvent, 'emit');
    component.submitFormHandler();
    expect(addNewSessionSpy).toHaveBeenCalled();
  });

  it('should emit a cancelEditingSession event after modal confimation', async () => {
    jest.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModal);
    const cancelEditingSessionSpy = jest.spyOn(component.cancelEditingSessionEvent, 'emit');
    component.cancelHandler();
    await mockModal.result;
    expect(cancelEditingSessionSpy).toHaveBeenCalled();
  });

  it('should emit a deleteExistingSession event after modal confimation', async () => {
    jest.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModal);
    const deleteExistingSessionSpy = jest.spyOn(component.deleteExistingSessionEvent, 'emit');
    component.deleteHandler();
    await mockModal.result;
    expect(deleteExistingSessionSpy).toHaveBeenCalled();
  });

  it('should emit editExistingSessionEvent when session edit form Mode is EDIT', () => {
    component.formMode = SessionEditFormMode.EDIT;
    const editExistingSessionSpy = jest.spyOn(component.editExistingSessionEvent, 'emit');
    component.submitFormHandler();
    expect(editExistingSessionSpy).toHaveBeenCalled();
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
