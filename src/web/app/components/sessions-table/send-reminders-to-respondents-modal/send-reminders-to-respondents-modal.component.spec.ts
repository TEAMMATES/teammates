import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SendRemindersToRespondentsModalComponent } from './send-reminders-to-respondents-modal.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { InstructorListInfoTableRowModel, StudentListInfoTableRowModel }
  from '../respondent-list-info-table/respondent-list-info-table-model';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('SendRemindersToRespondentsModalComponent', () => {
  let component: SendRemindersToRespondentsModalComponent;
  let fixture: ComponentFixture<SendRemindersToRespondentsModalComponent>;

  const studentModelBuilder = createBuilder<StudentListInfoTableRowModel>({
    email: 'student@gmail.com',
    name: 'Student',
    teamName: 'Team A',
    sectionName: 'Section 1',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const selectAllStudentCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('#remindAllStu'));
  };

  const selectAllNotSubmittedStudentCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('#remindNotSubmittedStu'));
  };

  const selectAllInstructorCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('#remindAllIns'));
  };

  const selectAllNotSubmittedInstructorCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('#remindNotSubmittedIns'));
  };

  const sendCopyToInsCheckBox = (): DebugElement => {
    return fixture.debugElement.query(By.css('#sendCopyToIns'));
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SendRemindersToRespondentsModalComponent,
        AjaxPreloadComponent,
        RespondentListInfoTableComponent,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SendRemindersToRespondentsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('changeSelectionStatusForAllStudentsHandler: should set all isSelected to true if not all are selected', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(false).build(),
      studentModelBuilder.isSelected(true).build(),
    ];

    const changeSelectionStatusForAllStudentsHandlerSpy =
      jest.spyOn(component, 'changeSelectionStatusForAllStudentsHandler');

    fixture.detectChanges();

    selectAllStudentCheckBox().nativeElement.click();
    expect(changeSelectionStatusForAllStudentsHandlerSpy).toHaveBeenCalledTimes(1);
    expect(component.studentListInfoTableRowModels).toStrictEqual([
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
    ]);
  });

  it('changeSelectionStatusForAllStudentsHandler: should set all isSelected to false'
    + 'if select all checkbox is clicked twice', () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
      ];

      const changeSelectionStatusForAllStudentsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllStudentsHandler');

      fixture.detectChanges();

      selectAllStudentCheckBox().nativeElement.click();
      selectAllStudentCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllStudentsHandlerSpy).toHaveBeenCalledTimes(2);
      expect(component.studentListInfoTableRowModels).toStrictEqual([
        studentModelBuilder.isSelected(false).build(),
        studentModelBuilder.isSelected(false).build(),
        studentModelBuilder.isSelected(false).build(),
      ]);
    });

  it('changeSelectionStatusForAllYetSubmittedStudentsHandler: should set all isSelected to true'
    + 'for all students that has not submitted session', () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      ];

      const changeSelectionStatusForAllYetSubmittedStudentsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllYetSubmittedStudentsHandler');

      fixture.detectChanges();

      selectAllNotSubmittedStudentCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllYetSubmittedStudentsHandlerSpy).toHaveBeenCalledTimes(1);
      expect(component.studentListInfoTableRowModels).toStrictEqual([
        studentModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
      ]);
    });

  it('changeSelectionStatusForAllInstructorsHandler: should set all isSelected to true'
    + 'if not all are selected', () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(false).build(),
        instructorModelBuilder.isSelected(true).build(),
      ];

      const changeSelectionStatusForAllInstructorsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllInstructorsHandler');

      fixture.detectChanges();

      selectAllInstructorCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllInstructorsHandlerSpy).toHaveBeenCalledTimes(1);
      expect(component.instructorListInfoTableRowModels).toStrictEqual([
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
      ]);
    });

  it('changeSelectionStatusForAllInstructorsHandler: should set all isSelected to false'
    + 'if select all checkbox is clicked twice', () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
      ];

      const changeSelectionStatusForAllInstructorsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllInstructorsHandler');

      fixture.detectChanges();

      selectAllInstructorCheckBox().nativeElement.click();
      selectAllInstructorCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllInstructorsHandlerSpy).toHaveBeenCalledTimes(2);
      expect(component.instructorListInfoTableRowModels).toStrictEqual([
        instructorModelBuilder.isSelected(false).build(),
        instructorModelBuilder.isSelected(false).build(),
        instructorModelBuilder.isSelected(false).build(),
      ]);
    });

  it('changeSelectionStatusForAllYetSubmittedInstructorsHandler: should set all isSelected to true'
    + 'for all students that has not submitted session', () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      ];

      const changeSelectionStatusForAllYetSubmittedInstructorsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllYetSubmittedInstructorsHandler');

      fixture.detectChanges();

      selectAllNotSubmittedInstructorCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllYetSubmittedInstructorsHandlerSpy).toHaveBeenCalledTimes(1);
      expect(component.instructorListInfoTableRowModels).toStrictEqual([
        instructorModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
      ]);
    });

  it('changeSelectionStatusForSendingCopyToInstructorHandler: should toggle isSendingCopyToInstructorHandler', () => {
    const changeSelectionStatusForSendingCopyToInstructorHandlerSpy =
      jest.spyOn(component, 'changeSelectionStatusForSendingCopyToInstructorHandler');

    component.isSendingCopyToInstructor = true;
    fixture.detectChanges();

    sendCopyToInsCheckBox().nativeElement.click();
    expect(changeSelectionStatusForSendingCopyToInstructorHandlerSpy).toHaveBeenCalledTimes(1);
    expect(component.isSendingCopyToInstructor).toBeFalsy();
  });

  it('collateReminderResponseHandler: should return correct ReminderResponseModel', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.name('A').isSelected(true).build(),
      studentModelBuilder.name('B').isSelected(false).build(),
      studentModelBuilder.name('C').isSelected(true).build(),
    ];

    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.name('A').isSelected(false).build(),
      instructorModelBuilder.name('B').isSelected(false).build(),
      instructorModelBuilder.name('C').isSelected(true).build(),
    ];

    component.isSendingCopyToInstructor = false;

    fixture.detectChanges();

    const expectedRespondentsToSend = [
      studentModelBuilder.name('A').isSelected(true).build(),
      studentModelBuilder.name('C').isSelected(true).build(),
      instructorModelBuilder.name('C').isSelected(true).build(),
    ];

    expect(component.collateReminderResponseHandler()).toStrictEqual({
      respondentsToSend: expectedRespondentsToSend,
      isSendingCopyToInstructor: false,
    });
  });

  it('isAllStudentsSelected: should return true and checkbox should be checked'
    + 'if all students are selected', async () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllStudentsSelected).toBeTruthy();
      expect(selectAllStudentCheckBox().nativeElement.checked).toBeTruthy();
    });

  it('isAllStudentsSelected: should return false and checkbox should not be checked'
    + 'if not all students are selected', async () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(false).build(),
        studentModelBuilder.isSelected(true).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllStudentsSelected).toBeFalsy();
      expect(selectAllStudentCheckBox().nativeElement.checked).toBeFalsy();
    });

  it('isAllYetToSubmitStudentsSelected: should return true and checkbox should be checked'
    + 'if all non-submitted students are selected', async () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitStudentsSelected).toBeTruthy();
      expect(selectAllNotSubmittedStudentCheckBox().nativeElement.checked).toBeTruthy();
    });

  it('isAllYetToSubmitStudentsSelected: should return false and checkbox should not be'
    + 'checked if not all non-submitted students are selected', async () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
      expect(selectAllNotSubmittedStudentCheckBox().nativeElement.checked).toBeFalsy();
    });

  it('isAllYetToSubmitStudentsSelected: should return false and checkbox should not be'
    + 'checked if all students have submitted', async () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        studentModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitStudentsSelected).toBeFalsy();
      expect(selectAllNotSubmittedStudentCheckBox().nativeElement.checked).toBeFalsy();
    });

  it('isAllYetToSubmitInstructorsSelected: should return true and checkbox should be checked'
    + 'if all non-submitted instructors are selected', async () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitInstructorsSelected).toBeTruthy();
      expect(selectAllNotSubmittedInstructorCheckBox().nativeElement.checked).toBeTruthy();
    });

  it('isAllYetToSubmitInstructorsSelected: should return false and checkbox should not be checked'
    + 'if not all non-submitted instructors are selected', async () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).hasSubmittedSession(false).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(false).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
      expect(selectAllNotSubmittedInstructorCheckBox().nativeElement.checked).toBeFalsy();
    });

  it('isAllYetToSubmitInstructorsSelected: should return false and checkbox should not be'
    + 'checked if all instructors have submitted', async () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
        instructorModelBuilder.isSelected(false).hasSubmittedSession(true).build(),
      ];

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.isAllYetToSubmitInstructorsSelected).toBeFalsy();
      expect(selectAllNotSubmittedInstructorCheckBox().nativeElement.checked).toBeFalsy();
    });
});
