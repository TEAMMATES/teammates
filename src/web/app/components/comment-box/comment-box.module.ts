import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentEditFormComponent } from './comment-edit-form/comment-edit-form.component';
import { CommentRowComponent } from './comment-row/comment-row.component';
import { CommentTableComponent } from './comment-table/comment-table.component';
import { CommentTableModalComponent } from './comment-table-modal/comment-table-modal.component';
import { CommentToCommentRowModelPipe } from './comment-to-comment-row-model.pipe';
import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from './comment-visibility-setting.pipe';
import { CommentsToCommentTableModelPipe } from './comments-to-comment-table-model.pipe';
import { SingleResponseModule } from '../question-responses/single-response/single-response.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

/**
 * Module for comments table
 */
@NgModule({
  declarations: [
    CommentEditFormComponent,
    CommentRowComponent,
    CommentTableModalComponent,
    CommentTableComponent,
    CommentVisibilityControlNamePipe,
    CommentVisibilityTypeDescriptionPipe,
    CommentVisibilityTypeNamePipe,
    CommentVisibilityTypesJointNamePipe,
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
  imports: [
    TeammatesCommonModule,
    CommonModule,
    SingleResponseModule,
    RichTextEditorModule,
    NgbTooltipModule,
    FormsModule,
  ],
  exports: [
    CommentEditFormComponent,
    CommentRowComponent,
    CommentTableComponent,
    CommentTableModalComponent,
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
  providers: [
    CommentToCommentRowModelPipe,
  ],
})
export class CommentBoxModule { }
