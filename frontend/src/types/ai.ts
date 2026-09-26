import { ComplaintCategory, ComplaintPriority } from './complaint';

export type DocumentCategory = 'RULES' | 'FAQ' | 'COMPLAINT_GUIDE' | 'MESS_MENU' | 'EMERGENCY';

export type AgentType = 'COMPLAINT_CLASSIFIER' | 'RAG_ASSISTANT' | 'ROOM_ALLOCATOR_AI';

export interface ClassifyComplaintAiRequest {
  title: string;
  description: string;
}

export interface ClassifyComplaintAiResponse {
  recommendedCategory: ComplaintCategory;
  recommendedPriority: ComplaintPriority;
  confidenceScore: number;
  reasoning: String;
  suggestedSlaHours: number;
}

export interface CitedDocumentDto {
  id: string;
  title: string;
  category: string;
  snippet: string;
}

export interface AskAiRequest {
  query: string;
  category?: DocumentCategory;
}

export interface AskAiResponse {
  answer: string;
  citedDocuments: CitedDocumentDto[];
  confidenceScore: number;
  tokensUsed: number;
  modelName: string;
}

export interface KnowledgeDocumentDto {
  id: string;
  title: string;
  category: DocumentCategory;
  content: string;
  metadataJson?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateKnowledgeDocumentRequest {
  title: string;
  category: DocumentCategory;
  content: string;
  metadataJson?: string;
}

export interface AiAgentLogDto {
  id: string;
  agentType: AgentType;
  prompt: string;
  response: string;
  modelName: string;
  tokensUsed: number;
  confidenceScore: number;
  performedBy?: string;
  createdAt: string;
}
