import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCollapseModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import {
  QuestionEditDetailsFormModule,
} from '../question-types/question-edit-details-form/question-edit-details-form.module';
import { RichTextEditorModule } from '../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { GiverTypeDescriptionPipe, RecipientTypeDescriptionPipe } from './feedback-path.pipe';
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
    NgbCollapseModule,
    NgbDropdownModule,
    NgbTooltipModule,
    AjaxLoadingModule,
    VisibilityMessagesModule,
    TeammatesCommonModule,
    RichTextEditorModule,
    QuestionEditDetailsFormModule,
    PanelChevronModule,
  ],
  declarations: [
    QuestionEditFormComponent,
    GiverTypeDescriptionPipe,
    RecipientTypeDescriptionPipe,
    VisibilityTypeDescriptionPipe,
    VisibilityTypeNamePipe,
    VisibilityControlNamePipe,
  ],
  exports: [
    QuestionEditFormComponent,
  ],
})
export class QuestionEditFormModule { }
