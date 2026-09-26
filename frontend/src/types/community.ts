export type AnnouncementCategory =
  | 'GENERAL'
  | 'MAINTENANCE'
  | 'EMERGENCY'
  | 'ACADEMIC'
  | 'EVENT'
  | 'MESS'
  | 'SPORTS'
  | 'HEALTH';

export type AnnouncementPriority = 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';

export interface Announcement {
  id: string;
  authorId: string;
  authorName: string;
  hostelId?: string;
  title: string;
  body: string;
  category: AnnouncementCategory;
  priority: AnnouncementPriority;
  pinned: boolean;
  expiresAt?: string;
  createdAt: string;
  updatedAt: string;
  reactionCount: number;
}

export interface CreateAnnouncementRequest {
  title: string;
  body: string;
  category: AnnouncementCategory;
  priority?: AnnouncementPriority;
  pinned?: boolean;
  hostelId?: string;
  expiresAt?: string;
}
