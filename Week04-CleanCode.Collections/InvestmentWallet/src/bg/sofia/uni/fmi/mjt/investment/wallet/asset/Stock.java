package bg.sofia.uni.fmi.mjt.investment.wallet.asset;

public class Stock extends AssetAbstract {
    public Stock(String id, String name) {
        super(id, name);
    }

    @Override
    public AssetType getType() {
        return AssetType.STOCK;
    }
}
