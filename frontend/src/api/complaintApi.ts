import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  ComplaintDto,
  ComplaintStatusHistoryDto,
  ComplaintStatsDto,
  CreateComplaintRequest,
  UpdateComplaintStatusRequest,
  ComplaintStatus,
} from '../types/complaint';

const BASE = '/complaints';

export const complaintApi = {
  // Create
  createComplaint: (data: CreateComplaintRequest): Promise<ComplaintDto> =>
    axiosClient.post<ComplaintDto>(BASE, data).then((r: AxiosResponse<ComplaintDto>) => r.data),

  // Fetch by ID
  getById: (id: string): Promise<ComplaintDto> =>
    axiosClient.get<ComplaintDto>(`${BASE}/${id}`).then((r: AxiosResponse<ComplaintDto>) => r.data),

  // History
  getHistory: (id: string): Promise<ComplaintStatusHistoryDto[]> =>
    axiosClient.get<ComplaintStatusHistoryDto[]>(`${BASE}/${id}/history`).then((r: AxiosResponse<ComplaintStatusHistoryDto[]>) => r.data),

  // Transition status
  updateStatus: (id: string, data: UpdateComplaintStatusRequest): Promise<ComplaintDto> =>
    axiosClient.patch<ComplaintDto>(`${BASE}/${id}/status`, data).then((r: AxiosResponse<ComplaintDto>) => r.data),

  // My complaints (student)
  getMyComplaints: (): Promise<ComplaintDto[]> =>
    axiosClient.get<ComplaintDto[]>(`${BASE}/my`).then((r: AxiosResponse<ComplaintDto[]>) => r.data),

  // By hostel
  getByHostel: (hostelId: string): Promise<ComplaintDto[]> =>
    axiosClient.get<ComplaintDto[]>(`${BASE}/${hostelId}`).then((r: AxiosResponse<ComplaintDto[]>) => r.data),

  // Assigned to me
  getAssignedToMe: (): Promise<ComplaintDto[]> =>
    axiosClient.get<ComplaintDto[]>(`${BASE}/assigned`).then((r: AxiosResponse<ComplaintDto[]>) => r.data),

  // By status
  getByStatus: (status: ComplaintStatus): Promise<ComplaintDto[]> =>
    axiosClient.get<ComplaintDto[]>(`${BASE}/status/${status}`).then((r: AxiosResponse<ComplaintDto[]>) => r.data),

  // Stats
  getStats: (): Promise<ComplaintStatsDto> =>
    axiosClient.get<ComplaintStatsDto>(`${BASE}/stats`).then((r: AxiosResponse<ComplaintStatsDto>) => r.data),

  // Delete
  deleteComplaint: (id: string): Promise<void> =>
    axiosClient.delete(`${BASE}/${id}`).then(() => undefined),
};
