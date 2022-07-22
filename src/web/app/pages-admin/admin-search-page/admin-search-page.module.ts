import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  ProcessAccountRequestPanelModule,
} from '../../components/process-account-request-panel/process-account-request-panel.module';
import { AdminSearchPageComponent } from './admin-search-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminSearchPageComponent,
  },
];

/**
 * Module for admin search page.
 */
@NgModule({
  declarations: [
    AdminSearchPageComponent,
  ],
  exports: [
    AdminSearchPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbTooltipModule,
    RouterModule.forChild(routes),
    ProcessAccountRequestPanelModule,
    LoadingSpinnerModule,
  ],
})
export class AdminSearchPageModule { }
