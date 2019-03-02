import { Component } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page RQG view.
 */
@Component({
  selector: 'tm-instructor-session-result-rqg-view',
  templateUrl: './instructor-session-result-rqg-view.component.html',
  styleUrls: ['./instructor-session-result-rqg-view.component.scss'],
})
export class InstructorSessionResultRqgViewComponent extends InstructorSessionResultView {

  constructor() {
    super(InstructorSessionResultViewType.RQG);
  }

}
