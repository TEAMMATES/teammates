import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorSessionsResultGqrViewComponent } from './instructor-sessions-result-gqr-view.component';
import { InstructorSessionsResultGrqViewComponent } from './instructor-sessions-result-grq-view.component';
import { InstructorSessionsResultPageComponent } from './instructor-sessions-result-page.component';
import { InstructorSessionsResultQuestionViewComponent } from './instructor-sessions-result-question-view.component';
import { InstructorSessionsResultRgqViewComponent } from './instructor-sessions-result-rgq-view.component';
import { InstructorSessionsResultRqgViewComponent } from './instructor-sessions-result-rqg-view.component';

/**
 * Module for instructor sessions result page.
 */
@NgModule({
  declarations: [
    InstructorSessionsResultPageComponent,
    InstructorSessionsResultQuestionViewComponent,
    InstructorSessionsResultRgqViewComponent,
    InstructorSessionsResultGrqViewComponent,
    InstructorSessionsResultRqgViewComponent,
    InstructorSessionsResultGqrViewComponent,
  ],
  exports: [
    InstructorSessionsResultPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
  ],
})
export class InstructorSessionsResultPageModule { }
