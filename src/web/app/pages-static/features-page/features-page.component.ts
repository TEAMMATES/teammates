import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from '../../navigation.service';

@Component({
  selector: 'tm-features-page',
  templateUrl: './features-page.component.html',
  styleUrls: ['./features-page.component.scss'],
})
export class FeaturesPageComponent {

  navigateTo(url: string, event: any) {
    this.navigationService.navigateTo(this.router, url, event);
  }

  constructor(private router: Router, private navigationService: NavigationService) {}

}
