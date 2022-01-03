package com.fermt.blockchain

import java.security.PrivateKey
import java.security.PublicKey

class Transaction(
    private val sender: PublicKey,
    val recipient: PublicKey,
    val value: Float,
    private val inputs: ArrayList<TransactionInput>?) {

    var transactionId = ""
    var outputs = arrayListOf<TransactionOutput>()
    private var signature = ByteArray(0)
    private var sequence = 0

    fun processTransaction(): Boolean {
        if(verifySignature() == false) {
            println("Transaction Signature failed to verify")
            return false
        }
/*
        if (inputs != null) {
            for(i in inputs) {
                if(NoobChain.UTXOs.get(i.transactionOutputId) != null)
                    i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId)!!
            }
        }*/
/*
        if(getInputsValue() < NoobChain.minimumTransaction) {
            println("Transaction inputs too small: " + getInputsValue())
            return false
        }
*/
//        val leftOver = getInputsValue() - value
        transactionId = calculateHash()
//        outputs.add(TransactionOutput(recipient, value, transactionId)) // send value to recipient
//        outputs.add(TransactionOutput(sender, leftOver, transactionId)) // send leftover back to sender
/*
        for(o in outputs) {
            NoobChain.UTXOs.put(o.id, o)
        }

        if (inputs != null) {
            for(i in inputs) {
                if(i.UTXO == null) continue
                NoobChain.UTXOs.remove(i.UTXO.id)
            }
        }*/
        return true
    }

    fun getInputsValue(): Float {
        var total = 0F
        if (inputs != null) {
            for(i in inputs){
                if(i.UTXO == null) continue
                total += i.UTXO.value
            }
        }
        return total
    }

    fun getOutputsValue(): Float {
        var total = 0f
        for (o: TransactionOutput in outputs) {
            total += o.value
        }
        return total
    }

    fun generateSignature(privateKey: PrivateKey) {
        val data = HashUtils.getStringFromKey(sender) + HashUtils.getStringFromKey(recipient) + value.toString()
        signature = HashUtils.applyECDSASig(privateKey, data)
    }

    fun verifySignature(): Boolean {
        val data = HashUtils.getStringFromKey(sender) + HashUtils.getStringFromKey(recipient) + value.toString()
        return HashUtils.verifyECDSASig(sender, data, signature)
    }

    private fun calculateHash(): String {
        sequence++ //increase the sequence to avoid 2 identical transactions having the same hash
        return HashUtils.sha256(
            HashUtils.getStringFromKey(sender) +
                    HashUtils.getStringFromKey(recipient).toString() +
                    java.lang.Float.toString(value) + sequence
        )
    }

    fun isMine(publicKey: PublicKey): Boolean {
        return publicKey == recipient
    }
}