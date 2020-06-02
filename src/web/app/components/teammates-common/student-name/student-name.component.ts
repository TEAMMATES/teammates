import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'tm-student-name',
  templateUrl: './student-name.component.html',
  styleUrls: ['./student-name.component.scss']
})
export class StudentNameComponent implements OnInit {

  @Input()
  name: string = '';

  @Input()
  courseId?: string = '';

  @Input()
  email?: string = '';

  constructor() { }

  ngOnInit() {
  }

}
