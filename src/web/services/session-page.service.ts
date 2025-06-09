import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SessionPageService {
  private isExpandableBehaviourSubject = new BehaviorSubject<boolean>(true);
  isExpandedObservable = this.isExpandableBehaviourSubject.asObservable();

  showExpansion(): void {
    this.isExpandableBehaviourSubject.next(true);
  }

  hideExpansion(): void {
    this.isExpandableBehaviourSubject.next(false);
  }
}
