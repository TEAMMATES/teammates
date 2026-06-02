tsx
import React, { useCallback, useRef, useState, memo } from 'react';

// ---------------------------------------------------------------------------
// Types & constants
// ---------------------------------------------------------------------------

/** Possible async callbacks or sync ones. */
type AsyncVoidFunction = () => void | Promise<void>;

/** Props for the EditCourseDetails component (read‑only). */
export interface EditCourseDetailsProps {
  /** Primary action (save / submit). */
  readonly onSave?: AsyncVoidFunction;
  /** Secondary action (cancel / dismiss). */
  readonly onCancel?: AsyncVoidFunction;
  /** Whether a save operation is currently in progress. */
  readonly saving?: boolean;
}

/** CSS classes – kept in a small constant for maintainability. */
const STYLES = {
  container: 'edit-course-details',
  buttonRow: 'edit-course-details__actions',
  cancelBtn: 'btn btn-secondary',
  saveBtn: 'btn btn-primary',
  srOnly: 'sr-only', // screen‑reader only class
} as const;

// ---------------------------------------------------------------------------
// Logger (production‑ready – replace with a proper lib for serious apps)
// ---------------------------------------------------------------------------

const logger = {
  debug: (msg: string, ...args: unknown[]) => {
    if (process.env.NODE_ENV === 'development') {
      console.debug(`[DEBUG] ${msg}`, ...args);
    }
  },
  info: (msg: string, ...args: unknown[]) => {
    if (process.env.NODE_ENV !== 'production') {
      console.info(`[INFO] ${msg}`, ...args);
    }
  },
  warn: (msg: string, ...args: unknown[]) => {
    if (process.env.NODE_ENV !== 'production') {
      console.warn(`[WARN] ${msg}`, ...args);
    }
  },
  error: (msg: string, ...args: unknown[]) => {
    console.error(`[ERROR] ${msg}`, ...args);
  },
};

// ---------------------------------------------------------------------------
// Runtime prop validation (dev mode only)
// ---------------------------------------------------------------------------

function validateProps(props: EditCourseDetailsProps): void {
  if (process.env.NODE_ENV === 'production') return;

  const { onSave, onCancel, saving } = props;

  if (onSave !== undefined && typeof onSave !== 'function') {
    logger.warn('Invalid prop "onSave": expected a function or undefined.');
  }
  if (onCancel !== undefined && typeof onCancel !== 'function') {
    logger.warn('Invalid prop "onCancel": expected a function or undefined.');
  }
  if (saving !== undefined && typeof saving !== 'boolean') {
    logger.warn('Invalid prop "saving": expected a boolean.');
  }
}

// ---------------------------------------------------------------------------
// Helper: safely call an async or sync callback
// ---------------------------------------------------------------------------

async function safeCallback(
  cb: AsyncVoidFunction | undefined,
  label: string,
): Promise<void> {
  if (typeof cb !== 'function') {
    logger.warn(`Callback "${label}" is not a function – ignoring.`);
    return;
  }
  try {
    const result = cb();
    // If it returns a promise, await it
    if (result instanceof Promise) {
      await result;
    }
    logger.info(`Callback "${label}" completed successfully.`);
  } catch (error: unknown) {
    const errorMessage =
      error instanceof Error ? error.message : 'Unknown error';
    logger.error(`Callback "${label}" failed: ${errorMessage}`, error);
    throw error; // rethrow so caller can handle it
  }
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

/**
 * EditCourseDetails – fully validated, production‑quality form action bar.
 *
 * Displays secondary action (Cancel) on the **left** and primary action (Save)
 * on the **right**, following LTR design conventions.
 *
 * @example
 *