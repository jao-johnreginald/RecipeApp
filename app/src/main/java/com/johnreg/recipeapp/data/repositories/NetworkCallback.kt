package com.johnreg.recipeapp.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class NetworkCallback @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isNetworkAvailable.value = false
    }

    fun isNetworkAvailable(): StateFlow<Boolean> {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(this)
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        isNetworkAvailable.value = if (network != null && capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            false
        }

        return isNetworkAvailable
    }

}