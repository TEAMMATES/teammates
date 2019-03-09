import { Component, Input, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Gender } from '../../../types/gender';
import { StudentProfile } from './student-profile';

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

  @Input() studentProfile: StudentProfile | undefined;

  private backendUrl: string = environment.backendUrl;

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Construct the url for the profile picture from the given key.
   */
  getPictureUrl(pictureKey: string): string {
    if (!pictureKey) {
      return '/assets/images/profile_picture_default.png';
    }
    return `${this.backendUrl}/webapi/students/profilePic?blob-key=${pictureKey}`;
  }

}
