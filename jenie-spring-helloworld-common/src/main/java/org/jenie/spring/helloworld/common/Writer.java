package org.jenie.spring.helloworld.common;

import com.google.common.base.Strings;

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
		return WriterMessage.newBuilder()
			.setWid(Strings.nullToEmpty(writer.getWid()))
			.setName(Strings.nullToEmpty(writer.getName()))
			.build();
	}

	public static Writer fromProtoMessage(WriterMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}
		return new Writer(protoMessage.getWid(), protoMessage.getName());
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
