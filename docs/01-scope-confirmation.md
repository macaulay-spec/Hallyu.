# HALLYU — Step 1: Scope Confirmation

> Gate deliverable. Nothing in Section 40 (Implementation Phases) begins until this document,
> the Architecture Plan, and the Design System + mockups have been reviewed and approved.
>
> Source of truth: `Hallyu_Final_Integrated_Master_Build_Specification (2).md` (v2.0).
> Repository state at planning time: **the spec only — no code, no schema, no design assets exist yet.** Phase 0 starts from a clean slate.

---

## 1.1 What is in the MVP (Section 20 — restated in full)

### Identity
- Email + social authentication (Supabase Auth)
- Profile: username, display name, avatar, banner, bio
- Follows / followers (user ↔ user)
- Privacy settings (private profile, block, mute)

### Community
- Posts (text, up to 5,000 chars) with categories: Reaction, Discussion, Theory, Recommendation, Meme, News, Question, Fan content
- Image posts (multiple images per post; max 6)
- Comments + nested replies (3 levels max)
- Reactions (on posts **and** comments)
- Reposts (share to own timeline)
- Bookmarks ("Saved")
- Hashtags (auto-created, browsable, hashtag pages)
- Mentions (@user, on posts and comments, notification-triggering)
- Communities (create, join, public/private, rules, moderators, community feeds)

### K-drama layer
- Dramas (metadata from TMDB, cached locally — see Architecture)
- Episodes (first-class objects with air dates)
- Actors (profiles, "in" list)
- Drama hubs (living community pages, not database pages)
- Episode discussions (first-class: Drama → Episode → Discussion → Posts)
- Currently Watching (add, episode progress, completed, remove — no reviews/ratings)
- Drama follows, actor follows
- Spoiler system driven by per-user watch progress

### Discovery
- Explore (trending, currently airing, upcoming, popular, actors, communities, official accounts, hashtags, fan content, episode activity)
- Search (dramas, episodes, actors, users, communities, hashtags, posts)
- Trending (velocity-based, anti-monoculture scoring)
- For You feed (explainable signal-based ranking)
- Following feed (chronological social graph — not algorithmic)

### Trust
- Report (posts, comments, users, communities, media)
- Block, mute (users; mute dramas & communities)
- Moderation pipeline (report → classify → queue → decision → appeal → audit log)
- Verified / official accounts (badge + visual identity layer; granted via internal admin tooling)
- Basic AI moderation (toxicity/spam classification + spoiler suggestion)
- Spoiler system (watch-progress-based auto-blur + manual reveal/hide)

### Retention
- Notifications (critical / important / optional categories; push + in-app; preferences; quiet hours)
- Followed-drama updates (official announcements, episode release alerts)
- Episode reminders (per-drama opt-in)

**Video:** NOT in the MVP as a feature — but `post_media` carries a `media_type` enum that includes `video`, so v1.1's short-form video lands without a migration (Section 22).

## 1.2 What is explicitly OUT of the MVP (Section 21 — each restated, with return window)

| # | Excluded feature | Returns in | Source |
|---|------------------|-----------|--------|
| 1 | Direct messaging | **v1.1** | §41 |
| 2 | Group messaging | Not scheduled in v1.1/v2 — later phase (flagged A-12) | — |
| 3 | Live streaming | **v2** | §42 |
| 4 | Creator monetization | **v2** | §42 |
| 5 | Subscription monetization | v2 family (not explicitly scheduled — flagged A-12) | §42 |
| 6 | Community events / watch parties | **v1.1** | §41 |
| 7 | Advanced video editor | **v1.1** (richer creator/media tooling) | §41, §22 |
| 8 | Large OST licensing system | Long-term (licensed media direction) | §43 |
| 9 | Third-party public API | **v2** | §42 |
| 10 | Web application | **v2** | §42 |
| 11 | Marketplace | Long-term (merchandise direction) | §43 |
| 12 | Integrated drama streaming | v2/long-term (licensed partnerships only — **never unauthorized streaming, ever**, §24) | §42–43 |
| 13 | AR filters | Long-term | §43 |
| 14 | Podcast integration | Long-term | §43 |

Adjacent exclusions decided by the spec's own decision log (§52, "deliberately rejected"):
embedding-based recommendation infra, Redis, absolute performance promises, fake HLS,
hard-coded provider pricing/limits, scraping restricted databases, client-only authorization,
four competing home feeds, and fake backend data presented as finished functionality.

**Copyright guardrail (§24):** no full-episode uploads, no unauthorized streaming/ripping, no
OST redistribution. Permitted: metadata, official links, authorized embeds, UGC under policy,
copyright report/takedown workflow (the report taxonomy includes a `copyright` reason from day one).

## 1.3 Ambiguities — flagged, not guessed

Per the gate instruction, nothing below is a silent pick. Each item states the two readings,
the resolution I **propose** for the architecture and mockups, and what I need from you to lock it.
Items marked **BLOCKER** must be answered before the affected phase; the rest I will proceed with
as proposed and they remain open for correction at review.

