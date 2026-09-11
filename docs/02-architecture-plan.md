# HALLYU — Step 2: Architecture Plan

> Gate deliverable. Companion to `01-scope-confirmation.md` (ambiguity refs `A-n` live there).
> Stack per §29: **React Native + Expo + TypeScript · Supabase (Postgres, Auth, Storage, Realtime,
> Edge Functions) · FCM/APNs via Expo notifications · Sentry · GitHub Actions + EAS.**
> Infra per §30: modular monolith/serverless. No Redis, no microservices, no extra databases.

---

## 2.1 System overview

```
┌─────────────────────────────┐        ┌──────────────────────────────────────────────┐
│  Expo RN app (iOS/Android)  │  TLS   │                   Supabase                   │
│                             │◄───────►                                            │
│  expo-router (deep links)   │  HTTPS  │  Postgres ── RLS ── realtime channels      │
│  TanStack Query (server s.) │  push   │  Storage (avatars, post images)             │
│  Zustand (client s.)        │◄────────│  Edge Functions: tmdb-sync, episode-releases│
│  FastImage (cache)          │  tokens │  ai-moderation, push-delivery, push-notify  │
│  Expo Notifications         │────────►│  Auth (email + Apple/Google)                │
└─────────────────────────────┘         └──────┬───────────────┬──────────────────────┘
                                               │               │
                                        cron 6h │               │ Bearer v4 token (server-side only)
                                               ▼               ▼
                                          TMDB v4 API      AI moderation provider
```

Client reads **only local Postgres** for metadata and content. TMDB and the AI provider are
server-side-only dependencies, each behind one adapter function (per §54, so a missing credential
degrades to documented config, never to fake success).

## 2.2 Data model (final schema)

Conventions: `uuid` PKs (identity tables from Supabase Auth), `timestamptz` everywhere,
explicit join tables, unique constraints on all "relationship" rows, soft-delete where audit
history matters (§25A). Denormalized counters are maintained by triggers and are **display
caches only** — never trusted for correctness.

### Identity & social graph

```sql
users                      -- 1:1 with Supabase auth.users
  id uuid pk                -- = auth.users.id
  email citext unique not null
  created_at timestamptz default now()
  updated_at timestamptz
  deleted_at timestamptz                 -- soft delete (deletion flow, A-13)

profiles
  id uuid pk references users(id) on delete cascade
  username text unique check (username ~ '^[a-z0-9_]{3,20}$')
  display_name text check (char_length(display_name) between 1 and 40)
  avatar_url text
  banner_url text
  bio text check (char_length(bio) <= 160)
  is_private boolean not null default false
  role text not null default 'user'      -- user | platform_moderator | admin
  is_verified boolean not null default false
  verified_type text                      -- official_account | verified_creator | null
  verified_at timestamptz
  followers_count int not null default 0  -- trigger-maintained
  following_count int not null default 0
  created_at timestamptz default now()
  updated_at timestamptz

user_preferences
  user_id uuid pk references users(id)
  spoiler_policy text not null default 'by_progress'  -- by_progress | always_blur | never_blur
  notify_critical  boolean not null default true      -- global category switches
  notify_important boolean not null default true
  notify_optional  boolean not null default false
  push_enabled boolean not null default true
  in_app_enabled boolean not null default true
  quiet_start time                               -- e.g. 23:00
  quiet_end   time                               -- e.g. 07:00
  muted_official boolean not null default false  -- §15: users may mute official accounts
  language text not null default 'en'
  theme text not null default 'dark'

device_tokens
  id bigint generated always as identity pk
  user_id uuid not null references users(id) on delete cascade
  token text unique not null
  platform text not null check (platform in ('ios','android'))
  last_used_at timestamptz
  created_at timestamptz default now()

follows                        -- user → user
  follower_id uuid references users(id) on delete cascade
  followee_id uuid references users(id) on delete cascade
  created_at timestamptz default now()
  primary key (follower_id, followee_id)
  constraint no_self_follow check (follower_id <> followee_id)
  -- index: (followee_id, follower_id) for follower lists

drama_follows
  user_id uuid references users(id) on delete cascade
  drama_id uuid references dramas(id) on delete cascade
  created_at timestamptz default now()
  primary key (user_id, drama_id)

actor_follows
  user_id uuid references users(id) on delete cascade
  actor_id uuid references actors(id) on delete cascade
  created_at timestamptz default now()
  primary key (user_id, actor_id)

blocks
  blocker_id uuid references users(id) on delete cascade
  blocked_id uuid references users(id) on delete cascade
  created_at timestamptz default now()
  primary key (blocker_id, blocked_id)
  constraint no_self_block check (blocker_id <> blocked_id)

mutes
  mutor_id uuid references users(id) on delete cascade
  scope text not null check (scope in ('user','drama','community'))
  target_id uuid not null
  created_at timestamptz default now()
  primary key (mutor_id, scope, target_id)   -- A-17 defines effect
```

