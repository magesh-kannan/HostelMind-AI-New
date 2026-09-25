import React from 'react';
import { Chip } from '@mui/material';

interface StatusBadgeProps {
  label: string;
  status?: 'success' | 'warning' | 'error' | 'info' | 'default';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ label, status = 'info' }) => {
  const getColors = () => {
    switch (status) {
      case 'success':
        return { bg: 'rgba(16, 185, 129, 0.15)', color: '#34D399', border: 'rgba(16, 185, 129, 0.3)' };
      case 'warning':
        return { bg: 'rgba(245, 158, 11, 0.15)', color: '#FBBF24', border: 'rgba(245, 158, 11, 0.3)' };
      case 'error':
        return { bg: 'rgba(239, 68, 68, 0.15)', color: '#F87171', border: 'rgba(239, 68, 68, 0.3)' };
      default:
        return { bg: 'rgba(99, 102, 241, 0.15)', color: '#818CF8', border: 'rgba(99, 102, 241, 0.3)' };
    }
  };

  const { bg, color, border } = getColors();

  return (
    <Chip
      label={label}
      size="small"
      style={{
        backgroundColor: bg,
        color: color,
        border: `1px solid ${border}`,
        fontWeight: 600,
        fontSize: '0.75rem',
      }}
    />
  );
};
