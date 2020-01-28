import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommentVisibilityType } from '../../../../types/api-output';
import { CommentVisibilityControl } from '../../../../types/visibility-control';

/**
 * Model for comment edit form.
 */
export interface CommentEditFormModel {
  commentText: string;
  commentVisibility: Map<CommentVisibilityControl, Set<CommentVisibilityType>>;
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
  CommentVisibilityControl: typeof CommentVisibilityControl = CommentVisibilityControl;
  CommentVisibilityType: typeof CommentVisibilityType = CommentVisibilityType;

  @Input() model: CommentEditFormModel = {
    commentText: '',
    commentVisibility: new Map(),
  };
  @Input() isDisabled: boolean = false;
  @Input() shouldHideSavingButton: boolean = false;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() showResponsesToInCommentVisibilityType: CommentVisibilityType[] = [];

  @Output() modelChange: EventEmitter<CommentEditFormModel> = new EventEmitter<CommentEditFormModel>();
  @Output() closeCommentBoxEvent: EventEmitter<void> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<void> = new EventEmitter();

  isVisibilityTableExpanded: boolean = false;

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

  /**
   * toggle the visibility table.
   */
  toggleVisibilityTable(): void {
    this.isVisibilityTableExpanded = !this.isVisibilityTableExpanded;
  }

  /**
   * Check whether the visibilityType is applicable.
   */
  isVisibilityTypeApplicable(commentVisibilityType: CommentVisibilityType): boolean {
    return commentVisibilityType === CommentVisibilityType.GIVER ||
        this.showResponsesToInCommentVisibilityType.includes(commentVisibilityType);
  }

  /**
   * Modifies visibility control of comment visibility type
   */
  modifyVisibilityControl(commentVisibilityType: CommentVisibilityType,
                          commentVisibilityControl: CommentVisibilityControl): void {
    const commentVisibilityCol: Set<CommentVisibilityType> =
        // tslint:disable-next-line: no-non-null-assertion
        this.model.commentVisibility.get(commentVisibilityControl)!;
    if (commentVisibilityCol.has(commentVisibilityType)) {
      commentVisibilityCol.delete(commentVisibilityType);
    } else {
      commentVisibilityCol.add(commentVisibilityType);
    }
    this.triggerModelChange('commentVisibility', this.model.commentVisibility);
  }

  /**
   * Checks whether the commentVisibilityControl contains the certain commentVisibilityType
   */
  hasVisibilityType(commentVisibilityType: CommentVisibilityType,
                    commentVisibilityControl: CommentVisibilityControl): boolean {
    // tslint:disable-next-line: no-non-null-assertion
    return this.model.commentVisibility.get(commentVisibilityControl)!.has(commentVisibilityType);
  }
}
