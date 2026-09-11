# HALLYU — Addendum: Frontend-Only Rescope (2026-09-11, product-owner decision)

> "Build the front-end only, no backend code at all. I will sort out the backend later."

This addendum adjusts `02-architecture-plan.md` for the current build session. The full
architecture document remains the **contract for when the backend lands** — schema, RLS,
deep links, and notification deep-link strings are unchanged and are the integration seam.

## What changes

1. **No backend code in this session.** No Supabase project setup, no SQL migrations, no RLS
   policies, no Edge Functions, no TMDB sync, no auth provider integration, no push service.
2. **The app is a complete, functional frontend** (Expo RN + TypeScript + expo-router):
   every screen in Section 37, all navigation, all client-side interactions, the design
   system, deliberate loading/empty/error states, and client state management.
3. **Data layer = an interface with one implementation now:**
   - `types/domain.ts` — the full domain model (Post, Comment, Drama, Episode, Actor,
     Community, Notification, Profile, WatchingEntry, Trend, …) — same shapes the future
     Supabase tables will return (see 02-architecture-plan §2.2).
   - `data/DataProvider` — interface: feeds (forYou/following), post detail + comments,
     drama hub, episode stream, actor, community, search, hashtag, notifications,
     profile lists, currently-watching, trending, composer actions (post, react, follow,
     bookmark, mark-watched, set spoiler reveal).
   - `data/LocalPreviewDataProvider` — deterministic, realistic dataset built from the
     mockup world-state (My Bias, My Boss etc.). Mutations update local state so every
     button works: follow toggles, reactions, bookmarks, comments, spoiler reveal,
     mark-episode-watched (which un-veils the episode page), composer publishing into
     the feed. Nothing pretends to be synced.
   - Future: `SupabaseDataProvider implements DataProvider` — drop-in, no UI changes.
4. **Auth screens are UI flows only** — local in-memory session state (sign up → onboarding
   → feed). No real credentials, no token storage. Apple/Google buttons are part of the
   approved mockups; in the preview build they advance the local flow.
5. **No push, no realtime, no persistence beyond AsyncStorage** (draft + preferences only,
   within the app).

## Definition of done — frontend build (session-local version)

A screen is done when: it matches its approved mockup and the design system · every
interactive element does something real within the local data layer (no dead buttons,
no placeholders) · loading/empty/error states exist and are reachable · navigation and
deep-link route structure is complete (deep links resolve locally) · accessible (labels,
contrast, 44pt targets) · typechecks + lints clean.

**Honesty rules (from §39, preserved):** the build is labeled as a preview — Settings →
About shows "Preview build — local sample data; backend pending." No fake synced
behavior, no fake verification beyond the sample dataset's own official accounts,
no invented backend claims.

## Ambiguity impact

- **A-1, A-8, A-9, A-10 (blockers) no longer block the frontend** — they become backend
  prerequisites for the future Supabase phase. The approved mockups (Apple/Google buttons,
  etc.) are unchanged.
- All other flagged items (A-2…A-7, A-11…) still shape the **frontend** (spoiler veil UX,
  episode-discussion layout, reaction set, repost presentation, official visual identity).

## Technology change (2026-09-11, product-owner decision)

**The mobile client will be Kotlin, Android-native — NOT React Native/Expo.** The partial
Expo scaffold created during planning was removed; no RN code exists. Everything in this
addendum that is technology-neutral (the `DataProvider` interface pattern, the local preview
dataset, the frontend-only definition of done, the honesty rules) carries over 1:1 into the
Android client. Stack specifics to be decided in the architecture follow-up *after* the
mockup review: Gradle/AGP + Compose vs. classic View system, DI, image loading, navigation —
and the Android mapping of the existing plan (deep links = `hallyu://` App Links, local data
layer behind a repository interface, session state via ViewModel/state hoisting). The design
system doc (03) is technology-neutral and remains the visual contract.
