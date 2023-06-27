package garipolesya.com.example.wifianalyzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import garipolesya.com.example.wifianalyzer.databinding.ActivityMainBinding
import garipolesya.com.example.wifianalyzer.ui.HomeFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showHomeScreen()
    }

    private fun showHomeScreen() {
        supportFragmentManager.commit {
            add(R.id.container, HomeFragment())
        }
    }

}
