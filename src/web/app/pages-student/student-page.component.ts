import { Component } from '@angular/core';

/**
 * Base skeleton for student pages.
 */
@Component({
  selector: 'tm-student-page',
  templateUrl: './student-page.component.html',
  styleUrls: ['./student-page.component.scss'],
})
export class StudentPageComponent {

  navItems: any[] = [
    {
      url: '/web/student',
      display: 'Home',
    },
  ];

  constructor() {}

}
