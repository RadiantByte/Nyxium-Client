package com.radiantbyte.nyxium.client.util

import com.radiantbyte.nyxium.client.game.TranslationManager
import java.util.Locale

inline val String.translatedSelf: String
    get() {
        return TranslationManager.getTranslationMap(Locale.getDefault().language)[this]
            ?: this
    }