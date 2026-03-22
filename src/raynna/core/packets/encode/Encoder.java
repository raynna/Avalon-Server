package raynna.core.packets.encode;

import raynna.core.networking.Session;

public abstract class Encoder {

	protected Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}
