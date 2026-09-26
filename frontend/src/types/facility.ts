// Types for Phase 6 — Facilities, Maintenance & Emergency SOS

export type AssetCategory =
  | 'WASHROOM'
  | 'ELEVATOR'
  | 'GYM'
  | 'STUDY_ROOM'
  | 'WATER_COOLER'
  | 'FIRE_EXTINGUISHER'
  | 'GENERATOR'
  | 'SECURITY_CAMERA'
  | 'OTHER';

export type AssetStatus =
  | 'OPERATIONAL'
  | 'UNDER_MAINTENANCE'
  | 'OUT_OF_SERVICE';

export type MaintenanceFrequency =
  | 'DAILY'
  | 'WEEKLY'
  | 'MONTHLY'
  | 'QUARTERLY'
  | 'YEARLY';

export type MaintenanceStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'OVERDUE';

export type SosType = 'MEDICAL' | 'FIRE' | 'SECURITY' | 'NATURAL_DISASTER' | 'OTHER';

export type SosAlertStatus = 'TRIGGERED' | 'ACKNOWLEDGED' | 'RESOLVED' | 'FALSE_ALARM';

export interface FacilityAssetDto {
  id: string;
  hostelId: string;
  name: string;
  category: AssetCategory;
  location: string;
  status: AssetStatus;
  lastInspectedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFacilityAssetRequest {
  hostelId: string;
  name: string;
  category: AssetCategory;
  location: string;
  status?: AssetStatus;
}

export interface MaintenanceScheduleDto {
  id: string;
  assetId: string;
  assetName: string;
  title: string;
  frequency: MaintenanceFrequency;
  assignedTechnician?: string;
  nextDueDate: string;
  status: MaintenanceStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMaintenanceScheduleRequest {
  assetId: string;
  title: string;
  frequency: MaintenanceFrequency;
  assignedTechnician?: string;
  nextDueDate: string; // ISO date string YYYY-MM-DD
}

export interface EmergencySosAlertDto {
  id: string;
  studentId: string;
  hostelId?: string;
  roomNumber?: string;
  sosType: SosType;
  locationDetails: string;
  status: SosAlertStatus;
  triggeredAt: string;
  resolvedAt?: string;
}

export interface TriggerSosAlertRequest {
  hostelId?: string;
  roomNumber?: string;
  sosType: SosType;
  locationDetails: string;
}

export interface UpdateSosStatusRequest {
  status: SosAlertStatus;
}

export interface FacilityEmergencyStatsDto {
  totalAssetsCount: number;
  operationalAssetsCount: number;
  underMaintenanceCount: number;
  activeSosAlertsCount: number;
  resolvedSosAlertsCount: number;
}
