import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../../visibility-messages/visibility-messages.module';
import {
  ContributionPointDescriptionPipe,
} from './contribution-recipient-submission-form/contribution-point-description.pipe';
import {
  ContributionRecipientSubmissionFormComponent,
} from './contribution-recipient-submission-form/contribution-recipient-submission-form.component';
import { QuestionSubmissionFormComponent } from './question-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';
import {
  TextRecipientSubmissionFormComponent,
} from './text-recipient-submission-form/text-recipient-submission-form.component';

/**
 * Module for all question submissions UI in session submissions page.
 */
@NgModule({
  imports: [
    CommonModule,
    TeammatesCommonModule,
    VisibilityMessagesModule,
    NgbModule,
    FormsModule,
  ],
  declarations: [
    QuestionSubmissionFormComponent,
    RecipientTypeNamePipe,
    TextRecipientSubmissionFormComponent,
    ContributionRecipientSubmissionFormComponent,
    ContributionPointDescriptionPipe,
  ],
  exports: [
    QuestionSubmissionFormComponent,
  ],
})
export class QuestionTypesSessionSubmissionModule { }
