import { Component, OnInit } from '@angular/core';
import { GeneralSectionQuestions } from './general-section-questions';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';

/**
 * General Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-general-section',
  templateUrl: './instructor-help-general-section.component.html',
  styleUrls: ['./instructor-help-general-section.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpGeneralSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  // enums
  GeneralSectionQuestions: typeof GeneralSectionQuestions = GeneralSectionQuestions;
  Sections: typeof Sections = Sections;

  readonly questionsOrder: string[] = [
    GeneralSectionQuestions.FREE_FOR_USE,
    GeneralSectionQuestions.DATA_PRIVACY,
    GeneralSectionQuestions.DST_ADJUSTMENT,
  ];

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }

}
