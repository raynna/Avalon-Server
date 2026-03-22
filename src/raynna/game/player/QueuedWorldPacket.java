package raynna.game.player;

public class QueuedWorldPacket {

    private final int id;
    private final byte[] data;
    private final boolean network;

    public QueuedWorldPacket(int id, byte[] data, boolean network) {
        this.id = id;
        this.data = data;
        this.network = network;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isNetwork() {
        return network;
    }
}
