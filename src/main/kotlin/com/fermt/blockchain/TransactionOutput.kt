package com.fermt.blockchain

import java.security.PublicKey

class TransactionOutput(
    val recipient: PublicKey,
    val value: Float,
    val parentTransactionId: String) {

    val id = HashUtils.sha256(HashUtils.getStringFromKey(recipient) + value.toString() + parentTransactionId)

    fun isMine(publicKey: PublicKey): Boolean {
        return publicKey == recipient
    }
}