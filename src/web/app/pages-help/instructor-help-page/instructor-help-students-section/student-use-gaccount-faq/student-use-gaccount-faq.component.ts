import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';


@Component({
  selector: 'tm-student-use-gaccount-faq',
  templateUrl: './student-use-gaccount-faq.component.html',
  styleUrls: ['./student-use-gaccount-faq.component.scss']
})
export class StudentUseGaccountFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `Is it compulsory for students to use Google accounts Students can submit feedback and view results without having to login to TEAMMATES, unless they choose to link their Google account (optional). TEAMMATES will send students a unique URL to access their feedback sessions and results. However, students
                    who link their TEAMMATES account with their Google account will be able to access a dashboard of all their sessions and results through the TEAMMATES website
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
