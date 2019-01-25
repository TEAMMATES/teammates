import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'tm-search-student-faq',
  templateUrl: './search-student-faq.component.html',
  styleUrls: ['./search-student-faq.component.scss']
})
export class SearchStudentFaqComponent implements OnInit, OnChanges {
  @Input() key : String;
  show : boolean = true;
  
  //paste search terms here
  raw_text : String = `How do I search for a student in my course You can search for students from the Search page. Click the Search tab in the navigation bar at the top of the page. You should see a search bar similar to the one below To search for a student Tick the option Students below the search box Type your search terms into the search bar. You can search for a student record based on:
           	             Section name
                            Team name
                            Student name
                            Email Click the Search button If you search for alice, the search results would show something like this assuming such a student exists
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
