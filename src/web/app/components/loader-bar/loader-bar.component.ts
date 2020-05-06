import { Component, OnInit } from '@angular/core';
import { LoadingBarService } from '../../../services/loading-bar.service';

/**
 * Loading progress bar for ajax requests.
 */
@Component({
  selector: 'tm-loader-bar',
  templateUrl: './loader-bar.component.html',
  styleUrls: ['./loader-bar.component.scss'],
})
export class LoaderBarComponent implements OnInit {

  isShown: boolean = false;

  constructor(private loadingBarService: LoadingBarService) {
  }

  ngOnInit(): void {
    this.loadingBarService.isShown.subscribe((isShown: boolean) => { this.isShown = isShown; });
  }

}
