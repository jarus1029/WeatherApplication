package com.suraj.weatherapplication.view.activity

import android.os.Bundle
import android.provider.Settings.Global.getString
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suraj.weatherapplication.R
import com.suraj.weatherapplication.data.CONSTANTS
import com.suraj.weatherapplication.model.WeatherData

class WeatherDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val weatherData = intent.getParcelableExtra<WeatherData>(CONSTANTS.INTENTEXTRANAME)
            weatherData?.let {
                WeatherDetailScreen(weatherData = it)
            }
        }
    }
}

@Composable
fun WeatherDetailScreen(weatherData: WeatherData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .paint(painterResource(R.drawable.weather_bg),
                    contentScale = ContentScale.Crop ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = weatherData.cityName,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = weatherData.weatherDescription.replaceFirstChar { it.uppercase() },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                WeatherDetailItem(
                    label = LocalContext.current.getString(R.string.weather_detail, CONSTANTS.TEMPERATURE),
                    value = String.format("%.2f °C", weatherData.temperature)
                )

                WeatherDetailItem(
                    label = LocalContext.current.getString(R.string.weather_detail, CONSTANTS.FEELS_LIKE),
                    value = String.format("%.2f °C", weatherData.feelsLike)
                )
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                WeatherDetailItem(
                    label = LocalContext.current.getString(R.string.weather_detail, CONSTANTS.HUMIDITY),
                    value = "${weatherData.humidity} %"
                )

                WeatherDetailItem(
                    label = LocalContext.current.getString(R.string.weather_detail, CONSTANTS.PRESSURE),
                    value = "${weatherData.pressure} Pa"
                )
            }
        }
    }
}


@Composable
fun WeatherDetailItem(label: String, value: String) {
    Box(
        modifier = Modifier
            .width(185.dp)
            .padding(10.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xEB0A98B4))
            .padding(16.dp),

        contentAlignment = Alignment.Center


    ) {
        Text(
            text = "$label $value",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center

        )
    }
}




