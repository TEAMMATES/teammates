import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * Handles sharing data to manage elements to be collapsed when following anchor tags.
 */
@Injectable()
export class InstructorHelpDataSharingService {
  studentProfileEditSource: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  peerEvalTipsSource: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currStudentProfileEdit: Observable<boolean> = this.studentProfileEditSource.asObservable();
  currPeerEvalTips: Observable<boolean> = this.peerEvalTipsSource.asObservable();

  constructor() {}

  /**
   * Collapses or de-collapses question on editing student's profile.
   */
  collapseStudentProfileEdit(collapse: boolean): void {
    this.studentProfileEditSource.next(collapse);
  }

  /**
   * Collapses or de-collapses question on tips for peer evaluation.
   */
  collapsePeerEvalTips(collapse: boolean): void {
    this.peerEvalTipsSource.next(collapse);
  }
}
