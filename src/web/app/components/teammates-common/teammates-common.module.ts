import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EnumToArrayPipe } from './enum-to-array.pipe';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { PublishStatusNamePipe } from './publish-status-name.pipe';
import { SubmissionStatusNamePipe } from './submission-status-name.pipe';

/**
 * Common module in the project.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
  ],
  exports: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
  ],
})
export class TeammatesCommonModule { }
