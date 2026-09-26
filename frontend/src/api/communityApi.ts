import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  Announcement,
  AnnouncementCategory,
  CreateAnnouncementRequest,
} from '../types/community';

const BASE = '/community';

export const communityApi = {
  getAnnouncements: (category?: AnnouncementCategory): Promise<Announcement[]> =>
    axiosClient
      .get<Announcement[]>(`${BASE}/announcements`, { params: { category } })
      .then((r: AxiosResponse<Announcement[]>) => r.data),

  getAnnouncementById: (id: string): Promise<Announcement> =>
    axiosClient
      .get<Announcement>(`${BASE}/announcements/${id}`)
      .then((r: AxiosResponse<Announcement>) => r.data),

  createAnnouncement: (req: CreateAnnouncementRequest): Promise<Announcement> =>
    axiosClient
      .post<Announcement>(`${BASE}/announcements`, req)
      .then((r: AxiosResponse<Announcement>) => r.data),

  deleteAnnouncement: (id: string): Promise<void> =>
    axiosClient
      .delete<void>(`${BASE}/announcements/${id}`)
      .then((r: AxiosResponse<void>) => r.data),

  reactToAnnouncement: (id: string, reaction: string): Promise<void> =>
    axiosClient
      .post<void>(`${BASE}/announcements/${id}/reactions`, { reaction })
      .then((r: AxiosResponse<void>) => r.data),

  removeReaction: (id: string): Promise<void> =>
    axiosClient
      .delete<void>(`${BASE}/announcements/${id}/reactions`)
      .then((r: AxiosResponse<void>) => r.data),
};
