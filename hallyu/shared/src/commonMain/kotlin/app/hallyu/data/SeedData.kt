package app.hallyu.data

import app.hallyu.domain.Actor
import app.hallyu.domain.AppNotification
import app.hallyu.domain.CastMember
import app.hallyu.domain.Comment
import app.hallyu.domain.Community
import app.hallyu.domain.Drama
import app.hallyu.domain.DramaStatus
import app.hallyu.domain.Episode
import app.hallyu.domain.Media
import app.hallyu.domain.Aspect
import app.hallyu.domain.NotificationCategory
import app.hallyu.domain.Post
import app.hallyu.domain.PostCategory
import app.hallyu.domain.Profile
import app.hallyu.domain.Reaction
import app.hallyu.domain.Trend
import app.hallyu.domain.WatchingEntry
import app.hallyu.domain.WorldState

/**
 * The preview world — identical to the approved mockups (docs/04-mockups/README.md).
 * Signed-in user @hallyu_hana (Hana Kim), through Episode 11 of My Bias, My Boss.
 * This is sample data for the frontend build; the backend (owner: product) replaces it.
 */
object Seed {

    val profiles = mapOf<String, Profile>(
        "u_hana" to Profile("u_hana", "hallyu_hana", "Hana Kim", "👩🏻", 0, "Queen of Tears main character energy. Ep 13 when?? 🎬"),
        "u_sunny" to Profile("u_sunny", "sunnyoook", "Sunny", "😄", 1),
        "u_ji" to Profile("u_ji", "dramaholic_ji", "Jiwoo", "🥹", 2),
        "u_minsu" to Profile("u_minsu", "seoulstorys", "Minsu", "🌙", 3),
        "u_minji" to Profile("u_minji", "edit_byminji", "Minji", "🎬", 4),
        "u_kim" to Profile("u_kim", "theorylab_kim", "Taehyuk", "🤓", 5),
        "u_sara" to Profile("u_sara", "mod_sara", "Sara", "🦸🏻", 6),
        "u_tvn" to Profile("u_tvn", "tvn", "tvN", "📺", 7, isOfficial = true, orgName = "tvN"),
    )

    val dramas = mapOf<String, Drama>(
        "d_mbb" to Drama(
            id = "d_mbb", title = "My Bias, My Boss", titleKo = "최애의 사원", year = 2026, network = "tvN",
            status = DramaStatus.AIRING, genres = listOf("Romance", "Comedy"), totalEpisodes = 16, currentEpisode = 12,
            airDay = "Mon–Tue", airTimeKst = "8:50pm KST", nextEpisodeLabel = "Ep 13 · Mon 8:50pm",
            posterSeed = 0, posterEmoji = "💼",
            overview = "New employee Nam Da-reum joins a fashion company led by her bias — the CEO himself — while idol Lee Chan of D.N.X complicates everything.",
        ),
        "d_bona" to Drama(
            id = "d_bona", title = "A Bona Fide Killer", titleKo = "유부녀 킬러", year = 2026, network = "MBC",
            status = DramaStatus.AIRING, genres = listOf("Crime", "Thriller"), totalEpisodes = 10, currentEpisode = 8,
            airDay = "Fri–Sat", airTimeKst = "9:20pm KST", nextEpisodeLabel = "Finale · Sep 12",
            posterSeed = 1, posterEmoji = "🔪",
        ),
        "d_scandal" to Drama(
            id = "d_scandal", title = "The Scandal", titleKo = "", year = 2026, network = "Netflix",
            status = DramaStatus.UPCOMING, genres = listOf("Thriller"), totalEpisodes = 12, currentEpisode = 0,
            nextEpisodeLabel = "Starts Sep 18", posterSeed = 2, posterEmoji = "🎭",
        ),
        "d_love" to Drama(
            id = "d_love", title = "A Love Other Than Yours", titleKo = "", year = 2026, network = "KBS2",
            status = DramaStatus.UPCOMING, genres = listOf("Romance", "Melodrama"), totalEpisodes = 16, currentEpisode = 0,
            nextEpisodeLabel = "Starts Sep 12", posterSeed = 3, posterEmoji = "🌧",
        ),
        "d_qot" to Drama(
            id = "d_qot", title = "Queen of Tears", titleKo = "눈물의 여왕", year = 2024, network = "tvN",
            status = DramaStatus.COMPLETED, genres = listOf("Romance", "Melodrama"), totalEpisodes = 16, currentEpisode = 16,
            posterSeed = 4, posterEmoji = "💍",
        ),
        "d_para" to Drama(
            id = "d_para", title = "Paradise", titleKo = "파라다이스", year = 2025, network = "tvN",
            status = DramaStatus.AIRING, genres = listOf("Action", "Thriller"), totalEpisodes = 10, currentEpisode = 4,
            airDay = "Fri", airTimeKst = "9:00pm KST", nextEpisodeLabel = "Ep 5 · Fri",
            posterSeed = 5, posterEmoji = "🌃",
        ),
        "d_mmh" to Drama(
            id = "d_mmh", title = "Marry My Husband", titleKo = "내 남편과 결혼해줘", year = 2024, network = "tvN",
            status = DramaStatus.COMPLETED, genres = listOf("Romance", "Thriller"), totalEpisodes = 16, currentEpisode = 16,
            posterSeed = 6, posterEmoji = "⚖",
        ),
        "d_lr" to Drama(
            id = "d_lr", title = "Lovely Runner", titleKo = "연인", year = 2024, network = "tvN",
            status = DramaStatus.COMPLETED, genres = listOf("Romance", "Fantasy"), totalEpisodes = 16, currentEpisode = 16,
            posterSeed = 7, posterEmoji = "🏃",
        ),
        "d_itok" to Drama(
            id = "d_itok", title = "It's Okay to Not Be Okay", titleKo = "이상한 변호사 우영우", year = 2020, network = "tvN",
            status = DramaStatus.COMPLETED, genres = listOf("Romance", "Drama"), totalEpisodes = 16, currentEpisode = 16,
            posterSeed = 8, posterEmoji = "🌾",
        ),
        "d_vagabond" to Drama(
            id = "d_vagabond", title = "Vagabond", titleKo = "배가본드", year = 2023, network = "tvN",
            status = DramaStatus.COMPLETED, genres = listOf("Action", "Thriller"), totalEpisodes = 16, currentEpisode = 16,
            posterSeed = 9, posterEmoji = "🕶",
        ),
    )

