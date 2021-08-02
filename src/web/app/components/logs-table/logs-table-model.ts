import { SourceLocation } from '../../../types/api-output';

/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  timestamp: string;
  severity: string;
  traceId: string;
  traceIdForSummary?: string;
  resourceIdentifier: Record<string, string>;
  sourceLocation: SourceLocation;
  httpStatus?: number;
  responseTime ?: number;
  summary: string;
  details: any;
  actionClass: string;
  exceptionClass: string;
  userInfo?: any;
  isDetailsExpanded: boolean;
}
