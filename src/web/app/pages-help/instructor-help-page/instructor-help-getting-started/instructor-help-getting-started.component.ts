import { Component, OnInit } from '@angular/core';
import { environment } from "../../../../environments/environment";

@Component({
  selector: 'tm-instructor-help-getting-started',
  templateUrl: './instructor-help-getting-started.component.html',
  styleUrls: ['./instructor-help-getting-started.component.scss']
})
export class InstructorHelpGettingStartedComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() { }

  ngOnInit() {
  }

}
