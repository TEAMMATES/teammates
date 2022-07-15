import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { AdminRequestsPageComponent } from './admin-requests-page.component';
import {
  ProcessAccountRequestPanelComponent
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
    FormsModule,
    NgbDatepickerModule,
    NgbTimepickerModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    PanelChevronModule,
    ReactiveFormsModule,
  ],
})
export class AdminRequestsPageModule { }
