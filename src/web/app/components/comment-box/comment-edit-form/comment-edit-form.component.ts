import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { CommentVisibilityStateMachine } from '../../../../services/comment-visibility-state-machine';
import { FeedbackResponseCommentService } from '../../../../services/feedback-response-comment.service';
import { StringHelper } from '../../../../services/string-helper';
import {
  CommentVisibilityType,
  FeedbackParticipantType,
  FeedbackVisibilityType,
  ResponseOutput,
} from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/comment-visibility-control';
import { collapseAnim } from '../../teammates-common/collapse-anim';
import { CommentRowMode } from '../comment-row/comment-row.mode';

/**
 * Model for comment edit form.
 */
export interface CommentEditFormModel {
  commentText: string;

  isUsingCustomVisibilities: boolean;
  showCommentTo: CommentVisibilityType[];
  showGiverNameTo: CommentVisibilityType[];
}

/**
 * Comment edit form component
 */
@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss'],
  animations: [collapseAnim],
})
export class CommentEditFormComponent implements OnInit, OnChanges {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  CommentVisibilityType: typeof CommentVisibilityType = CommentVisibilityType;
  CommentVisibilityControl: typeof CommentVisibilityControl = CommentVisibilityControl;
  CommentRowMode: typeof CommentRowMode = CommentRowMode;

  @Input()
  mode: CommentRowMode = CommentRowMode.ADD;

  @Input()
  model: CommentEditFormModel = {
    commentText: '',

    isUsingCustomVisibilities: false,
    showCommentTo: [],
    showGiverNameTo: [],
  };

  @Input()
  isFeedbackParticipantComment: boolean = false;

  @Input()
  response?: ResponseOutput;

  @Input()
  questionShowResponsesTo: FeedbackVisibilityType[] = [];

  @Output() modelChange: EventEmitter<CommentEditFormModel> = new EventEmitter<CommentEditFormModel>();

  @Input()
  isDisabled: boolean = false;

  @Input()
  shouldHideSavingButton: boolean = false;

  @Input()
  shouldHideClosingButton: boolean = false;

  @Input()
  isVisibilityOptionEnabled: boolean = true;

  @Output()
  closeCommentBoxEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<void> = new EventEmitter();

  isVisibilityTableExpanded: boolean = false;
  visibilityStateMachine: CommentVisibilityStateMachine;

  constructor(private commentService: FeedbackResponseCommentService) {
    this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
  }

  ngOnInit(): void {
    if (this.response) {
      this.response.giver = StringHelper.removeAnonymousHash(this.response.giver);
      this.response.recipient = StringHelper.removeAnonymousHash(this.response.recipient);
    }
  }

  ngOnChanges(): void {
    this.visibilityStateMachine = this.commentService.getNewVisibilityStateMachine(this.questionShowResponsesTo);
    if (this.model.isUsingCustomVisibilities) {
      const visibilitySetting: { [TKey in CommentVisibilityControl]: CommentVisibilityType[] } = {
        SHOW_COMMENT: this.model.showCommentTo,
        SHOW_GIVER_NAME: this.model.showGiverNameTo,
      };
      this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
    } else {
      // follow the question's visibilities settings
      this.visibilityStateMachine.allowAllApplicableTypesToSee();
      // sync the two visibilities settings to follow question
      // automatically change the isUsingCustomVisibilities flag to true
      this.triggerModelChangeBatch({
        isUsingCustomVisibilities: true,
        showCommentTo:
            this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(CommentVisibilityControl.SHOW_COMMENT),
        showGiverNameTo:
            this.visibilityStateMachine
                .getVisibilityTypesUnderVisibilityControl(CommentVisibilityControl.SHOW_GIVER_NAME),
      });
    }
  }

  /**
   * toggle the visibility table.
   */
  toggleVisibilityTable(): void {
    this.isVisibilityTableExpanded = !this.isVisibilityTableExpanded;
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({ ...this.model, [field]: data });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChangeBatch(obj: { [key: string]: any }): void {
    this.modelChange.emit({
      ...this.model,
      ...obj,
    });
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

  /**
   * Modifies visibility control of visibility type based on {@code isAllowed}.
   */
  modifyVisibilityControl(
      isAllowed: boolean, visibilityType: CommentVisibilityType, visibilityControl: CommentVisibilityControl): void {
    if (isAllowed) {
      this.visibilityStateMachine.allowToSee(visibilityType, visibilityControl);
    } else {
      this.visibilityStateMachine.disallowToSee(visibilityType, visibilityControl);
    }
    this.triggerModelChangeBatch({
      showCommentTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(CommentVisibilityControl.SHOW_COMMENT),
      showGiverNameTo:
          this.visibilityStateMachine
              .getVisibilityTypesUnderVisibilityControl(CommentVisibilityControl.SHOW_GIVER_NAME),
    });
  }

}
