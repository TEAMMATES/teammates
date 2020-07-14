import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';

import { EnumToArrayPipe } from './enum-to-array.pipe';
import { FormatDateBriefPipe } from './format-date-brief.pipe';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { FormatPhotoUrlPipe } from './format-photo-url.pipe';
import { InstructorRoleNamePipe } from './instructor-role-name.pipe';
import { PublishStatusNamePipe } from './publish-status-name.pipe';
import { QuestionTypeNamePipe } from './question-type-name.pipe';
import { SafeHtmlPipe } from './safe-html.pipe';
import { StripHtmlTagsPipe } from './strip-html-tags.pipe';
import { StudentNameWithPhotoComponent } from './student-name/student-name-with-photo.component';
import { SubmissionStatusNamePipe } from './submission-status-name.pipe';
import { ViewPhotoPopoverComponent } from './view-photo-popover/view-photo-popover.component';

/**
 * Common module in the project.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgbPopoverModule,
  ],
  declarations: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    StripHtmlTagsPipe,
    QuestionTypeNamePipe,
    InstructorRoleNamePipe,
    FormatDateBriefPipe,
    FormatPhotoUrlPipe,
    ViewPhotoPopoverComponent,
    StudentNameWithPhotoComponent,
  ],
  exports: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    StripHtmlTagsPipe,
    QuestionTypeNamePipe,
    InstructorRoleNamePipe,
    FormatDateBriefPipe,
    FormatPhotoUrlPipe,
    ViewPhotoPopoverComponent,
    StudentNameWithPhotoComponent,
  ],
})
export class TeammatesCommonModule { }
