package com.radiantbyte.nyxium.client.game

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.radiantbyte.nyxium.client.R

enum class ModuleCategory(
    @DrawableRes val iconResId: Int,
    @StringRes val labelResId: Int
) {
    Implementation(
        iconResId = R.drawable.masked_transitions_24px,
        labelResId = R.string.implementation
    ),
    Config(
        iconResId = R.drawable.manufacturing_24px,
        labelResId = R.string.config
    )

}