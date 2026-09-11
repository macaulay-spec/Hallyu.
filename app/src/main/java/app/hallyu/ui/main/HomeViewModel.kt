package app.hallyu.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.hallyu.data.model.Post
import app.hallyu.data.model.User
import app.hallyu.data.repository.AuthRepository
import app.hallyu.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds: StateFlow<Set<String>> = _likedPostIds

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            _posts.value = feedRepository.getFeed()
            _isLoading.value = false
        }
    }

    fun setFilter(dramaTitle: String) {
        _selectedFilter.value = dramaTitle
    }

    fun createPost(dramaTitle: String, content: String, hasSpoiler: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = currentUser.value
            val authorId = user?.uid ?: "user_${System.currentTimeMillis() % 1000}"
            val authorName = user?.username?.ifBlank { null }
                ?: user?.email?.substringBefore("@")
                ?: "K-Drama Fan"

            val result = feedRepository.createPost(
                authorId = authorId,
                authorName = authorName,
                dramaTitle = dramaTitle,
                content = content,
                hasSpoiler = hasSpoiler
            )

            result.onSuccess { newPost ->
                _posts.value = listOf(newPost) + _posts.value
                onComplete()
            }
        }
    }

    fun toggleLike(post: Post) {
        val currentSet = _likedPostIds.value
        val isLiked = currentSet.contains(post.id)
        val newSet = if (isLiked) currentSet - post.id else currentSet + post.id
        _likedPostIds.value = newSet

        val delta = if (isLiked) -1 else 1
        _posts.value = _posts.value.map {
            if (it.id == post.id) it.copy(likesCount = maxOf(0, it.likesCount + delta)) else it
        }

        viewModelScope.launch {
            feedRepository.toggleLike(post.id, !isLiked)
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        authRepository.signOut()
        onSignedOut()
    }
}