    val episodes = mapOf(
        "d_mbb" to listOf(
            Episode("e_mbb_10", "d_mbb", 10, "The First Date", "Aired Aug 25", "2.4k posts"),
            Episode("e_mbb_11", "d_mbb", 11, "The Recording Booth", "Aired Sep 8", "2.9k posts"),
            Episode("e_mbb_12", "d_mbb", 12, "The Confession Tape", "Aired Sep 9", "3.2k posts"),
            Episode("e_mbb_13", "d_mbb", 13, "", "Mon Sep 14 · 8:50pm KST", "", isUpcoming = true),
        ),
        "d_para" to listOf(
            Episode("e_para_4", "d_para", 4, "", "Aired last Fri", "1.1k posts"),
            Episode("e_para_5", "d_para", 5, "", "Fri 9:00pm KST", "", isUpcoming = true),
        ),
    )

    val cast = mapOf(
        "d_mbb" to listOf(
            CastMember("a_kanghoon", "Kang Ha-ji", isLead = true),
            CastMember("a_kimhyejun", "Nam Da-reum", isLead = true),
            CastMember("a_chawoomin", "Lee Chan", isLead = false),
        ),
        "d_qot" to listOf(CastMember("a_kimsoohyun", "Baek Hyun-woo", isLead = true)),
        "d_para" to listOf(CastMember("a_baesuzy", "Lee Se-kyung", isLead = false)),
    )

    val actors = mapOf(
        "a_kimsoohyun" to Actor("a_kimsoohyun", "Kim Soo-hyun", "김수현", "🏻", 10, "8.1M", listOf("d_qot|Baek Hyun-woo|2024", "d_itok|Moon Gang-tae|2020", "d_vagabond|Hong Si-oo|2023")),
        "a_kimhyejun" to Actor("a_kimhyejun", "Kim Hye-jun", "김혜준", "👩", 11, "1.9M", listOf("d_mbb|Nam Da-reum|2026")),
        "a_kanghoon" to Actor("a_kanghoon", "Kang Hoon", "김강훈", "👨", 12, "2.4M", listOf("d_mbb|Kang Ha-ji|2026")),
        "a_chawoomin" to Actor("a_chawoomin", "Cha Woo-min", "차우민", "🧑🏻", 13, "3.1M", listOf("d_mbb|Lee Chan|2026")),
        "a_baesuzy" to Actor("a_baesuzy", "Bae Suzy", "배수지", "👩", 14, "8.7M", listOf("d_para|Lee Se-kyung|2025")),
    )

