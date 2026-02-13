import { NgFor, NgIf, KeyValuePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import { SectionTabModel } from './instructor-session-tab.model';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import {
  GqrRqgViewResponsesComponent,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';

/**
 * Instructor sessions results page GQR view.
 */
@Component({
  selector: 'tm-instructor-session-result-gqr-view',
  templateUrl: './instructor-session-result-gqr-view.component.html',
  styleUrls: ['./instructor-session-result-gqr-view.component.scss'],
  animations: [collapseAnim],
  imports: [
    NgFor,
    PanelChevronComponent,
    NgIf,
    LoadingSpinnerDirective,
    LoadingRetryComponent,
    GqrRqgViewResponsesComponent,
    KeyValuePipe,
  ],
})
export class InstructorSessionResultGqrViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.GQR);
  }
}
