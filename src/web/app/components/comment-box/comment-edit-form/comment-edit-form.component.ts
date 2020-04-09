import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackParticipantType } from '../../../../types/api-output';

/**
 * Model for comment edit form.
 */
export interface CommentEditFormModel {
  commentText: string;
}

/**
 * Comment edit form component
 */
@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss'],
})
export class CommentEditFormComponent implements OnInit {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  @Input() model: CommentEditFormModel = {
    commentText: '',
  };
  @Output() modelChange: EventEmitter<CommentEditFormModel> = new EventEmitter<CommentEditFormModel>();

  @Input()
  isDisabled: boolean = false;

  @Input()
  shouldHideSavingButton: boolean = false;

  @Input()
  isVisibilityOptionEnabled: boolean = true;

  @Output()
  closeCommentBoxEvent: EventEmitter<void> = new EventEmitter();
  @Output()
  saveCommentEvent: EventEmitter<void> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit(Object.assign({}, this.model, { [field]: data }));
  }

  /**
   * Triggers close comment box event.
   */
  triggerCloseCommentBoxEvent(): void {
    this.closeCommentBoxEvent.emit();
  }

  /**
   * Triggers save comment event.
   */
  triggerSaveCommentEvent(): void {
    this.saveCommentEvent.emit();
  }

}
