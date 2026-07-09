/**
 * @author WengYuhao
 */
package com.nusiss.wellness.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nusiss.wellness.R
import com.nusiss.wellness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            switchFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_records -> RecordsFragment()
                R.id.nav_chat -> ChatFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            switchFragment(fragment)
            true
        }
    }

    fun selectTab(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
