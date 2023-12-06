package UIL;

public interface TransferListener {
    boolean canImport(final TransferEvent event);
    boolean onImport(final TransferEvent event);
}