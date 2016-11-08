package com.app.framework.entity;

import com.app.mybatisplus.activerecord.Model;
import com.app.mybatisplus.annotations.IdType;
import com.app.mybatisplus.annotations.TableId;

import java.io.Serializable;

public class InputPrimaryKey extends Model {

	@TableId(type = IdType.INPUT)
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
