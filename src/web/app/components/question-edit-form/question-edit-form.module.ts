import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbCollapseModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { QuestionEditFormComponent } from './question-edit-form.component';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { FeedbackPathPanelModule } from '../feedback-path-panel/feedback-path-panel.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import {
  QuestionEditBriefDescriptionFormModule,
} from '../question-edit-brief-description-form/question-edit-brief-description-form.module';
import {
  QuestionEditDetailsFormModule,
} from '../question-types/question-edit-details-form/question-edit-details-form.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { VisibilityPanelModule } from '../visibility-panel/visibility-panel.module';

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
    QuestionEditBriefDescriptionFormModule,
    QuestionEditDetailsFormModule,
    PanelChevronModule,
    FeedbackPathPanelModule,
    VisibilityPanelModule,
  ],
  declarations: [
    QuestionEditFormComponent,
  ],
  exports: [
    QuestionEditFormComponent,
  ],
})
export class QuestionEditFormModule { }
