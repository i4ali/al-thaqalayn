package com.thaqalayn.app.ui.onboarding

// Onboarding pages 12-13 (iOS PersonalizeScreen + FinalScreen).
// Personalize: display name, saved straight to UserProfileManager (iOS
// PersonalizeScreen; the language picker left with the English-only UI).
// Final: adapted for Android's local-only build - no Supabase, so the iOS
// Create Account / Sign In screen becomes a single "Begin" send-off.

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thaqalayn.app.settings.UserProfileManager
import com.thaqalayn.app.ui.components.pressable

private const val MAX_NAME_LENGTH = 30

// MARK: - Page 12: Personalize

@Composable
fun PersonalizePage(onContinue: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FadeRise(visible = isVisible, delayMillis = 200, riseDistance = (-20).dp, durationMillis = 600) {
                    Text(
                        text = "Make it yours",
                        style = onbFinalTitle,
                        color = OnbPalette.primaryText,
                        textAlign = TextAlign.Center
                    )
                }
                FadeRise(visible = isVisible, delayMillis = 300, riseDistance = 0.dp, durationMillis = 600) {
                    Text(
                        text = "Add your name so the app can greet you. You can change it anytime in Settings.",
                        style = onbBody,
                        color = OnbPalette.secondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // Name field
            FadeRise(visible = isVisible, delayMillis = 450, riseDistance = 20.dp, durationMillis = 600) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(text = "YOUR NAME", style = onbEyebrow, color = OnbPalette.gold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onboardingCard(padding = 16.dp)
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = OnbPalette.gold,
                            modifier = Modifier.size(18.dp)
                        )
                        BasicTextField(
                            value = UserProfileManager.displayName,
                            onValueChange = { newValue ->
                                UserProfileManager.setName(newValue.take(MAX_NAME_LENGTH))
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = OnbPalette.primaryText
                            ),
                            cursorBrush = SolidColor(OnbPalette.gold),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (UserProfileManager.displayName.isEmpty()) {
                                        Text(
                                            text = "Your name",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = OnbPalette.tertiaryText
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Continue
            FadeRise(visible = isVisible, delayMillis = 750, riseDistance = 0.dp, durationMillis = 600) {
                OnbGoldButton(text = "Continue") {
                    focusManager.clearFocus()
                    onContinue()
                }
            }
        }
    }
}

// MARK: - Page 13: Final (no-Supabase adaptation of iOS FinalScreen)

@Composable
fun FinalPage(onComplete: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FadeRise(visible = isVisible, delayMillis = 200, riseDistance = (-20).dp, durationMillis = 600) {
                Text(
                    text = "Begin Your Journey",
                    style = onbFinalTitle,
                    color = OnbPalette.primaryText,
                    textAlign = TextAlign.Center
                )
            }

            FadeRise(visible = isVisible, delayMillis = 300, riseDistance = 0.dp, durationMillis = 600) {
                Text(
                    text = "The Book and the Ahlul Bayt are waiting.\nRead, reflect, and journey - verse by verse.",
                    style = onbBody,
                    color = OnbPalette.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp)
                )
            }

            FadeRise(visible = isVisible, delayMillis = 500, riseDistance = 0.dp, durationMillis = 600) {
                OnbGoldButton(
                    text = "Begin",
                    icon = {
                        Icon(
                            Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = OnbPalette.onGold,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onComplete,
                    modifier = Modifier.padding(top = 40.dp)
                )
            }
        }
    }
}
