import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Student course details page.
 */
@Component({
  selector: 'tm-student-course-details-page',
  templateUrl: './student-course-details-page.component.html',
  styleUrls: ['./student-course-details-page.component.scss'],
})
export class StudentCourseDetailsPageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
