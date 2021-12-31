package com.fermt.blockchain

import java.math.BigInteger
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.util.*
import kotlin.collections.ArrayList


class HashUtils {

    companion object {
        fun sha256(input: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            val hexString = StringBuffer()
            for (byte in hash) {
                val hex = Integer.toHexString(0xff and byte.toInt())
                if (hex.length == 1)
                    hexString.append('0')
                hexString.append(hex)
            }
            return hexString.toString()
        }

        fun md5(input:String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }

        fun applyECDSASig(privateKey: PrivateKey, input: String): ByteArray {
            val dsa: Signature
            var output: ByteArray = ByteArray(0)
            try {
                dsa = Signature.getInstance("ECDSA", "BC")
                dsa.initSign(privateKey)
                val strByte = input.toByteArray()
                dsa.update(strByte)
                val realSig: ByteArray = dsa.sign()
                output = realSig
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            return output
        }

        fun verifyECDSASig(publicKey: PublicKey?, data: String, signature: ByteArray?): Boolean {
            return try {
                val ecdsaVerify = Signature.getInstance("ECDSA", "BC")
                ecdsaVerify.initVerify(publicKey)
                ecdsaVerify.update(data.toByteArray())
                ecdsaVerify.verify(signature)
            } catch (e: java.lang.Exception) {
                return false
            }
        }

        fun getDifficultyString(difficulty: Int): String {
            var diffArray = ""
            for(i in 0 until difficulty)
                diffArray += "0"
            return diffArray
        }

        fun getStringFromKey(key: Key): String? {
            return Base64.getEncoder().encodeToString(key.getEncoded())
        }

        fun getMerkleRoot(transactions: ArrayList<Transaction>): String {
            var count = transactions.size
            var previousTreeLayer = arrayListOf<String>()
            for(transaction in transactions) {
                previousTreeLayer.add(transaction.transactionId)
            }
            var treeLayer = previousTreeLayer
            while(count > 1){
                treeLayer = arrayListOf<String>()
                for((i, value) in previousTreeLayer.withIndex()){
                    treeLayer.add(sha256(
                        previousTreeLayer.get(i-1) + previousTreeLayer.get(i)))
                }
                count = treeLayer.size
                previousTreeLayer = treeLayer
            }
            return if(treeLayer.size == 1) treeLayer.get(0) else ""
        }

        fun generateKeyPair(): KeySet {
            val keyGen = KeyPairGenerator.getInstance("ECDSA", "BC")
            val random = SecureRandom.getInstance("SHA1PRNG")
            val ecSpec = ECGenParameterSpec("prime192v1")
            keyGen.initialize(ecSpec, random)
            val keyPair = keyGen.generateKeyPair()
            return KeySet(keyPair.private, keyPair.public)
        }
    }
}