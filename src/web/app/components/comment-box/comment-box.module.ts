import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SingleResponseModule } from '../question-responses/single-response/single-response.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { CommentEditFormComponent } from './comment-edit-form/comment-edit-form.component';
import { CommentRowComponent } from './comment-row/comment-row.component';
import { CommentTableModalComponent } from './comment-table-modal/comment-table-modal.component';
import { CommentTableComponent } from './comment-table/comment-table.component';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from './comment-visibility-setting.pipe';
import {
  ConfirmDeleteCommentModalComponent,
} from './confirm-delete-comment-modal/confirm-delete-comment-modal.component';
import { ParticipantCommentToCommentRowModelPipe } from './participant-comment-to-comment-row-model.pipe';
import { ResponseOutputToReadonlyCommentTableModelPipe } from './response-output-to-readonly-comment-table-model.pipe';

/**
 * Module for comments table
 */
@NgModule({
  declarations: [
    CommentEditFormComponent,
    CommentRowComponent,
    ConfirmDeleteCommentModalComponent,
    CommentTableModalComponent,
    CommentTableComponent,
    CommentVisibilityControlNamePipe,
    CommentVisibilityTypeDescriptionPipe,
    CommentVisibilityTypeNamePipe,
    CommentVisibilityTypesJointNamePipe,
    ParticipantCommentToCommentRowModelPipe,
    ResponseOutputToReadonlyCommentTableModelPipe,
  ],
  imports: [
    TeammatesCommonModule,
    CommonModule,
    SingleResponseModule,
    RichTextEditorModule,
    NgbModule,
    FormsModule,
  ],
  exports: [
    CommentRowComponent,
    CommentTableComponent,
    CommentTableModalComponent,
    ParticipantCommentToCommentRowModelPipe,
    ResponseOutputToReadonlyCommentTableModelPipe,
  ],
  entryComponents: [
    ConfirmDeleteCommentModalComponent,
  ],
  providers: [
    ParticipantCommentToCommentRowModelPipe,
  ],
})
export class CommentBoxModule { }
