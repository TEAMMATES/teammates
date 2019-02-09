import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../../visibility-messages/visibility-messages.module';
import {
  ContributionQuestionConstraintComponent,
} from './question-constraint/contribution-question-constraint/contribution-question-constraint.component';
import {
  TextQuestionConstraintComponent,
} from './question-constraint/text-question-constraint/text-question-constraint.component';
import {
  ContributionQuestionInstructionComponent,
} from './question-instruction/contribution-question-instruction/contribution-question-instruction.component';
import {
  TextQuestionInstructionComponent,
} from './question-instruction/text-question-instruction/text-question-instruction.component';
import {
  QuestionSubmissionFormComponent,
} from './question-submission-form.component';
import {
  ContributionPointDescriptionPipe,
} from './recipient-submission-form/contribution-recipient-submission-form/contribution-point-description.pipe';
import {
  ContributionRecipientSubmissionFormComponent,
// tslint:disable-next-line:max-line-length
} from './recipient-submission-form/contribution-recipient-submission-form/contribution-recipient-submission-form.component';
import {
  TextRecipientSubmissionFormComponent,
} from './recipient-submission-form/text-recipient-submission-form/text-recipient-submission-form.component';
import { RecipientTypeNamePipe } from './recipient-type-name.pipe';

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
    RichTextEditorModule,
  ],
  declarations: [
    QuestionSubmissionFormComponent,
    RecipientTypeNamePipe,
    TextRecipientSubmissionFormComponent,
    ContributionRecipientSubmissionFormComponent,
    ContributionPointDescriptionPipe,
    TextQuestionInstructionComponent,
    ContributionQuestionInstructionComponent,
    TextQuestionConstraintComponent,
    ContributionQuestionConstraintComponent,
  ],
  exports: [
    QuestionSubmissionFormComponent,
  ],
})
export class QuestionTypesSessionSubmissionModule { }
