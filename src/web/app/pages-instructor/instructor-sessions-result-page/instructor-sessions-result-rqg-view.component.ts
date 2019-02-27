import { Component } from '@angular/core';
import { InstructorSessionsResultView } from './instructor-sessions-result-view';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Instructor sessions results page RQG view.
 */
@Component({
  selector: 'tm-instructor-sessions-result-rqg-view',
  templateUrl: './instructor-sessions-result-rqg-view.component.html',
  styleUrls: ['./instructor-sessions-result-rqg-view.component.scss'],
})
export class InstructorSessionsResultRqgViewComponent extends InstructorSessionsResultView {

  constructor() {
    super(InstructorSessionsResultViewType.RQG);
  }

}