### A-1. Social auth providers — *BLOCKER for Phase 0 (auth)*
- Spec: "email/social authentication" — providers unspecified.
- Proposed: **Email+password (baseline) + Apple + Google** OAuth. Apple Sign In is effectively
  required for App Store review of any social app, and Google is the other universal.
- Needed: confirm provider list (or add KakaoTalk — common in KR, but adds a native dependency;
  I would defer it to v1.1 unless you say otherwise).

### A-2. What is an `episode_discussion` — *affects Phase 2 schema*
- Two readings: (a) one container thread per episode that holds posts; (b) a label/flag on posts.
- Proposed: **a post belongs to an episode discussion by tagging that episode** (`post_episode_tags`).
  The "discussion" is the episode's tagged-post stream, rendered as a first-class page.
  Tagged posts also appear in Home feeds (a post is never *only* in an episode thread — that would
  hide discovery). Episode page = drama-scoped, spoiler-gated, chronological feed + live reactions.
- Needed: confirm "tagged stream, not a silo."

### A-3. Spoiler model: level vs. episode tag — *affects Phase 2/5*
- Spec example shows "Episode context: 8" **and** "Spoiler level: Episode 8" — redundant on its face.
- Proposed: `posts.spoiler_level` (smallint, nullable) = "this content spoils through episode N of
  its tagged drama." It is **independent of** `post_episode_tags`: a post can discuss Ep 8 without
  spoiling (spoiler_level NULL, or lower) and can be a pure spoiler with no episode tag (level = drama's
  current episode). Blur rule: viewer's `watching_status.watched_through` on that drama
  < spoiler_level → media+text blurred with veil. `spoiler_source` = `user | ai` (auditability).
- Needed: confirm independent-axis model.

### A-4. Reaction set — *spec defines reactions but never the set*
- Proposed: **5 curated reactions with semantic colors** (see Design System §6): Like ❤, Crush 😍,
  Crying 😭, Fire 🔥, Clap 👏. Like = double-tap shortcut + primary button; others via long-press
  picker. Deliberately fandom-specific (Crying and Crush are core K-drama emotions) and not a
  Reddit/Slack emoji dump.
- Needed: approve set (rename/swap is a one-line enum change).

