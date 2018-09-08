import { Component } from '@angular/core';

/**
 * Base skeleton for instructor pages.
 */
@Component({
  selector: 'tm-instructor-page',
  templateUrl: './instructor-page.component.html',
  styleUrls: ['./instructor-page.component.scss'],
})
export class InstructorPageComponent {

  navItems: any[] = [
    {
      url: '/web/instructor',
      display: 'Home',
    },
  ];

  constructor() {}

}
