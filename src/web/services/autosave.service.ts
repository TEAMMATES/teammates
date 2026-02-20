import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { FeedbackResponseRecipientSubmissionFormModel } from '../app/components/question-submission-form/question-submission-form-model';

export interface StoredValue<T> {
  value: T;
  expiry: number;
}

interface FeedbackResponseSavedValue {
  hasResponseChangedSerialized: string;
  recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[];
}

/**
 * Service to handle autosaving of data to local storage.
 *
 * Saves are debounced to avoid excessive writes.
 * Saved data automatically expires after a set time and is cleaned up on load.
 */
@Injectable({ providedIn: 'root' })
export class AutosaveService {
  private readonly PREFIX = 'autosave_';
  private readonly DEBOUNCE_MS = 500;
  private readonly TTL_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

  private readonly streams = new Map<string, Subject<unknown>>();

  constructor() {
    this.cleanUpExpiredEntries();
  }

  getSavedFeedbackResponse(
    userId: string,
    feedbackQuestionId: string,
  ): {
    hasResponseChangedForRecipients: Map<string, boolean>,
    recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[],
  } {
    const key = this.getFeedbackResponseStorageKey(userId, feedbackQuestionId);
    const data = this.load<FeedbackResponseSavedValue>(key);
    const response = {
      hasResponseChangedForRecipients: data?.hasResponseChangedSerialized
        ? new Map<string, boolean>(JSON.parse(data.hasResponseChangedSerialized))
        : new Map<string, boolean>(),
      recipientSubmissionForms: data?.recipientSubmissionForms ?? [],
    };

    return response;
  }

  setSavedFeedbackResponse(
    userId: string,
    feedbackQuestionId: string,
    recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[],
    hasResponseChangedForRecipients: Map<string, boolean>,
  ): void {
    const key = this.getFeedbackResponseStorageKey(userId, feedbackQuestionId);
    const data: FeedbackResponseSavedValue = {
      hasResponseChangedSerialized: JSON.stringify(Array.from(hasResponseChangedForRecipients.entries())),
      recipientSubmissionForms,
    };
    this.queueSave(key, data);
  }

  clearSavedFeedbackResponse(userId: string, feedbackQuestionId: string): void {
    const key = this.getFeedbackResponseStorageKey(userId, feedbackQuestionId);
    this.clear(key);
  }

  private cleanUpExpiredEntries(): void {
    const keys: string[] = [];

    for (let i = 0; i < localStorage.length; i += 1) {
      const key = localStorage.key(i);
      if (key?.startsWith(this.PREFIX)) {
        keys.push(key);
      }
    }

    keys.forEach((key) => this.load(key));
  }

  private queueSave(key: string, value: unknown): void {
    if (!this.streams.has(key)) {
      const subject = new Subject<unknown>();

      subject
        .pipe(debounceTime(this.DEBOUNCE_MS))
        .subscribe((latestValue) => {
          this.save(key, latestValue);
        });

      this.streams.set(key, subject);
    }

    this.streams.get(key)!.next(value);
  }

  private save<T>(key: string, value: T): void {
    try {
      const payload: StoredValue<T> = {
        value,
        expiry: Date.now() + this.TTL_MS,
      };
      localStorage.setItem(key, JSON.stringify(payload));
    } catch {
      // fail silently
    }
  }

  private load<T>(key: string): T | null {
    const raw = localStorage.getItem(key);
    if (!raw) {
      return null;
    }

    try {
      const stored = JSON.parse(raw) as StoredValue<T>;

      if (!stored?.expiry || Date.now() > stored.expiry) {
        this.clear(key);
        return null;
      }

      return stored.value;
    } catch {
      this.clear(key);
      return null;
    }
  }

  private clear(key: string): void {
    const stream = this.streams.get(key);
    if (stream) {
      stream.complete();
      this.streams.delete(key);
    }

    localStorage.removeItem(key);
  }

  private getFeedbackResponseStorageKey(userId: string, feedbackQuestionId: string): string {
    return `${this.PREFIX}${userId}_${feedbackQuestionId}`;
  }
}
