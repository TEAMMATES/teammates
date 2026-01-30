import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SectionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { NgFor, NgIf, KeyValuePipe } from '@angular/common';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { GrqRgqViewResponsesComponent } from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.component';

/**
 * Instructor sessions results page RGQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-rgq-view',
  templateUrl: './instructor-session-result-rgq-view.component.html',
  styleUrls: ['./instructor-session-result-rgq-view.component.scss'],
  animations: [collapseAnim],
  imports: [
    NgFor,
    PanelChevronComponent,
    NgIf,
    LoadingSpinnerDirective,
    LoadingRetryComponent,
    GrqRgqViewResponsesComponent,
    KeyValuePipe,
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
