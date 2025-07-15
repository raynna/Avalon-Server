package com.rs.core.packets.encode;

import com.rs.core.networking.Session;

public abstract class Encoder {

	protected Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}
