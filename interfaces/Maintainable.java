package interfaces;

public interface Maintainable {
    void scheduleMaintenance();
    boolean needsMaintenance();
    void performMaintenance();
}
