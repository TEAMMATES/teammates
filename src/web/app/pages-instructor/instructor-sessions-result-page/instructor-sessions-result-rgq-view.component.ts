import { Component } from '@angular/core';
import { InstructorSessionsResultView } from './instructor-sessions-result-view';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Instructor sessions results page RGQ view.
 */
@Component({
  selector: 'tm-instructor-sessions-result-rgq-view',
  templateUrl: './instructor-sessions-result-rgq-view.component.html',
  styleUrls: ['./instructor-sessions-result-rgq-view.component.scss'],
})
export class InstructorSessionsResultRgqViewComponent extends InstructorSessionsResultView {

  constructor() {
    super(InstructorSessionsResultViewType.RGQ);
  }

}