### A-5. Repost semantics
- Spec: reposts exist, no mechanics.
- Proposed: X-style repost — appears in your timeline as "reposted by you" with the original
  author preserved; plain repost only (no quote/comment in MVP — that's a comment, not a feature);
  counts roll up on the original; visible in your Following followers' feeds.
- Needed: confirm.

### A-6. Official accounts in MVP — *how "official" is MVP-official?*
- Two readings: (a) full official-account onboarding in the product; (b) badge + display layer only,
  granted out-of-band.
- Proposed: **(b).** The feed/Explore render official accounts with a distinct visual identity
  (gradient wave-seal, "Official" chip), users can follow/mute them, and official posts can appear.
  Granting verification is an **internal, permission-gated admin screen** (functional only, no mockup
  per the gate). `verification_requests` table exists so the workflow is real, not fake.
- Needed: confirm; also confirm which real accounts get seeded at launch (proposed: tvN, JTBC,
  Netflix KR, 1–2 production houses) — seeding is editorial data entry, not fake features.

### A-7. Story / update rings — *excluded from MVP*
- §4 mentions "optional story/update rings where useful." Not in the §20 MVP list.
- Proposed: **not built.** Replaced by a "Now Airing" horizontal module on Home (poster cards with
  episode badges + airing times). Rings are a v1.1 candidate if that module underperforms.
- Needed: confirm exclusion.

### A-8. TMDB credentials in this environment — *BLOCKER for Phase 2 (metadata sync)*
- Spec: TMDB is the primary metadata source; §54 says isolate unconfigurable external deps behind a
  clean adapter and document the exact remaining configuration.
- Current state (verified): TMDB has moved to **API v4** — application-level "API Read Access Token"
  (Bearer), v3 keys still valid for v4 GETs. Token lives in Supabase secrets; the mobile client
  **never** touches TMDB.
- Plan: sync runs in a Supabase Edge Function (cron) upserting into Postgres; the app reads local
  rows only. If no TMDB account/token exists in this environment, I will (1) build the full adapter +
  sync code, (2) provide an idempotent seed script with a small editorial dataset (dramas/actors we
  feature) so flows are real end-to-end, and (3) document the exact one-line config needed.
- Needed: TMDB v4 read token (or confirmation to proceed with seed + documented config).

### A-9. AI moderation provider — *BLOCKER for Phase 5 (basic AI moderation slice)*
- Spec: "basic AI moderation," "AI may detect likely spoilers," no provider named.
- Proposed: a single `ai-moderation` Edge Function adapter (classify: spam/harassment/toxicity/
  spoiler-likely/duplicate, with confidence). MVP behavior: **suggest, don't punish** — spoiler
  suggestions are shown to the author and editable; auto-actions only for high-confidence spam/dup
  (hide + notify + appeal path), per §27 (AI is never sole authority).
- Needed: provider + API key (any chat-capable LLM endpoint), or proceed with rule-based
  heuristics + human queue and plug the model in later.

### A-10. Push infrastructure — *BLOCKER for Phase 5 (push slice)*
- Push = Expo push tokens → FCM/APNs. Needs an Expo account (EAS push) + FCM project + APNs cert.
- Plan: in-app notifications are real and complete regardless; push is behind the
  `push-delivery` adapter and switches on when credentials exist. Documented, not faked.
- Needed: Expo/FCM/APNs credentials (or confirmation that in-app-only is the launch state).

### A-11. "Private profiles where supported" (§28)
- Proposed: **private profile = in MVP.** Toggle in Settings → Privacy; when on, profile, posts and
  Following feed content are visible to accepted followers only (RLS-enforced).
- Needed: confirm.

### A-12. Unplaced deferred features
- **Group messaging** and **subscription monetization** appear in §21 but in neither §41 (v1.1) nor
  §42 (v2). Treated as "later, unscheduled" — out of MVP either way. Flagging so the schedule isn't
  silently assumed.

### A-13. Data export vs. deletion (§28)
- Proposed: **account deletion in MVP** (soft-delete → purge job; trust-critical, cheap to do right).
  **Data export deferred** ("where required" — no jurisdiction in scope mandates it at launch; the
  RLS + Postgres structure makes a later export a report, not a rebuild).
- Needed: confirm.

### A-14. Episode-release notification timing
- Two readings: TMDB air date (KST) vs. community-confirmed "it dropped."
- Proposed: **TMDB air date is the schedule of record** (it's synced); a community "Watched / It's
  out" quick-action on the episode page logs a `watched_episodes` row and (v1.1 candidate) nudges
  reminder tuning. No speculative early notifications.
- Needed: confirm.

### A-15. Comment length limit (spec silent; posts = 5,000)
- Proposed: **1,000 chars** for comments (replies are replies, not essays).

### A-16. Max images per post (spec says "multiple")
- Proposed: **6 images**, 4:5 or 16:9 per image, carousel in UI.

### A-17. What "mute a drama" does (spec lists the control, not the effect)
- Proposed: muted drama → its tagged posts hidden from For You **and** Following, notifications
  suppressed (in-app and push), mute reversible from Settings → Muted dramas. Mute community = same,
  community-scoped. Neither block nor mute is visible to the muted party.

### A-18. Following feed composition (spec §5 lists the sources — confirming mechanics)
- Following = chronological posts from: followed users, followed **dramas** (community posts tagged
  to that drama + official posts for it), followed actors (official/fan posts tagged to them via
  mention + official posts), joined communities (community posts), followed official accounts.
  No ranking — timestamp order, cursor-paginated.

### A-19. Session / device policy
- Standard Supabase session lifecycle; multiple concurrent sessions allowed; "Log out of all
  devices" in Settings; no session cap in MVP.

**Open decision count: 19 flagged, of which 4 are true blockers (A-1, A-8, A-9, A-10) — each has a
documented fallback so no phase is dead-ended while you decide.**

## 1.4 What "done" means for this build (Section 45 — in my own words)

A feature is **done** when all of the following are true, verified end-to-end against the real
Supabase backend — not a screenshot, not a mock, not "the UI renders":

1. **UI exists** and matches its approved mockup and the design system (no drift).
2. **Database exists**: real tables, real FKs, real migrations, run and verified.
3. **Permissions exist**: RLS policies written and *tested both ways* (allowed access works,
   forbidden access is refused — including the security test matrix in §44).
4. **Backend logic exists**: server-side (triggers/Edge Functions) where correctness or secrecy
   requires it — fan-out, spoiler computation, sync, moderation. No client-side-only enforcement.
5. **Loading state** is deliberate (skeleton wave system), not a spinner in the void.
6. **Error state** is deliberate with retry, never a blank or a raw stack trace.
7. **Empty state** is deliberate, in product voice, with a path forward.
8. **Analytics events** fire for every user-visible action listed in §33 (real events, no stubs).
9. **Accessibility addressed**: labels, contrast, touch targets, reduced motion — per Design System §9.
10. **Security addressed**: no secrets in bundle, no client-trusted privilege, rate limits on writes.
11. **Real device testing passes** for the flows §44 enumerates.
12. **End-to-end flow works**: I can perform the feature from a real authenticated account, see the
    persisted result from a second account, and undo it.

"Nice to see" is not done. A button that opens nothing is not a feature. A screen backed by fake
JSON is not a production application. If any screen in the final build can't hit the mockup's bar,
I will flag it explicitly at that phase rather than ship a flatter version quietly.

## 1.5 North-star check (why each MVP item earns its place)

Per §2, every MVP feature must strengthen **Discover → Follow → Watch → Discuss → React → Connect
→ Return**. Mapping: posts/comments/reactions/reposts/hashtags/mentions = Discuss+React;
dramas/episodes/drama hubs/episode discussions/watching/spoilers = Watch+Discuss (the
differentiator); Explore/search/trending/For You/Following = Discover; follows/communities/actor
follows/official accounts = Connect+Return; notifications/episode reminders = Return;
report/block/mute/moderation/verified = the trust floor that makes all of the above safe.
Nothing in the MVP list is decorative; if a Phase 1+ item can't be traced to a loop step, it gets
cut at that phase's review.
