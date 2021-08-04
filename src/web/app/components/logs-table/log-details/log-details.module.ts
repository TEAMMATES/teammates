import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { EmailLogDetailsComponent } from './email-log-details.component';
import { ExceptionLogDetailsComponent } from './exception-log-details.component';
import { GenericLogDetailsComponent } from './generic-log-details.component';
import { RequestLogDetailsComponent } from './request-log-details.component';

/**
 * Module for additional log details.
 */
@NgModule({
  declarations: [
    EmailLogDetailsComponent,
    ExceptionLogDetailsComponent,
    GenericLogDetailsComponent,
    RequestLogDetailsComponent,
  ],
  exports: [
    EmailLogDetailsComponent,
    ExceptionLogDetailsComponent,
    GenericLogDetailsComponent,
    RequestLogDetailsComponent,
  ],
  imports: [
    CommonModule,
    NgbTooltipModule,
  ],
})
export class LogDetailsModule { }
