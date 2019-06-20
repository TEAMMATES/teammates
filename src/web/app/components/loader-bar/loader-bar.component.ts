import { Component, OnInit } from '@angular/core';
import { LoaderService } from '../../../services/loader.service';

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

  constructor(private loaderService: LoaderService) {
  }

  ngOnInit(): void {
    this.loaderService.isShown.subscribe((isShown: boolean) => {
      this.isShown = isShown; });
  }

}
