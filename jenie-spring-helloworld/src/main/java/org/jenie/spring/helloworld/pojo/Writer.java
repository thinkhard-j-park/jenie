package org.jenie.spring.helloworld.pojo;

public class Writer {

	private String wid;

	private String name;

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
