# HALLYU — Step 3: Design System ("The Wave")

> Gate deliverable. This is the contract for every screen mockup (Step 4) and every screen
> implemented in Phase 0+. Original by design — §35 forbids copying Apple/X/TikTok/Netflix/Reddit.
> Target: Apple's **level of craft and restraint**, not Apple's look. The system is specific to
> K-drama culture: cinematic darkness, the 2:3 poster as a sacred ratio, fandom emotion as color.

## 3.1 Concept — "Where the Wave Lives"

한류 (Hallyu) literally means *Korean Wave*. The brand signature is a single
**sine-wave line** that recurs as the only ornament in the system: the logo, dividers,
the "Now Airing" hairline, progress, loading, and the empty-state illustration. One motif,
used sparingly, at low contrast — identity without decoration. Everything else is flat, dark,
and quiet, so **drama imagery (2:3 posters, 4:5 stills) and fan text carry the visual energy**.
The interface never competes with content (§36.1: content first).

## 3.2 Color

Base: near-black with a violet undertone (cinema dark, not pure black — pure black reads OLED
test-pattern; the violet undertone is the brand). Brand gradient used **only** on: primary
buttons, active tab, follow state, progress bars, the logo, and one hairline per "airing" card.
Never on backgrounds, never on card fills (§35A).

| Token | Hex | Use |
|---|---|---|
| `ink-950` | `#0C0B10` | App background (all screens) |
| `ink-900` | `#121116` | Alt background (tab bar base, header blur base) |
| `surface` | `#15141A` | Cards, chips-base, sheets |
| `surface-2` | `#1D1B24` | Raised: inputs, pressed states, skeletons |
| `line` | `#2A2833` | 1px borders, dividers |
| `text-1` | `#F4F2F7` | Primary text (~15:1 on ink-950) |
| `text-2` | `#A8A5B3` | Secondary text, metadata (~7:1) |
| `text-3` | `#6F6C7C` | Tertiary, placeholders (~4.2:1 — 13pt metadata only) |
| `brand-violet` | `#4A1C6E` | Gradient start |
| `brand-blue` | `#2D6CDF` | Gradient end |
| `brand-grad` | `linear 135° #4A1C6E → #2D6CDF` | Buttons, active tab, logo, progress |
| `accent-coral` | `#FF6B6B` | Likes, "now airing" dot, live pulse, unread dots |
| `success` | `#3DD68C` | Joined/followed confirmation, verified-on-green |
| `warning` | `#FFB454` | Pending, on-hold, quiet-hours |
| `danger` | `#FF5470` | Delete, ban, report, error |
| `info` | `#4DA3FF` | Links, info states |

