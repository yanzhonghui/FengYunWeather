package me.wsj.fengyun.service

import android.app.Notification
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.wsj.fengyun.BuildConfig
import me.wsj.fengyun.R
import me.wsj.fengyun.bean.Now
import me.wsj.fengyun.bean.WeatherNow
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.ui.fragment.CACHE_WEATHER_NOW
import me.wsj.fengyun.widget.WeatherWidget
import me.wsj.lib.net.HttpUtils
import me.wsj.lib.utils.IconUtils
import per.wsj.commonlib.utils.LogUtil
import java.util.*


class WidgetService : LifecycleService() {

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil.e("onCreate: ---------------------")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, Notification())
        }
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                updateWidget()
                delay(1800_000)
            }
        }
    }

    private suspend fun updateWidget() {
        LogUtil.e("update....")
        var cityId = ""
        var cityName = ""
        val cities = AppRepo.getInstance().getCities()
        if (cities.isEmpty()) {
            return
        }
        cityId = cities[0].cityId
        cityName = cities[0].cityName
        cities.forEach {
            if (it.isLocal) {
                cityId = it.cityId
                cityName = it.cityName
            }
            return@forEach
        }

        val url = "https://devapi.qweather.com/v7/weather/now"
        val param = HashMap<String, Any>()
        param["location"] = cityId
        param["key"] = BuildConfig.HeFengKey

        var now: Now? = null
        HttpUtils.get<WeatherNow>(url, param) { _, result ->
            now = result.now
        }

        val views = RemoteViews(packageName, R.layout.weather_widget)
        val location = if (cityName.contains("-")) cityName.split("-")[1] else cityName
        views.setTextViewText(R.id.tvLocation, location)

        now?.let {
            AppRepo.getInstance()
                .saveCache(CACHE_WEATHER_NOW + cityId, it)

            views.setTextViewText(R.id.tvWeather, it.text)
            views.setTextViewText(R.id.tvTemp, it.temp + "°C")
            views.setImageViewResource(R.id.ivWeather, IconUtils.getDayIconDark(this, it.icon))
        }

        val componentName = ComponentName(this, WeatherWidget::class.java)
        AppWidgetManager.getInstance(this).updateAppWidget(componentName, views);
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("onDestroy: ---------------------")
    }
}