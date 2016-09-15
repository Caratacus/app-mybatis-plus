package com.app.framework.entity;

import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.IdType;

public class UuidPrimaryKey {

	@Id(type = IdType.UUID)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
