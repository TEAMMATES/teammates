import { Component, OnInit } from '@angular/core';
import { LoaderService } from '../../../services/loader.service';

@Component({
  selector: 'tm-loader-bar',
  templateUrl: './loader-bar.component.html',
  styleUrls: ['./loader-bar.component.scss']
})
export class LoaderBarComponent implements OnInit {

  value: number = 0;
  isShown: boolean = false;

  constructor(private loaderService: LoaderService) { }

  ngOnInit() {
  }

  ngDoCheck() {
    this.value = this.loaderService.getValue();
    this.isShown = true;
  }

  endLoad() {
    this.isShown = false;
  }
}
