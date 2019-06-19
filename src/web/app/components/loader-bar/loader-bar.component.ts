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
    console.log(this.value);

    this.isShown = this.value > 0;

    if (this.value >= 100) {
      setTimeout(() => this.isShown = false,1000);
    }
  }

}
