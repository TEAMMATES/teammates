import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-email-student-faq',
  templateUrl: './email-student-faq.component.html',
  styleUrls: ['./email-student-faq.component.scss']
})
export class EmailStudentFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I email a student or students in my course On the Students page, filter out the selected student/students Click
                      Copy Emails to copy their email addresses to your clipboard Use your preferred email provider to email the students
                    `;

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
