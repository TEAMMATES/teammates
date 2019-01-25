import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss']
})
export class InstructorHelpStudentsSectionComponent implements OnInit, OnChanges {
  @Input() key: String = "";
  show : boolean = true;
  
  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  	if (changes.key.currentValue == '') this.show = true;
  	else this.show = false;
  }
}