    val communities = mapOf(
        "c_watch" to Community(
            id = "c_watch", slug = "watch-party", name = "My Bias, My Boss Watch Party",
            description = "Live watch parties, episode threads, and emotional support after every reveal.",
            membersLabel = "12.8k", avatarSeed = 0, visibility = "Public", createdBy = "mod_sara",
            rules = listOf("No spoilers before 8:50pm KST", "Keep it kind — the finale stretch is emotional"),
        ),
        "c_romance" to Community("c_romance", "k-romance", "K-Romance Corner", "Soft launches, big feelings, and every slow-burn ranked.", "31.4k", 1),
        "c_theory" to Community("c_theory", "theory-lab", "K-Drama Theory Lab", "Frame-by-frame analysis. Bring evidence.", "12.3k", 2),
        "c_dnx" to Community("c_dnx", "dnx-fans", "D.N.X Fans KR", "Everything D.N.X — Comebacks, fancams, and MBB appearances.", "41.2k", 3),
        "c_sageuk" to Community("c_sageuk", "sageuk", "Sageuk Society", "Historical dramas, hanbok appreciation, dynasty debates.", "18.9k", 4),
        "c_meme" to Community("c_meme", "memes", "K-Drama Memes", "If it hurts, it's funny.", "27.6k", 5),
    )

