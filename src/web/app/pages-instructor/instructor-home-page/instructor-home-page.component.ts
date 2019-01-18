import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Instructor home page.
 */
@Component({
  selector: 'tm-instructor-home-page',
  templateUrl: './instructor-home-page.component.html',
  styleUrls: ['./instructor-home-page.component.scss'],
})
export class InstructorHomePageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
