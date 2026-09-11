package app.hallyu.navigation

/** Single source of truth for routes + deep links (docs/02 §2.3). */
object Routes {
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val CREATE = "create"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    const val POST = "post/{postId}"
    const val DRAMA = "drama/{dramaId}"
    const val EPISODE = "drama/{dramaId}/ep/{ep}"
    const val ACTOR = "actor/{actorId}"
    const val COMMUNITY = "community/{slug}"
    const val HASHTAG = "hashtag/{slug}"
    const val SEARCH = "search/{query}"
    const val CREATE_COMMUNITY = "create/community"

    const val PROFILE_POSTS = "profile/me/posts"
    const val PROFILE_SAVED = "profile/me/saved"
    const val PROFILE_FOLLOWERS = "profile/me/followers"
    const val PROFILE_FOLLOWING = "profile/me/following"
    const val PROFILE_WATCHING = "profile/me/watching"
    const val PROFILE_COMMUNITIES = "profile/me/communities"
    const val PROFILE_SETTINGS = "profile/me/settings"
    const val PROFILE_BLOCKED = "profile/me/blocked"
    const val PROFILE_ABOUT = "profile/me/about"

    val TABS = listOf(HOME, EXPLORE, CREATE, NOTIFICATIONS, PROFILE)

    fun post(id: String) = "post/$id"
    fun drama(id: String) = "drama/$id"
    fun episode(dramaId: String, ep: Int) = "drama/$dramaId/ep/$ep"
    fun actor(id: String) = "actor/$id"
    fun community(slug: String) = "community/$slug"
    fun hashtag(slug: String) = "hashtag/$slug"
    fun search(query: String) = "search/${query.replace(" ", "%20")}"

    /** Deep link for a route (push payloads / universal links carry the same strings). */
    fun deepLink(route: String) = "hallyu://$route"
}
