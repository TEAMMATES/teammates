import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { LogsHistogramModule } from '../../components/logs-histogram/logs-histogram.module';
import { SessionEditFormModule } from '../../components/session-edit-form/session-edit-form.module';
import { LogsHistogramPageComponent } from './logs-histogram-page.component';

const routes: Routes = [
  {
    path: '',
    component: LogsHistogramPageComponent,
  },
];

/**
 * Module for logs histogram view page.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbDatepickerModule,
    FormsModule,
    LoadingSpinnerModule,
    SessionEditFormModule,
    LogsHistogramModule,
  ],
  declarations: [
    LogsHistogramPageComponent,
  ],
  exports: [
    LogsHistogramPageComponent,
  ],
})
export class LogsHistogramPageModule { }
