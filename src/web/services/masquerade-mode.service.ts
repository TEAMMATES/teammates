import { Injectable } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class MasqueradeModeService {

  private user: string = '';

  constructor(private route: ActivatedRoute) { }

  fetchMasqueradeUser(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.user) {
        this.user = queryParams.user;
      }
    });
  }

  getMasqueradeUser(): string {
    return this.user;
  }

}
