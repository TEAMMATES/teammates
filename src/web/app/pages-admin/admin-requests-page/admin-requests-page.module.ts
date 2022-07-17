import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { AdminRequestsPageComponent } from './admin-requests-page.component';
import {
  ProcessAccountRequestPanelComponent,
} from './process-account-request-panel/process-account-request-panel.component';

const routes: Routes = [
  {
    path: '',
    component: AdminRequestsPageComponent,
  },
];

/**
 * Module for admin requests page.
 */
@NgModule({
  declarations: [
    AdminRequestsPageComponent,
    ProcessAccountRequestPanelComponent,
  ],
  exports: [
    AdminRequestsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    PanelChevronModule,
    LoadingRetryModule,
    TeammatesCommonModule,
    LoadingSpinnerModule,
    FormsModule,
    AjaxLoadingModule,
  ],
})
export class AdminRequestsPageModule { }
