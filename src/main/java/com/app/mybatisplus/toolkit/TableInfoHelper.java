/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.app.mybatisplus.toolkit;

import com.app.mybatisplus.MybatisConfiguration;
import com.app.mybatisplus.annotations.Column;
import com.app.mybatisplus.annotations.Id;
import com.app.mybatisplus.annotations.Table;
import com.app.mybatisplus.exceptions.MybatisPlusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class TableInfoHelper {

    private final static Logger logger = LoggerFactory.getLogger(TableInfoHelper.class);

	/**
	 * 缓存反射类表信息
	 */
	private static Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<String, TableInfo>();

	/**
	 * <p>
	 * 获取实体映射表信息
	 * <p>
	 *
	 * @param clazz
	 *            反射实体类
	 * @return
	 */
	public static TableInfo getTableInfo(Class<?> clazz) {
		return tableInfoCache.get(clazz.getName());
	}

	/**
	 * <p>
	 * 实体类反射获取表信息【初始化】
	 * <p>
	 *
	 * @param clazz
	 *            反射实体类
	 * @return
	 */
	public synchronized static TableInfo initTableInfo(Class<?> clazz) {
		TableInfo ti = tableInfoCache.get(clazz.getName());
		if (ti != null) {
			return ti;
		}
		TableInfo tableInfo = new TableInfo();

		/* 表名 */
		Table table = clazz.getAnnotation(Table.class);
		if (table != null && StringUtils.isNotEmpty(table.value())) {
			tableInfo.setTableName(table.value());
		} else {
			tableInfo.setTableName(StringUtils.camelToUnderline(clazz.getSimpleName()));
		}

		/* 表结果集映射 */
		if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
			tableInfo.setResultMap(table.resultMap());
		}

		List<TableFieldInfo> fieldList = new ArrayList<TableFieldInfo>();
		List<Field> list = getAllFields(clazz);
		for (Field field : list) {
			/**
			 * 主键ID
			 */
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				if (tableInfo.getKeyColumn() == null) {
					tableInfo.setIdType(id.type());
					if (StringUtils.isNotEmpty(id.value())) {
						/* 自定义字段 */
						tableInfo.setKeyColumn(id.value());
						tableInfo.setKeyRelated(true);
					} else if (MybatisConfiguration.DB_COLUMN_UNDERLINE) {
						/* 开启字段下划线申明 */
						tableInfo.setKeyColumn(StringUtils.camelToUnderline(field.getName()));
					} else {
						tableInfo.setKeyColumn(field.getName());
					}
					tableInfo.setKeyProperty(field.getName());
					continue;
				} else {
					/* 发现设置多个主键注解抛出异常 */
					throw new MybatisPlusException("There must be only one, Discover multiple @Id annotation in " + clazz);
				}
			}

			/* 获取注解属性，自定义字段 */
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				String columnName = field.getName();
				if (StringUtils.isNotEmpty(column.value())) {
					columnName = column.value();
				}

				/*
				 * el 语法支持，可以传入多个参数以逗号分开
				 */
				String el = field.getName();
				if (StringUtils.isNotEmpty(column.el())) {
					el = column.el();
				}
				String[] columns = columnName.split(";");
				String[] els = el.split(";");
				if (null != columns && null != els && columns.length == els.length) {
					for (int i = 0; i < columns.length; i++) {
						fieldList.add(new TableFieldInfo(true, columns[i], field.getName(), els[i], column.validate()));
					}
				} else {
					String errorMsg = "Class: %s, Field: %s, 'value' 'el' Length must be consistent.";
					throw new MybatisPlusException(String.format(errorMsg, clazz.getName(), field.getName()));
				}

				continue;
			}

			/**
			 * 字段, 使用 camelToUnderline 转换驼峰写法为下划线分割法, 如果已指定 Column , 便不会执行这里
			 */
			if (MybatisConfiguration.DB_COLUMN_UNDERLINE) {
				/* 开启字段下划线申明 */
				fieldList.add(new TableFieldInfo(true, StringUtils.camelToUnderline(field.getName()), field.getName()));
			} else {
				fieldList.add(new TableFieldInfo(field.getName()));
			}
		}

		/* 字段列表 */
		tableInfo.setFieldList(fieldList);

		/*
		 * 未发现主键注解，跳过注入
		 */
		if (null == tableInfo.getKeyColumn()) {
			return null;
		}

		/*
		 * 注入
		 */
		tableInfoCache.put(clazz.getName(), tableInfo);
		return tableInfo;
	}

	/**
	 * 获取该类的所有属性列表
	 *
	 * @param clazz
	 *            反射类
	 * @return
	 */
	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			/* 过滤 transient关键字修饰的属性 */
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}

			/* 过滤注解非表字段属性 */
			Column Column = field.getAnnotation(Column.class);
			if (Column == null || Column.exist()) {
				result.add(field);
			}
		}

		/* 处理父类字段 */
		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(Object.class)) {
			return result;
		}
		result.addAll(getAllFields(superClass));
		return result;
	}

}
