import { GeneralLogEntry, RequestLogUser } from '../../../types/api-output';

/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel extends GeneralLogEntry {
  timestampForDisplay: string;
  traceIdForSummary?: string;
  httpStatus?: number;
  responseTime ?: number;
  summary: string;
  actionClass: string;
  exceptionClass: string;
  userInfo?: RequestLogUser;
  isDetailsExpanded: boolean;
}
