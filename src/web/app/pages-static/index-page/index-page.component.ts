import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavigationService } from '../../navigation.service';

@Component({
  selector: 'tm-index-page',
  templateUrl: './index-page.component.html',
  styleUrls: ['./index-page.component.scss'],
})
export class IndexPageComponent implements OnInit {

  testimonial: any;
  testimonials: any[];
  testimonialIndex = -1;
  submissionsNumber = '10,000,000+';

  navigateTo(url: string, event: any) {
    this.navigationService.navigateTo(this.router, url, event);
  }

  constructor(private router: Router, private navigationService: NavigationService,
      private httpClient: HttpClient) {}

  ngOnInit() {
    this.httpClient.get('./assets/data/index.json').subscribe(resObj => {
      const res = resObj as any;

      const formatNumber = (n) => {
        let number = String(n);
        const expression = /(\d+)(\d{3})/;
        while (expression.test(number)) {
          number = number.replace(expression, '$1,$2');
        }
        return number;
      };

      const timeElapsed = new Date().getTime() - new Date(res.submissionsBaseDate).getTime();
      this.submissionsNumber = formatNumber(res.submissionsBase + Math.floor(timeElapsed / 60 / 60 / 1000) * res.submissionsRate);

      this.testimonials = res.testimonials;

      const cycleTestimonial = () => {
        this.testimonialIndex = (this.testimonialIndex + 1) % this.testimonials.length;
        this.testimonial = this.testimonials[this.testimonialIndex];
      };

      cycleTestimonial();
      setInterval(cycleTestimonial, 5000);
    });
  }

}
