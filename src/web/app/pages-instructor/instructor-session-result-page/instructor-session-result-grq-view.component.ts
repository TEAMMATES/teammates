import { Component } from '@angular/core';
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

  constructor() {
    super(InstructorSessionResultViewType.GRQ);
  }

}
