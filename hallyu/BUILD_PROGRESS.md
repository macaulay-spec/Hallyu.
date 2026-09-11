# Build progress — Hallyu KMP preview

**Status: source-complete, pending first Studio compile (owner: user's machine).**
The sandbox has no access to Gradle/Maven/Google artifact hosts, so compilation happens
on first open in Android Studio. All code has been audited for API consistency against
the design-system files it calls.

## Done

- [x] Project skeleton: settings, root build, version catalog, wrapper properties,
      gradle.properties, AndroidManifest (hallyu:// filter), themes.
- [x] `:shared` — Kotlin 2.2.20, Compose Multiplatform 1.11.0, androidTarget +
      isMac-guarded iOS targets; navigation-compose 2.10.0; koin-core (appModule only).
- [x] `:androidApp` — CMP + androidApplication plugins, activity-compose, koin-android;
      MainActivity injects `HallyuStore` and passes it to `HallyuApp(store)`.
- [x] Domain model (Models.kt) — full preview-world shape incl. `Post.communityId/actorId`.
- [x] Design system: tokens, dark M3 theme, wave logo/divider/verified seal,
      buttons/follow/chip/top-bar/tabs/progress, poster/avatar/placeholder art,
      post card (chips, reactions, spoiler veil, media), feedback (shimmer/empty/error),
      5-tab bar with raised Create.
- [x] Preview data (SeedData.kt) — the mockup world-state, 1:1 with docs/04-mockups.
- [x] HallyuStore — StateFlow state + every mutation (auth session, follows, reactions,
      bookmarks, reposts, spoiler reveal logic, watched +1, notifications, mute,
      reminders, composer, comments, community creation). ~700ms `ready` for real
      skeleton states.
- [x] All 35 mockup screens implemented (see BUILD.md table) + routing (Routes + NavHost)
      + session gate (auth → onboarding → main tabs; tab bar hidden on deep routes).
- [x] Audit pass: no mid-file imports; all `app.hallyu.*` imports verified to resolve;
      design-system call signatures verified against actual definitions;
      old Expo scaffold (node_modules, 558MB) removed from the project root.

## Review pass (against the approved mockups) — done this round

Re-audited every screen against its mockup + the locked design-system copy. Fixed:
- **Spoiler leak:** veiled posts now replace text with blurred bars (spec §9) — previously raw
  text sat next to veiled media.
- **Home:** gradient logo tile, gradient active segment, NOW AIRING header (coral dot + wave),
  title-free Now Airing posters.
- **Post card feed anatomy:** single media renders beside the copy (mockup 11); reaction bar
  shows only the post's actual reactions (♥ first).
- **Post detail:** rebuilt to mockup 17 — author row + Follow, flat layout, chips, hairline,
  share (copies hallyu:// link locally).
- **Episode page:** rebuilt to mockup 20 — full-bleed hero with overline/title/meta, gradient
  border banner with exact copy, fixed bottom composer with "Spoiler: Ep N" chip.
- **Drama hub:** rebuilt to mockup 19 — share action, overlapping poster on taller hero,
  genre/air-time chips, status pill, gradient Following, "Episode N — Title" rows, compact
  community teaser + "View all discussions →".
- **Composer:** rebuilt to mockup 14 — Cancel/New post/Post header, removable drama tag,
  suggested tags, spoiler-warning toggle with live copy, category cycle, char counter.
- **Profile:** 200dp banner, 84dp avatar overlap, chevron menu, mockup stats (214/1.2k/89,
  saved live), Edit profile pill, underline tabs, EP-N overline + bookmark marks on tiles.
- **Locked state copy:** feed empty, no-search-results (+ Try another search), watching empty,
  search placeholder — all now verbatim from docs/03 §3.6.
- Also fixed: accidental ProfileScreens truncation (repaired), missing `flow.update` import,
  Community ctor arg slip, `state.comments` → `Seed.comments`.

## Still flagged (honest — what's built, and what remains)

1. ⋯ row-menu sheet on post detail — **built**: `HallyuSheet` (design/components/Sheet.kt,
   §3.6/§3.7: 40% scrim, spring 0.72/200, 24dp top radius, 40×4 handle, 15pt rows,
   destructive last). Detail's ⋯ opens it with: reveal spoiler (when hidden), bookmark,
   repost, copy link, follow/unfollow, mute drama, and a two-step report
   (Report → Confirm; reports route to moderators server-side later).
2. "…more" expand on feed post bodies — **built**: >140 chars → 4-line ellipsis + tappable
   "…more" (PostCard, both media layouts).
3. "Reposted by you" hairline row — **built**: seeded on the meme post
   (reposterId = @hallyu_hana), rendered above the author row in PostCard.
4. Motion language — **mostly built**: spoiler blur-reveal 300ms (art blur 24→0, veil
   crossfade, pill lift — §3.7 exact), reaction pop 1→1.3→1 spring per chip, follow
   ripple 400ms brand ring, now-airing coral dot pulse (1s every 4s). **Still static:**
   pull-to-refresh wave and screen parallax (need a data source / nav-level hook) and a
   reduced-motion fallback (needs platform expect/actual — noted for the iOS pass).
5. Offline banner — **built**: `HallyuState.offline` + "Offline — showing cached content"
   pill at top of content. **Dev hook:** long-press the ⚙ gear on Home to toggle.
6. Profile grid masonry — **built**: custom `Layout` (shortest-column placement, 8dp gap,
   tiles keep their own aspect ratio, no equal-height stretching).
7. Tab bar 64dp, no iOS home-indicator inset — Android edge-to-edge handles the gesture
   bar; the inset belongs to the iOS shell pass on a Mac.

## Next (needs the user's machine)

1. **First sync + compile in Android Studio** — report any errors verbatim; iterate.
2. Polish loop after first green build:
   - Visual diff each screen against its mockup (docs/04-mockups/README index);
     flag (don't flatten) anything that ships flatter.
   - Motion: the wave divider is static; mockups 33/34 (loading/error) shimmer works.
   - Deep-link intent filtering (hallyu://post/{id} etc.) — manifest ready, router
     dispatch pending.
3. iOS shell on a Mac (iosApp/README.md).
4. Backend seam: when Firebase lands, HallyuStore internals → repositories.

## Known risks (ranked)

1. Never-compiled source → possible small syntax/type slips (audit reduced but cannot
   eliminate). Fix loop is fast; errors will be localized.
2. CMP 1.11.0 × Kotlin 2.2.20 × AGP 8.13 × SDK 36 matrix — pinned conservatively;
   fallback: CMP 1.12.0 + SDK 37 + AGP 9 (no code changes expected).
3. navigation-compose 2.10.0 multiplatform targets — first use in this project;
   fallback: 2.9.x.
