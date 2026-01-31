import { NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NgbProgressbar } from '@ng-bootstrap/ng-bootstrap';
import { LoadingBarService } from '../../../services/loading-bar.service';

/**
 * Loading progress bar for ajax requests.
 */
@Component({
  selector: 'tm-loader-bar',
  templateUrl: './loader-bar.component.html',
  styleUrls: ['./loader-bar.component.scss'],
  imports: [NgIf, NgbProgressbar],
})
export class LoaderBarComponent implements OnInit {

  isShown: boolean = false;

  constructor(private loadingBarService: LoadingBarService) {
  }

  ngOnInit(): void {
    this.loadingBarService.isShown.subscribe((isShown: boolean) => { this.isShown = isShown; });
  }

}
