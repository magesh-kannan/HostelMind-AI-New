# HostelMind-AI Design System & Visual Guidelines

## 1. Visual Identity & Brand Philosophy
HostelMind-AI is an institutional operations platform that balances **academic authority** with **modern AI innovation**. It strictly avoids generic, plain material templates or copied SaaS card layouts.

### Primary Design Pillars
- **Institutional Elegance:** Deep Indigo, Emerald Accent, Slate Grays, and Dark Sapphire Mode.
- **Glassmorphism & Depth:** Soft multi-layer shadows, subtle border highlights, and backdrop blurs.
- **Micro-Animations:** Fluid transition timing (`cubic-bezier(0.4, 0, 0.2, 1)`), hover elevation, smooth page transitions.
- **Typography Pairing:**
  - **Display / Headings:** *Plus Jakarta Sans* / *Outfit* (Geometric, professional, modern)
  - **Body / Interface:** *Inter* (Crisp legibility for tables, forms, data grids)

---

## 2. Color Palette & Tokens

### Light Theme
- **Primary (Institutional Slate):** `#1E293B` (Slate 800)
- **Primary Accent (Royal Indigo):** `#4F46E5` (Indigo 600)
- **Secondary Accent (Emerald Mint):** `#10B981` (Emerald 500)
- **Background Main:** `#F8FAFC` (Slate 50)
- **Surface Elevation 1 (Card):** `#FFFFFF`
- **Surface Elevation 2 (Elevated):** `#F1F5F9` (Slate 100)
- **Border Default:** `#E2E8F0` (Slate 200)
- **Text Primary:** `#0F172A` (Slate 900)
- **Text Secondary:** `#475569` (Slate 600)

### Dark Theme (Sapphire Dark)
- **Primary Accent (Indigo Bright):** `#6366F1` (Indigo 500)
- **Secondary Accent (Emerald Bright):** `#34D399` (Emerald 400)
- **Background Main:** `#0F172A` (Slate 900)
- **Surface Elevation 1 (Card):** `#1E293B` (Slate 800)
- **Surface Elevation 2 (Elevated):** `#334155` (Slate 700)
- **Border Default:** `#334155` (Slate 700)
- **Text Primary:** `#F8FAFC` (Slate 50)
- **Text Secondary:** `#94A3B8` (Slate 400)

---

## 3. UI Component Standards
- **Buttons:** Subtle gradients on primary buttons, crisp 8px border-radius, active scale feedback.
- **Cards:** Custom border outline with hover border-color shift to Indigo/Emerald. No raw browser outlines.
- **Status Pills:** Semantic badge components with soft background tint + solid text (e.g., Active = Soft Emerald + Emerald text).
- **ID Cards:**
  - **Student ID:** Institutional Navy header with Holographic Foil effect border, QR code bottom right.
  - **Staff ID:** Deep Maroon/Crimson header with Gold metallic accent trim, QR code bottom right.
