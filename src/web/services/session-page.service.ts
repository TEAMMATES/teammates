import { BehaviorSubject, Observable, of } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class SessionPageService {
  private isExpandableBehaviourSubject = new BehaviorSubject<boolean>(true);
  isExpandableUser = this.isExpandableBehaviourSubject.asObservable();

  constructor() {}

  getIsExpanded(): Observable<boolean> {
    console.log("getting initial");
    return of(this.isExpandableBehaviourSubject.value);
  }

  toggleExpansion(): void {
    this.isExpandableBehaviourSubject.next(
      !this.isExpandableBehaviourSubject.value
    );
  }
}
