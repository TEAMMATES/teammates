import { Component, OnInit } from '@angular/core';
import { default as developers } from '../../../data/developers.json';

/**
 * About page.
 */
@Component({
  selector: 'tm-about-page',
  templateUrl: './about-page.component.html',
  styleUrls: ['./about-page.component.scss'],
})
export class AboutPageComponent implements OnInit {

  nDevelopers: number = 0;
  teamMembers: any[] = [];
  pastTeamMembers: any[] = [];
  committers: any[] = [];
  pastCommitters: any[] = [];
  majorContributors: any[] = [];
  multipleContributors: any[] = [];
  singleContributors: any[] = [];

  constructor() {}

  private setUrl(dev: any): any {
    if (dev.username) {
      if (!dev.url) {
        dev.url = `https://github.com/${dev.username}`;
      }
      dev.avatarUrl = `https://github.com/${dev.username}.png`;
    }
    return dev;
  }

  private setDisplayedName(dev: any): any {
    dev.displayedName = dev.name || `@${dev.username}`;
    return dev;
  }

  ngOnInit(): void {
    this.nDevelopers = developers.teammembers.length + developers.committers.length + developers.contributors.length;
    this.teamMembers = developers.teammembers.filter((n: any) => n.currentPosition).map(this.setUrl);
    this.pastTeamMembers = developers.teammembers.filter((n: any) => !n.currentPosition).map(this.setUrl);
    this.committers = developers.committers.filter((n: any) => !n.endPeriod).map(this.setUrl);
    this.pastCommitters = developers.committers.filter((n: any) => n.endPeriod).map(this.setUrl);
    this.majorContributors = developers.contributors.filter((n: any) => n.major).map(this.setUrl);
    this.multipleContributors = developers.contributors.filter((n: any) => !n.major && n.multiple)
        .map(this.setUrl).map(this.setDisplayedName);
    this.singleContributors = developers.contributors.filter((n: any) => !n.major && !n.multiple)
        .map(this.setUrl).map(this.setDisplayedName);
  }

}
