import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  ProcessAccountRequestPanelModule,
} from '../../components/process-account-request-panel/process-account-request-panel.module';
import { AdminRequestsPageComponent } from './admin-requests-page.component';

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
  ],
  exports: [
    AdminRequestsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingRetryModule,
    LoadingSpinnerModule,
    FormsModule,
    NgbModule,
    ProcessAccountRequestPanelModule,
  ],
})
export class AdminRequestsPageModule { }
