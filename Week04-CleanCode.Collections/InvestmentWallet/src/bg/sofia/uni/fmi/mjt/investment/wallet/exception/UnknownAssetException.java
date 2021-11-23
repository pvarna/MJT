package bg.sofia.uni.fmi.mjt.investment.wallet.exception;

public class UnknownAssetException extends WalletException {
    public UnknownAssetException() {
    }

    public UnknownAssetException(String message) {
        super(message);
    }

    public UnknownAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
