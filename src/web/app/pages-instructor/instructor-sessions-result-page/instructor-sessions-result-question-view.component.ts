import { Component } from '@angular/core';
import { InstructorSessionsResultView } from './instructor-sessions-result-view';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Instructor sessions results page question view.
 */
@Component({
  selector: 'tm-instructor-sessions-result-question-view',
  templateUrl: './instructor-sessions-result-question-view.component.html',
  styleUrls: ['./instructor-sessions-result-question-view.component.scss'],
})
export class InstructorSessionsResultQuestionViewComponent extends InstructorSessionsResultView {

  constructor() {
    super(InstructorSessionsResultViewType.QUESTION);
  }

}
