import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { SupportTicketListModule } from '../../components/support-ticket-list/support-ticket-list.module';
import { AdminSupportPageComponent } from './admin-support-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminSupportPageComponent,
  },
];

/**
 * Module for admin support page.
 */
@NgModule({
  declarations: [
    AdminSupportPageComponent,
  ],
  exports: [
    AdminSupportPageComponent,
  ],
  imports: [
    // Default stuff
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    PanelChevronModule,
    // Display the support ticket
    SupportTicketListModule
  ],
})
export class AdminSupportPageModule { }
