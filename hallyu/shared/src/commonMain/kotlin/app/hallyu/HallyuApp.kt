package app.hallyu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.hallyu.data.HallyuStore
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuTheme
import app.hallyu.design.components.BottomTabBar
import app.hallyu.navigation.Routes
import app.hallyu.ui.create.ComposerScreen
import app.hallyu.ui.create.CreateCommunityScreen
import app.hallyu.ui.drama.DramaHubScreen
import app.hallyu.ui.drama.EpisodeScreen
import app.hallyu.ui.explore.ExploreScreen
import app.hallyu.ui.home.HomeScreen
import app.hallyu.ui.notifications.NotificationsScreen
import app.hallyu.ui.onboarding.OnboardingFlow
import app.hallyu.ui.auth.AuthFlow
import app.hallyu.ui.people.ActorScreen
import app.hallyu.ui.people.CommunityScreen
import app.hallyu.ui.people.HashtagScreen
import app.hallyu.ui.post.PostDetailScreen
import app.hallyu.ui.profile.ProfileAboutScreen
import app.hallyu.ui.profile.ProfileBlockedScreen
import app.hallyu.ui.profile.ProfileCommunitiesScreen
import app.hallyu.ui.profile.ProfileFollowersScreen
import app.hallyu.ui.profile.ProfileFollowingScreen
import app.hallyu.ui.profile.ProfilePostsScreen
import app.hallyu.ui.profile.ProfileSavedScreen
import app.hallyu.ui.profile.ProfileScreen
import app.hallyu.ui.profile.ProfileSettingsScreen
import app.hallyu.ui.profile.ProfileWatchingScreen

/**
 * Root of the Hallyu client. Session gate:
 * no session → auth flow; not onboarded → onboarding; else → main tabs.
 */
@Composable
fun HallyuApp(store: HallyuStore) {
    val s by store.state.collectAsState()
    HallyuTheme {
        Surface(color = HallyuColors.Ink950) {
            Box(Modifier.fillMaxSize().background(HallyuColors.Ink950)) {
                when {
                    s.session == null -> AuthFlow(store)
                    s.session!!.onboarded -> MainTabs(store)
                    else -> OnboardingFlow(store)
                }
            }
        }
    }
}

@Composable
private fun MainTabs(store: HallyuStore) {
    val nav = rememberNavController()
    val s by store.state.collectAsState()
    val back by nav.currentBackStackEntryAsState()
    val current = back?.destination?.route

    Box(Modifier.fillMaxSize()) {
        NavHost(navController = nav, startDestination = Routes.HOME, modifier = Modifier.fillMaxSize()) {
            composable(Routes.HOME) { HomeScreen(store) { nav.navigate(it) } }
            composable(Routes.EXPLORE) { ExploreScreen(store) { nav.navigate(it) } }
            composable(Routes.CREATE) { ComposerScreen(store) { nav.navigate(it) } }
            composable(Routes.NOTIFICATIONS) { NotificationsScreen(store) }
            composable(Routes.PROFILE) { ProfileScreen(store) { nav.navigate(it) } }

            composable(Routes.POST) { b ->
                PostDetailScreen(
                    store,
                    b.arguments?.getString("postId").orEmpty(),
                    nav::popBackStack,
                ) { nav.navigate(it) }
            }
            composable(Routes.DRAMA) { b ->
                DramaHubScreen(
                    store,
                    b.arguments?.getString("dramaId").orEmpty(),
                    nav::popBackStack,
                ) { nav.navigate(it) }
            }
            composable(Routes.EPISODE) { b ->
                EpisodeScreen(
                    store,
                    b.arguments?.getString("dramaId").orEmpty(),
                    b.arguments?.getString("ep")?.toIntOrNull() ?: 1,
                    nav::popBackStack,
                ) { nav.navigate(it) }
            }
            composable(Routes.ACTOR) { b ->
                ActorScreen(store, b.arguments?.getString("actorId").orEmpty(), nav::popBackStack) { nav.navigate(it) }
            }
            composable(Routes.COMMUNITY) { b ->
                CommunityScreen(store, b.arguments?.getString("slug").orEmpty(), nav::popBackStack) { nav.navigate(it) }
            }
            composable(Routes.HASHTAG) { b ->
                HashtagScreen(store, b.arguments?.getString("slug").orEmpty(), nav::popBackStack) { nav.navigate(it) }
            }
            composable(Routes.CREATE_COMMUNITY) {
                CreateCommunityScreen(store, nav::popBackStack) { nav.navigate(it) }
            }

            composable(Routes.PROFILE_POSTS) { ProfilePostsScreen(store, { nav.navigate(it) }, nav::popBackStack) }
            composable(Routes.PROFILE_SAVED) { ProfileSavedScreen(store, { nav.navigate(it) }, nav::popBackStack) }
            composable(Routes.PROFILE_FOLLOWERS) { ProfileFollowersScreen(store, nav::popBackStack) }
            composable(Routes.PROFILE_FOLLOWING) { ProfileFollowingScreen(store, nav::popBackStack) }
            composable(Routes.PROFILE_WATCHING) { ProfileWatchingScreen(store, { nav.navigate(it) }, nav::popBackStack) }
            composable(Routes.PROFILE_COMMUNITIES) { ProfileCommunitiesScreen(store, { nav.navigate(it) }, nav::popBackStack) }
            composable(Routes.PROFILE_SETTINGS) { ProfileSettingsScreen(store, { nav.navigate(it) }, nav::popBackStack) }
            composable(Routes.PROFILE_BLOCKED) { ProfileBlockedScreen(store, nav::popBackStack) }
            composable(Routes.PROFILE_ABOUT) { ProfileAboutScreen(store, nav::popBackStack) }
        }

        if (current != null && current in Routes.TABS) {
            BottomTabBar(
                current = current,
                onNavigate = { route ->
                    nav.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                unreadNotifications = s.notificationsRead.isEmpty(),
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
