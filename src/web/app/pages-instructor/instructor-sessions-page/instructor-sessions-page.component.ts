import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Instructor feedback sessions list page.
 */
@Component({
  selector: 'tm-instructor-sessions-page',
  templateUrl: './instructor-sessions-page.component.html',
  styleUrls: ['./instructor-sessions-page.component.scss'],
})
export class InstructorSessionsPageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
