package com.example.strangetechissue

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.movika.player.sdk.InitConfig
import com.movika.player.sdk.android.MovikaSdk
import com.movika.player.sdk.android.defaultplayer.SimpleInteractivePlayer
import com.movika.player.sdk.base.Config
import com.movika.player.sdk.base.asset.ManifestURLAssets
import com.movika.player.sdk.base.model.exception.AssetsLoadException
import com.movika.player.sdk.base.model.exception.ExpiredApiKeyException
import com.movika.player.sdk.base.model.exception.IncompatibleManifestVersionException
import com.movika.player.sdk.base.model.exception.InvalidApiKeyException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var player: SimpleInteractivePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupMovikaSdk()
        val config = Config(
            isDebugMode = BuildConfig.DEBUG,
            isLocalVideoContent = false,
            isShowDefaultTimeBar = true
        )
        val stateBundle = savedInstanceState?.getBundle(BUNDLE_KEY_PLAYER_STATE)
        player = SimpleInteractivePlayer(this, config, stateBundle)

        lifecycle.addObserver(player)

        player.run(ManifestURLAssets(BuildConfig.URL))
        player.errorObservable.addObserver { error ->
            val msg = when (error) {
                is AssetsLoadException -> "Load error!"
                is InvalidApiKeyException -> "Invalid Api key!"
                is ExpiredApiKeyException -> "Expired Api key!"
                is IncompatibleManifestVersionException -> "Incompatible manifest version ${error.receivedVersion}"
                else -> error::class.simpleName
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        setContentView(player.view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val stateBundle = player.onSaveInstanceState()
        outState.putBundle(BUNDLE_KEY_PLAYER_STATE, stateBundle)
    }

    companion object {
        private const val BUNDLE_KEY_PLAYER_STATE = "player_state"
    }

    private fun setupMovikaSdk() {
        val initConfig = InitConfig(
            apiKey = BuildConfig.API_KEY,
            appName = BuildConfig.APPLICATION_ID,
            appVersion = BuildConfig.VERSION_NAME,
        )
        MovikaSdk.setup(initConfig, this)
    }
}
