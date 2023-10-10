package com.roland.android.odiyo.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class Haptic(private val context: Context) {
	@Suppress("DEPRECATION")
	fun vibrate(milliseconds: Long = 50) {
		when {
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
				(context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
					.vibrate(
						CombinedVibration.createParallel(
							VibrationEffect.createOneShot(
								milliseconds, VibrationEffect.EFFECT_CLICK
							)
						)
					)
			}

			Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
				(context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
					.vibrate(
						VibrationEffect.createOneShot(
							milliseconds, VibrationEffect.DEFAULT_AMPLITUDE
						)
					)
			}

			else -> {
				(context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
					.vibrate(milliseconds)
			}
		}
	}
}