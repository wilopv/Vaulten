package com.wilove.vaulten

import org.junit.Test
import androidx.credentials.provider.CredentialProviderService
import java.lang.reflect.Modifier

class ReflectTest {
    @Test
    fun printMethods() {
        val clazz = CredentialProviderService::class.java
        for (method in clazz.declaredMethods) {
            if (Modifier.isAbstract(method.modifiers)) {
                println("ABSTRACT METHOD: ${method.name}")
                for (param in method.parameterTypes) {
                    println("  PARAM: ${param.name}")
                }
            }
        }
    }
}
