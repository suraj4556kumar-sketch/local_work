package com.example.localconnect

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localconnect.data.FirebaseRepository
import com.example.localconnect.data.Review
import com.example.localconnect.ui.TranslatedText
import kotlinx.coroutines.launch

/**
 * A reusable bottom-sheet for leaving a star review + optional comment.
 *
 * Translation policy:
 *   Static UI labels  -> stringResource()   (pre-translated via strings.xml per locale)
 *   Dynamic data      -> TranslatedText()   (calls Google Translate API at runtime)
 *
 * @param reviewType   "service" or "job"
 * @param targetId     providerId (service) or employerId (job)
 * @param targetName   DYNAMIC: company/provider name from Firestore
 * @param serviceTitle DYNAMIC: service or job title from Firestore
 * @param referenceId  serviceRequestId or jobApplicationId
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveReviewBottomSheet(
    reviewType: String,
    targetId: String,
    targetName: String,
    entityId: String = "",

    serviceTitle: String,
    referenceId: String,
    reviewerUid: String,
    reviewerName: String,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedRating by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // STATIC text — resolved from locale-specific strings.xml
    val titleResId = if (reviewType == "service") R.string.how_was_service else R.string.how_was_employer
    val submitBtnLabel = stringResource(R.string.submit_review)
    val skipBtnLabel = stringResource(R.string.skip)
    val tapStarLabel = stringResource(R.string.tap_star_to_rate)
    val reviewSubmittedMsg = stringResource(R.string.review_submitted)
    val reviewFailedMsg = stringResource(R.string.review_failed)
    val selectRatingError = "Please select a star rating."

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // STATIC title (depends on reviewType — locale-safe)
            Text(
                text = stringResource(titleResId),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // DYNAMIC: comes from Firestore — must use TranslatedText
            TranslatedText(
                text = targetName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )
            TranslatedText(
                text = serviceTitle,
                fontSize = 13.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Star hint (STATIC)
            if (selectedRating == 0) {
                Text(text = tapStarLabel, fontSize = 13.sp, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Animated star row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (star in 1..5) {
                    val isSelected = star <= selectedRating
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.25f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
                        label = "star_$star"
                    )
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { selectedRating = star }
                    )
                }
            }

            if (selectedRating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$selectedRating / 5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF59E0B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))



            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = Color(0xFFEF4444), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // STATIC: Skip
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B7280))
                ) {
                    Text(skipBtnLabel, fontWeight = FontWeight.SemiBold)
                }

                // STATIC: Submit
                Button(
                    onClick = {
                        if (selectedRating == 0) { errorMessage = selectRatingError; return@Button }
                        isSubmitting = true
                        errorMessage = null
                        coroutineScope.launch {
                            val review = Review(
                                reviewerId = reviewerUid,
                                reviewerName = reviewerName,
                                targetId = targetId,
                                targetName = targetName,
                                referenceId = referenceId,
                                reviewType = reviewType,
                                entityId = entityId,
                                rating = selectedRating.toFloat(),
                                comment = "",
                                serviceTitle = serviceTitle
                            )
                            val success = FirebaseRepository.submitReview(review)
                            isSubmitting = false
                            if (success) {
                                android.widget.Toast.makeText(context, reviewSubmittedMsg, android.widget.Toast.LENGTH_SHORT).show()
                                onSubmitted()
                            } else {
                                errorMessage = reviewFailedMsg
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(2f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(submitBtnLabel, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
