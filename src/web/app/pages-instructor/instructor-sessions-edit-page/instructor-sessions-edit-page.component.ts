import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Instructor feedback session edit page.
 */
@Component({
  selector: 'tm-instructor-sessions-edit-page',
  templateUrl: './instructor-sessions-edit-page.component.html',
  styleUrls: ['./instructor-sessions-edit-page.component.scss'],
})
export class InstructorSessionsEditPageComponent implements OnInit {

  user: string = '';

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
  }

}
