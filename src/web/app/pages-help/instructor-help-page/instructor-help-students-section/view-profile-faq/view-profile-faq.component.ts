import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-view-profile-faq',
  templateUrl: './view-profile-faq.component.html',
  styleUrls: ['./view-profile-faq.component.scss']
})
export class ViewProfileFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I view a student's profile To view the profile of Student A from Course B:
                        Go to the Students page and click the panel heading for Course B. You will see a list of students enrolled in the course.
                        Click the
                        View button in the last column of the row corresponding to Student A. A new page will open displaying the student's profile, similar to the sample profile below.
                    The student's profile page displays the student's details and course-related information, such as:
                Section name: the name of the section you enrolled the student in. This only appears if sections are created for the course.
                    Team name: the name of the team you enrolled the student in, or NA if the student does not belong to a team.
                    Official email address: the email address that will be used to contact the student, taken from enrollment information
                        Comments: additional student information you entered in the Comments column during enrollment
                    Below this is the More Info section containing a personal description given by the student, if any.
                     You can press the button in the top-right corner to display the information in a lightbox for better readability.
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
