import { createBuilder } from './generic-builder';
import { GeneralLogEntry, LogEvent, LogSeverity } from '../types/api-output';

/**
 * A builder for creating GeneralLogEntry objects in tests with sensible defaults.
 *
 * This builder provides common default values for log entries and can be customized
 * per test using the fluent API.
 *
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
export const generalLogEntryBuilder = () => createBuilder<Required<GeneralLogEntry>>({
  severity: LogSeverity.DEFAULT,
  trace: '0123456789abcdef',
  insertId: '0123456789abcdef',
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
  timestamp: 1000,
  message: 'Test general log message',
  details: {
    event: LogEvent.DEFAULT_LOG,
    message: 'Test general log details message ',
  },
});
