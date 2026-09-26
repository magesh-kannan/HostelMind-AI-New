import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  FacilityAssetDto,
  CreateFacilityAssetRequest,
  AssetStatus,
  MaintenanceScheduleDto,
  CreateMaintenanceScheduleRequest,
  EmergencySosAlertDto,
  TriggerSosAlertRequest,
  UpdateSosStatusRequest,
  FacilityEmergencyStatsDto,
} from '../types/facility';

const BASE = '/facilities';

export const facilityApi = {
  // ── Assets ──────────────────────────────────────────────────────────────

  createAsset: (data: CreateFacilityAssetRequest): Promise<FacilityAssetDto> =>
    axiosClient
      .post<FacilityAssetDto>(`${BASE}/assets`, data)
      .then((r: AxiosResponse<FacilityAssetDto>) => r.data),

  getAssets: (hostelId?: string, status?: AssetStatus): Promise<FacilityAssetDto[]> =>
    axiosClient
      .get<FacilityAssetDto[]>(`${BASE}/assets`, { params: { hostelId, status } })
      .then((r: AxiosResponse<FacilityAssetDto[]>) => r.data),

  updateAssetStatus: (assetId: string, status: AssetStatus): Promise<FacilityAssetDto> =>
    axiosClient
      .patch<FacilityAssetDto>(`${BASE}/assets/${assetId}/status`, null, { params: { status } })
      .then((r: AxiosResponse<FacilityAssetDto>) => r.data),

  // ── Maintenance ──────────────────────────────────────────────────────────

  createMaintenanceSchedule: (data: CreateMaintenanceScheduleRequest): Promise<MaintenanceScheduleDto> =>
    axiosClient
      .post<MaintenanceScheduleDto>(`${BASE}/maintenance`, data)
      .then((r: AxiosResponse<MaintenanceScheduleDto>) => r.data),

  getMaintenanceSchedules: (): Promise<MaintenanceScheduleDto[]> =>
    axiosClient
      .get<MaintenanceScheduleDto[]>(`${BASE}/maintenance`)
      .then((r: AxiosResponse<MaintenanceScheduleDto[]>) => r.data),

  // ── Emergency SOS ────────────────────────────────────────────────────────

  triggerSos: (data: TriggerSosAlertRequest): Promise<EmergencySosAlertDto> =>
    axiosClient
      .post<EmergencySosAlertDto>(`${BASE}/sos`, data)
      .then((r: AxiosResponse<EmergencySosAlertDto>) => r.data),

  getAllSosAlerts: (): Promise<EmergencySosAlertDto[]> =>
    axiosClient
      .get<EmergencySosAlertDto[]>(`${BASE}/sos`)
      .then((r: AxiosResponse<EmergencySosAlertDto[]>) => r.data),

  getActiveSosAlerts: (): Promise<EmergencySosAlertDto[]> =>
    axiosClient
      .get<EmergencySosAlertDto[]>(`${BASE}/sos/active`)
      .then((r: AxiosResponse<EmergencySosAlertDto[]>) => r.data),

  updateSosStatus: (alertId: string, data: UpdateSosStatusRequest): Promise<EmergencySosAlertDto> =>
    axiosClient
      .patch<EmergencySosAlertDto>(`${BASE}/sos/${alertId}/status`, data)
      .then((r: AxiosResponse<EmergencySosAlertDto>) => r.data),

  // ── Stats ────────────────────────────────────────────────────────────────

  getStats: (): Promise<FacilityEmergencyStatsDto> =>
    axiosClient
      .get<FacilityEmergencyStatsDto>(`${BASE}/stats`)
      .then((r: AxiosResponse<FacilityEmergencyStatsDto>) => r.data),
};
