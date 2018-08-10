import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from '../../navigation.service';

/**
 * Features page.
 */
@Component({
  selector: 'tm-features-page',
  templateUrl: './features-page.component.html',
  styleUrls: ['./features-page.component.scss'],
})
export class FeaturesPageComponent {

  constructor(private router: Router, private navigationService: NavigationService) {}

  /**
   * Navigates user to another page.
   */
  public navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

}
