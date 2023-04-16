import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  PreviewSessionResultPanelModule,
} from '../../components/preview-session-result-panel/preview-session-result-panel.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { ViewResultsPanelModule } from '../../components/view-results-panel/view-results-panel.module';

import { InstructorSessionResultPageComponent } from './instructor-session-result-page.component';
import { InstructorSessionResultViewModule } from './instructor-session-result-view.module';
import { SectionTypeDescriptionModule } from './section-type-description.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionResultPageComponent,
  },
];

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  declarations: [
    InstructorSessionResultPageComponent,
  ],
  exports: [
    InstructorSessionResultPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbTooltipModule,
    TeammatesCommonModule,
    RouterModule.forChild(routes),
    InstructorSessionResultViewModule,
    LoadingSpinnerModule,
    AjaxLoadingModule,
    LoadingRetryModule,
    TeammatesRouterModule,
    ViewResultsPanelModule,
    SectionTypeDescriptionModule,
    PreviewSessionResultPanelModule,
  ],
})
export class InstructorSessionResultPageModule { }
