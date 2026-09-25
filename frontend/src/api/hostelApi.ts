import { axiosClient } from './axiosClient';
import {
  CampusDto,
  HostelDto,
  BlockDto,
  FloorDto,
  RoomDto,
  RoomAllocationDto,
  OccupancyStatsDto,
  CreateCampusRequest,
  CreateHostelRequest,
  CreateRoomRequest,
  AllocateBedRequest,
  TransferBedRequest,
  VacateBedRequest,
} from '../types/hostel';

export const hostelApi = {
  // Campuses
  getCampuses: async (): Promise<CampusDto[]> => {
    const res = await axiosClient.get<CampusDto[]>('/hostels/campuses');
    return res.data;
  },
  createCampus: async (data: CreateCampusRequest): Promise<CampusDto> => {
    const res = await axiosClient.post<CampusDto>('/hostels/campuses', data);
    return res.data;
  },

  // Hostels
  getHostels: async (): Promise<HostelDto[]> => {
    const res = await axiosClient.get<HostelDto[]>('/hostels');
    return res.data;
  },
  createHostel: async (data: CreateHostelRequest): Promise<HostelDto> => {
    const res = await axiosClient.post<HostelDto>('/hostels', data);
    return res.data;
  },

  // Blocks & Floors
  getBlocks: async (hostelId: string): Promise<BlockDto[]> => {
    const res = await axiosClient.get<BlockDto[]>(`/hostels/${hostelId}/blocks`);
    return res.data;
  },
  getFloors: async (blockId: string): Promise<FloorDto[]> => {
    const res = await axiosClient.get<FloorDto[]>(`/hostels/blocks/${blockId}/floors`);
    return res.data;
  },

  // Rooms
  getRoomsByFloor: async (floorId: string): Promise<RoomDto[]> => {
    const res = await axiosClient.get<RoomDto[]>(`/hostels/floors/${floorId}/rooms`);
    return res.data;
  },
  getAllRooms: async (): Promise<RoomDto[]> => {
    const res = await axiosClient.get<RoomDto[]>('/hostels/rooms');
    return res.data;
  },
  createRoom: async (data: CreateRoomRequest): Promise<RoomDto> => {
    const res = await axiosClient.post<RoomDto>('/hostels/rooms', data);
    return res.data;
  },

  // Allocations
  getAllocations: async (): Promise<RoomAllocationDto[]> => {
    const res = await axiosClient.get<RoomAllocationDto[]>('/allocations');
    return res.data;
  },
  allocateBed: async (data: AllocateBedRequest): Promise<RoomAllocationDto> => {
    const res = await axiosClient.post<RoomAllocationDto>('/allocations', data);
    return res.data;
  },
  transferBed: async (data: TransferBedRequest): Promise<RoomAllocationDto> => {
    const res = await axiosClient.post<RoomAllocationDto>('/allocations/transfer', data);
    return res.data;
  },
  vacateBed: async (data: VacateBedRequest): Promise<RoomAllocationDto> => {
    const res = await axiosClient.post<RoomAllocationDto>('/allocations/vacate', data);
    return res.data;
  },
  getStats: async (): Promise<OccupancyStatsDto> => {
    const res = await axiosClient.get<OccupancyStatsDto>('/allocations/stats');
    return res.data;
  },
};
