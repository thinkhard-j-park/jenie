package org.jenie.spring.helloworld.common;

import org.jenie.spring.helloworld.grpc.WriterMessage;

public class Writer {

	private String wid;

	private String name;

	public Writer() {
	}

	public Writer(String wid, String name) {
		this.wid = wid;
		this.name = name;
	}

	public static WriterMessage toProtoMessage(Writer writer) {
		if (writer == null) {
			return null;
		}
		return WriterMessage.newBuilder().setWid(writer.getWid()).setName(writer.getName()).build();
	}

	public String getWid() {
		return this.wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "Writer{" +
				"wid='" + this.wid + '\'' +
				", name='" + this.name + '\'' +
				'}';
		//@formatter:on
	}

}
