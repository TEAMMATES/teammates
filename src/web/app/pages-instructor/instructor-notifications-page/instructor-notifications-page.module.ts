import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InstructorNotificationsPageComponent } from './instructor-notifications-page.component';


const routes: Routes = [
  {
    path: '',
    component: InstructorNotificationsPageComponent,
  },
];

/**
 * Module for instructor notifications page.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    InstructorNotificationsPageComponent,
],
  exports: [
    InstructorNotificationsPageComponent,
  ],
})
export class InstructorNotificationsPageModule { }
