import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TimeFormat, DateFormat } from 'src/web/types/datetime-const';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';
import { Course } from 'src/web/types/api-output';
import { SessionEditFormMode } from './session-edit-form-model';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;
  const mockModalRef = { result: Promise.resolve(true) };

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

  it('should configure submissionStartTime with minTimeForSubmissionStart when date is' 
  + 'earliest date and time is earlier than earliest possible time', () => {
    
  });

  it('should trigger modelChange with field and date', () => {

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
    const openConfirmationModalSpy = jest.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);
    const cancelEditingSessionSpy = jest.spyOn(component.cancelEditingSessionEvent, 'emit');
    component.cancelHandler();
    await mockModalRef.result;
    expect(openConfirmationModalSpy).toHaveBeenCalled();
    expect(cancelEditingSessionSpy).toHaveBeenCalled();
  });

  it('should emit a deleteExistingSession event after modal confimation', async () => {
    const openConfirmationModalSpy = jest.spyOn((component as any).simpleModalService, 'openConfirmationModal').mockReturnValue(mockModalRef);
    const deleteExistingSessionSpy = jest.spyOn(component.deleteExistingSessionEvent, 'emit');
    component.deleteHandler();
    await mockModalRef.result;
    expect(openConfirmationModalSpy).toHaveBeenCalled();
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
