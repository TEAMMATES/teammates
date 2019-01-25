import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-student-change-gaccount-faq',
  templateUrl: './student-change-gaccount-faq.component.html',
  styleUrls: ['./student-change-gaccount-faq.component.scss']
})
export class StudentChangeGaccountFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I change the Google ID associated with a student At the moment, there is no way for students to update their own Google IDs
                Please ask the student to teammates@comp.nus.edu.sg contact us for assistance changing his her Google ID.
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
