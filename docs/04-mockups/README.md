# HALLYU — Screen Mockups (Step 4 deliverable)

Rendered mockups for every screen in Section 37 of the Master Build Specification, plus the
three deliberate states (empty / loading / error) from Section 38. All screens use the design
system in `../03-design-system.md` — same palette, type, spacing, wave motif, tab bar, and
component language. **No lorem ipsum anywhere.**

## Shared world-state (so every screen is the same app)

- **Signed-in user:** `@hallyu_hana` (Hana Kim) — avatar: young woman in a beanie. She is
  **through Episode 11** of the hero drama (one behind the latest).
- **Hero drama:** *My Bias, My Boss* (최애의 사원) — tvN, Mon–Tue 8:50pm KST, 16 episodes.
  Romance + comedy, based on a webtoon. New employee **Nam Da-reum** (Kim Hye-jun) joins a
  fashion company run by her bias — **Kang Ha-ji**, CEO (Kang Hoon) — while idol **Lee Chan**
  (Cha Woo-min, from group **D.N.X**) is entangled in the love triangle.
  **Ep 12 ("The Confession Tape") aired Tue Sep 9, 2026 · Ep 13 airs Mon Sep 14.**
- **Also real (Sep 2026):** *A Bona Fide Killer* (MBC, crime, finale Sep 12) · *The Scandal*
  (Netflix, starts Sep 18, Son Ye-jin) · *A Love Other Than Yours* (starts Sep 12,
  Seo Kang-joon & Ahn Eun-jin).
- **Completed hits:** *Queen of Tears* (2024, Kim Soo-hyun & Kim Ji-won) · *Paradise* (2025,
  Ji Chang-wook & Bae Suzy) · *Marry My Husband* (2024) · *Lovely Runner* (2024) · *My Mister* (2018).
- **Actor page:** Kim Soo-hyun (김수현) — Queen of Tears, It's Okay to Not Be Okay, Vagabond.
- **Communities:** My Bias, My Boss Watch Party (12.8k, mod @mod_sara) · K-Romance Corner (31.4k) ·
  K-Drama Theory Lab (12.3k) · D.N.X Fans KR (41.2k) · Sageuk Society (18.9k, Hana is pending).
- **Official accounts:** tvN · Netflix KR.
- **Fans:** @sunnyoook (Sunny) · @dramaholic_ji (Jiwoo) · @seoulstorys (Minsu) · @edit_byminji (Minji)
  · @theorylab_kim (Taehyuk).
- **Hashtags:** #MyBiasMyBoss (1.2M posts, Trending #1) · #Ep12 (482k) · #Dnx (318k).
- **Signature posts (recycle across screens so they're the same posts):**
  - @sunnyoook — "he remembered her coffee order from episode 1. EPISODE ONE. i am on the floor 💔" (♥ 1,284)
  - @dramaholic_ji — "the confession tape scene in episode 12. i have rewatched it four times. the handwriting in the letter matches the one from 2019… this show is cooking 🔥" (♥ 2,148, 💬 512)
  - @theorylab_kim — "the white gloves reappear every time he's lying. count the scenes in eps 1–7." (🔥 1,502)
  - @edit_byminji — "30s edit of the rooftop scene to the ost — the rain timing is perfect 🌧" (🔥 2,041)
  - @seoulstorys — "watch party monday 8:50pm kst. bring tissues 🍿"
  - tvN (official) — "New preview for episode 13 — the confession attempt (spoiler: the rain wins). Premieres Monday 8:50pm KST."

## Index (35 files)

`00-design-system.png` — token sheet (palette, type, buttons, chips, cards, reactions, tab bar)

**Authentication:** `auth/01-splash.png` · `02-welcome.png` · `03-signup.png` · `04-login.png` · `05-recovery.png`

**Onboarding:** `onboarding/06-interests.png` · `07-dramas.png` · `08-actors.png` · `09-communities.png` · `10-completion.png`

**Main:** `main/11-home-foryou.png` · `12-home-following.png` · `13-explore.png` · `14-create.png` · `15-notifications.png` · `16-profile.png`

**Content:** `content/17-post-detail.png` · `18-comments.png` · `19-drama-hub.png` · `20-episode-page.png` · `21-actor-page.png` · `22-community-page.png` · `23-search-results.png` · `24-hashtag-page.png`

**Profile:** `profile/25-profile-posts.png` · `26-profile-saved.png` · `27-followers.png` · `28-following.png` · `29-currently-watching.png` · `30-my-communities.png` · `31-settings.png`

**States:** `states/32-empty.png` · `33-loading.png` · `34-error.png`

Moderator/admin screens are intentionally not mocked (internal tooling, permission-gated — see
`../02-architecture-plan.md` §2.8/§2.10).
