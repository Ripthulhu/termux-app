# Termux for Android and Codex

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

The owner uses Codex CLI directly in Termux on an Android 17 phone, with touch input, gesture navigation, split screen and floating windows.

## Product Purpose

Run terminal tools and answer Codex prompts comfortably on a phone. Keep sessions reachable while switching apps and make saved Codex conversations easy to resume after an interruption.

## Operating Context

Existing terminal sessions, custom extra-key layouts, projects, login, instructions and launcher/Widget shortcuts are user data. App updates must preserve them. Codex owns conversation storage; resuming a saved conversation does not restore terminated tools or shells.

## Capabilities and Constraints

Native Java/Android Views app with an existing Material Components dependency and a custom terminal renderer. Minimum Android API 21; target SDK 28 remains necessary for the current executable environment. No target-SDK migration is included in the UI work.

System Back must retain the screen edges. Session access uses an explicit control. Support Tab, Shift+Tab, arrows, Enter and Escape. Preserve the existing file-access protections and nonblocking, bounded terminal input.

## Brand Commitments

Use Material Design 3 for Android controls, navigation, typography, color and interaction. The user explicitly rejected oversized, wrapping controls and requested a polished interface.

## Evidence on Hand

Android 17 emulator and the owner's rooted phone are available. Private migration backups and phone data are outside the Git checkout.

## Product Principles

- Prioritize terminal legibility and reliable input.
- Adapt to the available window, including split screen and bubbles.
- Preserve the user's environment and customization.
- Use platform components and the existing implementation where practical.
