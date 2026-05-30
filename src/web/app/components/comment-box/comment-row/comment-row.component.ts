import { Component, EventEmitter, Input, OnChanges, Output, inject } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { CommentRowMode } from './comment-row.mode';
import { CommentVisibilityStateMachine } from '../../../../services/comment-visibility-state-machine';
import { ResponseInstructorCommentService } from '../../../../services/feedback-response-comment.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { CommentVisibilityType, FeedbackVisibilityType, ResponseOutput } from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/comment-visibility-control';
import { SimpleModalType } from '../../simple-modal/simple-modal-type';
import { FormatDateBriefPipe } from '../../teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../../teammates-common/format-date-detail.pipe';
import { SafeHtmlPipe } from '../../teammates-common/safe-html.pipe';
import { CommentEditFormComponent } from '../comment-edit-form/comment-edit-form.component';
import type { CommentRowModel, InstructorCommentRowModel, SavedCommentRowModel } from '../comment.model';
import { CommentVisibilityTypesJointNamePipe } from '../comment-visibility-setting.pipe';

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
  imports: [
    CommentEditFormComponent,
    NgbTooltip,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    FormatDateBriefPipe,
    CommentVisibilityTypesJointNamePipe,
  ],
})
export class CommentRowComponent implements OnChanges {
  private simpleModalService = inject(SimpleModalService);
  private commentService = inject(ResponseInstructorCommentService);

  // enum
  CommentRowMode!: typeof CommentRowMode;
  CommentVisibilityControl!: typeof CommentVisibilityControl;

  @Input()
  mode: CommentRowMode = CommentRowMode.ADD;

  @Input()
  isVisibilityOptionEnabled = true;

  @Input()
  isDisabled = false;

  @Input()
  shouldHideSavingButton = false;

  @Input()
  shouldHideClosingButton = false;

  @Input()
  shouldHideEditButton = false;

  @Input()
  shouldHideDeleteButton = false;

  @Input()
  isFeedbackParticipantComment = false;

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

  @Input()
  model: CommentRowModel = {
    commentType: 'new',
    commentEditFormModel: {
      commentText: '',

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

  constructor() {
    this.CommentRowMode = CommentRowMode;
    this.CommentVisibilityControl = CommentVisibilityControl;
    this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
  }

  get savedCommentModel(): SavedCommentRowModel | undefined {
    return this.model.commentType === 'new' ? undefined : this.model;
  }

  get instructorCommentModel(): InstructorCommentRowModel | undefined {
    return this.model.commentType === 'instructor' ? this.model : undefined;
  }

  ngOnChanges(): void {
    const savedCommentModel: SavedCommentRowModel | undefined = this.savedCommentModel;
    if (savedCommentModel) {
      this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
      const visibilitySetting: { [TKey in CommentVisibilityControl]: CommentVisibilityType[] } = {
        SHOW_COMMENT: savedCommentModel.originalCommentFormModel.showCommentTo,
        SHOW_GIVER_NAME: savedCommentModel.originalCommentFormModel.showGiverNameTo,
      };
      this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
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
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      'Delete the comment permanently?',
      SimpleModalType.DANGER,
      'Are you sure you want to continue?',
    );

    modalRef.result.then(
      () => {
        this.deleteCommentEvent.emit();
      },
      () => {},
    );
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({ ...this.model, [field]: data });
  }
}
