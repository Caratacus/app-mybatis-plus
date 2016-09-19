package com.app.framework.entity;

import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.IdType;

public class AutoPrimaryKey {

	@Id(type = IdType.ID_WORKER)
	protected Long id;

	protected Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

}
