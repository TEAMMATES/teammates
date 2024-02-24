import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SectionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';

/**
 * Instructor sessions results page GQR view.
 */
@Component({
  selector: 'tm-instructor-session-result-gqr-view',
  templateUrl: './instructor-session-result-gqr-view.component.html',
  styleUrls: ['./instructor-session-result-gqr-view.component.scss'],
  animations: [collapseAnim],
})
export class InstructorSessionResultGqrViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  @Input() responses: Record<string, SectionTabModel> = {};

  constructor() {
    super(InstructorSessionResultViewType.GQR);
  }
}
