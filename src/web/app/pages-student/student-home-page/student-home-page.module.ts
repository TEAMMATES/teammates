import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  NgbCollapseModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { StudentHomePageComponent } from './student-home-page.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';
import { Pipes } from '../../pipes/pipes.module';

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
  declarations: [StudentHomePageComponent],
  exports: [StudentHomePageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbTooltipModule,
    Pipes,
    TeammatesCommonModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
    TeammatesRouterModule,
    NgbCollapseModule,
    PanelChevronModule,
  ],
})
export class StudentHomePageModule { }
