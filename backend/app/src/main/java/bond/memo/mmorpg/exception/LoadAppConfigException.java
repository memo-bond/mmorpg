package bond.memo.mmorpg.exception;

public class LoadAppConfigException extends RuntimeException {

    public LoadAppConfigException(String msg, Exception e) {
        super(msg, e);
    }
}
