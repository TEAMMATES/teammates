import { BehaviorSubject } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class SessionPageService {
  private isExpandableBehaviourSubject = new BehaviorSubject<boolean>(true);
  isExpandedObservable = this.isExpandableBehaviourSubject.asObservable();

  constructor() {}

  showExpansion(): void {
    this.isExpandableBehaviourSubject.next(true);
  }

  hideExpansion(): void {
    this.isExpandableBehaviourSubject.next(false);
  }
}