### K-drama layer

```sql
genres
  id smallint pk
  name text unique not null            -- Romance, Thriller, Comedy, Historical, …

dramas
  id uuid pk
  tmdb_id int unique                   -- sync identity; nullable if editorial-only
  title text not null
  title_ko text
  overview text
  poster_url text
  backdrop_url text
  status text not null default 'upcoming'  -- upcoming | airing | completed
  network text                           -- tvN, Netflix, MBC, TVING, …
  release_date date
  end_date date
  air_day smallint                       -- 0=Sun … 6=Sat (KST schedule)
  air_time time                          -- KST
  total_episodes int
  current_episode int not null default 0
  runtime_min int
  country text not null default 'KR'
  created_at timestamptz default now()
  updated_at timestamptz
  -- indexes: (status), (release_date), gin (title, title_ko) pg_trgm for search

drama_genres
  drama_id uuid references dramas(id) on delete cascade
  genre_id smallint references genres(id)
  primary key (drama_id, genre_id)

actors
  id uuid pk
  tmdb_id int unique
  name text not null
  name_ko text
  bio text
  profile_url text
  known_for jsonb not null default '[]'   -- display titles, editorial-curated
  followers_count int not null default 0
  created_at timestamptz default now()
  updated_at timestamptz
  -- index: gin (name, name_ko) pg_trgm

drama_cast
  drama_id uuid references dramas(id) on delete cascade
  actor_id uuid references actors(id) on delete cascade
  character_name text
  is_lead boolean not null default false
  billing int not null default 100        -- sort order
  primary key (drama_id, actor_id)
  -- index: (actor_id) for "In" list on actor pages

episodes
  id uuid pk
  drama_id uuid not null references dramas(id) on delete cascade
  season smallint not null default 1
  episode_number int not null
  title text
  air_date timestamptz                    -- KST-normalized
  overview text
  still_url text
  discussion_count int not null default 0 -- trigger-maintained (posts tagged)
  primary key (drama_id, season, episode_number)
  -- indexes: (air_date), (drama_id, episode_number)

watching_status                  -- §13 + §9: drives spoiler safety
  user_id uuid references users(id) on delete cascade
  drama_id uuid references dramas(id) on delete cascade
  status text not null default 'watching'  -- watching | completed | on_hold | dropped
  watched_through int not null default 0   -- episode number
  started_at timestamptz default now()
  completed_at timestamptz
  updated_at timestamptz
  primary key (user_id, drama_id)

watched_episodes                 -- explicit per §25A (fine-grained progress)
  user_id uuid references users(id) on delete cascade
  episode_id uuid references episodes(id) on delete cascade
  watched_at timestamptz default now()
  primary key (user_id, episode_id)
```

### Content

