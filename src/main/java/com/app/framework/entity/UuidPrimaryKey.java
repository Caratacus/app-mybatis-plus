package com.app.framework.entity;

import com.app.mybatisplus.activerecord.Model;
import com.app.mybatisplus.annotations.IdType;
import com.app.mybatisplus.annotations.TableId;

import java.io.Serializable;

public class UuidPrimaryKey extends Model {

	@TableId(value = "ID", type = IdType.UUID)
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected Serializable getPrimaryKey() {
		return id;
	}
}
