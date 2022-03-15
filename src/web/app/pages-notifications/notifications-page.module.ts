import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoadingRetryModule } from '../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../components/panel-chevron/panel-chevron.module';
import { NotificationsPageComponent } from './notifications-page.component';

const routes: Routes = [
  {
    path: '',
    component: NotificationsPageComponent,
  },
];

/**
 * Module for notification page.
 */
@NgModule({
  imports: [
    CommonModule,
    PanelChevronModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    NotificationsPageComponent,
  ],
  exports: [
    NotificationsPageComponent,
  ],
})
export class NotificationsPageModule { }
