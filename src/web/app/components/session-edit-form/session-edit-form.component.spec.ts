import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import moment from 'moment-timezone';
import { SessionEditFormMode } from './session-edit-form-model';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Course } from '../../../types/api-output';
import { SimpleModalType } from '../simple-modal/simple-modal-type';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;
  let simpleModalService: SimpleModalService;

  // A fixed reference timestamp: 2024-07-12 10:00 UTC.
  const startTimestamp: number = Date.UTC(2024, 6, 12, 10, 0);

  // Frozen "now" for all time-sensitive tests: 2024-07-12 15:30 UTC (mid-minute, not near a boundary).
  const FROZEN_NOW = new Date('2024-07-12T15:30:00.000Z');

  beforeEach(async () => {
    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(FROZEN_NOW);

    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SessionEditFormComponent);
    simpleModalService = TestBed.inject(SimpleModalService);
    component = fixture.componentInstance;
    component.model.timeZone = 'UTC';
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit a modelChange event with the updated field when triggerModelChange is called', () => {
    const field = 'courseId';
    const data = 'testId';
    const modelChangeSpy = vi.spyOn(component.modelChange, 'emit');
    component.triggerModelChange(field, data);
    expect(modelChangeSpy).toHaveBeenCalledWith({
      ...component.model,
      [field]: data,
    });
  });

  it('should return the minimum submission opening timestamp as 2 hours before now', () => {
    const expected = moment().tz('UTC').subtract(2, 'hours').second(0).millisecond(0).valueOf();
    expect(component.minTimestampForSubmissionStart).toEqual(expected);
  });

  it('should return the maximum submission opening timestamp as 23:59 of the day 12 months from now', () => {
    const expected = moment().tz('UTC').add(12, 'months').hour(23).minute(59).second(0).millisecond(0).valueOf();
    expect(component.maxTimestampForSubmissionStart).toEqual(expected);
  });

  it('should return the submission opening timestamp as the minimum closing time when it is later than now', () => {
    component.model.submissionStartTimestamp = moment().tz('UTC').add(1, 'day').valueOf();
    expect(component.minTimestampForSubmissionEnd).toEqual(component.model.submissionStartTimestamp);
  });

  it('should return one hour before now as the minimum closing time when the opening time is in the past', () => {
    component.model.submissionStartTimestamp = moment().tz('UTC').subtract(1, 'year').valueOf();
    const expected = moment().tz('UTC').subtract(1, 'hour').second(0).millisecond(0).valueOf();
    expect(component.minTimestampForSubmissionEnd).toEqual(expected);
  });

  it('should return the submission opening timestamp as the min response visible time', () => {
    component.model.submissionStartTimestamp = startTimestamp;
    expect(component.minTimestampForResponseVisible).toEqual(startTimestamp);
  });

  it('should emit modelChange event when a valid course ID is provided', () => {
    const newCourseId = 'testId1';
    const courseCandidates: Course[] = [
      {
        courseId: 'testId1',
        courseName: 'testCourse1',
        timeZone: 'Asia/Singapore',
        institute: 'Institute 1',
        country: 'SG',
        instituteId: 'test-institute-id',
        creationTimestamp: 1000000000000,
        deletionTimestamp: 1500000000000,
      },
    ];
    component.courseCandidates = courseCandidates;
    const modelChangeSpy = vi.spyOn(component.modelChange, 'emit');
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
    const modelChangeSpy = vi.spyOn(component.modelChange, 'emit');
    component.courseIdChangeHandler(newCourseId);
    expect(modelChangeSpy).not.toHaveBeenCalled();
  });

  it('should emit addNewSessionEvent when session edit form mode is ADD', () => {
    component.formMode = SessionEditFormMode.ADD;
    const addNewSessionSpy = vi.spyOn(component.addNewSessionEvent, 'emit');
    component.submitFormHandler();
    expect(addNewSessionSpy).toHaveBeenCalled();
  });

  it('should emit editExistingSessionEvent when session edit form Mode is EDIT', () => {
    component.formMode = SessionEditFormMode.EDIT;
    const editExistingSessionSpy = vi.spyOn(component.editExistingSessionEvent, 'emit');
    component.submitFormHandler();
    expect(editExistingSessionSpy).toHaveBeenCalled();
  });

  it('should display warning when discarding edit to current feedback session', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy = vi
      .spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.cancelHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Discard unsaved edit?',
      SimpleModalType.WARNING,
      'Warning: Any unsaved changes will be lost.',
    );
  });

  it('should display warning when deleting the current feedback session', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy = vi
      .spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.deleteHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      `Delete the session <strong>${component.model.feedbackSessionName}</strong>?`,
      SimpleModalType.WARNING,
      'The session will be moved to the recycle bin. This action can be reverted ' +
        'by going to the "Sessions" tab and restoring the desired session(s).',
    );
  });

  it('should emit copyCurrentSessionEvent when copyHandler is called', () => {
    const copyCurrentSessionSpy = vi.spyOn(component.copyCurrentSessionEvent, 'emit');
    component.copyHandler();
    expect(copyCurrentSessionSpy).toHaveBeenCalled();
  });

  it('should emit copyOtherSessionsEvent when copyOthersHandler is called', () => {
    const copyOtherSessionsSpy = vi.spyOn(component.copyOtherSessionsEvent, 'emit');
    component.copyOthersHandler();
    expect(copyOtherSessionsSpy).toHaveBeenCalled();
  });

  it('should emit closeEditFormEvent when closeEditFormHandler is called', () => {
    const closeEditFormSpy = vi.spyOn(component.closeEditFormEvent, 'emit');
    component.closeEditFormHandler();
    expect(closeEditFormSpy).toHaveBeenCalled();
  });
});
