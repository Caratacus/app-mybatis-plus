package com.app.framework.entity;

import com.app.mybatisplus.activerecord.Model;
import com.app.mybatisplus.annotations.IdType;
import com.app.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

public class IdWorkPrimaryKey extends Model {

	@TableId(type = IdType.ID_WORKER)
	@JsonSerialize(using = ToStringSerializer.class)
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	protected Serializable getPrimaryKey() {
		return id;
	}
}
