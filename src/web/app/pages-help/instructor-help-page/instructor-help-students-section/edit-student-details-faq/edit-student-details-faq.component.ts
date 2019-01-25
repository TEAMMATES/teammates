import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-edit-student-details-faq',
  templateUrl: './edit-student-details-faq.component.html',
  styleUrls: ['./edit-student-details-faq.component.scss']
})
export class EditStudentDetailsFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I edit a student's details after enrolling the student To edit the name, section, team, contact email, or instructor comments of Student A from Course B Go to the Students page and click the panel heading for Course B. You will see a list of students enrolled in Course B Click the Edit button in the last column of the row corresponding to Student A. In the new page that opens, edit the relevant fields of Student A's details The page will look similar to the example below Click Save Changes to save your changes to Student A's details.Note that moving a student to a different team (i.e. changing the student's Team ID) will change the student's team in all existing sessions in the course
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
