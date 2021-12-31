package com.fermt.blockchain

// https://github.com/longfeizheng/blockchain-java/tree/master/blockchain-part2/src/main/java/cn/merryyou/blockchain

class Block(private val prevHash: String){

    private val timeStamp = System.currentTimeMillis()
    private var merkleRoot = ""
    private var nonce = 0
    var hash = calculateHash()

    val transactions = arrayListOf<Transaction>()

    private fun calculateHash(): String {
        return HashUtils.sha256(
            prevHash + timeStamp.toString() + nonce.toString() + merkleRoot)
    }

    fun mineBlock(difficulty: Int){
        merkleRoot = HashUtils.getMerkleRoot(transactions)
        val target = HashUtils.getDifficultyString(difficulty)
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    fun addTransaction(transaction: Transaction): Boolean {
        //if(prevHash != "0") {
            if(transaction.processTransaction() != true){
                println("Transaction failed to process. Discarded")
                return false
            }
        //}
        transactions.add(transaction)
        println("Transaction successfully added to block")
        return true
    }
}
