import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to inform the completion of the saving process
 */
@Component({
  template: `
    <div class="modal-header text-white"
         [ngClass]="{'bg-danger': failToSaveQuestions.length !== 0, 'bg-success': failToSaveQuestions.length === 0}">
      <h5 class="modal-title">
        <span *ngIf="failToSaveQuestions.length === 0">
          <i class="fas fa-check-circle"></i> All responses submitted successfully!
        </span>
        <span *ngIf="failToSaveQuestions.length !== 0">
          <i class="fas fa-exclamation-circle"></i> Some questions do not saved!
        </span>
      </h5>
      <button type="button" class="close" (click)="activeModal.dismiss()">
        <i class="fas fa-times"></i>
      </button>
    </div>
    <div class="modal-body">
      <p *ngIf="notYetAnsweredQuestions.length !== 0">
        <span class="unanswered-exclamation-mark"> &#10071; </span> Note that some questions are yet to be answered.
        They are: {{ notYetAnsweredQuestions }}.
      </p>
      <p *ngIf="failToSaveQuestions.length !== 0" class="text-danger">
        <i class="fas fa-exclamation"></i> Some answers to questions are not saved due to errors.
        Those questions are: {{ failToSaveQuestions }}. Refreshing the page and submit again might solve the problem.
      </p>
      <p *ngIf="failToSaveQuestions.length === 0">
        All your responses have been successfully recorded! You may now leave this page.
      </p>
      <p>
        Note that you can change your responses and submit them again any time before the session closes.
      </p>
      <div class="alert alert-danger" role="alert" *ngIf="hasSubmissionConfirmationError">
        Your responses might be saved but we cannot confirm your submission. Please try again.
      </div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn"
              [ngClass]="
                {'btn-danger': failToSaveQuestions.length !== 0, 'btn-success': failToSaveQuestions.length === 0}"
              (click)="activeModal.close()">OK</button>
    </div>
  `,
})
export class SavingCompleteModalComponent {

  @Input()
  notYetAnsweredQuestions: string = '';

  @Input()
  failToSaveQuestions: string = '';

  @Input()
  hasSubmissionConfirmationError: boolean = false;

  constructor(public activeModal: NgbActiveModal) {}
}

/**
 * Modal to alert the deletion of a feedback session.
 */
@Component({
  template: `
    <div class="modal-header bg-danger text-white">
      <h4 class="modal-title">Feedback Session Deleted!</h4>
      <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <p>The feedback session has been permanently deleted and is no longer accessible.</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-warning" (click)="activeModal.close()">OK</button>
    </div>
  `,
})
export class FeedbackSessionDeletedModalComponent {
  constructor(public activeModal: NgbActiveModal) {}
}

/**
 * Modal to alert that a feedback session is closing soon.
 */
@Component({
  template: `
    <div class="modal-header bg-warning text-white">
      <h4 class="modal-title">Feedback Session Will Be Closing Soon!</h4>
      <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <p>Warning: you have less than 15 minutes before the submission deadline expires!</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-warning" (click)="activeModal.close()">OK</button>
    </div>
  `,
})
export class FeedbackSessionClosingSoonModalComponent {
  constructor(public activeModal: NgbActiveModal) {}
}

/**
 * Modal to alert that a feedback session is closed.
 */
@Component({
  template: `
    <div class="modal-header bg-warning text-white">
      <h4 class="modal-title">Feedback Session Closed</h4>
      <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <p><strong>Feedback Session is Closed</strong></p>
      <p>You can view the questions and any submitted responses
        for this feedback session but cannot submit new responses.</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-warning" (click)="activeModal.close()">OK</button>
    </div>
  `,
})
export class FeedbackSessionClosedModalComponent {
  constructor(public activeModal: NgbActiveModal) {}
}

/**
 * Modal to alert that a feedback session is not open yet.
 */
@Component({
  template: `
    <div class="modal-header bg-warning text-white">
      <h4 class="modal-title">Feedback Session Not Open</h4>
      <button type="button" class="close" aria-label="Close" (click)="activeModal.dismiss()">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>
    <div class="modal-body">
      <p><strong>The feedback session is currently not open for submissions.</strong></p>
      <p>You can view the questions and any submitted responses
        for this feedback session but cannot submit new responses.</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-warning" (click)="activeModal.close()">OK</button>
    </div>
  `,
})
export class FeedbackSessionNotOpenModalComponent {
  constructor(public activeModal: NgbActiveModal) {}
}
