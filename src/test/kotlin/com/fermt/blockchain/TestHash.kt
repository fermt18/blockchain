package com.fermt.blockchain

import NoobChain
import NoobChain.Companion.UTXOs
import NoobChain.Companion.UTXs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.Security

class TestHash {

    private lateinit var keySet1: KeySet
    private lateinit var keySet2: KeySet
    private lateinit var t0: Transaction
    private lateinit var t1: Transaction
    private lateinit var t2: Transaction

    @BeforeEach
    fun init(){
        Security.addProvider(BouncyCastleProvider())
        keySet1 = HashUtils.generateKeyPair()
        keySet2 = HashUtils.generateKeyPair()
        t0 = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
        t1 = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
        t2 = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
    }

    @Test
    fun test_hashLength(){
        assertEquals(64, Block("0").hash.length)
    }

    // Transaction
    @Test
    fun test_processTransactionWithOutSignature(){
        assertFalse(t0.processTransaction())
    }

    @Test
    fun test_processTransactionWithSignature(){
        t0.generateSignature(keySet1.privateKey)
        assertTrue(t0.processTransaction())
        assertEquals(64, t0.transactionId.length)
    }

    // Block
    @Test
    fun test_addValidTransactionToABlock(){
        t0.generateSignature(keySet1.privateKey)
        val block = Block(t0.transactionId)
        val h1 = block.hash
        assertTrue(block.addTransaction(t0))
        assertTrue(h1 == block.hash)
        assertEquals(1, block.transactions.size)
    }

    @Test
    fun test_mineABlock(){
        t0.generateSignature(keySet1.privateKey)
        val block = Block(t0.transactionId)
        val h1 = block.hash
        block.mineBlock(3)
        assertFalse(h1 == block.hash)
    }

    // Wallet
    @Test
    fun test_walletCreation(){
        assertEquals(0F, Wallet().getBalance())
    }

    @Test
    fun test_walletTransactionWithNoFunds(){
        val w1 = Wallet()
        val w2 = Wallet()
        assertEquals(null, w1.sendFunds(w2.publicKey, 10f))
    }

    @Test
    fun test_walletTransferFunds(){
        val walletA = Wallet()
        makeGenesisTransactionToWallet(walletA)
        assertEquals(100f, walletA.getBalance())
    }

    @Test
    fun test_walletSendFundsWithoutBlock(){
        val walletA = Wallet()
        val walletB = Wallet()
        makeGenesisTransactionToWallet(walletA)
        assertEquals(100f, walletA.getBalance())
        assertEquals(0f, walletB.getBalance())
        walletA.sendFunds(walletB.publicKey, 10f)
        assertEquals(100f, walletA.getBalance())
        assertEquals(0f, walletB.getBalance())
    }

    @Test
    fun test_walletSendFundsWithinABlock(){
        val walletA = Wallet()
        val walletB = Wallet()
        val tx0 = makeGenesisTransactionToWallet(walletA)
        assertEquals(100f, walletA.getBalance())
        assertEquals(0f, walletB.getBalance())
        val genesis = Block("0")
        genesis.addTransaction(tx0)
        genesis.mineBlock(1)
        val block1 = Block(genesis.hash)
        val tx1 = walletA.sendFunds(walletB.publicKey, 10f)
        if (tx1 != null) {
            block1.addTransaction(tx1)
        }
        block1.mineBlock(1)
        assertEquals(90f, walletA.getBalance())
        assertEquals(10f, walletB.getBalance())
    }

    // HashUtils
    @Test
    fun test_difficultyString(){
        assertEquals("000", HashUtils.getDifficultyString(3))
    }

    @Test
    fun test_main(){
        NoobChain.main()
    }

    private fun makeGenesisTransactionToWallet(w: Wallet): Transaction {
        val coinbase = Wallet()
        val genesisTransaction = Transaction(coinbase.publicKey, w.publicKey, 100f, null)
        genesisTransaction.generateSignature(coinbase.privateKey)
        genesisTransaction.transactionId = "0"
        UTXs.put(genesisTransaction.transactionId, genesisTransaction)
        return genesisTransaction
    }
}