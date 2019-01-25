import { Component, OnInit } from '@angular/core';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
})
export class InstructorHelpPageComponent implements OnInit {

  search_term : String = "";
  key: String = "";

  constructor() { }

  ngOnInit(): void {
  }

  search(): void {
    if (this.search_term != "")
  	  this.key = this.search_term; 
  }

  clear(): void {
  	this.search_term = "";
    this.key = "";
  }

}
