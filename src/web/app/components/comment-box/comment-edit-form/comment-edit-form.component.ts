import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { StringHelper } from '../../../../services/string-helper';
import { ResponseOutput } from '../../../../types/api-output';
import { RichTextEditorComponent } from '../../rich-text-editor/rich-text-editor.component';
import type { CommentEditFormModel } from '../comment.model';
import { CommentRowMode } from '../comment-row/comment-row.mode';

/**
 * Comment edit form component
 */
@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  imports: [RichTextEditorComponent],
})
export class CommentEditFormComponent implements OnInit {
  // enum
  CommentRowMode!: typeof CommentRowMode;

  @Input()
  mode: CommentRowMode = CommentRowMode.ADD;

  @Input()
  model: CommentEditFormModel = {
    commentText: '',
  };

  @Input()
  isFeedbackParticipantComment = false;

  @Input()
  response?: ResponseOutput;

  @Output() modelChange: EventEmitter<CommentEditFormModel> = new EventEmitter<CommentEditFormModel>();

  @Input()
  isDisabled = false;

  @Input()
  shouldHideSavingButton = false;

  @Input()
  shouldHideClosingButton = false;

  @Output()
  closeCommentBoxEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<void> = new EventEmitter();

  constructor() {
    this.CommentRowMode = CommentRowMode;
  }

  ngOnInit(): void {
    if (this.response) {
      this.response.giver = StringHelper.removeAnonymousHash(this.response.giver);
      this.response.recipient = StringHelper.removeAnonymousHash(this.response.recipient);
    }
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: unknown): void {
    this.modelChange.emit({ ...this.model, [field]: data });
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