    val posts = mapOf(
        "p_coffee" to Post(
            "p_coffee", "u_sunny", "he remembered her coffee order from episode 1. EPISODE ONE. i am on the floor 💔",
            PostCategory.REACTION, "d_mbb", 12, null,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(21), timeLabel = "2h",
            reactions = mapOf(Reaction.LIKE to 1284, Reaction.CRYING to 892), commentCount = 342, repostCount = 96,
            hashtag = "#MyBiasMyBoss",
        ),
        "p_letter" to Post(
            "p_letter", "u_ji", "the confession tape scene in episode 12. i have rewatched it four times. the handwriting in the letter matches the one from 2019. it matches. this show is cooking 🔥",
            PostCategory.THEORY, "d_mbb", 12, null,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(22), timeLabel = "2h",
            reactions = mapOf(Reaction.LIKE to 2148, Reaction.CRYING to 940, Reaction.CRUSH to 410),
            commentCount = 512, repostCount = 234, hashtag = "#MyBiasMyBoss",
        ),
        "p_gloves" to Post(
            "p_gloves", "u_kim", "count the scenes in eps 1–7 where the white gloves reappear. every single time he's lying. i'll wait 🎩",
            PostCategory.THEORY, "d_mbb", null, null,
            media = List(3) { Media(aspect = Aspect.PORTRAIT_4_5) }, mediaSeeds = listOf(23, 24, 25), timeLabel = "1h",
            reactions = mapOf(Reaction.FIRE to 1502, Reaction.LIKE to 830), commentCount = 233, repostCount = 118,
            hashtag = "#Ep12",
        ),
        "p_edit" to Post(
            "p_edit", "u_minji", "30s edit of the rooftop scene to the ost — the rain timing is perfect 🌧",
            PostCategory.FAN_CONTENT, "d_mbb", 12, null,
            media = listOf(Media(aspect = Aspect.WIDE_16_9, isVideo = true)), mediaSeeds = listOf(26), timeLabel = "3h",
            reactions = mapOf(Reaction.FIRE to 2041, Reaction.CLAP to 566), commentCount = 189, repostCount = 97,
            hashtag = "#MyBiasMyBoss",
        ),
        "p_watchparty" to Post(
            "p_watchparty", "u_minsu", "watch party monday 8:50pm kst. bring tissues 🍿",
            PostCategory.DISCUSSION, null, null, null, timeLabel = "7h",
            reactions = mapOf(Reaction.LIKE to 96), commentCount = 41, repostCount = 18, communityId = "c_watch",
        ),
        "p_official" to Post(
            "p_official", "u_tvn", "📢 New preview for episode 13 — the confession attempt (spoiler: the rain wins). Premieres Monday 8:50pm KST.",
            PostCategory.NEWS, "d_mbb", null, null,
            media = listOf(Media(aspect = Aspect.WIDE_16_9)), mediaSeeds = listOf(27), timeLabel = "6h",
            reactions = mapOf(Reaction.CLAP to 862, Reaction.LIKE to 1204), commentCount = 412, repostCount = 156,
            hashtag = "#MyBiasMyBoss",
        ),
        "p_meme" to Post(
            "p_meme", "u_minsu", "no thoughts, just feelings",
            PostCategory.MEME, null, null, null,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(28), timeLabel = "8h",
            reactions = mapOf(Reaction.LIKE to 1502, Reaction.CRYING to 640), commentCount = 128, repostCount = 74,
            reposterId = "u_hana",
            communityId = "c_watch",
        ),
        "p_countdown" to Post(
            "p_countdown", "u_ji", "counting down to the finale and the show KNOWS",
            PostCategory.REACTION, "d_mbb", null, null, timeLabel = "4h",
            reactions = mapOf(Reaction.LIKE to 284, Reaction.CRUSH to 96), commentCount = 12, communityId = "c_watch",
        ),
        "p_pinned" to Post(
            "p_pinned", "u_sara", "📌 Watch party Monday — Episode 13 at 8:50pm KST. Join the thread when it airs. Be kind: this is the finale stretch.",
            PostCategory.DISCUSSION, "d_mbb", null, null, timeLabel = "1d",
            reactions = mapOf(Reaction.LIKE to 1042), commentCount = 220, isPinned = true, pinnedBy = "mod_sara",
            communityId = "c_watch",
        ),
        "p_qot" to Post(
            "p_qot", "u_minji", "the wedding scene in QOT still gives me chills. soohyun's hands alone are a film.",
            PostCategory.FAN_CONTENT, "d_qot", null, null,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(29), timeLabel = "6h",
            reactions = mapOf(Reaction.LIKE to 942, Reaction.CRUSH to 510), commentCount = 88, repostCount = 41,
            actorId = "a_kimsoohyun",
        ),
        "p_vagabond" to Post(
            "p_vagabond", "u_minsu", "who else has watched Vagabond three times. the final chase is perfect cinema 🎬",
            PostCategory.RECOMMENDATION, "d_vagabond", null, null, timeLabel = "1d",
            reactions = mapOf(Reaction.LIKE to 618, Reaction.CLAP to 204), commentCount = 37, repostCount = 12,
            actorId = "a_kimsoohyun",
        ),
        "p_father" to Post(
            "p_father", "u_ji", "anyone else have a feeling about the father's secret or am i overthinking? 👀",
            PostCategory.QUESTION, "d_mbb", null, null, timeLabel = "18m",
            reactions = mapOf(Reaction.LIKE to 214, Reaction.CRYING to 66), commentCount = 89,
        ),
        "p_analysis" to Post(
            "p_analysis", "u_kim", "the letter scene analysis. the handwriting match, frame by frame.",
            PostCategory.DISCUSSION, "d_mbb", 12, 12,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(30), timeLabel = "2d",
            reactions = mapOf(Reaction.LIKE to 342, Reaction.FIRE to 120), commentCount = 41, repostCount = 8,
        ),
        // Episode 12 discussion (spoiler-gated)
        "p_veil1" to Post(
            "p_veil1", "u_sunny", "the flashback changes everything. the recording booth. it was always the recording booth.",
            PostCategory.REACTION, "d_mbb", 12, 12,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(31), timeLabel = "1h",
            reactions = mapOf(Reaction.LIKE to 412, Reaction.CRYING to 180), commentCount = 23,
        ),
        "p_veil2" to Post(
            "p_veil2", "u_ji", "the letter is from chan's manager. prove me wrong",
            PostCategory.THEORY, "d_mbb", 12, 12, timeLabel = "28m",
            reactions = mapOf(Reaction.LIKE to 289), commentCount = 17,
        ),
        "p_veil3" to Post(
            "p_veil3", "u_kim", "full breakdown: the tape was recorded twice. here's the frame evidence.",
            PostCategory.THEORY, "d_mbb", 12, 12,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(32), timeLabel = "10m",
            reactions = mapOf(Reaction.FIRE to 1502, Reaction.LIKE to 340), commentCount = 88,
        ),
        // Signed-in user's posts
        "p_mine1" to Post(
            "p_mine1", "u_hana", "the confession tape changes everything. i'm not the same after that scene 💔",
            PostCategory.REACTION, "d_mbb", 12, null, timeLabel = "1h",
            reactions = mapOf(Reaction.LIKE to 342), commentCount = 21,
        ),
        "p_mine2" to Post(
            "p_mine2", "u_hana", "watch party monday. bring tissues 🍿",
            PostCategory.DISCUSSION, "d_mbb", null, null, timeLabel = "3h",
            reactions = mapOf(Reaction.LIKE to 96), commentCount = 8,
        ),
        "p_mine3" to Post(
            "p_mine3", "u_hana", "every. single. time.",
            PostCategory.MEME, null, null, null,
            media = listOf(Media(aspect = Aspect.PORTRAIT_4_5)), mediaSeeds = listOf(33), timeLabel = "5h",
            reactions = mapOf(Reaction.LIKE to 210, Reaction.CRYING to 44), commentCount = 12,
        ),
    )

