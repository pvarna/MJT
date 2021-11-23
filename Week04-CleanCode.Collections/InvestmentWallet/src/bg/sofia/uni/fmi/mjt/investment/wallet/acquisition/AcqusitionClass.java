package bg.sofia.uni.fmi.mjt.investment.wallet.acquisition;

import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.QuoteService;

import java.time.LocalDateTime;

public class AcqusitionClass implements Acquisition {
    private final Asset asset;
    private final int quantity;
    private final double pricePerUnit;
    private final LocalDateTime timestamp;

    public AcqusitionClass(Asset asset, int quantity, double pricePerUnit) {
        this.asset = asset;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public double getPrice() {
        return this.pricePerUnit;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public Asset getAsset() {
        return this.asset;
    }
}
