import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { InstructorSessionResultPageComponent } from './instructor-session-result-page.component';
import { InstructorSessionResultViewModule } from './instructor-session-result-view.module';









const routes: Routes = [
  {
    path: '',
    component: InstructorSessionResultPageComponent,
  },
];

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  exports: [
    InstructorSessionResultPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbTooltipModule,
    RouterModule.forChild(routes),
    InstructorSessionResultViewModule,
    InstructorSessionResultPageComponent,
],
})
export class InstructorSessionResultPageModule { }
