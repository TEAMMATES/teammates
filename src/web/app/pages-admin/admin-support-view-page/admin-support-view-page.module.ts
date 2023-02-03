import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { SupportTicketViewModule } from '../../components/support-ticket-view/support-ticket-view.module';
import { AdminSupportViewPageComponent } from './admin-support-view.component';

const routes: Routes = [
  {
    path: '',
    component: AdminSupportViewPageComponent,
  },
];

/**
 * Module for admin support page.
 */
@NgModule({
  declarations: [
    AdminSupportViewPageComponent,
  ],
  exports: [
    AdminSupportViewPageComponent,
  ],
  imports: [
    // Default stuff
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    // Display the support ticket
    SupportTicketViewModule
  ],
})
export class AdminSupportViewPageModule { }
