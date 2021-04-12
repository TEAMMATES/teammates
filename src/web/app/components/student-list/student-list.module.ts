import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { JoinStatePipe } from './join-state.pipe';
import { StudentListComponent } from './student-list.component';

/**
 * Module for student list table component.
 */
@NgModule({
  declarations: [
    JoinStatePipe,
    StudentListComponent,
  ],
  exports: [
    StudentListComponent,
  ],
  imports: [
    CommonModule,
    NgbTooltipModule,
    RouterModule,
    TeammatesCommonModule,
    TeammatesRouterModule,
  ],
})
export class StudentListModule { }
