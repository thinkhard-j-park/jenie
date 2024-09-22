package org.jenie.spring.helloworld.entity.board;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "board")
public class BoardEntity {

	@Id
	private String id;

	private String name;

	private String parentId;

	private String rootId;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getRootId() {
		return this.rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "BoardEntity{" +
				"id='" + this.id + '\'' +
				", name='" + this.name + '\'' +
				", parentId='" + this.parentId + '\'' +
				", rootId='" + this.rootId + '\'' +
				'}';
		//@formatter:on
	}
}
