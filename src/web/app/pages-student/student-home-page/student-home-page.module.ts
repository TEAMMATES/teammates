import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  NgbCollapseModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { StudentHomePageComponent } from './student-home-page.component';







const routes: Routes = [
  {
    path: '',
    component: StudentHomePageComponent,
  },
];

/**
 * Module for student home page.
 */
@NgModule({
  exports: [StudentHomePageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbTooltipModule,
    NgbCollapseModule,
    StudentHomePageComponent,
],
})
export class StudentHomePageModule { }
