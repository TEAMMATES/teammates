import { Component, OnInit } from '@angular/core';
import { GeneralSectionQuestions } from './general-section-questions';
import { RouterLink } from '@angular/router';
import { InstructorHelpPanelComponent } from '../instructor-help-panel/instructor-help-panel.component';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';
import { Sections } from '../sections';

/**
 * General Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-general-section',
  templateUrl: './instructor-help-general-section.component.html',
  styleUrls: ['./instructor-help-general-section.component.scss'],
  imports: [InstructorHelpPanelComponent, RouterLink],
})
export class InstructorHelpGeneralSectionComponent extends InstructorHelpSectionComponent implements OnInit {
  // enums
  GeneralSectionQuestions!: typeof GeneralSectionQuestions;
  Sections!: typeof Sections;

  readonly questionsOrder: string[] = [
    GeneralSectionQuestions.FREE_FOR_USE,
    GeneralSectionQuestions.DATA_PRIVACY,
    GeneralSectionQuestions.DST_ADJUSTMENT,
  ];

  constructor() {
    super();
    this.GeneralSectionQuestions = GeneralSectionQuestions;
    this.Sections = Sections;
  }

  getQuestionsOrder(): string[] {
    return this.questionsOrder;
  }
}
