import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbDatepickerModule, NgbTimepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { LogsPageComponent } from './logs-page.component';





const routes: Routes = [
  {
    path: '',
    component: LogsPageComponent,
  },
];

/**
 * Module for log page.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    NgbDatepickerModule,
    NgbTimepickerModule,
    LogsPageComponent,
],
  exports: [
    LogsPageComponent,
  ],
})
export class LogsPageModule { }
