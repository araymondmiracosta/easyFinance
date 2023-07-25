package net.araymond.application;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class Utility {

    public static int indexFromName(String accountName) {
        for (int i = 0; i < Values.accounts.size(); i++) {
            if (accountName.compareTo(Values.accounts.get(i).getName()) == 0) {
                return i;
            }
        }
        return -1;
    }

    public static void readAccounts() {
        for (Account account : Values.accounts) {
            Values.accountsNames = new ArrayList<>();
            Values.accountsNames.add(account.getName());
        }
    }

    public static void readCategories() {
        ArrayList<Transaction> transactions;
        HashSet<String> duplicatesRemoved;

        for (Account account : Values.accounts) {
            transactions = account.getTransactions();
            for (Transaction transaction : transactions) {
                Values.categories.add(transaction.getCategory());
            }
        }

        duplicatesRemoved = new HashSet<>(Values.categories);
        Values.categories = new ArrayList<>(duplicatesRemoved);

    }

    public static boolean readSaveData(Context context) {
        try {
            FileInputStream inputStream = context.openFileInput("ledger");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Values.accounts = (ArrayList<Account>) objectInputStream.readObject();

            objectInputStream.close();
            inputStream.close();

            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static boolean writeSaveData(Context context) {
        try {
            FileOutputStream outputStream = context.openFileOutput("ledger", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(Values.accounts);

            outputStream.flush();
            outputStream.close();
            objectOutputStream.close();

            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }
}
