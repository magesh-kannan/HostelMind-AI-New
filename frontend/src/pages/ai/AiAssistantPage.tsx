import React, { useState } from 'react';
import {
  Box, Typography, Grid, Card, Button, TextField,
  Chip, Avatar, Paper, IconButton, CircularProgress,
  Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, Dialog, DialogTitle, DialogContent, DialogActions,
  FormControl, InputLabel, Select, MenuItem, LinearProgress, Divider,
} from '@mui/material';
import {
  SmartToy as RobotIcon,
  Send as SendIcon,
  AutoAwesome as SparklesIcon,
  MenuBook as BookIcon,
  History as LogIcon,
  Add as AddIcon,
  Delete as DeleteIcon,
  Lightbulb as TipIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { aiApi } from '../../api/aiApi';
import type {
  AskAiResponse,
  ClassifyComplaintAiResponse,
  DocumentCategory,
  KnowledgeDocumentDto,
  AiAgentLogDto,
} from '../../types/ai';

const CATEGORIES: DocumentCategory[] = ['RULES', 'FAQ', 'COMPLAINT_GUIDE', 'MESS_MENU', 'EMERGENCY'];

const QUICK_PROMPTS = [
  'What are the hostel curfew hours and entry rules?',
  'How do I report a plumbing or water leak issue?',
  'What is the procedure for requesting a room change?',
  'What are the mess dining hours on weekends?',
];

export const AiAssistantPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState(0);

  // ─── RAG Assistant State ───
  const [chatQuery, setChatQuery] = useState('');
  const [chatHistory, setChatHistory] = useState<
    Array<{ sender: 'user' | 'ai'; text: string; citations?: AskAiResponse['citedDocuments']; confidence?: number }>
  >([
    {
      sender: 'ai',
      text: 'Hello! I am HostelMind AI Assistant. How can I help you today with hostel rules, mess schedules, or complaint guidance?',
    },
  ]);

  const askMutation = useMutation({
    mutationFn: (q: string) => aiApi.askAssistant({ query: q }),
    onSuccess: (data) => {
      setChatHistory((prev) => [
        ...prev,
        {
          sender: 'ai',
          text: data.answer,
          citations: data.citedDocuments,
          confidence: data.confidenceScore,
        },
      ]);
      queryClient.invalidateQueries({ queryKey: ['ai-logs'] });
    },
  });

  const handleSendChat = (q?: string) => {
    const text = q || chatQuery;
    if (!text.trim()) return;

    setChatHistory((prev) => [...prev, { sender: 'user', text }]);
    if (!q) setChatQuery('');
    askMutation.mutate(text);
  };

  // ─── Complaint Classifier State ───
  const [clsTitle, setClsTitle] = useState('');
  const [clsDesc, setClsDesc] = useState('');
  const [clsResult, setClsResult] = useState<ClassifyComplaintAiResponse | null>(null);

  const classifyMutation = useMutation({
    mutationFn: () => aiApi.classifyComplaint({ title: clsTitle, description: clsDesc }),
    onSuccess: (res) => {
      setClsResult(res);
      queryClient.invalidateQueries({ queryKey: ['ai-logs'] });
    },
  });

  // ─── Knowledge Documents State ───
  const [docFilterCategory, setDocFilterCategory] = useState<DocumentCategory | 'ALL'>('ALL');
  const [addDocOpen, setAddDocOpen] = useState(false);
  const [newDocTitle, setNewDocTitle] = useState('');
  const [newDocCategory, setNewDocCategory] = useState<DocumentCategory>('RULES');
  const [newDocContent, setNewDocContent] = useState('');

  const { data: knowledgeDocs } = useQuery({
    queryKey: ['knowledge-docs', docFilterCategory],
    queryFn: () =>
      aiApi.getKnowledgeDocuments(docFilterCategory === 'ALL' ? undefined : docFilterCategory),
  });

  const createDocMutation = useMutation({
    mutationFn: () =>
      aiApi.createKnowledgeDocument({
        title: newDocTitle,
        category: newDocCategory,
        content: newDocContent,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['knowledge-docs'] });
      setAddDocOpen(false);
      setNewDocTitle('');
      setNewDocContent('');
    },
  });

  const deleteDocMutation = useMutation({
    mutationFn: aiApi.deleteKnowledgeDocument,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['knowledge-docs'] }),
  });

  // ─── Logs State ───
  const { data: aiLogs } = useQuery({
    queryKey: ['ai-logs'],
    queryFn: aiApi.getAgentLogs,
    enabled: activeTab === 2,
  });

  return (
    <Box>
      {/* ── Page Title ── */}
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
        <Box display="flex" alignItems="center" gap={1.5}>
          <Avatar sx={{ bgcolor: 'primary.main', width: 44, height: 44 }}>
            <SparklesIcon />
          </Avatar>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
              HostelMind AI Hub
            </Typography>
            <Typography variant="body2" color="text.secondary">
              RAG Smart Assistant, Complaint Auto-Classifier & Knowledge Engine
            </Typography>
          </Box>
        </Box>
      </Box>

      {/* ── Navigation Tabs ── */}
      <Paper sx={{ mb: 3, borderRadius: 3 }}>
        <Tabs value={activeTab} onChange={(_, v) => setActiveTab(v)} variant="fullWidth">
          <Tab icon={<RobotIcon />} label="RAG Assistant" iconPosition="start" />
          <Tab icon={<SparklesIcon />} label="Smart Classifier" iconPosition="start" />
          <Tab icon={<BookIcon />} label="Knowledge Base" iconPosition="start" />
          <Tab icon={<LogIcon />} label="Provenance Logs" iconPosition="start" />
        </Tabs>
      </Paper>

      {/* ── Tab 0: RAG Assistant ── */}
      {activeTab === 0 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Card sx={{ height: 580, display: 'flex', flexDirection: 'column', borderRadius: 3 }}>
              {/* Chat Feed */}
              <Box flex={1} p={3} sx={{ overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 2 }}>
                {chatHistory.map((msg, idx) => (
                  <Box
                    key={idx}
                    display="flex"
                    justifyContent={msg.sender === 'user' ? 'flex-end' : 'flex-start'}
                  >
                    <Box
                      sx={{
                        maxWidth: '80%',
                        p: 2,
                        borderRadius: 3,
                        bgcolor: msg.sender === 'user' ? 'primary.main' : 'action.selected',
                        color: msg.sender === 'user' ? '#fff' : 'text.primary',
                      }}
                    >
                      <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                        {msg.text}
                      </Typography>

                      {msg.citations && msg.citations.length > 0 && (
                        <Box mt={1.5} pt={1} borderTop="1px dashed rgba(0,0,0,0.12)">
                          <Typography variant="caption" fontWeight={700} color="primary" display="block">
                            Source Citations:
                          </Typography>
                          {msg.citations.map((c) => (
                            <Chip
                              key={c.id}
                              label={c.title}
                              size="small"
                              variant="outlined"
                              sx={{ mr: 0.5, mt: 0.5, fontSize: '0.65rem' }}
                            />
                          ))}
                        </Box>
                      )}
                    </Box>
                  </Box>
                ))}
                {askMutation.isPending && (
                  <Box display="flex" alignItems="center" gap={1} color="text.secondary">
                    <CircularProgress size={16} />
                    <Typography variant="caption">AI is thinking...</Typography>
                  </Box>
                )}
              </Box>

              {/* Chat Input */}
              <Box p={2} borderTop="1px solid" borderColor="divider" display="flex" gap={1}>
                <TextField
                  placeholder="Ask any question about hostel rules, mess schedule, policies..."
                  fullWidth
                  size="small"
                  value={chatQuery}
                  onChange={(e) => setChatQuery(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSendChat()}
                />
                <Button
                  variant="contained"
                  onClick={() => handleSendChat()}
                  disabled={!chatQuery.trim() || askMutation.isPending}
                >
                  <SendIcon />
                </Button>
              </Box>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card sx={{ p: 2.5, borderRadius: 3, height: 580 }}>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <TipIcon color="primary" />
                <Typography variant="subtitle1" fontWeight={700}>
                  Suggested Prompts
                </Typography>
              </Box>
              <Typography variant="caption" color="text.secondary" display="block" mb={2}>
                Click any template to query the institutional RAG vector engine:
              </Typography>
              <Box display="flex" flexDirection="column" gap={1.5}>
                {QUICK_PROMPTS.map((prompt, idx) => (
                  <Paper
                    key={idx}
                    variant="outlined"
                    sx={{
                      p: 1.5,
                      borderRadius: 2,
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      '&:hover': { bgcolor: 'primary.50', borderColor: 'primary.main' },
                    }}
                    onClick={() => handleSendChat(prompt)}
                  >
                    <Typography variant="body2" fontWeight={500}>
                      {prompt}
                    </Typography>
                  </Paper>
                ))}
              </Box>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* ── Tab 1: Smart Complaint Classifier ── */}
      {activeTab === 1 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 3 }}>
              <Typography variant="h6" fontWeight={700} mb={1}>
                AI Complaint Analyzer
              </Typography>

              <Box display="flex" flexDirection="column" gap={2.5} mt={2}>
                <TextField
                  label="Complaint Title"
                  fullWidth
                  value={clsTitle}
                  onChange={(e) => setClsTitle(e.target.value)}
                  placeholder="e.g. Water leaking in washroom tap"
                />
                <TextField
                  label="Detailed Description"
                  fullWidth
                  multiline
                  rows={4}
                  value={clsDesc}
                  onChange={(e) => setClsDesc(e.target.value)}
                  placeholder="e.g. Tap is continuously dripping water and socket nearby has sparks..."
                />
                <Button
                  variant="contained"
                  size="large"
                  disabled={!clsTitle || !clsDesc || classifyMutation.isPending}
                  onClick={() => classifyMutation.mutate()}
                  startIcon={classifyMutation.isPending ? <CircularProgress size={18} /> : <SparklesIcon />}
                >
                  Analyze & Recommend Category
                </Button>
              </Box>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 3, minHeight: 340 }}>
              <Typography variant="h6" fontWeight={700} mb={2}>
                AI Classification Prediction
              </Typography>

              {!clsResult && !classifyMutation.isPending && (
                <Box textAlign="center" py={6} color="text.secondary">
                  <RobotIcon sx={{ fontSize: 48, mb: 1, opacity: 0.5 }} />
                  <Typography variant="body2">
                    Enter a complaint title and description to see instant AI classification recommendations.
                  </Typography>
                </Box>
              )}

              {classifyMutation.isPending && (
                <Box py={6} textAlign="center">
                  <CircularProgress size={32} />
                  <Typography variant="caption" display="block" mt={1}>
                    Running model inference...
                  </Typography>
                </Box>
              )}

              {clsResult && (
                <Box display="flex" flexDirection="column" gap={2}>
                  <Box display="flex" gap={2} alignItems="center">
                    <Typography variant="subtitle2">Recommended Category:</Typography>
                    <Chip label={clsResult.recommendedCategory} color="primary" sx={{ fontWeight: 700 }} />
                  </Box>

                  <Box display="flex" gap={2} alignItems="center">
                    <Typography variant="subtitle2">Recommended Priority:</Typography>
                    <Chip label={clsResult.recommendedPriority} color="warning" sx={{ fontWeight: 700 }} />
                  </Box>

                  <Box display="flex" gap={2} alignItems="center">
                    <Typography variant="subtitle2">Suggested SLA Timeout:</Typography>
                    <Typography variant="body2" fontWeight={700}>
                      {clsResult.suggestedSlaHours} Hours
                    </Typography>
                  </Box>

                  <Box>
                    <Typography variant="subtitle2" mb={0.5}>
                      Model Confidence: {(Number(clsResult.confidenceScore) * 100).toFixed(1)}%
                    </Typography>
                    <LinearProgress
                      variant="determinate"
                      value={Number(clsResult.confidenceScore) * 100}
                      sx={{ height: 8, borderRadius: 4 }}
                    />
                  </Box>

                  <Divider sx={{ my: 1 }} />

                  <Box>
                    <Typography variant="caption" fontWeight={700} color="text.secondary">
                      Reasoning Provenance:
                    </Typography>
                    <Typography variant="body2" sx={{ fontStyle: 'italic', mt: 0.5 }}>
                      "{clsResult.reasoning}"
                    </Typography>
                  </Box>
                </Box>
              )}
            </Card>
          </Grid>
        </Grid>
      )}

      {/* ── Tab 2: Knowledge Base Manager ── */}
      {activeTab === 2 && (
        <Box display="flex" flexDirection="column" gap={2}>
          <Box display="flex" alignItems="center" justifyContent="space-between">
            <Box display="flex" gap={1}>
              {(['ALL', 'RULES', 'FAQ', 'COMPLAINT_GUIDE', 'MESS_MENU', 'EMERGENCY'] as const).map((c) => (
                <Chip
                  key={c}
                  label={c}
                  size="small"
                  onClick={() => setDocFilterCategory(c)}
                  color={docFilterCategory === c ? 'primary' : 'default'}
                />
              ))}
            </Box>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setAddDocOpen(true)}
            >
              Ingest Document
            </Button>
          </Box>

          <Grid container spacing={2}>
            {knowledgeDocs?.map((doc: KnowledgeDocumentDto) => (
              <Grid item xs={12} md={6} key={doc.id}>
                <Card sx={{ p: 2.5, borderRadius: 3 }}>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                    <Typography variant="h6" fontSize="1rem" fontWeight={700}>
                      {doc.title}
                    </Typography>
                    <IconButton size="small" color="error" onClick={() => deleteDocMutation.mutate(doc.id)}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>
                  <Chip label={doc.category} size="small" sx={{ mb: 1.5, fontSize: '0.65rem' }} />
                  <Typography variant="body2" color="text.secondary" sx={{ whiteSpace: 'pre-wrap' }}>
                    {doc.content}
                  </Typography>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}

      {/* ── Tab 3: AI Provenance Logs ── */}
      {activeTab === 3 && (
        <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell sx={{ fontWeight: 700 }}>Agent Type</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Prompt</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Response</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Model</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Tokens</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Confidence</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {aiLogs?.map((log: AiAgentLogDto) => (
                <TableRow key={log.id}>
                  <TableCell>
                    <Chip label={log.agentType} size="small" />
                  </TableCell>
                  <TableCell sx={{ maxWidth: 200, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {log.prompt}
                  </TableCell>
                  <TableCell sx={{ maxWidth: 250, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {log.response}
                  </TableCell>
                  <TableCell>{log.modelName}</TableCell>
                  <TableCell>{log.tokensUsed}</TableCell>
                  <TableCell>{(Number(log.confidenceScore) * 100).toFixed(0)}%</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* ── Add Document Modal ── */}
      <Dialog open={addDocOpen} onClose={() => setAddDocOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Ingest Knowledge Document</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <TextField
            label="Document Title"
            fullWidth
            value={newDocTitle}
            onChange={(e) => setNewDocTitle(e.target.value)}
          />
          <FormControl fullWidth>
            <InputLabel>Category</InputLabel>
            <Select
              label="Category"
              value={newDocCategory}
              onChange={(e) => setNewDocCategory(e.target.value as DocumentCategory)}
            >
              {CATEGORIES.map((c) => (
                <MenuItem key={c} value={c}>{c}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Document Content"
            fullWidth
            multiline
            rows={5}
            value={newDocContent}
            onChange={(e) => setNewDocContent(e.target.value)}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setAddDocOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            disabled={!newDocTitle || !newDocContent || createDocMutation.isPending}
            onClick={() => createDocMutation.mutate()}
          >
            Ingest Document
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
