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
 * Instructor sessions results page RGQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-rgq-view',
  templateUrl: './instructor-session-result-rgq-view.component.html',
  styleUrls: ['./instructor-session-result-rgq-view.component.scss'],
  animations: [collapseAnim],
  imports: [
    PanelChevronComponent,
    LoadingSpinnerDirective,
    LoadingRetryComponent,
    GrqRgqViewResponsesComponent,
    KeyValuePipe
],
})
export class InstructorSessionResultRgqViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.RGQ);
  }

}
