import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgbPopoverModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';

import { EnumToArrayPipe } from './enum-to-array.pipe';
import {
  GiverTypeDescriptionPipe,
  RecipientTypeDescriptionPipe,
  RecipientTypeSimplifiedDescriptionPipe,
} from './feedback-path.pipe';
import { FormatDateBriefPipe } from './format-date-brief.pipe';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { GeneratedChoicePipe } from './generated-choice.pipe';
import { InstructorRoleDescriptionPipe } from './instructor-role-description.pipe';
import { InstructorRoleNamePipe } from './instructor-role-name.pipe';
import { NotificationStyleClassPipe } from './notification-style-class.pipe';
import { NotificationStyleDescriptionPipe } from './notification-style-description.pipe';
import { PublishStatusNamePipe } from './publish-status-name.pipe';
import { QuestionTypeHelpPathPipe } from './question-type-help-path.pipe';
import { QuestionTypeNamePipe } from './question-type-name.pipe';
import { SafeHtmlPipe } from './safe-html.pipe';
import { StripHtmlTagsPipe } from './strip-html-tags.pipe';
import { SubmissionStatusNamePipe } from './submission-status-name.pipe';
import { SubmissionStatusTooltipPipe } from './submission-status-tooltip.pipe';
import {
  VisibilityControlNamePipe,
  VisibilityTypeDescriptionPipe,
  VisibilityTypeNamePipe,
} from './visibility-setting.pipe';

/**
 * Common module in the project.
 */
@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgbPopoverModule,
    NgbTooltipModule,
    TeammatesRouterModule,
  ],
  declarations: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    SubmissionStatusTooltipPipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    StripHtmlTagsPipe,
    QuestionTypeNamePipe,
    QuestionTypeHelpPathPipe,
    InstructorRoleDescriptionPipe,
    InstructorRoleNamePipe,
    FormatDateBriefPipe,
    GeneratedChoicePipe,
    GiverTypeDescriptionPipe,
    RecipientTypeDescriptionPipe,
    RecipientTypeSimplifiedDescriptionPipe,
    VisibilityControlNamePipe,
    VisibilityTypeDescriptionPipe,
    VisibilityTypeNamePipe,
    NotificationStyleDescriptionPipe,
    NotificationStyleClassPipe,
  ],
  exports: [
    EnumToArrayPipe,
    SubmissionStatusNamePipe,
    SubmissionStatusTooltipPipe,
    PublishStatusNamePipe,
    FormatDateDetailPipe,
    SafeHtmlPipe,
    StripHtmlTagsPipe,
    QuestionTypeNamePipe,
    QuestionTypeHelpPathPipe,
    InstructorRoleDescriptionPipe,
    InstructorRoleNamePipe,
    FormatDateBriefPipe,
    GeneratedChoicePipe,
    GiverTypeDescriptionPipe,
    RecipientTypeDescriptionPipe,
    RecipientTypeSimplifiedDescriptionPipe,
    VisibilityControlNamePipe,
    VisibilityTypeDescriptionPipe,
    VisibilityTypeNamePipe,
    NotificationStyleDescriptionPipe,
    NotificationStyleClassPipe,
  ],
})
export class TeammatesCommonModule { }
