---
name: Intellect
colors:
  surface: '#f8f9fb'
  surface-dim: '#d9dadc'
  surface-bright: '#f8f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f6'
  surface-container: '#edeef0'
  surface-container-high: '#e7e8ea'
  surface-container-highest: '#e1e2e4'
  on-surface: '#191c1e'
  on-surface-variant: '#464554'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f3'
  outline: '#767586'
  outline-variant: '#c7c4d7'
  surface-tint: '#494bd6'
  primary: '#4648d4'
  on-primary: '#ffffff'
  primary-container: '#6063ee'
  on-primary-container: '#fffbff'
  inverse-primary: '#c0c1ff'
  secondary: '#5c5f60'
  on-secondary: '#ffffff'
  secondary-container: '#e1e3e4'
  on-secondary-container: '#626566'
  tertiary: '#4b41e1'
  on-tertiary: '#ffffff'
  tertiary-container: '#645efb'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#07006c'
  on-primary-fixed-variant: '#2f2ebe'
  secondary-fixed: '#e1e3e4'
  secondary-fixed-dim: '#c5c7c8'
  on-secondary-fixed: '#191c1d'
  on-secondary-fixed-variant: '#454748'
  tertiary-fixed: '#e2dfff'
  tertiary-fixed-dim: '#c3c0ff'
  on-tertiary-fixed: '#0f0069'
  on-tertiary-fixed-variant: '#3323cc'
  background: '#f8f9fb'
  on-background: '#191c1e'
  surface-variant: '#e1e2e4'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1280px
  gutter: 24px
  margin-desktop: 40px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

The design system focuses on cognitive clarity and academic confidence. Designed for a high-stakes yet supportive educational environment, the aesthetic is **Corporate / Modern** with a lean towards precision and focus. 

The brand personality is authoritative yet encouraging. It avoids the "gamified" clutter of children's apps in favor of a desktop productivity aesthetic that treats the user as a serious learner. The interface utilizes generous whitespace, crisp structural alignment, and a sophisticated color logic to reduce cognitive load during assessment.

The visual signature is defined by:
- **Clarity over Decoration:** Every element serves a functional purpose in the learning flow.
- **Academic Professionalism:** A balanced mix of soft geometry and rigorous grid structures.
- **High Information Density:** Optimized for desktop layouts where complex question types and data visualizations coexist.

## Colors

The palette is rooted in Indigo and Purple to evoke intelligence and stability. 

- **Primary & Action:** `#6366F1` is used for primary actions and interactive states. The deeper `#4F46E5` is reserved for hover states and high-contrast text links.
- **Surfaces:** A tiered gray system (`#FFFFFF`, `#F9FAFB`, `#F3F4F6`) creates logical grouping without the need for heavy borders.
- **Feedback Logic:** Semantic colors are used strictly for status. **Success Green** indicates correct answers and completions; **Error Red** highlights incorrect selections and critical failures; **Warning Amber** is dedicated to time-sensitive elements like countdowns or system alerts.

## Typography

This design system utilizes **Inter** exclusively to ensure maximum legibility across variable screen resolutions. The typeface's tall x-height makes it ideal for long-form question text and technical explanations.

- **Scale:** A strict typographic hierarchy is used to differentiate between the "Question" (Display/Headline) and the "Options" (Body).
- **Contrast:** Use `fontWeight: 600` for interactive labels and `fontWeight: 400` for instructional text to help users scan the interface quickly.
- **Readability:** Line heights for body text are set to 1.5x - 1.6x the font size to prevent eye fatigue during long study sessions.

## Layout & Spacing

The system follows a **Fixed Grid** philosophy for the main content area to maintain a readable line length for educational content, centered on the screen.

- **Grid:** A 12-column grid with 24px gutters. Content is typically housed in an 8-column central container (approx. 800px) for maximum focus during quizzes.
- **Rhythm:** An 8px linear scale governs all padding and margins. 
- **Adaptation:** On tablet, the margins reduce to 24px. On desktop, sidebars (for navigation or progress tracking) are fixed at 280px, with the remaining space dedicated to the quiz stage.

## Elevation & Depth

Visual hierarchy is achieved through a combination of **Tonal Layers** and **Ambient Shadows**.

- **Level 0 (Background):** `#F9FAFB` – The base canvas.
- **Level 1 (Cards/Content):** `#FFFFFF` – White surfaces with a 1px border (`#E5E7EB`) and a very soft, diffused shadow (0px 1px 3px rgba(0,0,0,0.1)).
- **Level 2 (Interactive/Floating):** Used for active state cards or dropdowns. Increased shadow (0px 10px 15px -3px rgba(0,0,0,0.1)) to suggest lift.

Avoid heavy blacks; use Indigo-tinted grays for shadows to keep the UI feeling "airy" and modern.

## Shapes

The shape language is consistently **Rounded**. 

- **Components:** Standard buttons, input fields, and selection chips use a `0.5rem` (8px) radius. 
- **Containers:** Large content cards and modals use `1rem` (16px) to soften the large surface areas.
- **Selection Indicators:** Radio buttons and checkboxes maintain a soft square or circular profile, but the "active" highlight should always follow the 8px corner rule.

## Components

### Buttons
- **Primary:** Solid Indigo (`#6366F1`) with white text. 8px border radius.
- **Secondary:** White background with 1px gray border. Transitions to a light indigo tint on hover.
- **Tertiary:** Ghost style, text-only with a subtle background highlight on hover.

### Quiz Cards (Selection)
The core component of the platform.
- **Default:** White background, 1px border (`#E5E7EB`).
- **Hover:** Border color changes to `#6366F1`.
- **Selected:** Border color `#6366F1` with a 2px width and a very subtle light indigo fill (`#EEF2FF`).
- **Correct/Incorrect:** Transitions the entire card border to Success Green or Error Red respectively, with matching soft-tint backgrounds.

### Progress Indicators
- **Steppers:** Small 8px circles. Completed = Indigo; Active = Indigo Ring; Upcoming = Light Gray.
- **Bar:** A 4px tall track (`#F3F4F6`) with a primary indigo fill transitioning smoothly as questions are answered.

### Input Fields
- Understated styling. 1px border, 8px radius. Use the "Label-md" typography for floating labels or top-aligned titles. Focus state uses a 2px Indigo ring with 0.2 opacity.