package com.dicoding.picodiploma.loginwithanimation

import org.junit.BeforeClass
import org.robolectric.shadows.ShadowLog


open class LogMock {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            ShadowLog.stream = System.out
        }
    }
}