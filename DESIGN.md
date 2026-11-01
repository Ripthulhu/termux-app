---
name: Termux for Android and Codex
description: A polished native Android utility with a readable terminal and Material 3 controls.
colors:
  primary: "#006D43"
  on-primary: "#FFFFFF"
  primary-container: "#99F7BA"
  on-primary-container: "#00210F"
  secondary: "#4D6355"
  on-secondary: "#FFFFFF"
  secondary-container: "#D0E8D6"
  on-secondary-container: "#0A1F13"
  tertiary: "#3C6472"
  on-tertiary: "#FFFFFF"
  tertiary-container: "#C0E9F8"
  on-tertiary-container: "#001F28"
  surface: "#F6FBF4"
  on-surface: "#181D18"
  surface-variant: "#DCE5DA"
  on-surface-variant: "#414941"
  surface-container-low: "#F0F5EE"
  surface-container: "#EAF0E8"
  surface-container-high: "#E4EAE2"
  surface-container-highest: "#DEE4DC"
  outline: "#717970"
  outline-variant: "#C0C9BE"
  error: "#BA1A1A"
  on-error: "#FFFFFF"
  error-container: "#FFDAD6"
  on-error-container: "#410002"
  dark-primary: "#7DDAA0"
  dark-on-primary: "#00391E"
  dark-primary-container: "#00522F"
  dark-on-primary-container: "#99F7BA"
  dark-secondary: "#B5CCBA"
  dark-on-secondary: "#203527"
  dark-secondary-container: "#364B3D"
  dark-on-secondary-container: "#D0E8D6"
  dark-tertiary: "#A5CDDC"
  dark-on-tertiary: "#073543"
  dark-tertiary-container: "#234C5A"
  dark-on-tertiary-container: "#C0E9F8"
  dark-surface: "#101510"
  dark-on-surface: "#E0E4DC"
  dark-surface-variant: "#414941"
  dark-on-surface-variant: "#C0C9BE"
  dark-surface-container-low: "#181D18"
  dark-surface-container: "#1C211C"
  dark-surface-container-high: "#262B26"
  dark-surface-container-highest: "#313630"
  dark-outline: "#8A9389"
  dark-outline-variant: "#414941"
  dark-error: "#FFB4AB"
  dark-on-error: "#690005"
  dark-error-container: "#93000A"
  dark-on-error-container: "#FFDAD6"
typography:
  title-large:
    fontFamily: "sans-serif"
    fontSize: "22sp"
    lineHeight: "28sp"
    letterSpacing: "0em"
  title-medium:
    fontFamily: "sans-serif-medium"
    fontSize: "16sp"
    lineHeight: "24sp"
    letterSpacing: "0.009375em"
  title-small:
    fontFamily: "sans-serif-medium"
    fontSize: "14sp"
    lineHeight: "20sp"
    letterSpacing: "0.00714286em"
  body-large:
    fontFamily: "sans-serif"
    fontSize: "16sp"
    lineHeight: "24sp"
    letterSpacing: "0.03125em"
  body-small:
    fontFamily: "sans-serif"
    fontSize: "12sp"
    lineHeight: "16sp"
    letterSpacing: "0.03333333em"
  label-large:
    fontFamily: "sans-serif-medium"
    fontSize: "14sp"
    lineHeight: "20sp"
    letterSpacing: "0.00714286em"
  extra-key:
    fontFamily: "sans-serif-medium"
    fontSize: "12sp"
    lineHeight: "16sp"
    letterSpacing: "0em"
  terminal:
    fontFamily: "monospace"
rounded:
  extra-key: "6dp"
  panel: "16dp"
  session-badge: "16dp"
  icon-toggle: "24dp"
  session-row: "28dp"
  action: "50%"
