// Types for Phase 7 — Audit Logs & Digital ID Card

export interface AuditLogDto {
  id: string;
  userId?: string;
  action: string;
  entityType?: string;
  entityId?: string;
  details?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface AuditStatsDto {
  totalEvents: number;
  byEntityType: Record<string, number>;
  byAction: Record<string, number>;
}
