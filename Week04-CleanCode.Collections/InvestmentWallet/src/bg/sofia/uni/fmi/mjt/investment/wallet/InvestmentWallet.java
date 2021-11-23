package bg.sofia.uni.fmi.mjt.investment.wallet;

import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.Acquisition;
import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.AcqusitionClass;
import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.InsufficientResourcesException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.OfferPriceException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.UnknownAssetException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.WalletException;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.Quote;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.QuoteService;

import java.util.*;

public class InvestmentWallet implements Wallet {
    private final QuoteService quoteService;
    double cashBalance;
    Map<Asset, Integer> assets;
    List<Acquisition> acquisitions;

    public InvestmentWallet(QuoteService quoteService) {
        this.quoteService = quoteService;
        this.assets = new HashMap<>();
        this.acquisitions = new ArrayList<>();
    }

    @Override
    public double deposit(double cash) {
        if (cash < 0.0) {
            throw new IllegalArgumentException();
        }
        this.cashBalance += cash;
        return this.cashBalance;
    }

    @Override
    public double withdraw(double cash) throws InsufficientResourcesException {
        if (cash < 0.0) {
            throw new IllegalArgumentException();
        }

        if (cash > this.cashBalance) {
            throw new InsufficientResourcesException();
        }

        this.cashBalance -= cash;
        return this.cashBalance;
    }

    @Override
    public Acquisition buy(Asset asset, int quantity, double maxPrice) throws WalletException {
        if (quantity < 0.0 || maxPrice < 0.0 || asset == null) {
            throw new IllegalArgumentException();
        }

        Quote currentQuote = this.quoteService.getQuote(asset);
        if (currentQuote == null) {
            throw new UnknownAssetException();
        }

        if (currentQuote.askPrice() > maxPrice) {
            throw new OfferPriceException();
        }

        if (this.cashBalance < currentQuote.askPrice() * quantity) {
            throw new InsufficientResourcesException();
        }

        Acquisition result = new AcqusitionClass(asset, quantity, currentQuote.askPrice());
        this.acquisitions.add(result);
        this.assets.put(asset, quantity);
        this.cashBalance -= currentQuote.askPrice() * quantity;

        return result;
    }

    @Override
    public double sell(Asset asset, int quantity, double minPrice) throws WalletException {
        if (asset == null || quantity < 0.0 || minPrice < 0.0) {
            throw new IllegalArgumentException();
        }

        if (!this.assets.containsKey(asset)) {
            this.assets.put(asset, 0);
        }

        if (this.assets.get(asset) < quantity) {
            throw new InsufficientResourcesException();
        }

        Quote currentQuote = this.quoteService.getQuote(asset);
        if (currentQuote == null) {
            throw new UnknownAssetException();
        }

        if (currentQuote.bidPrice() < minPrice) {
            throw new OfferPriceException();
        }

        this.assets.put(asset, this.assets.get(asset) - quantity);

        double cashToAdd = currentQuote.bidPrice() * quantity;
        this.cashBalance += cashToAdd;

        return cashToAdd;
    }

    @Override
    public double getValuation() {
        double result = 0.0;

        for (Map.Entry<Asset, Integer> set : this.assets.entrySet()) {
            result += this.quoteService.getQuote(set.getKey()).bidPrice() * set.getValue();
        }

        return result;
    }

    @Override
    public double getValuation(Asset asset) throws UnknownAssetException {
        if (asset == null) {
            throw new IllegalArgumentException();
        }

        if (!this.assets.containsKey(asset) || this.quoteService.getQuote(asset) == null) {
            throw new UnknownAssetException();
        }

        return this.quoteService.getQuote(asset).bidPrice() * this.assets.get(asset);
    }

    @Override
    public Asset getMostValuableAsset() {
        if (this.assets.size() == 0) {
            return null;
        }

        Asset result = null;
        double highestValuation = 0.0;

        for (Map.Entry<Asset, Integer> set : this.assets.entrySet()) {
            double currentValuation = this.quoteService.getQuote(set.getKey()).bidPrice() * set.getValue();
            if (currentValuation > highestValuation) {
                result = set.getKey();
                highestValuation = currentValuation;
            }
        }

        return result;
    }

    @Override
    public Collection<Acquisition> getAllAcquisitions() {
        return List.copyOf(this.acquisitions);
    }

    @Override
    public Set<Acquisition> getLastNAcquisitions(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        int acquisitionsSize = this.acquisitions.size();
        if (n > this.acquisitions.size()) {
            return Set.copyOf(this.acquisitions);
        }

        List<Acquisition> lastNAcquisitions = new ArrayList<>();
        int counter = 0;
        while (counter < n) {
            lastNAcquisitions.add(this.acquisitions.get(acquisitionsSize - 1 - counter));
            ++counter;
        }

        return Set.copyOf(lastNAcquisitions);
    }
}
