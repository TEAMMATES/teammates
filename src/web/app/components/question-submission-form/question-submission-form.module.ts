import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { CommentBoxModule } from '../comment-box/comment-box.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { QuestionConstraintModule } from '../question-types/question-constraint/question-constraint.module';
import {
  QuestionEditAnswerFormModule,
} from '../question-types/question-edit-answer-form/question-edit-answer-form.module';
import { QuestionInstructionModule } from '../question-types/question-instruction/question-instruction.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

/**
 * Module for all question submissions UI in session submissions page.
 */
@NgModule({
  imports: [
    CommonModule,
    TeammatesCommonModule,
    VisibilityMessagesModule,
    NgbTooltipModule,
    FormsModule,
    RichTextEditorModule,
    QuestionConstraintModule,
    QuestionInstructionModule,
    QuestionEditAnswerFormModule,
    CommentBoxModule,
    LoadingSpinnerModule,
    AjaxLoadingModule,
    PanelChevronModule,
  ],
  declarations: [
    QuestionSubmissionFormComponent,
    RecipientTypeNamePipe,
  ],
  exports: [
    QuestionSubmissionFormComponent,
  ],
})
export class QuestionSubmissionFormModule { }
