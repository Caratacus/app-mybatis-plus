package com.app.framework.entity;

import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.IdType;

public class AutoPrimaryKey {

	@Id(type = IdType.AUTO)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
