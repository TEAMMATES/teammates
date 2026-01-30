import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HotTableModule } from '@handsontable/angular';
import { registerAllModules } from 'handsontable/registry';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';








registerAllModules();

const routes: Routes = [
  {
    path: '',
    component: InstructorCourseEnrollPageComponent,
  },
];

/**
 * Module for instructor course enroll page.
 */
@NgModule({
  exports: [
    InstructorCourseEnrollPageComponent,
  ],
  imports: [
    CommonModule,
    HotTableModule,
    RouterModule.forChild(routes),
    InstructorCourseEnrollPageComponent,
],
})
export class InstructorCourseEnrollPageModule { }