    val feedForYou = listOf("p_coffee", "p_edit", "p_official", "p_gloves", "p_meme")
    val feedFollowing = listOf("p_father", "p_gloves", "p_watchparty")
    val episodeDiscussion = mapOf("e_mbb_12" to listOf("p_veil1", "p_veil2", "p_veil3"))

    val comments = mapOf(
        "p_letter" to listOf(
            Comment("c1", "p_letter", null, "u_kim", "i wrote the full breakdown in the community", "45m", mapOf(Reaction.LIKE to 1208), 0),
            Comment("c2", "p_letter", "c1", "u_sunny", "sending you a screenshot of the 2019 photo rn", "32m", mapOf(Reaction.LIKE to 96), 1),
            Comment("c3", "p_letter", "c2", "u_ji", "he's not wrong. this is the best comment in this thread", "18m", mapOf(Reaction.LIKE to 312), 2),
            Comment("c4", "p_letter", null, "u_minsu", "the rain in that scene is not rain. it's my tears", "28m", mapOf(Reaction.LIKE to 208), 0),
            Comment("c5", "p_letter", "c4", "u_minji", "can confirm. i have been a rain cloud all week", "12m", mapOf(Reaction.LIKE to 77), 1),
            Comment("c6", "p_letter", null, "u_sunny", "the handwriting match is the scariest part tbh", "1h", mapOf(Reaction.LIKE to 84), 0),
            Comment("c7", "p_letter", null, "u_minsu", "wait did anyone rewatch the recording booth scene?? i'm losing my mind", "45m", mapOf(Reaction.LIKE to 51), 0),
        ),
    )

    val notifications = listOf(
        AppNotification("n1", NotificationCategory.CRITICAL, "My Bias, My Boss · Episode 12", "Just aired — join the episode discussion", "2d", icon = "📺", hasThumbnail = true, thumbnailSeed = 0),
        AppNotification("n2", NotificationCategory.CRITICAL, "@sunnyoook replied to your comment", "the white gloves theory is REAL okay", "1h", actorId = "u_sunny", icon = "💬"),
        AppNotification("n3", NotificationCategory.CRITICAL, "You were mentioned by @theorylab_kim", "your booth theory was right — see my post", "3h", actorId = "u_kim", icon = "@"),
        AppNotification("n4", NotificationCategory.IMPORTANT, "tvN", "New preview: Episode 13", "6h", actorId = "u_tvn", icon = "📺"),
        AppNotification("n5", NotificationCategory.IMPORTANT, "My Bias, My Boss Watch Party", "Watch party Monday 8:50pm KST", "1d", icon = "🎬"),
        AppNotification("n6", NotificationCategory.OPTIONAL, "Trending for you", "#Ep12 is gaining momentum · 482k posts", "1d", icon = "✦"),
    )

    val trends = listOf(
        Trend(1, "#MyBiasMyBoss", "+342%", "1.2M posts"),
        Trend(2, "#Ep12", "+128%", "482k posts"),
        Trend(3, "#Dnx", "+96%", "318k posts"),
        Trend(4, "#TheScandal", "+74%", "96k posts"),
        Trend(5, "#KdramaFinds", "+41%", "203k posts"),
    )

    val watching = mapOf(
        "d_mbb" to WatchingEntry("d_mbb", "watching", 11, "updated 2h ago"),
        "d_para" to WatchingEntry("d_para", "watching", 4, "Ep 5 airs Friday"),
        "d_bona" to WatchingEntry("d_bona", "watching", 8, "finale Sep 12"),
        "d_qot" to WatchingEntry("d_qot", "completed", 16, "2024"),
    )

    val bookmarks = listOf("p_analysis", "p_edit", "p_meme")

    val followers = listOf("u_sunny" to "Mutual", "u_ji" to "New", "u_minsu" to "Mutual", "u_minji" to "New", "u_kim" to "", "u_sara" to "Mutual")
    val blockedUsers = listOf("u_spam1", "u_spam2", "u_spam3", "u_spam4")

    val world: WorldState = WorldState(
        profiles = profiles,
        dramas = dramas,
        episodes = episodes,
        cast = cast,
        actors = actors,
        communities = communities,
        posts = posts,
        feedForYou = feedForYou,
        feedFollowing = feedFollowing,
        comments = comments,
        notifications = notifications,
        trends = trends,
        watching = watching,
        bookmarks = bookmarks,
    )
}
