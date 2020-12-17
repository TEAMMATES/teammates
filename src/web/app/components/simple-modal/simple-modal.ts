import { TemplateRef } from '@angular/core';

/**
 * Represents a button for the modal.
 */
export interface SimpleModalButton {
  text: string;
  classes?: string;
  action?: Function;
}

/**
 * The standard cancel button for modal.
 */
export const standardCancelButton: (text?: string) => SimpleModalButton = (text?: string) => {
  return {
    text: text || 'Cancel',
    classes: 'btn-light modal-btn-cancel',
  };
};

/**
 * The standard confirm button for modal.
 */
export const standardConfirmButton: (type: string, action: Function, text?: string) => SimpleModalButton =
    (type: string, action: Function, text?: string) => {
      return {
        action,
        text: text || 'Yes',
        classes: `${type} modal-btn-ok`,
      };
    };

/**
 * Parameters for modal.
 */
export interface SimpleModalOptions {
  type: SimpleModalType;
  header: string | TemplateRef<any>;
  content: string | TemplateRef<any>;
  context?: Record<string, any>;
  buttons?: SimpleModalButton[];
  options?: Record<string, string | boolean>;
}

/**
 * Represents the type of the modal to be displayed.
 */
export enum SimpleModalType {
  /**
   * Warning modal.
   */
  WARNING,
  /**
   * Danger modal.
   */
  DANGER,
  /**
   * Info modal.
   */
  INFO,
  /**
   * Neutral modal.
   */
  NEUTRAL,
}