```sql
hashtags
  id uuid pk
  slug text unique not null              -- lowercased, # optional in UI
  display_name text not null
  post_count int not null default 0      -- trigger-maintained
  created_at timestamptz default now()

posts
  id uuid pk
  author_id uuid not null references users(id) on delete cascade
  content text not null check (char_length(content) <= 5000)
  category text not null default 'discussion'
       -- reaction | discussion | theory | recommendation | meme | news | question | fan_content
  spoiler_level int                          -- A-3: NULL = no spoiler; else spoils through
                                             -- ep N of the post's tagged drama
  spoiler_source text not null default 'user'  -- user | ai
  is_hidden boolean not null default false    -- moderation state (kept, hidden)
  deleted_at timestamptz
  created_at timestamptz default now()
  updated_at timestamptz
  -- indexes: (author_id, created_at desc), (created_at desc),
  --          gin (content) for search, (spoiler_level) where spoiler_level is not null

post_drama_tags                -- ≤ 1 drama per post (enforced by trigger)
  post_id uuid references posts(id) on delete cascade
  drama_id uuid references dramas(id) on delete cascade
  primary key (post_id, drama_id)

post_episode_tags              -- episode must belong to the post's drama (trigger)
  post_id uuid references posts(id) on delete cascade
  episode_id uuid references episodes(id) on delete cascade
  primary key (post_id, episode_id)
  -- index: (episode_id) for episode discussion streams

post_media                     -- metadata separate from posts per §25A
  id uuid pk
  post_id uuid not null references posts(id) on delete cascade
  media_type text not null default 'image'   -- image | video (video from v1.1, A: §22)
  url text not null
  thumb_url text
  width int, height int, duration_ms int
  blurhash text
  alt_text text                            -- accessibility + future AI description
  position smallint not null default 0     -- ≤ 6 (A-16)
  primary key (post_id, position)

post_hashtags
  post_id uuid references posts(id) on delete cascade
  hashtag_id uuid references hashtags(id) on delete cascade
  char_start smallint, char_end smallint
  primary key (post_id, hashtag_id)

post_mentions
  post_id uuid references posts(id) on delete cascade
  mentioned_user_id uuid references users(id) on delete cascade
  char_start smallint, char_end smallint
  primary key (post_id, mentioned_user_id)
  -- index: (mentioned_user_id) for "mentions of me"

reposts
  id uuid pk
  actor_id uuid not null references users(id) on delete cascade
  original_post_id uuid not null references posts(id) on delete cascade
  created_at timestamptz default now()
  primary key (actor_id, original_post_id)  -- one repost per post (A-5)
  -- index: (created_at desc) for feed pagination

bookmarks
  user_id uuid references users(id) on delete cascade
  post_id uuid references posts(id) on delete cascade
  created_at timestamptz default now()
  primary key (user_id, post_id)

post_reactions
  post_id uuid references posts(id) on delete cascade
  user_id uuid references users(id) on delete cascade
  type text not null check (type in ('like','crush','crying','fire','clap'))   -- A-4
  created_at timestamptz default now()
  primary key (post_id, user_id, type)
  -- index: (post_id) for counts; reaction counts served as (type, count) rows

comments
  id uuid pk
  post_id uuid not null references posts(id) on delete cascade
  parent_id uuid references comments(id) on delete cascade
  author_id uuid not null references users(id) on delete cascade
  content text not null check (char_length(content) <= 1000)   -- A-15
  spoiler_level int
  spoiler_source text not null default 'user'
  depth smallint not null default 0        -- 0..2 (3 levels, §11), trigger-enforced
  is_hidden boolean not null default false
  deleted_at timestamptz
  created_at timestamptz default now()
  updated_at timestamptz
  -- indexes: (post_id, created_at), (parent_id), (author_id, created_at desc)

comment_reactions
  comment_id uuid references comments(id) on delete cascade
  user_id uuid references users(id) on delete cascade
  type text not null check (type in ('like','crush','crying','fire','clap'))
  created_at timestamptz default now()
  primary key (comment_id, user_id, type)

comment_mentions
  comment_id uuid references comments(id) on delete cascade
  mentioned_user_id uuid references users(id) on delete cascade
  char_start smallint, char_end smallint
  primary key (comment_id, mentioned_user_id)
```

### Communities

```sql
communities
  id uuid pk
  slug text unique not null
  name text not null
  display_name text not null
  description text not null check (char_length(description) <= 280)
  avatar_url text
  banner_url text
  visibility text not null default 'public'   -- public | private
  member_count int not null default 0
  post_count int not null default 0
  created_by uuid not null references users(id)
  created_at timestamptz default now()
  updated_at timestamptz

community_members
  community_id uuid references communities(id) on delete cascade
  user_id uuid references users(id) on delete cascade
  status text not null default 'member'       -- pending | member | banned
  joined_at timestamptz
  primary key (community_id, user_id)

community_roles                  -- §14: community mods are NOT platform admins
  community_id uuid references communities(id) on delete cascade
  user_id uuid references users(id) on delete cascade
  role text not null check (role in ('owner','moderator'))
  primary key (community_id, user_id)

community_rules
  id uuid pk
  community_id uuid references communities(id) on delete cascade
  rule_text text not null
  position smallint not null default 0
```

### Official & verification

```sql
official_accounts
  id uuid pk
  user_id uuid unique not null references users(id) on delete cascade
  org_name text not null
  org_type text not null   -- broadcaster | production_company | publication | streaming | creator
  website text
  verified_at timestamptz
  -- profiles.is_verified/verified_type are the display flags; this row is the
  -- administrative record. Granting happens ONLY via admin-gated flow (A-6).

verification_requests
  id uuid pk
  requester_id uuid references users(id) on delete cascade   -- null for org self-service? kept: user-driven
  org_name text not null
  org_type text
  evidence_url text
  status text not null default 'pending'   -- pending | approved | denied
  notes text
  reviewed_by uuid references users(id)
  reviewed_at timestamptz
  created_at timestamptz default now()
```

### Notifications

```sql
notifications
  id bigint generated always as identity pk
  recipient_id uuid not null references users(id) on delete cascade
  category text not null check (category in ('critical','important','optional'))
  type text not null
       -- episode_release | reply | mention | reaction | community_announcement
       -- | actor_update | official_announcement | trending | recommendation | milestone
  actor_id uuid references users(id)
  post_id uuid references posts(id)
  episode_id uuid references episodes(id)
  drama_id uuid references dramas(id)
  community_id uuid references communities(id)
  title text not null
  body text
  deep_link text not null               -- hallyu://… resolved by expo-router
  read_at timestamptz
  created_at timestamptz default now()
  -- indexes: (recipient_id, created_at desc) where read_at is null (badge),
  --          (recipient_id, read_at, created_at desc) (list)

notification_preferences        -- per-category overrides on top of user_preferences
  user_id uuid references users(id) on delete cascade
  category text not null check (category in ('critical','important','optional'))
  enabled boolean not null default true
  push boolean not null default true
  in_app boolean not null default true
  primary key (user_id, category)

drama_notification_settings     -- §18 per-drama settings
  user_id uuid references users(id) on delete cascade
  drama_id uuid references dramas(id) on delete cascade
  episode_releases boolean not null default true
  official_updates boolean not null default true
  primary key (user_id, drama_id)
```

