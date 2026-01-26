import { createBuilder } from './generic-builder';
import { GeneralLogEntry, LogEvent, LogSeverity } from '../types/api-output';

type GeneralLogEntryBuilder = ReturnType<typeof createBuilder<Required<GeneralLogEntry>>>;

/**
 * A factory function for creating GeneralLogEntryBuilder, which can be used to create a
 * GeneralLogEntry objects in tests with sensible defaults.
 *
 * This builder provides common default values for log entries and can be customized
 * per test using the fluent API.
 *
 * @returns A generic log entry builder object.
 * @example
 * // Basic usage with custom details and message
 * const logEntry = generalLogEntryBuilder
 *   .details(emailSentLogDetails)
 *   .message('Test email log message')
 *   .build();
 *
 * @example
 * // Customize source location
 * const logEntry = generalLogEntryBuilder
 *   .details(exceptionLogDetails)
 *   .sourceLocation({ file: 'com.mock.Mock', line: 100, function: 'handleException' })
 *   .build();
 */
export const generalLogEntryBuilder: () => GeneralLogEntryBuilder =
    () => createBuilder<Required<GeneralLogEntry>>({
  severity: LogSeverity.DEFAULT,
  trace: '0123456789abcdef',
  insertId: 'abcdef0123456789',
  resourceIdentifier: {
    module_id: 'mock',
    version_id: '1-0-0',
    project_id: 'mock-project',
    zone: 'mock-zone-1',
  },
  sourceLocation: {
    file: 'com.mock.Mock',
    line: 100,
    function: 'handle',
  },
  timestamp: 1700000000,
  message: 'Test general log message',
  details: {
    event: LogEvent.DEFAULT_LOG,
    message: 'Test general log details message',
  },
});
