/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.app.mybatisplus.activerecord;

import com.app.common.CollectionUtil;
import com.app.mybatisplus.exceptions.MybatisPlusException;
import com.app.mybatisplus.mapper.EntityWrapper;
import com.app.mybatisplus.mapper.SqlMethod;
import com.app.mybatisplus.plugins.Page;
import com.app.mybatisplus.toolkit.StringUtils;
import com.app.mybatisplus.toolkit.TableInfo;
import com.app.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.session.SqlSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ActiveRecord 模式 CRUD
 * </p>
 * 
 * @author hubin
 * @param <T>
 * @Date 2016-11-06
 */
@SuppressWarnings({ "serial", "rawtypes" })
public abstract class Model<T extends Model> implements Serializable {

	/**
	 * <p>
	 * 插入
	 * </p>
	 */
	public boolean insert() {
		return retBool(sqlSession().insert(sqlStatement(SqlMethod.INSERT_ONE), this));
	}

	/**
	 * <p>
	 * 插入 OR 更新
	 * </p>
	 */
	public boolean insertOrUpdate() {
		if (null != getPrimaryKey()) {
			// update
			return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE_BY_ID), this));
		} else {
			// insert
			return retBool(sqlSession().insert(sqlStatement(SqlMethod.INSERT_ONE), this));
		}
	}

	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return
	 */
	public boolean deleteById(Serializable id) {
		return retBool(sqlSession().delete(sqlStatement(SqlMethod.DELETE_BY_ID), id));
	}

	/**
	 * 根据主键删除
	 * 
	 * @return
	 */
	public boolean deleteById() {
		return deleteById(getPrimaryKey());
	}

	/**
	 * <p>
	 * 删除记录
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public boolean delete(String whereClause, Object... args) {
		Map<String, Object> map = new HashMap<String, Object>();
		EntityWrapper<T> ew = null;
		if (StringUtils.isNotEmpty(whereClause)) {
			ew = new EntityWrapper<T>();
			ew.addFilter(whereClause, args);
		}
		// delete
		map.put("ew", ew);
		return retBool(sqlSession().delete(sqlStatement(SqlMethod.DELETE), map));
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	public boolean updateById() {
		if (null == getPrimaryKey()) {
			throw new MybatisPlusException("primaryKey is null.");
		}
		// updateById
		return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE_BY_ID), this));
	}

	/**
	 * <p>
	 * 执行 SQL 更新
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public boolean update(String whereClause, Object... args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("et", this);
		if (StringUtils.isNotEmpty(whereClause)) {
			EntityWrapper<T> ew = new EntityWrapper<T>();
			ew.addFilter(whereClause, args);
			map.put("ew", ew);
		}
		// update
		return retBool(sqlSession().update(sqlStatement(SqlMethod.UPDATE), map));
	}

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	public List<T> selectAll() {
		return sqlSession().selectList(sqlStatement(SqlMethod.SELECT_LIST));
	}

	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 * 
	 * @param id
	 *            主键ID
	 * @return
	 */
	public T selectById(Serializable id) {
		return sqlSession().selectOne(sqlStatement(SqlMethod.SELECT_BY_ID), id);
	}

	/**
	 * 根据主键查询
	 * 
	 * @return
	 */
	public T selectById() {
		return selectById(getPrimaryKey());
	}

	/**
	 * <p>
	 * 查询总记录数
	 * </p>
	 * 
	 * @param columns
	 *            查询字段
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public List<T> selectList(String columns, String whereClause, Object... args) {
		EntityWrapper<T> ew = new EntityWrapper<T>(null, columns);
		if (StringUtils.isNotEmpty(whereClause)) {
			ew.addFilter(whereClause, args);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", ew);
		return sqlSession().selectList(sqlStatement(SqlMethod.SELECT_LIST), map);
	}

	/**
	 * <p>
	 * 执行 SQL 查询
	 * </p>
	 * 
	 * @param sql
	 *            SQL 语句
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql) {
		return sqlSession().selectList(table().getCurrentNamespace() + ".selectListSql", sql);
	}

	/**
	 * 查询所有
	 * 
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public List<T> selectList(String whereClause, Object... args) {
		return selectList(null, whereClause, args);
	}

	/**
	 * 查询一条记录
	 * 
	 * @param columns
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public T selectOne(String columns, String whereClause, Object... args) {
		List<T> tl = selectList(columns, whereClause, args);
		if (CollectionUtil.isEmpty(tl)) {
			return null;
		}
		return tl.get(0);
	}

	/**
	 * 查询一条记录
	 * 
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public T selectOne(String whereClause, Object... args) {
		return selectOne(null, whereClause, args);
	}

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 * 
	 * @param page
	 *            翻页查询条件
	 * @param columns
	 *            查询字段
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public Page<T> selectPage(Page<T> page, String columns, String whereClause, Object... args) {
		EntityWrapper<T> ew = new EntityWrapper<T>(null, columns);
		if (StringUtils.isNotEmpty(whereClause)) {
			ew.addFilter(whereClause, args);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ew", ew);
		List<T> tl = sqlSession().selectList(sqlStatement(SqlMethod.SELECT_PAGE), map, page);
		page.setRecords(tl);
		return page;
	}

	/**
	 * 查询所有(分页)
	 * 
	 * @param page
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public Page<T> selectPage(Page<T> page, String whereClause, Object... args) {
		return selectPage(page, null, whereClause, args);
	}

	/**
	 * 查询所有(分页)
	 * 
	 * @param page
	 * @return
	 */
	public Page<T> selectPage(Page<T> page) {
		return selectPage(page, null);
	}

	/**
	 * <p>
	 * 查询总数
	 * </p>
	 * 
	 * @param whereClause
	 *            查询条件
	 * @param args
	 *            查询条件值
	 * @return
	 */
	public int selectCount(String whereClause, Object... args) {
		List<T> tl = selectList(whereClause, args);
		if (CollectionUtil.isEmpty(tl)) {
			return 0;
		}
		return tl.size();
	}

	/**
	 * <p>
	 * 查询总数
	 * </p>
	 * 
	 * @return
	 */
	public int selectCount() {
		return selectCount(null);
	}

	/**
	 * <p>
	 * 判断数据库操作是否成功
	 * </p>
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	private boolean retBool(int result) {
		return result >= 1;
	}

	/**
	 * 获取Session
	 * 
	 * @param autoCommit
	 *            true自动提交false则相反
	 * @return SqlSession
	 */
	private SqlSession sqlSession(boolean autoCommit) {
		return table().getSqlSessionFactory().openSession(autoCommit);
	}

	/**
	 * 获取Session 默认自动提交
	 * <p>
	 * 特别说明:这里获取SqlSession时这里虽然设置了自动提交但是如果事务托管了的话 是不起作用的 切记!!
	 * <p/>
	 *
	 * @return SqlSession
	 */
	private SqlSession sqlSession() {
		return table().getSqlSessionFactory().openSession(true);
	}

	/**
	 * 获取SqlStatement
	 * 
	 * @param sqlMethod
	 * @return
	 */
	private String sqlStatement(SqlMethod sqlMethod) {
		return table().getSqlStatement(sqlMethod);
	}

	/**
	 * 获取TableInfo
	 * 
	 * @return TableInfo
	 */
	private TableInfo table() {
		return TableInfoHelper.getTableInfo(getClass());
	}

	protected abstract Serializable getPrimaryKey();

}
