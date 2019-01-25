import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-view-student-responses-faq',
  templateUrl: './view-student-responses-faq.component.html',
  styleUrls: ['./view-student-responses-faq.component.scss']
})
export class ViewStudentResponsesFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I view all the responses a student has given and received To view the responses that Student A from Course B has given and received 
                        Go to the Students page and click the panel heading for Course B. You will see a list of students enrolled in the course.
                        Click All Records button corresponding to Student A to access all the responses Student A has given and received.`;

  terms : Array<String>;

  constructor() { 
  	this.terms = [];
    this.key = "";
  }

  ngOnInit() {
  	this.terms = this.raw_text.split(" ").filter(word => word.length > 3);
  }

  ngOnChanges(changes: SimpleChanges) {
  	this.check_filter(changes.key.currentValue);
  }

  check_filter(val : String) {
  	if (val == "" || this.terms.includes(val)) this.show = true;
  	else this.show = false;
  }
}
