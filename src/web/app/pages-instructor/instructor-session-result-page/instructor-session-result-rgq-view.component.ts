import { Component } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page RGQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-rgq-view',
  templateUrl: './instructor-session-result-rgq-view.component.html',
  styleUrls: ['./instructor-session-result-rgq-view.component.scss'],
})
export class InstructorSessionResultRgqViewComponent extends InstructorSessionResultView {

  constructor() {
    super(InstructorSessionResultViewType.RGQ);
  }

}
