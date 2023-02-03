import { Component } from '@angular/core';

@Component({
  selector: 'app-back-to-top-button',
  template: `
    <button (click)="scrollToTop()">Back to Top</button>
  `,
  styles: [`
    button {
      position: fixed;
      bottom: 20px;
      right: 20px;
      padding: 10px 20px;
      background-color: #333;
      color: #fff;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }
  `]
})
export class BackToTopButtonComponent {
  scrollToTop() {
    window.scrollTo({
      top: 0,
      left: 0,
      behavior: 'smooth'
    });
  }
}
