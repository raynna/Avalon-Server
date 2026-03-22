package raynna.core.networking;

public final class GamePacket {
    public final int id;
    public final byte[] payload;
    public final long receivedAt;

    public GamePacket(int id, byte[] payload, long receivedAt) {
        this.id = id;
        this.payload = payload;
        this.receivedAt = receivedAt;
    }
}
