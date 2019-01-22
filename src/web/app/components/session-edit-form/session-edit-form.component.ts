import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbDateParserFormatter, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { FeedbackSessionSubmissionStatus, ResponseVisibleSetting, SessionVisibleSetting } from '../../feedback-session';
import { SessionEditFormDatePickerFormatter } from './session-edit-form-datepicker-formatter';
import { SessionEditFormMode, SessionEditFormModel, SessionTemplate } from './session-edit-form-model';

/**
 * Form to Add/Edit feedback sessions.
 */
@Component({
  selector: 'tm-session-edit-form',
  templateUrl: './session-edit-form.component.html',
  styleUrls: ['./session-edit-form.component.scss'],
  providers: [{ provide: NgbDateParserFormatter, useClass: SessionEditFormDatePickerFormatter }],
})
export class SessionEditFormComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  SessionVisibleSetting: typeof SessionVisibleSetting = SessionVisibleSetting;
  ResponseVisibleSetting: typeof ResponseVisibleSetting = ResponseVisibleSetting;

  @Input()
  model: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: { hour: 0, minute: 0 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 0, minute: 0 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 0, minute: 0 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 0, minute: 0 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: '',

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    isSaving: false,
    isEditable: true,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  @Output()
  modelChange: EventEmitter<SessionEditFormModel> = new EventEmitter();

  @Input()
  formMode: SessionEditFormMode = SessionEditFormMode.ADD;

  // add mode specific
  @Input()
  coursesIdCandidates: string[] = [];
  @Input()
  sessionTemplates: SessionTemplate[] = [];
  sessionTemplateName: string = '';

  // event emission
  @Output()
  addNewSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  editExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  deleteExistingSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyCurrentSessionEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  copyOtherSessionsEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Handles submit button click event.
   */
  submitFormHandler(): void {
    // resolve local date time to timestamp
    if (this.formMode === SessionEditFormMode.ADD) {
      this.addNewSessionEvent.emit();
    }

    if (this.formMode === SessionEditFormMode.EDIT) {
      this.editExistingSessionEvent.emit();
    }
  }

  /**
   * Handles delete current feedback session button click event.
   */
  deleteHandler(modal: any): void {
    this.modalService.open(modal).result.then(() => {
      this.deleteExistingSessionEvent.emit();
    }, () => {});
  }

  /**
   * Handles copy current feedback session button click event.
   */
  copyHandler(): void {
    this.copyCurrentSessionEvent.emit();
  }

  /**
   * Handles copy from other feedback sessions button click event.
   */
  copyOthersHandler(): void {
    this.copyOtherSessionsEvent.emit();
  }

  /**
   * Handles session 'Help' link click event.
   */
  sessionHelpHandler(): void {
    window.open(`${environment.frontendUrl}/web/instructor/help`);
    // TODO scroll down to the session setup specific section in the help page
  }
}
