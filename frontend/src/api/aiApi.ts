import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  ClassifyComplaintAiRequest,
  ClassifyComplaintAiResponse,
  AskAiRequest,
  AskAiResponse,
  KnowledgeDocumentDto,
  CreateKnowledgeDocumentRequest,
  AiAgentLogDto,
  DocumentCategory,
} from '../types/ai';

const BASE = '/ai';

export const aiApi = {
  // Classify complaint
  classifyComplaint: (data: ClassifyComplaintAiRequest): Promise<ClassifyComplaintAiResponse> =>
    axiosClient.post<ClassifyComplaintAiResponse>(`${BASE}/classify`, data).then((r: AxiosResponse<ClassifyComplaintAiResponse>) => r.data),

  // Ask RAG assistant
  askAssistant: (data: AskAiRequest): Promise<AskAiResponse> =>
    axiosClient.post<AskAiResponse>(`${BASE}/ask`, data).then((r: AxiosResponse<AskAiResponse>) => r.data),

  // Get knowledge docs
  getKnowledgeDocuments: (category?: DocumentCategory): Promise<KnowledgeDocumentDto[]> =>
    axiosClient.get<KnowledgeDocumentDto[]>(`${BASE}/knowledge`, { params: { category } }).then((r: AxiosResponse<KnowledgeDocumentDto[]>) => r.data),

  // Create knowledge doc
  createKnowledgeDocument: (data: CreateKnowledgeDocumentRequest): Promise<KnowledgeDocumentDto> =>
    axiosClient.post<KnowledgeDocumentDto>(`${BASE}/knowledge`, data).then((r: AxiosResponse<KnowledgeDocumentDto>) => r.data),

  // Delete knowledge doc
  deleteKnowledgeDocument: (id: string): Promise<void> =>
    axiosClient.delete(`${BASE}/knowledge/${id}`).then(() => undefined),

  // Get AI agent logs
  getAgentLogs: (): Promise<AiAgentLogDto[]> =>
    axiosClient.get<AiAgentLogDto[]>(`${BASE}/logs`).then((r: AxiosResponse<AiAgentLogDto[]>) => r.data),
};
