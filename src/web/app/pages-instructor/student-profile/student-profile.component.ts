import { Component, Input, OnInit } from '@angular/core';
import { Gender, StudentProfile } from '../../../types/api-output';

/**
 * A table displaying a details from a student's profile and a modal to view the more info field.
 */
@Component({
  selector: 'tm-student-profile',
  templateUrl: './student-profile.component.html',
  styleUrls: ['./student-profile.component.scss'],
})
export class StudentProfileComponent implements OnInit {
  Gender: typeof Gender = Gender; // enum

  @Input() photoUrl: string = '/assets/images/profile_picture_default.png';
  @Input() studentProfile: StudentProfile | undefined;

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Sets the profile picture of a student as the default image.
   */
  setDefaultPic(): void {
    this.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
