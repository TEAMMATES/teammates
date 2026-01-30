import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentNotificationsPageComponent } from './student-notifications-page.component';


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
    RouterModule.forChild(routes),
    StudentNotificationsPageComponent,
],
  exports: [
    StudentNotificationsPageComponent,
  ],
})
export class StudentNotificationsPageModule { }