**Reaction semantics (A-4)** — five reactions, each with its own hue (the "semantic colors for
reactions" the gate requires); reaction colors are the *most colorful pixels on screen*:

| Reaction | Icon | Color | Fandom meaning |
|---|---|---|---|
| like | ♥ | `#FF6B6B` coral | agree / good |
| crush | 😍 | `#FF8FB1` pink | ship it / the lead / a scene |
| crying | 😭 | `#6FA8FF` sky | this destroyed me (the K-drama essential) |
| fire | 🔥 | `#FF9F45` amber | hype, masterpiece |
| clap | 👏 | `#9D7BFF` violet | respect, craft, OST, cinematography |

## 3.3 Typography

- **UI face: Inter** (variable, 400/500/600/700) — per §35A baseline.
- **Display treatment:** Inter 700 with tight tracking (−0.02em) for drama titles and hero
  headlines, paired with the **Korean title (한글) rendered as a quiet sub-line** — this is the
  "distinctive Korean-inspired display treatment used sparingly": the hanja never leads, it
  annotates. The logo pairs wordmark `HALLYU` with `한류` and the wave line.
- Korean text (titles, hanja) uses the system Korean fallback stack (Apple SD Gothic Neo /
  Noto Sans KR) at the same size — it's content, not decoration.

| Token | Size/weight | Use |
|---|---|---|
| `caption` | 11/600, ls +0.01em | Timestamps, counts, badges |
| `small` | 13/400 | Metadata, chips, secondary lines |
| `body` | 15/400, lh 1.45 | Post text, bios, comments |
| `body-strong` | 15/600 | Usernames, labels |
| `title` | 20/700, ls −0.01em | Card titles, drama titles in lists |
| `heading-2` | 24/700, ls −0.02em | Screen titles (Explore, Drama hub) |
| `heading-1` | 30/700, ls −0.02em | Hero titles (welcome, onboarding) |
| `hero` | 40/700, ls −0.03em | Splash/welcome display only |

**Copy voice:** warm, fandom-fluent, plain. Never corporate, never sassy-at-the-user.
Empty states speak to the fan ("Your fandom is quiet here…"), never to the user ("No data").

## 3.4 Spacing, grid, layout

- **Base unit 4pt.** Scale: 4 · 8 · 12 · 16 · 24 · 32 · 48.
- **Screen gutter:** 16pt. **Card internal padding:** 16pt (per §35A). **Card-to-card (feed):** 8pt.
  **Section spacing:** 24pt. **List row height:** 56–72pt.
- **Canvas:** designed at 390×844pt (iPhone-class), safe-area aware: content top 54pt (below
  status bar), bottom 78pt (above tab bar).
- **The 2:3 law:** K-drama posters are 2:3 everywhere (carousels, hub, watching list, search).
  Still images in posts: **4:5** (portrait, poster-adjacent) or 16:9 for wide shots. The grid is
  poster-native — carousels advance by one 2:3 card, which is the system's distinctive rhythm.
- **Thumb reach (§36.2):** all primary actions live in the bottom third or in the top-right
  100pt zone (follow button). Tab bar, composer bar, and the raised Create button are the three
  permanent thumb anchors. Long-press is always paired with a visible menu alternative (§36.3
  research detail: visible gesture alternatives).

## 3.5 Iconography

- **Style:** custom line icons, 24px grid, **1.8px stroke, rounded caps/joins**, single weight.
  No pictogram mashups; each icon is one continuous idea.
- **The wave** is the app icon's subject and the Home tab's icon — a single sine stroke.
- **Tab bar icons:** Home = wave line · Explore = four-point spark (discovery, not a magnifier —
  search is the search field, not a tab) · Create = plus inside a circle (raised, gradient) ·
  Notifications = bell (a wave stroke replaces the clapper arc) · Profile = person in a circle.
  Active = **gradient fill + text-1 label**; inactive = text-2.
- **Status icons:** verified = 6-point **wave seal** (gradient) — not a checkmark badge;
  "official" chip accompanies it. Live/airing = coral dot with one expanding ring (one pulse,
  then static — calm, not alarming).
- All icons 44pt touch targets minimum, icon glyphs 24px.

## 3.6 Component language

### Buttons
| Variant | Spec |
|---|---|
| **Primary** | brand-grad fill, white 15/600, 48pt tall, full pill (999 radius), subtle 20% inner top-light. Pressed: 8% darken + scale 0.98. |
| **Secondary** | surface-2 fill, 1px line border, text-1, 44pt, 12 radius. |
| **Ghost** | text-2, 44pt hit, no fill. Pressed: 8% white bg. |
| **Icon** | 44pt circle, surface-2 on hover/press, line border optional. |
| **Follow (stateful)** | Follow = primary pill, 40pt tall, "＋ Follow". Following = surface-2 pill "✓ Following" in success — the state change is the reward animation (§3.10). |
| **Destructive** | danger text (list rows) or danger fill (confirm sheets). Always two-step (sheet confirm). |

### Cards
- **Base card:** surface fill, 1px line, 12 radius (per §35A), 16 padding. No drop shadows —
  separation comes from fill difference + border (restraint, not elevation).
- **Airing card:** base + 2px brand-grad hairline along the top edge + coral "NOW" dot beside the
  airing time. Used on Now Airing module, hub header, episode list active row.
- **Post card anatomy (the workhorse, §51):**
  ```
  [avatar 40]  @username  ·  2h            [drama chip: 2:3 thumb 16×24 + "My Bias, My Boss · Ep 12"]  [⋯]
  body text (15pt, 2–4 lines; "…more" to expand in feed)
  [4:5 image, 12 radius, spoiler veil if flagged]
  [♥ 1,284] [💬 342] [↻ 96] [🔖]                    ← reaction row, counts text-2, icons 20px
  ```
  Official posts: wave-seal + "Official" chip after the name. Reposts: "Reposted by you —" hairline
  row above the original author row.
- **Drama chip:** the system's signature affordance — a 16×24px 2:3 poster thumbnail + drama title
  + optional "· Ep N". Tappable → drama hub. This is how "a post about an episode should know
  which drama and episode it belongs to" (§36.6) becomes one component.

### Chips & tags
- 8 radius, 32pt tall, 13pt text, surface-2 fill, line border, text-2; selected: brand-grad border
  + text-1. Genre chips on Explore; category chips on posts (🧠 Theory, 📰 News, ❓ Question —
  emoji + word, 11pt caption style, text-3).

### Inputs
- Field: surface-2 fill, 12 radius, 48pt, 15pt text-1, placeholder text-3; label 13pt text-2
  above; error: danger border + 13pt danger message. Composer: full-bleed, 20pt line-height,
  auto-growing; no character counter under 80% of 5,000.
- Search field: full-width pill, surface-2, spark-less magnifier icon 20px, "Search dramas,
  actors, fans…" placeholder, 52pt.

### Tab bar
- 58pt + home indicator, ink-900 at 92% + blur, 1px line top. 5 items, order per §4:
  Home · Explore · **Create (raised)** · Notifications · Profile.
- **Create:** 54pt gradient circle raised 14pt above the bar, soft shadow — the app's only
  shadowed element. Unread notification dot: coral, 8pt, top-right of bell.

### Avatars
- Circle 40 (feed) / 32 (comments) / 24 (notification rows). No default gray face — un-set avatar
  = monogram (initials, text-1 on surface-2). Community avatars = 12-radius squares.
- Gradient ring (1.5pt) marks: verified official, or "live now" in a watch-party thread (v1.1).

### Spoiler veil (§9 — the signature interaction)
- Veiled media/text: **Gaussian blur 24pt + brand-grad veil at 35% opacity** over the media, and
  the text itself is replaced by blurred bars in the same line count.
- Center pill: "⚠ Spoiler · My Bias, My Boss · Episode 12" (13/600) + "Tap to reveal" (11, text-2).
- Reveal: blur 24→0 in 300ms ease-out, veil lifts upward. "Reveal for this session" and
  "Always reveal this drama" persist (client-side — content is never withheld from the user,
  only their *casual exposure* is managed).
- Episode page banner: "⚠ Spoilers for Episode 12 — you're through Episode 11" with two actions:
  **Reveal for session** (secondary) / **I watched Ep 12 ✓** (primary — updates
  `watched_through`, un-veils the page, §9's three entry points).

### Reaction bar & picker
- Row: like ♥ (coral when liked), count; comment 💬; repost ; bookmark 🔖 — icons 20px,
  44pt targets, counts 13pt text-2. Long-press (or tap the ♥ when already liked) opens the
  **reaction picker**: a wave-arc sheet — five reaction chips rising on a sine arc, each its
  semantic color, spring-in with 40ms stagger.

### States (per §38 — deliberate, never blank)
- **Loading:** skeleton cards — surface-2 blocks matching the card anatomy (avatar circle, 2 text
  bars, 4:5 block) with a 15%-white **wave shimmer** sweeping left→right, 1.2s loop.
- **Empty:** a **wave-line illustration** (2–3 sine strokes, brand-grad at 60% opacity, 96px) +
  heading-2 + body copy in product voice + one primary action. Copy examples (locked):
  - No posts: *"Your fandom is quiet here. Follow a few dramas or communities to get things moving."* + [Explore dramas]
  - No search: *"No matching dramas, actors, users, or communities found."* + [Try another search]
  - Notifications: *"You're caught up."* (no button — the good kind of empty)
  - Watching: *"You're not watching anything yet. Pick a drama and we'll keep your spoilers safe."*
- **Error:** the wave with a single broken segment (gap + small danger x) + *"Couldn't load this
  right now."* + body (network hint) + [Retry] primary + ghost "Report a problem". Retry re-fires
  the query; 3 failures → error persists with a "check your connection" line.
- **Offline banner:** 28pt pill top of content, surface-2, "Offline — showing cached content"
  (warning icon).

### Sheets, toasts, menus
- **Bottom sheet:** 24 top radius, 36pt drag handle (40×4 surface-2), ink-900 96% + blur, max
  85% height. Used for: spoiler controls, post options, follow confirm, watch-party join.
- **Toast:** 44pt pill, surface-2, bottom 84pt, 13pt text-1, 2.5s, one at a time.
- **Row menu (⋯):** sheet, 15pt rows, destructive last, always labeled (no icon-only actions).

## 3.7 Motion language

| Moment | Motion |
|---|---|
| Screen push/pop | 250ms ease-out slide + 4% source parallax; pop reverses |
| Tab switch | 180ms crossfade (no slide — tabs are places, not pages) |
| Press | scale 0.98, 120ms, spring back |
| Follow | button morphs to "✓ Following" + a **wave ripple** expands once from the button (400ms, brand-grad at 30%) |
| Reaction | chip scales 1→1.3→1 (300ms spring); count ticks |
| Spoiler reveal | blur 24→0, 300ms ease-out; veil lifts up 12pt |
| Loading (infinite) | wave-draw: two sine strokes tracing in a 1.6s loop, brand-grad |
| Pull-to-refresh | wave crest rises and breaks into the feed |
| Episode "now airing" | coral dot pulses once per 4s (calm) |
| Sheet in/out | spring (damping 26, stiffness 200), dim 40% |

**Reduced motion:** all transitions collapse to 80ms opacity; shimmer and ripples become static.
Respects the system setting (a11y, §32). No autoplaying motion on content (posters never animate
unless the user starts it — v1.1 video only).

## 3.8 Screen frame conventions (all mockups + build)

- Status bar: `9:41`, dark icons on the ink background.
- Safe area respected; no content under the status bar or in the bottom 78pt (tab-bar zone).
- Max text line length ≈ 34em; post bodies wrap, never ellipsize mid-word.
- Hairlines are always `line` at 1px; the only gradient hairline is the airing one.

## 3.9 Accessibility (part of the system, §32)

- Contrast: all text token pairs ≥ 4.5:1 on their surfaces (text-3 reserved for 13pt metadata on
  ink-950 only). Reaction colors always paired with icon + label (never color-only meaning).
- Touch targets ≥ 44pt. Dynamic type up to +30%: cards reflow, chips wrap, the drama chip stays
  single-line with the thumbnail (title truncates with "…", full name in a11y label).
- VoiceOver/TalkBack labels: spoiler veil = "Spoiler content for Episode 12. Double tap to
  reveal."; wave logo = "Hallyu"; follow button states announced.
- Captions/descriptive media: `post_media.alt_text` required on upload (suggested by AI,
  editable — §19), rendered as a11y label and long-press "Describe image".

## 3.10 Screen inventory (Step 4 deliverable → files)

All under `docs/04-mockups/`. Consistent world-state across every screen (see `README` in that
folder): hero drama **My Bias, My Boss** (최애의 사원, tvN, airing — Ep 12 aired Sep 9 2026,
Ep 13 airs Sep 14; cast Kang Hoon / Kim Hye-jun / Cha Woo-min), the signed-in user **@hallyu_hana
(Hana Kim)** who is **through Episode 11**.

| # | Screen | File | Group |
|---|---|---|---|
| 0 | Design system sheet | `00-design-system.png` | system |
| 1 | Splash | `auth/01-splash.png` | Authentication |
| 2 | Welcome | `auth/02-welcome.png` | Authentication |
| 3 | Sign up | `auth/03-signup.png` | Authentication |
| 4 | Login | `auth/04-login.png` | Authentication |
| 5 | Account recovery | `auth/05-recovery.png` | Authentication |
| 6 | Onboarding: interests | `onboarding/06-interests.png` | Onboarding |
| 7 | Onboarding: dramas | `onboarding/07-dramas.png` | Onboarding |
| 8 | Onboarding: actors | `onboarding/08-actors.png` | Onboarding |
| 9 | Onboarding: communities/accounts | `onboarding/09-communities.png` | Onboarding |
| 10 | Onboarding: completion | `onboarding/10-completion.png` | Onboarding |
| 11 | Home — For You | `main/11-home-foryou.png` | Main |
| 12 | Home — Following | `main/12-home-following.png` | Main |
| 13 | Explore | `main/13-explore.png` | Main |
| 14 | Create | `main/14-create.png` | Main |
| 15 | Notifications | `main/15-notifications.png` | Main |
| 16 | Profile (self) | `main/16-profile.png` | Main |
| 17 | Post detail | `content/17-post-detail.png` | Content |
| 18 | Comments (thread view) | `content/18-comments.png` | Content |
| 19 | Drama hub | `content/19-drama-hub.png` | Content |
| 20 | Episode page (spoiler-gated) | `content/20-episode-page.png` | Content |
| 21 | Actor page | `content/21-actor-page.png` | Content |
| 22 | Community page | `content/22-community-page.png` | Content |
| 23 | Search results | `content/23-search-results.png` | Content |
| 24 | Hashtag page | `content/24-hashtag-page.png` | Content |
| 25 | Profile → Posts | `profile/25-profile-posts.png` | Profile |
| 26 | Profile → Saved | `profile/26-profile-saved.png` | Profile |
| 27 | Profile → Followers | `profile/27-followers.png` | Profile |
| 28 | Profile → Following | `profile/28-following.png` | Profile |
| 29 | Currently Watching | `profile/29-currently-watching.png` | Profile |
| 30 | My Communities | `profile/30-my-communities.png` | Profile |
| 31 | Settings | `profile/31-settings.png` | Profile |
| 32 | State: empty | `states/32-empty.png` | States |
| 33 | State: loading | `states/33-loading.png` | States |
| 34 | State: error | `states/34-error.png` | States |

Moderator/admin screens: deliberately **no mockups** — internal tooling, permission-gated,
per the gate (they're in the architecture: `admin/queue`, report/appeal flows).
