package com.appnroll.box.utils

import android.os.Build


fun isAtLeastPie() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

fun isAtLeastMarshamallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M