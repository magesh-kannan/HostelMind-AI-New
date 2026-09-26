import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type { AuditLogDto, AuditStatsDto } from '../types/audit';
import type { UserDto } from '../types/auth';

const BASE = '/audit';

export const auditApi = {
  // ── Audit Logs ────────────────────────────────────────────────────────────

  getAllLogs: (params?: {
    entityType?: string;
    action?: string;
    userId?: string;
  }): Promise<AuditLogDto[]> =>
    axiosClient
      .get<AuditLogDto[]>(`${BASE}/logs`, { params })
      .then((r: AxiosResponse<AuditLogDto[]>) => r.data),

  getMyLogs: (): Promise<AuditLogDto[]> =>
    axiosClient
      .get<AuditLogDto[]>(`${BASE}/logs/my`)
      .then((r: AxiosResponse<AuditLogDto[]>) => r.data),

  getStats: (): Promise<AuditStatsDto> =>
    axiosClient
      .get<AuditStatsDto>(`${BASE}/stats`)
      .then((r: AxiosResponse<AuditStatsDto>) => r.data),

  // ── User Directory ────────────────────────────────────────────────────────

  getAllUsers: (): Promise<UserDto[]> =>
    axiosClient
      .get<UserDto[]>(`${BASE}/users`)
      .then((r: AxiosResponse<UserDto[]>) => r.data),

  getUserById: (userId: string): Promise<UserDto> =>
    axiosClient
      .get<UserDto>(`${BASE}/users/${userId}`)
      .then((r: AxiosResponse<UserDto>) => r.data),
};
