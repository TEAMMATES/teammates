import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EnumToArrayPipe } from './enum-to-array.pipe';
import { FormatDateBriefPipe } from './format-date-brief.pipe';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { PublishStatusNamePipe } from './publish-status-name.pipe';
import { QuestionTypeNamePipe } from './question-type-name.pipe';
import { SafeHtmlPipe } from './safe-html.pipe';
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
    FormatDateBriefPipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    QuestionTypeNamePipe,
  ],
  exports: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    FormatDateBriefPipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    QuestionTypeNamePipe,
  ],
})
export class TeammatesCommonModule { }
