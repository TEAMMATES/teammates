import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavigationService } from '../../navigation.service';

@Component({
  selector: 'tm-about-page',
  templateUrl: './about-page.component.html',
  styleUrls: ['./about-page.component.scss'],
})
export class AboutPageComponent implements OnInit {

  nDevelopers: number;
  teamMembers: any[];
  pastTeamMembers: any[];
  committers: any[];
  pastCommitters: any[];
  majorContributors: any[];
  multipleContributors: any[];
  singleContributors: any[];

  setUrl(dev: any): any {
    if (dev.url) {
      return dev;
    }
    if (dev.username) {
      dev.url = `https://github.com/${dev.username}`;
    }
    return dev;
  }

  setDisplayedName(dev: any): any {
    dev.displayedName = dev.name || `@${dev.username}`;
    return dev;
  }

  navigateTo(url: string, event: any) {
    this.navigationService.navigateTo(this.router, url, event);
  }

  constructor(private router: Router, private navigationService: NavigationService,
      private httpClient: HttpClient) {}

  ngOnInit() {
    this.httpClient.get('./assets/data/developers.json').subscribe(resObj => {
      const res = resObj as any;
      this.nDevelopers = res.teammembers.length + res.committers.length + res.contributors.length;
      this.teamMembers = res.teammembers.filter(n => n.currentPosition).map(this.setUrl);
      this.pastTeamMembers = res.teammembers.filter(n => !n.currentPosition).map(this.setUrl);
      this.committers = res.committers.filter(n => !n.endPeriod).map(this.setUrl);
      this.pastCommitters = res.committers.filter(n => n.endPeriod).map(this.setUrl);
      this.majorContributors = res.contributors.filter(n => n.major).map(this.setUrl);
      this.multipleContributors = res.contributors.filter(n => !n.major && n.multiple)
          .map(this.setUrl).map(this.setDisplayedName);
      this.singleContributors = res.contributors.filter(n => !n.major && !n.multiple)
          .map(this.setUrl).map(this.setDisplayedName);
    });
  }

}
