import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';

import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Students Section of the Instructor Help Page.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() {
    super();
  }

  //<div (click)="createMessage()" class="message" *ngIf="showCreateMessage(array,message)"></div>

  displaySubsection(array:any, firstPoint: number, lastPoint:number) {
    return array.length == 0 || this.showQuestion.slice(firstPoint, lastPoint).reduce((x, y) => x || y, false);
  }


  ngOnInit(): void {

  }

}
