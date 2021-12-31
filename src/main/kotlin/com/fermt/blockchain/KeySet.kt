package com.fermt.blockchain

import java.security.PrivateKey
import java.security.PublicKey

data class KeySet(
    val privateKey: PrivateKey,
    val publicKey: PublicKey
)
