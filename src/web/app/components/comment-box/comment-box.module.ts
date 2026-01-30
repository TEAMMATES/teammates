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

import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';


/**
 * Module for comments table
 */
@NgModule({
  imports: [
    CommonModule,
    RichTextEditorModule,
    NgbTooltipModule,
    FormsModule,
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