spacing:
  key-inset: "3dp"
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  panel-title-start: "20dp"
  action-horizontal: "16dp"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.label-large}"
    rounded: "{rounded.action}"
    padding: "6dp 16dp"
    height: "56dp"
  button-tonal:
    backgroundColor: "{colors.secondary-container}"
    textColor: "{colors.on-secondary-container}"
    typography: "{typography.label-large}"
    rounded: "{rounded.action}"
    padding: "6dp 16dp"
    height: "56dp"
  session-row:
    backgroundColor: "transparent"
    textColor: "{colors.on-surface}"
    typography: "{typography.title-small}"
    rounded: "{rounded.session-row}"
    padding: "{spacing.md}"
    height: "68dp"
  session-row-selected:
    backgroundColor: "{colors.secondary-container}"
    textColor: "{colors.on-secondary-container}"
    typography: "{typography.title-small}"
    rounded: "{rounded.session-row}"
    padding: "{spacing.md}"
    height: "68dp"
  extra-key:
    backgroundColor: "{colors.surface-container-high}"
    textColor: "{colors.on-surface}"
    typography: "{typography.extra-key}"
    rounded: "{rounded.extra-key}"
    padding: "0dp 4dp"
    width: "48dp"
    height: "48dp"
  extra-key-active:
    backgroundColor: "{colors.secondary-container}"
    textColor: "{colors.on-secondary-container}"
    typography: "{typography.extra-key}"
    rounded: "{rounded.extra-key}"
    padding: "0dp 4dp"
    width: "48dp"
    height: "48dp"
  visibility-toggle:
    backgroundColor: "transparent"
    textColor: "{colors.on-surface-variant}"
    rounded: "{rounded.icon-toggle}"
    size: "48dp"
  visibility-toggle-checked:
    backgroundColor: "{colors.secondary-container}"
    textColor: "{colors.on-secondary-container}"
    rounded: "{rounded.icon-toggle}"
    size: "48dp"
  session-panel:
    backgroundColor: "{colors.surface-container-low}"
    textColor: "{colors.on-surface}"
    rounded: "{rounded.panel}"
    width: "320dp"
  terminal-text-input:
    textColor: "{colors.on-surface}"
    typography: "{typography.body-large}"
    padding: "0dp"
---

# Design System: Termux for Android and Codex

## Overview

**Creative North Star: "Polished native Android utility"**

The interface uses Material Design 3 on the existing Java/Android Views stack. Native controls provide familiar navigation, tonal surfaces and readable labels around the custom terminal renderer. This records core Material 3 implementation, not a Compose conversion or a complete Material 3 Expressive migration.

The terminal remains the main content. Controls have deliberate touch areas, sessions remain reachable through an explicit control, and the same session panel adapts to the available window. Dynamic color personalizes the Android chrome; a green light/dark palette supplies the static fallback. Terminal colors and fonts remain user controlled.

**Key Characteristics:**
- Native Material 3 Views controls and semantic color roles.
- Readable terminal content with separate Android interface typography.
- Explicit session access that preserves Android edge Back gestures.
- A modal panel in compact windows and a persistent panel in expanded windows.
- Single-line extra keys that scroll horizontally rather than wrap.

Source of truth: `app/src/main/res/values/themes.xml`, `values/material_colors.xml`, `values-night/material_colors.xml`, `values/terminal_styles.xml`, the terminal and settings layouts, terminal drawables, `TermuxActivity.java`, `TermuxApplication.java`, `TermuxTerminalSessionActivityClient.java`, and `ExtraKeysView.java`. Inherited typography and action shapes come from the existing Material Components for Android 1.12.0 library resources. This document records source evidence only; it makes no screenshot or runtime-verification claim. Sidecar HTML snippets are approximate static light-fallback previews of native components, not app-rendered screenshots. Their derived OKLCH tonal strips are panel metadata, not additional Android color resources.

## Colors

The fallback palette pairs deep green actions, quiet green-grey containers and a blue-grey tertiary family with warm red error roles. Unprefixed tokens are the static light palette; `dark-` tokens are the corresponding static night palette. Their semantic role names map to the Android `md_theme_*` resources. Dynamic color is applied to activities when the library reports support and can replace these values at runtime.

### Primary

- **Deep green / night mint:** `primary` and `dark-primary` mark the filled New session action and focus emphasis. Use their matching on-color for text and icons.
- **Green containers:** the primary container pairs are available through the app theme; the terminal session selection specifically uses the secondary container family.

### Secondary

- **Muted green-grey:** the secondary role family supplies tonal actions and selected control surfaces.
- **Soft green containers:** `secondary-container` and its night equivalent distinguish selected sessions, session-number badges and active extra keys. Their matching on-colors carry readable foregrounds.

### Tertiary

- **Blue-grey:** the tertiary role family is assigned in the app theme. It is not an additional accent painted into the terminal screen's custom components.

### Neutral

