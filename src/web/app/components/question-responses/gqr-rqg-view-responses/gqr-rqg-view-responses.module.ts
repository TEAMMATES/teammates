import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { GqrRqgViewResponsesComponent } from './gqr-rqg-view-responses.component';





import { PerQuestionViewResponsesModule } from '../per-question-view-responses/per-question-view-responses.module';


/**
 * Module for component to display list of responses in GQR/RQG view.
 */
@NgModule({
  exports: [GqrRqgViewResponsesComponent],
  imports: [
    CommonModule,
    PerQuestionViewResponsesModule,
    NgbCollapseModule,
    GqrRqgViewResponsesComponent,
],
})
export class GqrRqgViewResponsesModule { }
