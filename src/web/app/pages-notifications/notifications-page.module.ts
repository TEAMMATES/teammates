import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
