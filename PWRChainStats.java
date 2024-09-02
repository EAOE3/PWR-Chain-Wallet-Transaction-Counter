package user;

import com.github.pwrlabs.pwrj.protocol.PWRJ;
import com.github.pwrlabs.pwrj.record.transaction.VmDataTransaction;

import java.util.HashMap;
import java.util.Map;

public class PWRChainStats {
    static PWRJ pwrj = new PWRJ("https://pwrrpc.pwrlabs.io/");
    private static long chainId = 9898;

    private static Map<String /*Sender*/, Integer /*Transactions Count*/> transactionsCount = new HashMap<>();

    public static void checkOnChainStats(long fromBlock, long toBlock) {
        while(fromBlock < toBlock) {
            long maxBlockToCheck;
            if(toBlock > fromBlock + 1000) {
                maxBlockToCheck = fromBlock + 1000;
            } else {
                maxBlockToCheck = toBlock;
            }

            try {
                VmDataTransaction[] txns = pwrj.getVMDataTransactions(fromBlock, maxBlockToCheck, chainId);

                for(VmDataTransaction txn : txns) {
                    String sender = txn.getSender();
                    if(transactionsCount.containsKey(sender)) {
                        transactionsCount.put(sender, transactionsCount.get(sender) + 1);
                    } else {
                        transactionsCount.put(sender, 1);
                    }
                }

                fromBlock = maxBlockToCheck + 1;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error fetching transactions from block " + fromBlock + " to " + maxBlockToCheck);
                System.exit(0);
            }

        }
    }

    //A function to get number of wallets that had more than x transactions
    public static int getWalletsWithMoreThanXTransactions(int x) {
        int count = 0;
        for(Map.Entry<String, Integer> entry : transactionsCount.entrySet()) {
            if(entry.getValue() > x) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        checkOnChainStats(0, 10000);
        System.out.println("Number of wallets with more than 2 transactions: " + getWalletsWithMoreThanXTransactions(2));
    }
}
