# Hallyu — Kotlin Multiplatform preview build

**Frontend-only preview build** of the approved Hallyu design (35/35 mockups, `docs/04-mockups/`).
One Kotlin codebase → native Android + iOS via Compose Multiplatform.

> **Preview build — local sample data; backend pending.**
> The user's Firebase backend (auth, feeds, publishing, notifications, TMDB-sourced drama data)
> is owned and built separately. This project is repository-shaped for that swap; no Firebase
> code is present or needed here.

---

## Open it (Android Studio)

1. Android Studio **Ladybug or newer** (Kotlin 2.2.x support), SDK **36** installed.
2. **File → Open → `hallyu/`** folder.
3. Let Gradle sync. It will download:
   - Gradle **8.14** (per `gradle/wrapper/gradle-wrapper.properties`)
   - Kotlin **2.2.20**, Compose Multiplatform **1.11.0**, AGP **8.13.0**,
     navigation-compose **2.10.0**, Koin **4.1.0**
4. Run the **`androidApp`** configuration on any API 26+ device/emulator.

No wrapper jar is committed; Android Studio imports from the wrapper properties
(terminal users: `gradle wrapper` once, or let Studio handle it).

### If sync or compile fails

This source was written in a network-isolated sandbox and could not be compiled here.
**Paste the exact error(s) back** — the pinned versions above are the ones the code is
written against, and fixes are fast once the compiler speaks.

### Version notes

- `composeMultiplatform = 1.11.0` was pinned to stay on **compileSdk 36 / AGP 8.13**.
  If your machine has SDK 37, you may bump to `composeMultiplatform = 1.12.0`
  (+ AGP 9) — the code uses no 1.12-only APIs, so both should work.
- `iosArm64` / `iosSimulatorArm64` targets activate automatically on macOS hosts.

---

## What's implemented (all 35 mockup screens)

| Area | Screens |
|---|---|
| Auth (UI-only) | Splash · Welcome · Login · Sign up · Password recovery |
| Onboarding | Interests · Dramas · Actors · Communities · Done |
| Home | For You / Following · Now Airing · skeleton loading · error (5 taps on the HALLYU brand) · empty |
| Post | Detail · spoiler veil + reveal · threaded comments (reply, react) |
| Drama | Hub (cast, episodes, drop-day status, watch-on-network, community, reminders) · Episode page (spoiler gate · **Watched +1** · session reveal) |
| Create | Composer (category, drama, episode tagging with auto-veil) · Create community |
| Explore | Trending · Airing · Upcoming · Popular · Actors · Communities · Official accounts · Search (dramas/actors/communities/posts + no-results state) |
| People | Actor profile · Community (rules, pinned, join/pending) · Hashtag |
| Notifications | Critical/Important/Optional styling · filters · mark-all-read |
| Profile | Main (fandom stats) · Posts · Saved · Followers · Following · **Watching (Watched +1)** · Communities · Settings (privacy, spoiler policy, muted dramas) · Blocked · About |
| Spoiler system | Computed read-time from watched-through progress (never deletion) · one-time explainer banner · per-post and per-session reveal · user policy (by-progress / always / never) |

MVP additions shipped: **Drop Day airing state · Watched +1 · Top Theories (category + theory posts) · Watch-on-network row · profile fandom stats · spoiler explainer**.

## Preview world (deterministic sample data)

- Signed-in as **@hallyu_hana** (Hana Kim), through **Episode 11** of *My Bias, My Boss*
  (tvN, airing — Ep 12 out, Ep 13 drops Monday 8:50pm KST).
- 8 profiles (incl. official tvN), 10 dramas, 4 MBB episodes, 5 actors, 6 communities,
  18 posts (incl. 3 spoiler-veiled Ep 12 posts), a full comment thread, 6 notifications,
  5 trends, 4 watching entries.
- All interactions work locally: reactions, bookmarks, reposts, follows, joins,
  posting, commenting, revealing, watched +1, muting, creating a community.
- "External" things (real auth, TMDB images, streaming links, push) are labeled as
  preview no-ops with visible notes — nothing is silently dead.

## Preview hooks (dev-only)

Two gestures reach states that a real backend would drive:

- **Five taps on the "HALLYU" wordmark** (Home) → simulates a network failure, so the
  error state + retry is testable.
- **Long-press the ⚙ gear** (Home) → toggles the offline state; the
  "Offline — showing cached content" banner appears over the cached feed.

## Code map

```
hallyu/
├── shared/                          # ~85% of the app (Kotlin, commonMain)
│   └── app/hallyu/
│       ├── HallyuApp.kt             # session gate + NavHost + tab bar
│       ├── domain/Models.kt         # domain model (the future repository contract)
│       ├── data/SeedData.kt         # the preview world
│       ├── data/HallyuStore.kt      # StateFlow store + all mutations (the seam)
│       ├── navigation/Routes.kt     # routes + hallyu:// deep-link strings
│       ├── design/                  # tokens, theme, wave mark, components
│       └── ui/…                     # auth, onboarding, home, post, drama,
│                                    # explore, people, create, notifications, profile
├── androidApp/                      # shell: MainActivity (Koin injection only)
└── iosApp/README.md                 # Xcode wiring (on your Mac)
```

**Backend swap path:** replace `HallyuStore` internals with repositories over Firebase
(behind the same `state` surface) — no UI files change. `Routes.deepLink()` carries the
push/universal-link payloads already.

## Out of scope (per the spec, by design)

DMs, watch parties, events, fan-edit video, live, monetization, web, marketplace,
OST, AR, podcasts — deferred to v1.1+/v2. Moderator/admin surfaces are planned, not built.
