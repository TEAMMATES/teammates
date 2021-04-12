import { Component, EventEmitter, Input, Output } from '@angular/core';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { SectionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page RQG view.
 */
@Component({
  selector: 'tm-instructor-session-result-rqg-view',
  templateUrl: './instructor-session-result-rqg-view.component.html',
  styleUrls: ['./instructor-session-result-rqg-view.component.scss'],
  animations: [collapseAnim],
})
export class InstructorSessionResultRqgViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.RQG);
  }

}
