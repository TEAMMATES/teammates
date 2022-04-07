import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommentVisibilityStateMachine } from '../../../../services/comment-visibility-state-machine';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import {
  CommentVisibilityType,
  FeedbackResponseComment,
  FeedbackVisibilityType,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/comment-visibility-control';
import { SimpleModalType } from '../../simple-modal/simple-modal-type';
import { CommentEditFormModel } from '../comment-edit-form/comment-edit-form.component';
import { CommentRowMode } from './comment-row.mode';

/**
 * Model for a comment row.
 */
export interface CommentRowModel {
  // original comment and recipient identifier can be null under ADD mode
  originalComment?: FeedbackResponseComment;
  originalRecipientIdentifier?: string;
  /**
   * Timezone of the original comment.
   */
  timezone?: string;
  // timezone and originalComment are optional under ADD mode.

  // optional fields that make the display name more readable
  commentGiverName?: string;
  lastEditorName?: string;

  commentEditFormModel: CommentEditFormModel;
  isEditing: boolean;
}

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
})
export class CommentRowComponent implements OnChanges {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;
  CommentVisibilityControl: typeof CommentVisibilityControl = CommentVisibilityControl;

  @Input()
  mode: CommentRowMode = CommentRowMode.ADD;

  @Input()
  isVisibilityOptionEnabled: boolean = true;

  @Input()
  isDisabled: boolean = false;

  @Input()
  shouldHideSavingButton: boolean = false;

  @Input()
  shouldHideClosingButton: boolean = false;

  @Input()
  shouldHideEditButton: boolean = false;

  @Input()
  shouldHideDeleteButton: boolean = false;

  @Input()
  isFeedbackParticipantComment: boolean = false;

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

  @Input()
  model: CommentRowModel = {
    commentEditFormModel: {
      commentText: '',

      isUsingCustomVisibilities: false,
      showCommentTo: [],
      showGiverNameTo: [],
    },

    isEditing: false,
  };
  @Output()
  modelChange: EventEmitter<CommentRowModel> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output()
  deleteCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output()
  closeEditingEvent: EventEmitter<void> = new EventEmitter();

  visibilityStateMachine: CommentVisibilityStateMachine;

  constructor(private simpleModalService: SimpleModalService,
              private commentService: FeedbackResponseCommentService) {
    this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
  }

  ngOnChanges(): void {
    if (this.model.originalComment) {
      this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
      if (this.model.originalComment.isVisibilityFollowingFeedbackQuestion) {
        // follow the question's visibilities settings
        this.visibilityStateMachine.allowAllApplicableTypesToSee();
      } else {
        const visibilitySetting: { [TKey in CommentVisibilityControl]: CommentVisibilityType[] } = {
          SHOW_COMMENT: this.model.originalComment.showCommentTo,
          SHOW_GIVER_NAME: this.model.originalComment.showCommentTo,
        };
        this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
      }
    }
  }

  /**
   * Closes editing of current comment and restore back the original comment.
   */
  triggerCloseEditing(): void {
    this.closeEditingEvent.emit();
  }

  /**
   * Triggers update comment event.
   */
  triggerSaveCommentEvent(): void {
    this.saveCommentEvent.emit();
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(): void {
    const modalRef: NgbModalRef = this.simpleModalService
        .openConfirmationModal('Delete the comment permanently?', SimpleModalType.DANGER,
            'Are you sure you want to continue?');

    modalRef.result.then(() => {
      this.deleteCommentEvent.emit();
    }, () => {});
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({ ...this.model, [field]: data });
  }
}
