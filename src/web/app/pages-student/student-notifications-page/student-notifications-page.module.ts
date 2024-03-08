import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentNotificationsPageComponent } from './student-notifications-page.component';
import { UserNotificationsListModule } from '../../components/user-notifications-list/user-notifications-list.module';

const routes: Routes = [
  {
    path: '',
    component: StudentNotificationsPageComponent,
  },
];

/**
 * Module for student notifications page.
 */
@NgModule({
  imports: [
    CommonModule,
    UserNotificationsListModule,
    RouterModule.forChild(routes),
  ],
  declarations: [
    StudentNotificationsPageComponent,
  ],
  exports: [
    StudentNotificationsPageComponent,
  ],
})
export class StudentNotificationsPageModule { }
