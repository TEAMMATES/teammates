import { KeyValuePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import { SectionTabModel } from './instructor-session-tab.model';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import {
  GrqRgqViewResponsesComponent,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';

/**
 * Instructor sessions results page GRQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-grq-view',
  templateUrl: './instructor-session-result-grq-view.component.html',
  styleUrls: ['./instructor-session-result-grq-view.component.scss'],
  animations: [collapseAnim],
  imports: [
    PanelChevronComponent,
    LoadingSpinnerDirective,
    LoadingRetryComponent,
    GrqRgqViewResponsesComponent,
    KeyValuePipe,
],
})
export class InstructorSessionResultGrqViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.GRQ);
  }

}
