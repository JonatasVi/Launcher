package de.jrpie.android.launcher.settings.launcher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Switch
import androidx.fragment.app.Fragment
import de.jrpie.android.launcher.PREF_DATE_LOCALIZED
import de.jrpie.android.launcher.PREF_DATE_TIME_FLIP
import de.jrpie.android.launcher.PREF_DATE_VISIBLE
import de.jrpie.android.launcher.PREF_DOUBLE_ACTIONS_ENABLED
import de.jrpie.android.launcher.PREF_EDGE_ACTIONS_ENABLED
import de.jrpie.android.launcher.PREF_SCREEN_FULLSCREEN
import de.jrpie.android.launcher.PREF_SCREEN_TIMEOUT_DISABLED
import de.jrpie.android.launcher.PREF_SEARCH_AUTO_KEYBOARD
import de.jrpie.android.launcher.PREF_SEARCH_AUTO_LAUNCH
import de.jrpie.android.launcher.PREF_TIME_VISIBLE
import de.jrpie.android.launcher.R
import de.jrpie.android.launcher.UIObject
import de.jrpie.android.launcher.getPreferences
import de.jrpie.android.launcher.getSavedTheme
import de.jrpie.android.launcher.resetToDarkTheme
import de.jrpie.android.launcher.resetToDefaultTheme
import de.jrpie.android.launcher.setButtonColor
import de.jrpie.android.launcher.setSwitchColor
import de.jrpie.android.launcher.setWindowFlags
import de.jrpie.android.launcher.vibrantColor
import de.jrpie.android.launcher.databinding.SettingsLauncherBinding
import de.jrpie.android.launcher.setDefaultHomeScreen


/**
 * The [SettingsFragmentLauncher] is a used as a tab in the SettingsActivity.
 *
 * It is used to change themes, select wallpapers ... theme related stuff
 */
class SettingsFragmentLauncher : Fragment(), UIObject {

    private lateinit var binding: SettingsLauncherBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsLauncherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart(){
        super<Fragment>.onStart()
        super<UIObject>.onStart()
    }


    override fun applyTheme() {

        setButtonColor(binding.settingsLauncherButtonChooseHomeScreen, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchScreenTimeout, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchScreenFull, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchAutoLaunch, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchAutoKeyboard, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchEnableDouble, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchEnableEdge, vibrantColor)

        setSwitchColor(binding.settingsLauncherSwitchDateLocalized, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchDateVisible, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchTimeVisible, vibrantColor)
        setSwitchColor(binding.settingsLauncherSwitchDateTimeFlip, vibrantColor)

        setButtonColor(binding.settingsLauncherButtonChooseWallpaper, vibrantColor)
    }

    override fun setOnClicks() {

        val preferences = getPreferences(requireActivity())

        fun bindSwitchToPref(switch: Switch, pref: String, default: Boolean, onChange: (Boolean) -> Unit){
            switch.isChecked = preferences.getBoolean(pref, default)
            switch.setOnCheckedChangeListener { _, isChecked -> // Toggle double actions
                preferences.edit()
                    .putBoolean(pref, isChecked)
                    .apply()
                onChange(isChecked)
            }
        }

        binding.settingsLauncherButtonChooseHomeScreen.setOnClickListener {
            setDefaultHomeScreen(requireContext(), checkDefault = false)
        }

        binding.settingsLauncherButtonChooseWallpaper.setOnClickListener {
            // https://github.com/LineageOS/android_packages_apps_Trebuchet/blob/6caab89b21b2b91f0a439e1fd8c4510dcb255819/src/com/android/launcher3/views/OptionsPopupView.java#L271
            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("com.android.wallpaper.LAUNCH_SOURCE", "app_launched_launcher")
                .putExtra("com.android.launcher3.WALLPAPER_FLAVOR", "focus_wallpaper")
            startActivity(intent)
        }

        bindSwitchToPref(binding.settingsLauncherSwitchDateLocalized, PREF_DATE_LOCALIZED, false) { }
        bindSwitchToPref(binding.settingsLauncherSwitchDateVisible, PREF_DATE_VISIBLE, true) {}
        bindSwitchToPref(binding.settingsLauncherSwitchTimeVisible, PREF_TIME_VISIBLE, true) {}
        bindSwitchToPref(binding.settingsLauncherSwitchDateTimeFlip, PREF_DATE_TIME_FLIP, false) {}

        bindSwitchToPref(binding.settingsLauncherSwitchScreenTimeout, PREF_SCREEN_TIMEOUT_DISABLED, false) {
            activity?.let{setWindowFlags(it.window)}
        }
        bindSwitchToPref(binding.settingsLauncherSwitchScreenFull, PREF_SCREEN_FULLSCREEN, true) {
            activity?.let{setWindowFlags(it.window)}
        }
        bindSwitchToPref(binding.settingsLauncherSwitchAutoLaunch, PREF_SEARCH_AUTO_LAUNCH, false) {}
        bindSwitchToPref(binding.settingsLauncherSwitchAutoKeyboard, PREF_SEARCH_AUTO_KEYBOARD, true) {}
        bindSwitchToPref(binding.settingsLauncherSwitchEnableDouble, PREF_DOUBLE_ACTIONS_ENABLED, false) {}
        bindSwitchToPref(binding.settingsLauncherSwitchEnableEdge, PREF_EDGE_ACTIONS_ENABLED, false) {}
    }

    override fun adjustLayout() {

        // Load values into the theme spinner
        val staticThemeAdapter = ArrayAdapter.createFromResource(
            requireActivity(), R.array.settings_launcher_theme_spinner_items,
            android.R.layout.simple_spinner_item )

        staticThemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.settingsLauncherThemeSpinner.adapter = staticThemeAdapter

        val themeInt = when (getSavedTheme(requireActivity())) {
            "finn" -> 0
            "dark" -> 1
            else -> 0
        }

        binding.settingsLauncherThemeSpinner.setSelection(themeInt)

        binding.settingsLauncherThemeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> if (getSavedTheme(activity!!) != "finn") resetToDefaultTheme(activity!!)
                    1 -> if (getSavedTheme(activity!!) != "dark") resetToDarkTheme(activity!!)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }
}
