import { Component } from '@angular/core';
import { InstructorSessionsResultView } from './instructor-sessions-result-view';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Instructor sessions results page GRQ view.
 */
@Component({
  selector: 'tm-instructor-sessions-result-grq-view',
  templateUrl: './instructor-sessions-result-grq-view.component.html',
  styleUrls: ['./instructor-sessions-result-grq-view.component.scss'],
})
export class InstructorSessionsResultGrqViewComponent extends InstructorSessionsResultView {

  constructor() {
    super(InstructorSessionsResultViewType.GRQ);
  }

}