- **Green-tinted surface:** the terminal and settings toolbars, extra-key pager and window decor use `surface`; the session panel uses `surface-container-low`. The current terminal palette's background is explicitly applied to TerminalView because its renderer leaves default-background cells transparent. System/decor areas therefore follow the Android theme while the terminal retains its own colors.
- **Raised tonal surface:** idle extra keys and unselected focused session rows use `surface-container-high`. The intermediate and highest container roles remain available to inherited Material components.
- **Ink and secondary ink:** main labels use `on-surface`; supporting text and idle utility icons use `on-surface-variant`.
- **Outlines:** `outline` and `outline-variant` are theme roles for inherited component boundaries.
- **Errors:** failed terminal exits use `error` in the session subtitle, alongside an explicit exit-status label. Error container pairs remain available to the theme.

**The Semantic Pair Rule.** Resolve interface colors through Material attributes and pair each container with its matching on-color; hard-coded fallback colors belong in resources, not individual controls.

**The Terminal Boundary Rule.** Keep ANSI colors, terminal background and terminal font preferences independent of the Android Material palette.

## Typography

Android controls use the platform `sans-serif` and `sans-serif-medium` families inherited from Material 3. The terminal falls back to `Typeface.MONOSPACE` and may load the user's existing font file. No display or headline scale is introduced on this surface.

The frontmatter records the library's API 21+ text appearances. Sizes and line heights are in scalable Android text units; letter spacing is in em. Platform family selection supplies regular or medium weight without a bundled font. Extra keys apply LabelMedium through TextViewCompat for API 21 compatibility and explicitly reset letter spacing to zero. Their capitalization remains the existing user property.

- **Title large:** app title in the top toolbar.
- **Title medium:** Sessions panel title.
- **Title small:** session name, limited to two lines with end ellipsis.
- **Body large:** the single-line terminal text-input page.
- **Body small:** session status or terminal title, limited to one line with end ellipsis.
- **Label large:** session-number badge and inherited Material action labels.
- **Extra key:** compact medium labels, one line, with Shift+Tab given an explicit accessibility description.
- **Terminal:** renderer font size is the user's saved preference in renderer pixels; no fixed Material text size is substituted.

**The Two Type Systems Rule.** Use scalable Material text appearances for Android controls and preserve the terminal renderer's configurable monospace typography.

## Layout

The root fits system windows and uses a narrow horizontal content margin (3dp). The top MaterialToolbar has a minimum height (56dp), inner horizontal padding (8dp), a single-line app title, an explicit Sessions control and a conditional visibility toggle. Icon hit areas are square (48dp).

Below the toolbar, a window narrower than the expanded breakpoint uses a start-aligned modal session panel. Its width is the smaller of 320dp and the usable root content width minus 16dp, clamped at zero; this is recalculated on every compact resize, including compact-to-compact changes. At an available content width of at least 840dp, outside picture-in-picture, the same panel is reparented beside the terminal at a fixed width (320dp). The frontmatter's panel width is therefore the compact maximum and the expanded fixed width. Terminal content and the extra-key pager receive an equal start margin in expanded mode. The open and close controls hide while the panel is persistent. Returning to compact restores the modal drawer. The breakpoint uses current window width, not device category or orientation.

The panel header has a minimum height (64dp) and a padded title start (20dp). It measures the current title text plus start padding and space for three icon targets (144dp); when that exceeds the panel's available width, the title stacks above the end-aligned action row. This responds to title length, text scaling and container width rather than a second fixed window breakpoint. Its session list and bottom action group use the medium inset (12dp). Rows have a minimum height (68dp), a square number badge (32dp), a text-column gap (12dp) and a supporting-text gap (4dp). List rows are separated by a transparent gap (4dp).

The key toolbar starts from a row unit (48dp), applies the user's height scale, clamps each row to at least 48dp, multiplies by the configured row count, then adds pager bottom padding (8dp). Each key enforces a minimum width (48dp) through both the TextView and View minimum-width properties, with horizontal margins (4dp each), internal horizontal padding (4dp), and a top/bottom shape inset (3dp). A horizontal scroll container keeps labels and targets from wrapping or squeezing into a narrow viewport. The extra-key width and height entries in the frontmatter are minimum target dimensions, not fixed clipping dimensions.

## Elevation & Depth

Depth primarily comes from Material tonal surfaces: a low session panel, higher key surfaces and secondary-container selection. No custom shadow vocabulary is added. Material buttons, dialogs, ripples and the modal DrawerLayout retain their library-provided elevation, scrim and interaction behavior. The filled/tonal button base elevation is the library's zero-elevation role (0dp); native state handling remains inherited.

