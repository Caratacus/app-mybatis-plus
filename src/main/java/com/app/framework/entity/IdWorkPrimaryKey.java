package com.app.framework.entity;

import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class IdWorkPrimaryKey {

	@Id(type = IdType.ID_WORKER)
	@JsonSerialize(using = ToStringSerializer.class)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
