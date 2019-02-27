import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Instructor feedback session result page.
 */
@Component({
  selector: 'tm-instructor-sessions-result-page',
  templateUrl: './instructor-sessions-result-page.component.html',
  styleUrls: ['./instructor-sessions-result-page.component.scss'],
})
export class InstructorSessionsResultPageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
