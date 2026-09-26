-- V8: Community Announcements & Noticeboard Schema

CREATE TABLE announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hostel_id UUID REFERENCES hostels(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_announcements_author ON announcements(author_id);
CREATE INDEX idx_announcements_hostel ON announcements(hostel_id);
CREATE INDEX idx_announcements_category ON announcements(category);
CREATE INDEX idx_announcements_pinned ON announcements(pinned);
CREATE INDEX idx_announcements_created_at ON announcements(created_at);

-- Comments / Reactions on announcements
CREATE TABLE announcement_reactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    announcement_id UUID NOT NULL REFERENCES announcements(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reaction VARCHAR(20) NOT NULL DEFAULT 'LIKE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (announcement_id, user_id)
);

CREATE INDEX idx_reactions_announcement ON announcement_reactions(announcement_id);
