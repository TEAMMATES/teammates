import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  GqrRqgViewResponsesModule,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';
import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import {
  PerQuestionViewResponsesModule,
} from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultPageComponent } from './instructor-session-result-page.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  declarations: [
    InstructorSessionResultPageComponent,
    InstructorSessionResultQuestionViewComponent,
    InstructorSessionResultRgqViewComponent,
    InstructorSessionResultGrqViewComponent,
    InstructorSessionResultRqgViewComponent,
    InstructorSessionResultGqrViewComponent,
  ],
  exports: [
    InstructorSessionResultPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    QuestionTextWithInfoModule,
    PerQuestionViewResponsesModule,
    GqrRqgViewResponsesModule,
    GrqRgqViewResponsesModule,
  ],
})
export class InstructorSessionResultPageModule { }
