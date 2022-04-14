package PersonalFinance.saveload;

import PersonalFinance.exception.ModelException;
import PersonalFinance.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SaveData {

    private static SaveData instance;

    private List<Article> articles = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private List<Currency> currencies = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private List<Transfer> transfers = new ArrayList<>();

    private final Filter filter;
    private Common oldCommon;
    private boolean saved = true;


    private SaveData() {
        load();
        this.filter = new Filter();
    }

    public void load() {
        SaveLoad.load(this);
        sort();
        for (Account a : accounts) {
            a.setAmountFromTransactionAndTransfers(transactions, transfers);
        }
    }

    private void sort() {
        //this.articles.sort((Article a, Article a1) -> a.getTitle().compareToIgnoreCase(a1.getTitle()));
        this.accounts.sort((Account a, Account a1) -> a.getTitle().compareToIgnoreCase(a1.getTitle()));
        this.transactions.sort((Transaction t, Transaction t1) -> (int)(t1.getDate().compareTo(t.getDate())));
        this.transfers.sort((Transfer t, Transfer t1) -> (int)(t1.getDate().compareTo(t.getDate())));
        this.currencies.sort(new Comparator<Currency>() {
            @Override
            public int compare(Currency c, Currency c1) {
                if(c.isBase()) return -1;
                if(c1.isBase()) return 1;
                if (c.isOn() ^ c1.isOn()) {
                    if (c1.isOn()) return 1;
                    else return -1;
                }
                return c.getTitle().compareToIgnoreCase(c1.getTitle());
            }
        });
    }

    public void save() {
        SaveLoad.save(this);
        saved = true;
    }

    public boolean isSaved() {
        return saved;
    }

    public static SaveData getInstance() {
        if (instance == null) instance = new SaveData();
        return instance;
    }

    public Filter getFilter() {
        return filter;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public Currency getBaseCurrency() {
        for (Currency c : currencies)
            if (c.isBase()) return c;
        return new Currency();
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public ArrayList<Currency> getEnableCurrencies() {
        ArrayList<Currency> list = new ArrayList<>();
        for (Currency c : currencies) {
            if (c.isOn()) list.add(c);
        }
        return list;
    }

    public List<Transaction> getFilterTransaction() {
        ArrayList<Transaction> list = new ArrayList<>();
        for (Transaction t : list) {
            if (filter.check(t.getDate())) list.add(t);
        }
        return list;
    }

    public List<Transfer> getFilterTransfer() {
        ArrayList<Transfer> list = new ArrayList<>();
        for (Transfer t : list) {
            if (filter.check(t.getDate())) list.add(t);
        }
        return list;
    }

    public List<Transaction> getTransactionToCount(int count) {
        return new ArrayList<>(transactions.subList(0, Math.min(count, transactions.size())));
    }

    public Common getOldCommon() {
        return oldCommon;
    }

    public void add(Common c) throws ModelException {
        List ref = getRef(c);
        if (ref.contains(c)) throw new ModelException(ModelException.IF_EXISTS);
        ref.add(c);
        c.postAdd(this);
        sort();
        saved = false;
    }

    public void edit(Common oldC, Common newC) throws ModelException {
        List ref = getRef(oldC);
        if (ref.contains(newC) && oldC != ref.get(ref.indexOf(newC))) throw new ModelException(ModelException.IF_EXISTS);
        ref.set(ref.indexOf(oldC), newC);
        oldCommon = oldC;
        newC.postEdit(this);
        sort();
        saved = false;
    }

    public void remove(Common c) {
        getRef(c).remove(c);
        c.postRemove(this);
        saved = false;
    }

    private List getRef(Common c) {
        if (c instanceof Account) return accounts;
        if (c instanceof Article) return articles;
        if (c instanceof Currency) return currencies;
        if (c instanceof Transaction) return transactions;
        if (c instanceof Transfer) return transfers;
        return null;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "articles=" + articles +
                ", accounts=" + accounts +
                ", currencies=" + currencies +
                ", transactions=" + transactions +
                ", transfers=" + transfers +
                '}';
    }
}
