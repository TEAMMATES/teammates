import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Student recover session links page.
 */
@Component({
  selector: 'tm-student-recover-session-links-page',
  templateUrl: './link-recovery-page.component.html',
  styleUrls: ['./link-recovery-page.component.scss'],
})
export class LinkRecoveryPageComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  constructor() { }

  ngOnInit(): void {
  }

}
