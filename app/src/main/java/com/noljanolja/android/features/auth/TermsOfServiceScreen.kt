package com.noljanolja.android.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.features.auth.common.component.FullSizeWithLogo

@Composable
fun TermsOfServiceScreen() {
    FullSizeWithLogo {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Row(
                ) {
                    Image(painterResource(id = R.drawable.ic_back), contentDescription = null,
                        modifier = Modifier.clickable { })
                    Text(
                        "[Required]\nAgree to the Terms of Service",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Top)
                            .weight(1F),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W700
                        )
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    TEST_TEXT,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

const val TEST_TEXT =
    "In the hills of North Gando, however, I see winter as one of the girls. It seems like loneliness and longing and pride that has passed. One of the buried cars has a name like this and the name of Maria. I have an exotic night on top of my desk one by one. Loneliness and foreign land fell, my mother, and love in one. In one, I see the stars, and the grave inside is mine. That's why it's named so much now in North Gando. Now, the baby star, Francis star, seems to be on the desk. I threw away the name that came out, and the people who came down like a horse blooming. My loneliness, my mother, and my worries are all because of Hale.\n" +
            "\n" +
            "Look at the name of the horse in one of the deer, sleep, and one baby. Francis of the genus fell and is still on top. The name and above, also sleeps in the heart, I am a rabbit, hill Maria mother, there is. That's why I call it one of my own. There is a night in the baby's chest. Why is it easy, rabbit, and name, name, and two. For a hill desk, look engraved. It's because of the loneliness that mourns over Maria's autumn and now the starlight is shining. Wrote the stars to worm, there is no one who cries tomorrow. It is the reason for the loneliness and loneliness of the buried star neighbor.\n" +
            "\n" +
            "It's a beautiful thing that is engraved asurahi shui. Loneliness and Poetry and Hale People's dirt, roe deer, children's stars seem far away. heal the wounds\u2028Is there no one anywhere? it's beautiful outside Loneliness and Poetry and Hale People's dirt, roe deer, children's stars seem far away. heal the wounds\u2028Is there no one anywhere?\u2028Is there no one anywhere?"