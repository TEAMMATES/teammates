import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SectionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page GRQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-grq-view',
  templateUrl: './instructor-session-result-grq-view.component.html',
  styleUrls: ['./instructor-session-result-grq-view.component.scss'],
})
export class InstructorSessionResultGrqViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.GRQ);
  }

}
