import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CommentVisibilityType, FeedbackResponseComment } from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/visibility-control';
import { CommentEditFormModel } from '../comment-edit-form/comment-edit-form.component';
// tslint:disable-next-line:max-line-length
import { ConfirmDeleteCommentModalComponent } from '../confirm-delete-comment-modal/confirm-delete-comment-modal.component';

/**
 * Model for a comment row.
 */
export interface CommentRowModel {
  originalComment?: FeedbackResponseComment;
  /**
   * Timezone of the original comment.
   */
  timezone?: string;
  // timezone and originalComment are optional under ADD mode.

  commentEditFormModel: CommentEditFormModel;
  isEditing?: boolean;
}

/**
 * Mode of current comment row.
 */
export enum CommentRowMode {
  /**
   * Add new comment.
   */
  ADD,

  /**
   * Edit existing comment.
   */
  EDIT,
}

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
})
export class CommentRowComponent implements OnInit {

  // enum
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input() mode: CommentRowMode = CommentRowMode.EDIT;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() isDisabled: boolean = false;
  @Input() shouldHideSavingButton: boolean = false;
  @Input() model: CommentRowModel = {
    commentEditFormModel: {
      commentText: '',
      commentVisibility: new Map(),
    },
    isEditing: false,
  };
  @Input() showResponsesToInCommentVisibilityType: CommentVisibilityType[] = [];

  @Output() modelChange: EventEmitter<CommentRowModel> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output() deleteCommentEvent: EventEmitter<void> = new EventEmitter();
  @Output() closeEditingEvent: EventEmitter<void> = new EventEmitter();

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
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
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then(() => {
      this.deleteCommentEvent.emit();
    }, () => {});
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit(Object.assign({}, this.model, { [field]: data }));
  }

  /**
   * Gets hint for visibility setting
   */
  getVisibilityHint(): string {
    const showCommentTo: Set<CommentVisibilityType> =
        // tslint:disable-next-line: no-non-null-assertion
        this.model.commentEditFormModel.commentVisibility.get(CommentVisibilityControl.SHOW_COMMENT)!;
    if (showCommentTo.size === 0) {
      return 'nobody';
    }
    let hint: string = '';
    let i: number = 0;
    showCommentTo.forEach((commentVisibilityType: CommentVisibilityType) => {
      if (i === showCommentTo.size - 1 && showCommentTo.size > 1) {
        hint += 'and ';
      }

      switch (commentVisibilityType) {
        case CommentVisibilityType.GIVER:
          hint += 'response giver, ';
          break;
        case CommentVisibilityType.RECIPIENT:
          hint += 'response recipient, ';
          break;
        case CommentVisibilityType.GIVER_TEAM_MEMBERS:
          hint += "response giver's team members, ";
          break;
        case CommentVisibilityType.RECIPIENT_TEAM_MEMBERS:
          hint += "response recipient's team, ";
          break;
        case CommentVisibilityType.STUDENTS:
          hint += 'other students in this course, ';
          break;
        case CommentVisibilityType.INSTRUCTORS:
          hint += 'instructors, ';
          break;
        default:
      }
      i = i + 1;
    });
    return hint.substr(0, hint.length - 2);
  }
}
