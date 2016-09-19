package com.app.framework.entity;

import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.IdType;

public class UuidPrimaryKey {

	@Id(value = "ID", type = IdType.UUID)
	protected String id;

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

}
