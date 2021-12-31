import com.fermt.blockchain.Block
import com.fermt.blockchain.Transaction
import com.fermt.blockchain.TransactionOutput
import com.fermt.blockchain.Wallet
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class NoobChain {

    companion object {
        val blockChain = arrayListOf<Block>()
        val UTXOs = hashMapOf<String, TransactionOutput>()
        val difficulty = 3

        fun main() {
            Security.addProvider(BouncyCastleProvider())
            val walletA = Wallet()
            val walletB = Wallet()
            val coinbase = Wallet()

            val genesisTransaction = Transaction(coinbase.publicKey, walletA.publicKey, 100f, null)
            genesisTransaction.generateSignature(coinbase.privateKey)
            genesisTransaction.transactionId = "0"
            genesisTransaction.outputs.add(
                TransactionOutput(
                    genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId))
            UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0))

            println("Creating and mining Genesis Block...")
            val genesis = Block("0")
            genesis.addTransaction(genesisTransaction)
            addBlock(genesis)

            // testing
            val block1 = Block(genesis.hash)
            println("WalletA balance is: " + walletA.getBalance())
            println("WalletB balance is: " + walletB.getBalance())
            println("Sending 40 to WalletB...")
            walletA.sendFunds(walletB.publicKey, 40f)?.let { block1.addTransaction(it) }
            addBlock(block1)
            println("WalletA balance is: " + walletA.getBalance())
            println("WalletB balance is: " + walletB.getBalance())

            val block2 = Block(block1.hash)
            println("Sending 1000 (not enough) to WalletB...")
            walletA.sendFunds(walletB.publicKey, 1000f)?.let { block2.addTransaction(it) }
            addBlock(block2)
            println("WalletA balance is: " + walletA.getBalance())
            println("WalletB balance is: " + walletB.getBalance())

            val block3 = Block(block2.hash)
            println("Sending 20 (not enough) to WalletA...")
            walletB.sendFunds(walletA.publicKey, 20f)?.let { block3.addTransaction(it) }
            addBlock(block3)
            println("WalletA balance is: " + walletA.getBalance())
            println("WalletB balance is: " + walletB.getBalance())
        }

        fun addBlock(newBlock: Block) {
            newBlock.mineBlock(difficulty)
            blockChain.add(newBlock)
        }
    }
}