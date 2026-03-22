package raynna.core.packets.decode;

import raynna.core.packets.InputStream;
import raynna.core.networking.Session;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract void decode(InputStream stream);

}