Drawer open/close animation uses the native component and is skipped when the system animator duration scale is zero. No custom animation duration or easing curve is defined by the app for this work.

## Shapes

The session panel and number badge use the shared soft container radius. Session rows use a larger rounded silhouette, with transparent fill at rest and a secondary-container fill when selected. Every focused row gains a primary-colored stroke (2dp): an unselected row uses a higher tonal surface, while a selected row retains its secondary-container fill. Its ripple comes from `colorControlHighlight`.

Actions inherit the full-corner Material button shape; the pill is defined as half the control height rather than a new fixed radius. Extra keys use a much smaller radius to keep adjacent controls distinct. The circular visibility toggle has its own fixed radius. These roles are recorded separately in the frontmatter; do not replace them with a single universal corner size.

## Components

### Buttons

New session uses the filled primary treatment. Resume Codex uses the Material tonal treatment. Both share `TerminalAction`, a full-width, content-height button with a minimum touch height (56dp), start/end padding (16dp), vertical insets (4dp each), and a leading icon (20dp). Labels permit up to two lines so actions remain usable in small windows; this does not change the single-line extra-key treatment. Material label typography, vertical content padding and ripple/state behavior remain inherited. Native dialogs use `MaterialAlertDialogBuilder` at the migrated terminal call sites.

The component height entries above record minimum heights for actions, session rows and extra keys; they do not mean fixed clipping heights. Do not force long or enlarged labels into those exact heights.

### Session panel and navigation

Sessions is an explicit icon action with a content description and tooltip. Opening it stops terminal selection and hides the soft keyboard. A closed modal drawer is locked against edge opening so Android Back retains the edge; native dismissal is enabled once open. Selecting a row switches the current session and closes the compact panel. Long press retains session rename behavior.

Rows carry a number badge, name and supporting status. Selection changes container and foreground roles; unsuccessful exits add the error color and exit status. The persistent panel keeps the same list and action controls. It is not a separate navigation-rail component.

The settings screen uses a MaterialToolbar with a minimum height (56dp), a surface background, on-surface title text and on-surface navigation-icon tint. This updates the toolbar treatment without redefining all shared settings or dialog styles.

### Extra keys

Keys are native `MaterialButton` instances with LabelMedium typography, compact corners, no outline stroke, and semantic idle/pressed fills applied through the Material background tint list. The native background and ripple remain intact: touch handling explicitly sets pressed state and clears it on release or cancellation. Modifier text color tracks its independent active/locked state; the `extra-key-active` preview represents an active modifier during a press, not a permanently filled locked key. The installed default layout has two rows and includes Escape, Tab, Shift+Tab, arrows and Enter; user layouts remain authoritative. Repeat, popup and haptic behavior retain the existing extra-key implementation. Narrow widths scroll horizontally.

### Visibility toggle

The conditional picture-in-picture/bubble control is a checkable Material icon button. It uses a transparent idle surface and secondary-container checked surface, with matching icon colors and a native icon (24dp). Its meaning is supplied by its content description and tooltip, not color alone.

### Inputs / Fields

The terminal toolbar's input page remains a single-line native EditText with BodyLarge text, a hint, actionSend IME behavior, no fullscreen extraction, and zero top/bottom padding. It is not a Material outlined text field. Border, cursor and focus treatment remain inherited from the native theme; no custom field error or disabled style is introduced.

## Do's and Don'ts

### Do:

- **Do** use Material semantic color pairs for Android controls and retain dynamic-color support.
- **Do** keep the terminal's saved font, colors and custom key layout independent of the interface theme.
- **Do** use explicit Sessions access and preserve Android's edge Back gesture.
- **Do** adapt the same session panel to current window width, including resize back to compact mode.
- **Do** retain scalable interface text, minimum 48dp key targets and horizontal key scrolling in narrow windows.
- **Do** use content descriptions and explicit status text alongside icons and color.

### Don't:

- **Don't** wrap the extra-key toolbar into oversized controls or replace the user's key layout.
- **Don't** introduce a Compose dependency or describe the existing Views implementation as a complete Expressive migration.
- **Don't** make a terminal color scheme follow the Android wallpaper palette automatically.
- **Don't** treat legacy shared Material Components themes or inherited native input styling as newly standardized Material 3 patterns.

Not canonized: shared `Theme.BaseActivity` / `ThemeOverlay.BaseDialog` resources still contain legacy Material Components/red styling; the migrated terminal theme does not establish those as the new system. Device behavior and visual verification remain separate from this source-derived record.
