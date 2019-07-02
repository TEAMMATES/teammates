import { Injectable } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class MasqueradeModeService {

  private user: string = '';

  constructor(private route: ActivatedRoute) { }

  getMasqueradePerson(): string {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      return this.user;
    });
  }

}
