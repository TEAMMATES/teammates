import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AdminNotificationsPageComponent } from './admin-notifications-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminNotificationsPageComponent,
  },
];

/**
 * Module for admin timezone page.
 */
@NgModule({
  declarations: [
    AdminNotificationsPageComponent,
  ],
  exports: [
    AdminNotificationsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
  ],
})
export class AdminNotificationsPageModule { }
