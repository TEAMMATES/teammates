import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
  photoUrl?: string;

  @Input() studentProfile: StudentProfile | undefined;

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const courseId: string = queryParams.course;
      const studentEmail: string = queryParams.studentemail;

      this.photoUrl
        = `${this.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${studentEmail}`;
    });
  }

  /**
   * Sets the profile picture of a student as the default image
   */
  setDefaultPic(): void {
    this.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
