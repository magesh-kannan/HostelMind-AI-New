import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  MessMenuDto,
  CreateMessMenuRequest,
  GenerateQrTokenRequest,
  QrTokenResponse,
  ScanQrTokenRequest,
  MealAttendanceDto,
  CreateMessFeedbackRequest,
  MessFeedbackDto,
  MessStatsDto,
} from '../types/mess';

const BASE = '/mess';

export const messApi = {
  // Menu operations
  createOrUpdateMenu: (data: CreateMessMenuRequest): Promise<MessMenuDto> =>
    axiosClient.post<MessMenuDto>(`${BASE}/menu`, data).then((r: AxiosResponse<MessMenuDto>) => r.data),

  getWeeklyMenu: (hostelId?: string): Promise<MessMenuDto[]> =>
    axiosClient.get<MessMenuDto[]>(`${BASE}/menu`, { params: { hostelId } }).then((r: AxiosResponse<MessMenuDto[]>) => r.data),

  // QR Pass & Scanner
  generateQrToken: (data: GenerateQrTokenRequest): Promise<QrTokenResponse> =>
    axiosClient.post<QrTokenResponse>(`${BASE}/generate-qr`, data).then((r: AxiosResponse<QrTokenResponse>) => r.data),

  scanQrToken: (data: ScanQrTokenRequest): Promise<MealAttendanceDto> =>
    axiosClient.post<MealAttendanceDto>(`${BASE}/scan-qr`, data).then((r: AxiosResponse<MealAttendanceDto>) => r.data),

  getAttendance: (date?: string): Promise<MealAttendanceDto[]> =>
    axiosClient.get<MealAttendanceDto[]>(`${BASE}/attendance`, { params: { date } }).then((r: AxiosResponse<MealAttendanceDto[]>) => r.data),

  // Feedback & Stats
  submitFeedback: (data: CreateMessFeedbackRequest): Promise<MessFeedbackDto> =>
    axiosClient.post<MessFeedbackDto>(`${BASE}/feedback`, data).then((r: AxiosResponse<MessFeedbackDto>) => r.data),

  getAllFeedback: (): Promise<MessFeedbackDto[]> =>
    axiosClient.get<MessFeedbackDto[]>(`${BASE}/feedback`).then((r: AxiosResponse<MessFeedbackDto[]>) => r.data),

  getMessStats: (): Promise<MessStatsDto> =>
    axiosClient.get<MessStatsDto>(`${BASE}/stats`).then((r: AxiosResponse<MessStatsDto>) => r.data),
};
