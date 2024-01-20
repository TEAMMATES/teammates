import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { AddingQuestionPanelModule } from '../../components/adding-question-panel/adding-question-panel.module';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { CopySessionModalModule } from '../../components/copy-session-modal/copy-session-modal.module';
import { ExtensionConfirmModalModule } from '../../components/extension-confirm-modal/extension-confirm-modal.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  ModifiedTimestampModalModule,
} from '../../components/modified-timestamps-modal/modified-timestamps-module.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { PreviewSessionPanelModule } from '../../components/preview-session-panel/preview-session-panel.module';
import { QuestionEditFormModule } from '../../components/question-edit-form/question-edit-form.module';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import {
  CopyQuestionsFromOtherSessionsModalComponent,
} from './copy-questions-from-other-sessions-modal/copy-questions-from-other-sessions-modal.component';
import { InstructorSessionEditPageComponent } from './instructor-session-edit-page.component';
import { TemplateQuestionModalComponent } from './template-question-modal/template-question-modal.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionEditPageComponent,
  },
];

/**
 * Module for instructor session edit page.
 */
@NgModule({
  imports: [
    AddingQuestionPanelModule,
    AjaxLoadingModule,
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbDropdownModule,
    NgbTooltipModule,
    TeammatesCommonModule,
    SessionEditFormModule,
    QuestionEditFormModule,
    CopySessionModalModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    LoadingRetryModule,
    TeammatesRouterModule,
    PreviewSessionPanelModule,
    PanelChevronModule,
    ExtensionConfirmModalModule,
    ModifiedTimestampModalModule,
  ],
  declarations: [
    InstructorSessionEditPageComponent,
    TemplateQuestionModalComponent,
    CopyQuestionsFromOtherSessionsModalComponent,
  ],
  exports: [
    InstructorSessionEditPageComponent,
  ],
})
export class InstructorSessionEditPageModule { }
