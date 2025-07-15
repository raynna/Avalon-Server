package com.rs.core.packets.decode;

import com.rs.core.packets.InputStream;
import com.rs.core.networking.Session;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract void decode(InputStream stream);

}
