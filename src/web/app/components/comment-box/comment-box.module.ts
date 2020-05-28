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
import { ParticipantCommentToCommandRowModelPipePipe } from './participant-comment-to-command-row-model-pipe.pipe';

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
    ParticipantCommentToCommandRowModelPipePipe,
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
    ParticipantCommentToCommandRowModelPipePipe,
  ],
  entryComponents: [
    ConfirmDeleteCommentModalComponent,
  ],
})
export class CommentBoxModule { }