### Moderation & audit

```sql
reports
  id uuid pk
  reporter_id uuid not null references users(id) on delete cascade
  target_type text not null check (target_type in ('post','comment','user','community','media'))
  target_id uuid not null
  reason_code text not null   -- harassment | spam | misinformation | copyright | other
  details text
  ai_result jsonb                          -- classification + confidence (A-9)
  status text not null default 'pending'
       -- pending | reviewed | actioned | dismissed | appealed | appeal_resolved
  resolved_by uuid
  resolved_at timestamptz
  created_at timestamptz default now()
  -- index: (status, created_at) — moderator queue

moderation_actions              -- the decision record
  id uuid pk
  actor_id uuid                  -- null when system/automated (logged in audit_logs)
  report_id uuid references reports(id)
  target_type text not null
  target_id uuid not null
  action text not null
       -- hide | unhide | remove | pin | unpin | lock | unlock | ban_member | unban_member
       -- | approve_member | deny_member | verify | unverify | restore
  details jsonb
  created_at timestamptz default now()

audit_logs                      -- privileged actions + config changes
  id bigint generated always as identity pk
  actor_id uuid
  action text not null
  entity text not null
  entity_id uuid
  meta jsonb
  ip inet
  created_at timestamptz default now()
```

### Sync & discovery plumbing

```sql
tmdb_sync_state                 -- A-8: incremental sync watermark
  tmdb_id int not null
  entity text not null check (entity in ('drama','actor','episode'))
  last_synced_at timestamptz
  hash text                      -- response fingerprint; skip when unchanged
  primary key (tmdb_id, entity)

trends                          -- §6 trending: snapshots, not "most likes"
  window_start timestamptz not null
  rank int not null
  kind text not null check (kind in ('hashtag','drama','actor','post'))
  target_id uuid not null
  score double precision not null
  post_count int not null
  velocity double precision not null
  primary key (window_start, kind, target_id)
  -- 15-min snapshot window; Explore reads the latest window only
```

**Feed ranking (For You, §17):** a Postgres RPC `fn_for_you(p_user uuid, p_cursor)` scores
candidates from the union of (followed users' posts, followed dramas' tagged posts, followed
actors' tagged posts, joined communities' posts, followed officials' posts, trending-adjacent
posts from followed-interest tags) using only explainable signals — follows, watches, episode
progress, reactions, comments, saves, recent views, interests, freshness, trending momentum —
as weighted sums in SQL. No embeddings, no models (deliberately rejected, §52). The same
candidates minus weighting, ordered by `created_at`, is the **Following** feed — one query
family, two orderings, so the social graph is never hidden behind an opaque ranking (§5).

**Spoiler computation (§9):** at read time, the feed/episode RPC joins the viewer's
`watching_status` against each post's `spoiler_level`; the client receives `spoiled: true` +
`spoiler_label` and renders the veil. Blurring is **presentation, not deletion** — content is
always sent (a "hide spoiler" user can reveal; a logged-out reviewer/moderator sees it).
If content must be withheld from a class of users, that's moderation (`is_hidden`), not spoilers.

### RLS strategy (§25A — designed before client exposure)

Helper security-definer functions: `is_platform_staff()`, `is_community_staff(cid)`,
`is_follower(a, b)`. All policies reference these; **no policy ever checks `is_verified`**
(verification never confers power, §26).

| Table group | Read | Write |
|---|---|---|
| profiles | public unless `is_private` (then self/follower/staff) | self; staff (role, verified flags) |
| user_preferences, device_tokens, watching_status, watched_episodes, bookmarks, mutes, blocks, notification prefs, drama notification settings | owner only | owner only |
| follows, drama_follows, actor_follows | self's rows + public follower lists (subject to target privacy) | authenticated; unique-constraint idempotent |
| posts | not `deleted_at` and not (`is_hidden` and not author/staff); private-author posts → followers only | insert: authenticated (rate-limited); update: author within edit window; delete: author or staff |
| post_drama/episode/tags, post_media, hashtags (rows), mentions, reposts, reactions, comments (+children) | follows parent post visibility | author/owner; unique constraints |
| comments depth | — | trigger enforces `depth ≤ 2` |
| dramas, episodes, actors, genres, hashtags (meta), trends | public read | **service role only** (sync + admin) |
| communities | public: everyone; private: members (+pending for join prompt) | insert: authenticated (creator becomes owner); member mgmt: community staff |
| community_members, community_roles | self + community staff | community staff (owner for roles) |
| official_accounts, verification_requests | staff | staff (grant flow) |
| notifications | recipient only | recipient (mark read) + system insert via service role |
| reports | self's own reports + staff (all) | authenticated (rate-limited per target) |
| moderation_actions, audit_logs | staff | staff / system |

