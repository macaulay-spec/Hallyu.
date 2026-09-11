package app.hallyu.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.hallyu.data.HallyuStore
import app.hallyu.design.HallyuBrand
import app.hallyu.design.HallyuColors
import app.hallyu.design.HallyuShape
import app.hallyu.design.HallyuType
import app.hallyu.design.WaveLogo
import app.hallyu.design.components.ButtonVariant
import app.hallyu.design.components.HallyuButton
import app.hallyu.design.components.BackCircle
import kotlinx.coroutines.delay

private const val PREVIEW_USER = "hallyu_hana"

@Composable
fun AuthFlow(store: HallyuStore) {
    var screen by remember { mutableStateOf("splash") }
    when (screen) {
        "splash" -> SplashScreen(onDone = { screen = "welcome" })
        "welcome" -> WelcomeScreen(onSignup = { screen = "signup" }, onLogin = { screen = "login" })
        "login" -> LoginScreen(onBack = { screen = "welcome" }, onLogin = { store.completeAuth(PREVIEW_USER) }, onSignup = { screen = "signup" }, onRecovery = { screen = "recovery" })
        "signup" -> SignupScreen(onBack = { screen = "welcome" }, onSignup = { name -> store.completeAuth(name.ifBlank { PREVIEW_USER }) })
        "recovery" -> RecoveryScreen(onBack = { screen = "login" })
    }
}

@Composable
private fun BrandRow() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        WaveLogo(size = 28.dp)
        Spacer(Modifier.width(10.dp))
        Text("HALLYU", style = HallyuType.Title)
    }
}

@Composable
private fun LabeledField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(label, style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(HallyuShape.Input))
                .background(HallyuColors.Surface2)
                .border(1.dp, HallyuColors.Line)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            SelectionContainer {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = HallyuType.Body.copy(color = if (value.isEmpty()) HallyuColors.Text3 else HallyuColors.Text1),
                    cursorBrush = SolidColor(HallyuColors.Coral),
                )
            }
            if (value.isEmpty()) {
                Text(placeholder, style = HallyuType.Body, color = HallyuColors.Text3)
            }
        }
    }
}

@Composable
private fun SocialButton(emoji: String, label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(HallyuShape.Card))
            .border(1.dp, HallyuColors.Line)
            .background(HallyuColors.Surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (emoji.isNotEmpty()) {
                Text(emoji, style = HallyuType.BodyStrong)
                Spacer(Modifier.width(8.dp))
            }
            Text(label, style = HallyuType.BodyStrong, color = HallyuColors.Text1)
        }
    }
}

@Composable
private fun PreviewNote() {
    Spacer(Modifier.height(10.dp))
    Text(
        "Preview build — signs in locally; real auth arrives with the backend.",
        style = HallyuType.Caption, color = HallyuColors.Text3, textAlign = TextAlign.Center,
    )
}

@Composable
private fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onDone()
    }
    Box(Modifier.fillMaxSize().background(HallyuColors.Ink950), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WaveLogo(size = 72.dp)
            Spacer(Modifier.height(20.dp))
            Text("HALLYU", style = HallyuType.H1)
            Spacer(Modifier.height(8.dp))
            Text("Where the Wave Lives", style = HallyuType.Small)
        }
    }
}

@Composable
private fun WelcomeScreen(onSignup: () -> Unit, onLogin: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(12.dp))
        BrandRow()
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(HallyuShape.Card))
                .background(HallyuBrand.Gradient),
            contentAlignment = Alignment.Center,
        ) {
            // Preview placeholder art — real poster art arrives with the backend.
            Text("🎬", fontSize = 64.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text("Where the Wave Lives", style = HallyuType.H1)
        Spacer(Modifier.height(8.dp))
        Text("한류 — real-time fandom for every drama and every episode.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(12.dp))
        Text(
            "Follow your biases. Watch with the world at drop time. Never get spoiled again.",
            style = HallyuType.Small, color = HallyuColors.Text3,
        )
        Spacer(Modifier.height(24.dp))
        HallyuButton(label = "Get Started", onClick = onSignup)
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(HallyuShape.Card))
                .clickable(onClick = onLogin)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("I already have an account →", style = HallyuType.BodyStrong)
        }
        Spacer(Modifier.height(10.dp))
        PreviewNote()
    }
}

@Composable
private fun LoginScreen(onBack: () -> Unit, onLogin: () -> Unit, onSignup: () -> Unit, onRecovery: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        BrandRow()
        Spacer(Modifier.height(28.dp))
        Text("Welcome back", style = HallyuType.H1)
        Spacer(Modifier.height(24.dp))
        LabeledField("Email", "you@example.com", email) { email = it }
        Spacer(Modifier.height(16.dp))
        LabeledField("Password", "••••••••", password) { password = it }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.End) {
            Text(
                "Forgot password?", style = HallyuType.Small, color = HallyuColors.Coral,
                modifier = Modifier.clickable(onClick = onRecovery),
            )
        }
        Spacer(Modifier.height(20.dp))
        HallyuButton(label = "Log In", onClick = onLogin)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("or continue with", style = HallyuType.Small)
        }
        Spacer(Modifier.height(12.dp))
        SocialButton("", "Continue with Apple", onLogin)
        Spacer(Modifier.height(10.dp))
        SocialButton("🇬", "Continue with Google", onLogin)
        Spacer(Modifier.height(16.dp))
        Text(
            "New to Hallyu? Sign up", style = HallyuType.BodyStrong, color = HallyuColors.Coral,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onSignup)
                .padding(vertical = 12.dp),
        )
        PreviewNote()
    }
}

@Composable
private fun SignupScreen(onBack: () -> Unit, onSignup: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackCircle(onClick = onBack)
            Spacer(Modifier.width(8.dp))
            BrandRow()
        }
        Spacer(Modifier.height(28.dp))
        Text("Create account", style = HallyuType.H1)
        Spacer(Modifier.height(24.dp))
        LabeledField("Display name", "Hana Kim", name) { name = it }
        Spacer(Modifier.height(16.dp))
        LabeledField("Email", "you@example.com", email) { email = it }
        Spacer(Modifier.height(16.dp))
        LabeledField("Password", "••••••••", password) { password = it }
        Spacer(Modifier.height(20.dp))
        HallyuButton(label = "Create Account", onClick = { onSignup(name.substringBefore(" ").lowercase()) })
        Spacer(Modifier.height(16.dp))
        Text(
            "I already have an account →", style = HallyuType.BodyStrong, color = HallyuColors.Coral,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onBack)
                .padding(vertical = 8.dp),
        )
        PreviewNote()
    }
}

@Composable
private fun RecoveryScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackCircle(onClick = onBack)
            Spacer(Modifier.width(8.dp))
            BrandRow()
        }
        Spacer(Modifier.height(28.dp))
        Text("Reset password", style = HallyuType.H1)
        Spacer(Modifier.height(8.dp))
        Text("We'll send a reset link to your email.", style = HallyuType.Body, color = HallyuColors.Text2)
        Spacer(Modifier.height(20.dp))
        LabeledField("Email", "you@example.com", email) { email = it }
        Spacer(Modifier.height(20.dp))
        HallyuButton(label = "Send reset link", onClick = { sent = true })
        if (sent) {
            Spacer(Modifier.height(16.dp))
            Text("✓ Reset link sent (preview).", style = HallyuType.BodyStrong, color = HallyuColors.Success)
        }
    }
}
