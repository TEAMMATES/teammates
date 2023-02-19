import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ErrorReportComponent } from './error-report.component';

/**
 * Error report module.
 */
@NgModule({
  imports: [
    CommonModule,
    FormsModule,
  ],
  exports: [ErrorReportComponent],
  declarations: [ErrorReportComponent],
})
export class ErrorReportModule { }