Blocks are enforced at the feed RPC level (blocked author's posts excluded for blocker;
blocked user's writes to blocker fail) and in profile access — RLS alone can't express
"visible unless we block each other" cleanly, so the read path goes through RPCs that apply
it deterministically.

## 2.3 Navigation architecture & deep links

**Framework: `expo-router`** (file-based routes, typed, deep links free) inside React Navigation.

```
/                          → session gate (redirect to (auth) or (tabs))
(auth)
  /splash   /welcome   /login   /signup   /recovery
(onboard)
  /onboard/interests   /onboard/dramas   /onboard/actors   /onboard/communities   /onboard/done
(tabs)                     ← bottom tab bar (5 items, §4)
  /                    Home: For You | Following (segmented, state kept per tab)
  /explore             Explore
  /create              Create (composer)
  /notifications       Notifications
  /profile             Own profile
post/[id]                Post detail (+ comments inline, /post/[id]?c=<commentId> scrolls)
drama/[id]               Drama hub
drama/[id]/ep/[n]        Episode page (spoiler-gated)
actor/[id]               Actor page
community/[slug]         Community page
hashtag/[slug]           Hashtag page
search?q=…               Search results
profile/[username]/[tab] Public profile; tab ∈ posts|saved|watching|communities|followers|following
settings/…               settings, settings/privacy, settings/notifications, settings/moderation
admin/queue              Moderation queue (permission-gated, A-6; functional, no mockup)
```

**Deep-link structure** (single source of truth, `deepLinks.ts`; push payloads carry the same
strings; expo-linking maps them 1:1, including cold-start queueing until the session is ready):

```
hallyu://post/{id}[?c={commentId}]
hallyu://drama/{id}
hallyu://drama/{id}/ep/{n}
hallyu://actor/{id}
hallyu://community/{slug}
hallyu://hashtag/{slug}
hallyu://profile/{username}[/{tab}]
hallyu://search?q={query}
hallyu://home?feed=following
hallyu://notifications
hallyu://create?drama={id}[&ep={n}]      ← pre-filled composer (episode page CTA)
```

Universal links: identical path structure under the app domain (once domain verified in Phase 6).

## 2.4 Core Daily Loop (§2) mapped onto screens

| Loop step | Where the user lands | What happens / how they move |
|---|---|---|
| **1 Discover** | **Home → For You** (default tab) or **Explore** | For You: "Now Airing" module (poster cards, ep badges), ranked posts with drama/episode context chips. Explore: Trending (ranked, velocity), Currently Airing, Upcoming, Popular, Actors, Communities, Official Accounts, Hashtags. |
| **2 Follow** | **Drama hub / Actor page / Profile / Community page** | Gradient Follow button in header (thumb reach: header row, right side). Follow toggles optimistically; drama follow also offers "add to Currently Watching" (one sheet, one choice — §13 "not homework"). |
| **3 Watch** | **Episode page** (via notification → deep link, or hub → episode list) | Header: backdrop, air time. Spoiler banner computes from `watching_status`: "⚠ Spoilers for Ep 12 — you're through Ep 11" with **Reveal for session** / **Mark Ep 12 watched**. Progress editable here, on the hub, and in Profile → Currently Watching (§9, three entry points per spec). |
| **4 Discuss** | **Episode page → composer bar** (pre-filled: drama + episode + suggested spoiler level) or **Create tab** | Composer flow (§19): write → tag drama/episode → media → hashtags/mentions → spoiler status → preview → publish. AI spoiler suggestion appears inline, editable. |
| **5 React** | **Post detail** | Reaction bar (5 reactions, A-4), double-tap = Like, comments (3-level), repost, bookmark, share (system sheet + deep link). Optimistic reactions with rollback. |
| **6 Connect** | **Post detail → author row → Profile**; **Explore → Actors/Communities**; community pages | Follow fan / join community / follow actor; each surfaces the "their people" next (followers/following, members, cast). |
| **7 Return** | **Notification (push) → deep link**; **Home next open** | Episode-release push (critical, per-drama opt-in, quiet-hours aware) lands on the episode page. In-app: badge + "You're caught up" empty state. Home re-ranks with fresh momentum; episode reminders persist for unfollowed-but-watched dramas. |

## 2.5 State management & data fetching

- **Server state: TanStack Query v5** (one library for server data — §29: no overlapping state
  libraries). Query keys are factories: `posts.feed('following', cursor)`, `drama.hub(id)`,
  `episode.stream(id, n)`, `trends.window()`, `notifications.list(cursor)`, etc.
  - Feeds: infinite queries, cursor = `(created_at, id)`, page size 20.
  - Drama/actor metadata: `staleTime: 24h` (it's synced, not live — §31 "cached drama metadata").
  - Social reads: `staleTime: 30s`, refetch on focus for Following.
- **Client state: Zustand** (small, explicit): `useUiStore` — active feed segment, spoiler
  reveal set (session-scoped + "always reveal for this drama" persisted), composer draft (survives
  navigation), onboarding progress. `useSessionStore` — current user, profile, preferences,
  followed sets (for client-side spoiler/optimistic checks), muted sets.
- **Auth: Supabase session** via React context; secure-store token; session listener re-renders
  route gate. Session recovery on app cold start before any screen renders.
- **Realtime: Supabase Realtime**, deliberately narrow: (a) in-app notification channel
  (insert on `notifications` where recipient = me) → badge + list prepend; (b) **episode
  discussion live mode** — while an episode page is open, subscribe to inserts for that
  episode (posts + comments) and prepend. Home feeds do **not** stream; they pull (staleTime +
  pull-to-refresh). This keeps Realtime load proportional to open surface, not user base.
- **Optimistic updates** (safe, idempotent writes): reactions, bookmarks, follows (all three
  kinds), comment send, mark-watched. Each has an explicit rollback + error toast.
- **Images: FastImage** with disk cache; poster 2:3 sized variants requested from TMDB/Storage;
  blurhash placeholder on cards (rendered before load — no layout jump).
- **Offline/intermittent network** (§44): reads from cache with "stale" indicator; writes queue
  in-memory and surface "Couldn't send — will retry" toast (no silent local-only writes — §39.8:
  no fake local state presented as synced).

## 2.6 TMDB integration plan (A-8)

**Auth:** TMDB **API v4**, application-level **API Read Access Token** sent as
`Authorization: Bearer <token>` (v3 `api_key` still valid for v4 GETs). Token stored in
**Supabase Vault/secrets**; referenced only by Edge Functions. Nothing TMDB-related ships in the
client bundle (§39.2).

**Sync architecture** — the app reads local rows; TMDB is a batch source:

```
Edge Function tmdb-sync (cron: every 6h + manual trigger)
 ├─ discovery (find new/changed Korean TV)
 │   GET /4/discover/tv  { region: KR, language: ko-KR, first_air_date window, status filters }
 │   GET /4/tv/on_the_air, /4/tv/upcoming  (language ko-KR, region KR)
 ├─ per drama (new or hash-changed only, via tmdb_sync_state)
 │   GET /4/tv/{id}            → title, title_ko, overview, poster, backdrop, status, dates, network
 │   GET /4/tv/{id}/seasons    → season structure
 │   GET /4/tv/{id}/season/{s} → episodes: number, name, air_date, overview, still
 │   GET /4/tv/{id}/credits    → top cast (person ids)
 │   GET /4/person/{pid}       → name, name_ko (names[]), profile, known_for
 └─ upsert by tmdb_id (dramas, episodes, actors, drama_cast)
    never deletes on absence (a TMDB miss is not a deletion); editorial fields (known_for
    curation, network) are service-role-writable and never clobbered by sync
```

- **Incremental:** response fingerprint (`tmdb_sync_state.hash`) → skip unchanged entities.
  First run = seed (airing + upcoming + top popular KR titles, ~last 3 seasons).
- **What's cached vs fetched live:** *everything is cached* — the client never calls TMDB.
  "Live" data for the app = Postgres rows refreshed every 6h (metadata doesn't need to be
  fresher than that; air-date *changes* are the one hot path and are caught within one cycle).
- **Images:** hotlink TMDB image CDN URLs (poster/w342 or w500; stills w780) with FastImage
  caching + attribution in Settings → About ("Drama data provided by TMDB") per TMDB terms.
  Fallback path (if terms tighten): download-and-rehost into Supabase Storage at sync time —
  the adapter owns the URL source, UI unchanged.
- **Air dates:** stored KST-normalized to `timestamptz`; client renders in the viewer's local
  timezone with the KST time always visible (the fandom lives in KST).
- **If no TMDB token exists at build time** (A-8 fallback): full adapter + sync code ships, plus
  an idempotent **seed script** (editorial rows for the featured dramas — My Bias, My Boss,
  Queen of Tears, Paradise, etc. — real metadata, entered as data, documented as editorial seed,
  **not** fake backend behavior), and the remaining config is exactly: set `TMDB_V4_TOKEN`.

## 2.7 Notification architecture (§18)

**Categories & types** (per spec, verbatim mapping):

| Category | Types | Delivery default |
|---|---|---|
| Critical | episode_release, reply, mention | push + in-app (always on unless user disables) |
| Important | community_announcement, actor_update, official_announcement | push + in-app (on) |
| Optional | trending, recommendation, milestone | in-app only (push off by default) |

**Pipeline:**

```
event (Postgres trigger or Edge Function)
 → fan-out service (fn_notify, service role)
   → filter per recipient: category prefs (notification_preferences + user_preferences),
     per-drama settings (drama_notification_settings), mutes (mutes: user/drama/community),
     blocked users, quiet hours
   → INSERT notifications (deep_link always set)
   → if push allowed & token exists: push-delivery (FCM HTTP v1 / APNs) via Expo tokens
       quiet hours: push deferred, in-app kept, badge carries total
in-app: Realtime channel on notifications (recipient = me) → badge + list prepend
tap: expo-router resolves deep_link (cold start: queued until session ready)
```

- **Episode releases:** `episode-releases` Edge Function (cron every 30 min): episodes whose
  `air_date` fell within the last 45 min → recipients = users with `drama_follows` OR
  `watching_status='watching'` for that drama, minus muted, minus those with
  `episode_releases=false` for that drama. Notification: critical, body includes next-episode
  schedule, deep link `hallyu://drama/{id}/ep/{n}`. (A-14: TMDB air date is the schedule of
  record; "It's out" community quick-action updates `watched_episodes`, not the schedule.)
- **Mentions/replies:** triggers on posts/comments parse `post_mentions`/`comment_mentions` and
  reply chains → critical notifications. Reaction notifications to the post author only when the
  reactor is followed by the author (keeps the feed of pings humane; type `reaction`).
- **Official announcements:** posts by `is_verified/verified_type=official_account` authors →
  notify followers of that account (important, per-user `official_updates` setting).
- **Trending/recommendations:** nightly batch inserts optional notifications for opted-in
  users (notify_optional on). No engagement-spike spam: one per drama per 7 days.
- **Token lifecycle:** app registers Expo push token on first authenticated start → upsert
  `device_tokens`; re-registers on token rotation; "notifications off" deletes tokens.
- **Push infrastructure** (A-10): Expo push → FCM (Android) / APNs (iOS). Until credentials
  exist, `push-delivery` logs and no-ops; in-app notifications are fully functional in the
  meantime — documented, not faked.

## 2.8 Permission / role model (§26)

Roles: `user` (default) · `community_moderator` (per community, via `community_roles`) ·
`official_account` (profile flag + `official_accounts` row) · `platform_moderator` · `admin`
(`users.role` → `profiles.role`).

| Capability | user | community mod | official | platform mod | admin |
|---|---|---|---|---|---|
| Post, comment, react, follow, join | ✓ | ✓ | ✓ | ✓ | ✓ |
| Pin/lock/hide/remove posts **in own community** | — | ✓ (community-scoped) | — | ✓ | ✓ |
| Ban/approve members, edit rules **of own community** | — | ✓ (community-scoped) | — | ✓ | ✓ |
| Platform-wide hide/remove/restore, unhide appeals | — | — | — | ✓ | ✓ |
| Read & resolve reports | own reports | community reports | — | ✓ | ✓ |
| Grant/revoke verification (official accounts) | — | — | — | review | ✓ (approve) |
| Assign community moderators | — | — (owner) | — | — | ✓ |
| Manage platform config (roles, policies, trends window, moderation thresholds) | — | — | — | — | ✓ |
| Read audit logs | — | — | — | limited | ✓ |

Enforcement rules:
1. **Verification never confers power** — no RLS policy, no RPC, no route reads `is_verified`
   as an authorization input (§26, §39.5).
2. Community moderation is **scoped**: every community-staff check is
   `is_community_staff(this_community_id)` — passing it for one community grants nothing in
   another (§14).
3. Privileged writes (verify, ban, platform remove, role changes) require the actor to exist in
   `profiles.role` at query time (defense against token replay with stale client state) and
   always write an `audit_logs` row.
4. Admin/mod surfaces are **routes gated by a server-side RPC** (`fn_staff_gate`) — the route
   exists but 403s without role; the UI hides it from non-staff.
5. `official_account` is a **content-identity** role (visual badge, announcement fan-out
   eligibility, Explore section), not a privilege.

## 2.9 Search (§16)

- **MVP:** Postgres full-text (`tsvector`, english) + `pg_trgm` similarity for titles/handles
  (Korean titles searched via trigram + `ILIKE`, not Korean morphological analysis — a managed-
  Postgres limitation, documented; titles are short so trigrams suffice).
- One RPC: `fn_search(p_query, p_kinds array, p_cursor, p_limit)` → typed buckets
  (dramas, episodes, actors, users, communities, hashtags, posts), each bucket ranked by
  (match quality → popularity → freshness), paginated per bucket with an overall cursor.
- Episode search resolves to the episode page (deep link). No semantic search, no dedicated
  search service (§16: not until usage requires it).
- Rate-limited (per user, per minute) at the RPC edge.

## 2.10 Moderation pipeline (§27)

```
report (user)
 → ai-moderation adapter: classify (spam | harassment | toxicity | likely_spoiler | duplicate |
   unsafe) + confidence + suggested action      [A-9: suggest, thresholds explicit]
 → automated action ONLY for policy-certain classes (confidence ≥ threshold):
     spam/duplicate → hide + notify reporter "action taken" + appeal link
 → human queue (admin/queue, severity-sorted)
 → decision → moderation_actions row → notification to target + reporter → appeal window
     (target may appeal once → re-queue, "appealed")
 → audit_logs on every privileged step
```

- AI spoiler detection also runs **at post-creation** (drama-tagged posts, no spoiler level):
  shows the author "This may spoil Episode N — mark it?" (editable, one tap). Auto-apply only at
  high confidence, always logged (`spoiler_source='ai'`) and always reversible (§9: AI is not
  infallible).
- Human moderators retain final authority for all serious cases (§27, §39.13).

## 2.11 Phases (Section 40 — mapping to workstreams + exit criteria)

| Phase | Workstream | Exit criteria (Definition of Done, §45) |
|---|---|---|
| 0 Foundation | Expo+TS project, expo-router scaffold, Supabase project + migrations runner, Auth (email + social per A-1), env/secrets layout, **full schema + RLS migrations**, design system implementation (tokens → components), error handling + analytics foundation (Sentry + event pipeline) | Clean room: sign up → session → logged out; every table exists with RLS; a staff role can be granted and observed in audit logs; CI green (typecheck, lint, tests, migration replay) |
| 1 Core social | Profiles, follows (3 kinds), posts (+images), comments/replies, reactions, reposts, bookmarks, mentions, hashtags | Two accounts: post → image → comment → reply (3 levels) → react → repost → bookmark → mention → hashtag page, all persisted, all RLS-tested both ways, all states present, events firing |
| 2 K-drama graph | Drama/actor/episode tables live, tmdb-sync + seed, drama hubs, episode discussions, watching progress, drama/actor follows, spoiler engine (veil + reveal + progress) | Hub for a real drama renders synced data; episode discussion streams; a user at Ep 7 is veiled on an Ep 8 post and can reveal; progress change un-veils correctly; sync is incremental and idempotent (run twice = no drift) |
| 3 Discovery | Explore (all sections), search RPC + UI, trending snapshot job + scoring, For You RPC, Following feed | New account → Explore → find a drama → follow → it's in Following feed; search finds a drama/actor/user/community/hashtag/post from one box; trending list changes with real engagement and no single post monoculture (velocity decay) |
| 4 Communities | Create/join/leave, public/private, rules, moderators, community feeds, community announcement notifications | Private community blocks non-members (RLS-tested); mod can pin/hide/ban in own community and nothing in another; community posts flow into Following feed |
| 5 Trust & retention | Reports + queue + appeals, blocks/mutes (incl. drama/community mute), verification grant flow (internal), AI moderation adapter, notifications (fan-out, prefs, quiet hours, push adapter), deep links end-to-end | §44 security matrix passes (unauthorized access, privilege escalation, block behavior, private leakage, rate limits); report → hide → appeal → restore leaves a full audit trail; episode-release notification fires from the cron for a seeded air date and deep-links to the veiled episode page |
| 6 Hardening | Performance (low-end device pass, image pipeline, query tuning), accessibility audit (VoiceOver/TalkBack, contrast, reduced motion), analytics completeness (§33 events), Sentry review, test suite (unit + integration + E2E flows), security review, release builds (EAS) | §48 final product test answered YES for every bullet on a real device; no P0/P1 open; release build signed and installable |

**Dependency note:** Phases run in order; Phase 2's seed (A-8) unblocks everything downstream
that needs real drama data. Phase 5's push slice waits on A-10 without blocking the rest.

## 2.12 Blockers & risks (carried from scope confirmation)

| Blocker | Affects | Fallback until resolved |
|---|---|---|
| A-1 social auth providers | Phase 0 auth | Email+password ships alone; OAuth slots are isolated in one provider module |
| A-8 TMDB v4 token | Phase 2 sync | Seed script + full adapter; one-line config to go live |
| A-9 AI moderation key | Phase 5 AI slice | Rule-based heuristics + human queue; adapter is provider-agnostic |
| A-10 Expo/FCM/APNs push | Phase 5 push slice | In-app notifications complete; push adapter logs + no-ops |
| No physical device in this environment | Phase 6 validation | Simulator + exhaustive E2E where possible; real-device matrix documented as a remaining step (flagged, not papered over) |
| TMDB image terms drift | All metadata images | URL source is adapter-owned; Storage rehosting is a one-function change |
