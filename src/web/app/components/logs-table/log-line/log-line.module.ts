import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ExceptionLogLineComponent } from './exception-log-line.component';
import { GenericLogLineComponent } from './generic-log-line.component';
import { RequestLogLineComponent } from './request-log-line.component';

/**
 * Module for log lines.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbTooltipModule,
    ExceptionLogLineComponent,
    GenericLogLineComponent,
    RequestLogLineComponent,
  ],
  exports: [
    ExceptionLogLineComponent,
    GenericLogLineComponent,
    RequestLogLineComponent,
  ],
})
export class LogLineModule { }
