import { Component, Input, OnInit } from '@angular/core';

/**
 * Display student name with photo popover
 */
@Component({
  selector: 'tm-student-name-with-photo',
  templateUrl: './student-name-with-photo.component.html',
  styleUrls: ['./student-name-with-photo.component.scss'],
})
export class StudentNameWithPhotoComponent implements OnInit {

  @Input()
  name: string = '';

  @Input()
  courseId: string = '';

  @Input()
  email: string = '';

  constructor() { }

  ngOnInit(): void {
  }

}
