-- V4: Vector & AI Agent Schema

-- Enable pgvector extension if available (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS vector;

-- Knowledge Base Documents table for RAG
CREATE TABLE knowledge_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'RULES',
    content TEXT NOT NULL,
    metadata_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_knowledge_docs_category ON knowledge_documents(category);

-- AI Agent Provenance & Audit Logs table
CREATE TABLE ai_agent_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_type VARCHAR(50) NOT NULL,
    prompt TEXT NOT NULL,
    response TEXT NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    tokens_used INT NOT NULL DEFAULT 0,
    confidence_score NUMERIC(5, 4) DEFAULT 0.0000,
    performed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_ai_agent_logs_agent_type ON ai_agent_logs(agent_type);
CREATE INDEX idx_ai_agent_logs_performed_by ON ai_agent_logs(performed_by);
