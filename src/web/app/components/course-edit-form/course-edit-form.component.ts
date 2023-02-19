import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, FeedbackSessions } from '../../../types/api-output';
import { FormValidator } from '../../../types/form-validator';
import { ErrorMessageOutput } from '../../error-message-output';
import { CopyCourseModalResult } from '../copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import {
  CourseEditFormMode,
  CourseFormModel,
  CourseEditFormModel,
  DEFAULT_COURSE_FORM_MODEL,
  CourseAddFormModel,
} from './course-edit-form-model';

const formatTwoDigits: Function = (n: number): string => {
  if (n < 10) {
    return `0${n}`;
  }
  return String(n);
};

/**
 * Course edit form component.
 */
@Component({
  selector: 'tm-course-edit-form',
  templateUrl: './course-edit-form.component.html',
  styleUrls: ['./course-edit-form.component.scss'],
})
export class CourseEditFormComponent implements OnInit, OnDestroy {

  // enum
  CourseEditFormMode: typeof CourseEditFormMode = CourseEditFormMode;
  FormValidator: typeof FormValidator = FormValidator;

  @ViewChild('courseForm') form!: UntypedFormGroup;

  @Input()
  isDisplayOnly: boolean = false;

  @Input()
  formMode: CourseEditFormMode = CourseEditFormMode.EDIT;

  @Input()
  set formModel(model: CourseFormModel) { this.model = model; }

  @Input()
  resetFormEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  createNewCourseEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  updateCourseEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  deleteCourseEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  closeFormEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  copyCourseEvent: EventEmitter<CopyCourseModalResult> = new EventEmitter<CopyCourseModalResult>();

  model: CourseFormModel = DEFAULT_COURSE_FORM_MODEL();
  editModel: CourseEditFormModel | undefined = undefined;
  addModel: CourseAddFormModel | undefined = undefined;
  resetEventSubscription: Subscription = new Subscription();

  constructor(private timezoneService: TimezoneService,
    private ngbModal: NgbModal,
    private feedbackSessionsService: FeedbackSessionsService,
    private statusMessageService: StatusMessageService,
  ) { }

  get isInAddMode(): boolean {
    return this.formMode === CourseEditFormMode.ADD;
  }

  get isInEditMode(): boolean {
    return this.formMode === CourseEditFormMode.EDIT;
  }

  get canModifyCourse(): boolean {
    return this.editModel ? this.editModel.canModifyCourse : false;
  }

  get isEditing(): boolean {
    return this.editModel ? this.editModel.isEditing : false;
  }

  get isSaving(): boolean {
    return this.model.isSaving;
  }

  get isInstitutesArrayEmpty(): boolean {
    return this.institutes.length === 0;
  }

  get isCopying(): boolean {
    return this.addModel ? this.addModel.isCopying : false;
  }

  get institutes(): string[] {
    return this.addModel ? this.addModel.institutes : [];
  }

  get isInputDisabled(): boolean {
    return this.isDisplayOnly || (this.isInEditMode && !this.isEditing) || this.isSaving;
  }

  setIsEditing(value: boolean): void {
    if (this.editModel) {
      this.editModel.isEditing = value;
    }
  }

  ngOnInit(): void {
    if (this.isDisplayOnly) {
      return;
    }

    this.updateTimezones();

    if (this.isInAddMode) {
      this.addModel = this.model as CourseAddFormModel;
      this.model.course.timeZone = this.timezoneService.guessTimezone();
      this.updateInstitutes();
    }

    if (this.isInEditMode) {
      this.editModel = this.model as CourseEditFormModel;
    }

    this.resetEventSubscription = this.resetFormEvent.subscribe(() => this.resetForm());
  }

  ngOnDestroy(): void {
    this.resetEventSubscription.unsubscribe();
  }

  updateInstitutes(): void {
    if (this.addModel) {
      this.addModel.institutes =
        Array.from(new Set(this.addModel.allCourses.map((course: Course) => course.institute)));
      if (this.institutes.length) {
        this.addModel.course.institute = this.institutes[0];
      }
    }
  }

  updateTimezones(): void {
    for (const [id, offset] of Object.entries(this.timezoneService.getTzOffsets())) {
      const hourOffset: number = Math.floor(Math.abs(offset) / 60);
      const minOffset: number = Math.abs(offset) % 60;
      const sign: string = offset < 0 ? '-' : '+';
      this.model.timezones.push({
        id,
        offset: offset === 0 ? 'UTC' : `UTC ${sign}${formatTwoDigits(hourOffset)}:${formatTwoDigits(minOffset)}`,
      });
    }
  }

  /**
   * Replaces the timezone value with the detected timezone.
   */
  detectTimezoneHandler(): void {
    if (this.isDisplayOnly) {
      return;
    }
    this.model.course.timeZone = this.timezoneService.guessTimezone();
  }

  /**
   * Handles form submission.
   */
  submitHandler(): void {
    if (this.isDisplayOnly) {
      return;
    }

    if (this.form.invalid) {
      Object.values(this.form.controls).forEach((control: any) => control.markAsTouched());
      return;
    }

    if (this.formMode === CourseEditFormMode.ADD) {
      this.createNewCourseEvent.emit();
    }

    if (this.formMode === CourseEditFormMode.EDIT) {
      this.updateCourseEvent.emit();
    }
  }

  /**
   * Handles event for closing form.
   */
  closeFormHandler(): void {
    this.closeFormEvent.emit();
  }

  /**
   * Handles event for course deletion.
   */
  deleteCourseHandler(): void {
    this.deleteCourseEvent.emit();
  }

  /**
   * Handles event when edits are discarded.
   */
  discardChangesHandler(): void {
    // const editFormModel: CourseEditFormModel = this.model as CourseEditFormModel;
    if (this.editModel) {
      this.editModel.course = { ...this.editModel.originalCourse };
      this.editModel.isEditing = false;
      this.resetForm();
    }
  }

  /**
   * Handles copying from other courses event
   */
  copyCourseHandler(): void {
    if (this.addModel) {
      const modalRef: NgbModalRef = this.ngbModal.open(CopyCourseModalComponent);
      modalRef.componentInstance.isCopyFromOtherSession = true;
      modalRef.componentInstance.allCourses = this.addModel.allCourses;
      modalRef.componentInstance.activeCourses = this.addModel.activeCourses;

      modalRef.componentInstance.fetchFeedbackSessionsEvent.subscribe((courseId: string) => {
        this.feedbackSessionsService
          .getFeedbackSessionsForInstructor(courseId)
          .subscribe((feedbackSessions: FeedbackSessions) => {
            modalRef.componentInstance.courseToFeedbackSession[courseId] = [...feedbackSessions.feedbackSessions];
          });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });

      modalRef.result.then((result: CopyCourseModalResult) => this.copyCourseEvent.emit(result),
        (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
    }
  }

  /**
   * Resets form controls to be untouched and pristine.
   */
  private resetForm(): void {
    Object.values(this.form.controls).forEach((control: any) => control.markAsUntouched());
    Object.values(this.form.controls).forEach((control: any) => control.markAsPristine());
  }
}
