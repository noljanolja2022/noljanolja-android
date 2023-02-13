package com.noljanolja.android.features.auth.signup.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R

@Composable
fun AgreementRow(
    checked: Boolean,
    tag: String,
    description: String,
    onToggle: () -> Unit,
    onGoDetail: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.clickable {
            onToggle.invoke()
        }
    ) {
        Image(
            painterResource(id = if (checked) R.drawable.ic_checked else R.drawable.ic_uncheck),
            contentDescription = null
        )
        Text(
            tag,
            style = TextStyle(
                fontSize = 14.sp,
                color = colorResource(id = R.color.primary_text_color)
            ),
            modifier = Modifier.padding(top = 7.dp, start = 4.dp)
        )
        Text(
            description,
            style = TextStyle(
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondary_text_color)
            ),
            modifier = Modifier
                .weight(1F)
                .padding(top = 7.dp, start = 6.dp)
        )
        Image(
            painterResource(id = R.drawable.ic_next),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable {
                    onGoDetail.invoke()
                }
        )
    }
}

@Composable
fun FullAgreement(checked: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.clickable {
            onClick.invoke()
        }
    ) {
        Image(
            painterResource(id = if (checked) R.drawable.ic_checked else R.drawable.ic_uncheck),
            contentDescription = null
        )
        Text(
            "Full agreement",
            style = TextStyle(
                fontSize = 14.sp,
                color = colorResource(id = R.color.primary_text_color),
                fontWeight = FontWeight.W700,
            ),
            modifier = Modifier
                .weight(1F)
                .padding(top = 7.dp, start = 4.dp)
        )
        Image(
            painterResource(id = R.drawable.ic_next),
            contentDescription = null,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}