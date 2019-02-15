import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../ajax-loading/ajax-loading.module';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../../visibility-messages/visibility-messages.module';
import { GiverTypeDescriptionPipe, RecipientTypeDescriptionPipe } from './feedback-path.pipe';
import {
  ContributionQuestionDetailsFormComponent,
} from './question-details-form/contribution-question-details-form/contribution-question-details-form.component';
import {
  TextQuestionDetailsFormComponent,
} from './question-details-form/text-question-details-form/text-question-details-form.component';
import { QuestionEditFormComponent } from './question-edit-form.component';
import {
  VisibilityControlNamePipe,
  VisibilityTypeDescriptionPipe,
  VisibilityTypeNamePipe,
} from './visibility-setting.pipe';

/**
 * Module for all question edit UI in session edit page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    AjaxLoadingModule,
    VisibilityMessagesModule,
    TeammatesCommonModule,
    RichTextEditorModule,
  ],
  declarations: [
    QuestionEditFormComponent,
    GiverTypeDescriptionPipe,
    RecipientTypeDescriptionPipe,
    VisibilityTypeDescriptionPipe,
    VisibilityTypeNamePipe,
    VisibilityControlNamePipe,
    ContributionQuestionDetailsFormComponent,
    TextQuestionDetailsFormComponent,
  ],
  exports: [
    QuestionEditFormComponent,
  ],
})
export class QuestionTypesSessionEditModule { }
