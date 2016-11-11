/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.app.framework.service.impl;

import com.app.common.CollectionUtil;
import com.app.common.MapUtils;
import com.app.framework.entity.AutoPrimaryKey;
import com.app.framework.entity.IdWorkPrimaryKey;
import com.app.framework.entity.InputPrimaryKey;
import com.app.framework.entity.UuidPrimaryKey;
import com.app.framework.service.IService;
import com.app.mybatisplus.annotations.IdType;
import com.app.mybatisplus.exceptions.MybatisPlusException;
import com.app.mybatisplus.mapper.BaseMapper;
import com.app.mybatisplus.mapper.Wrapper;
import com.app.mybatisplus.plugins.Page;
import com.app.mybatisplus.toolkit.ReflectionKit;
import com.app.mybatisplus.toolkit.StringUtils;
import com.app.mybatisplus.toolkit.TableInfo;
import com.app.mybatisplus.toolkit.TableInfoHelper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 * </p>
 *
 * @author hubin
 * @Date 2016-04-20
 */
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

	protected static final Logger logger = Logger.getLogger("ServiceImpl");

	@Autowired
	protected M baseMapper;

	/**
	 * 判断数据库操作是否成功
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	protected boolean retBool(int result) {
		return result >= 1;
	}

	/**
	 * 根据对象主键属性正确的调用保存或修改方法
	 *
	 * @param entity
	 * @return boolean
	 * @throws MybatisPlusException
	 *             entity need @Id
	 * @author Caratacus
	 * @date 2016/8/27 0027
	 * @version 1.0
	 */
	@Override
	public boolean saveOrUpdate(T entity) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo) {
				if (entity instanceof AutoPrimaryKey) {
					AutoPrimaryKey auto = (AutoPrimaryKey) entity;
					return saveOrUpdate(auto.getId(), entity, tableInfo);
				} else if (entity instanceof UuidPrimaryKey) {
					UuidPrimaryKey uuid = (UuidPrimaryKey) entity;
					return saveOrUpdate(uuid.getId(), entity, tableInfo);
				} else if (entity instanceof IdWorkPrimaryKey) {
					IdWorkPrimaryKey idwork = (IdWorkPrimaryKey) entity;
					return saveOrUpdate(idwork.getId(), entity, tableInfo);
				} else if (entity instanceof InputPrimaryKey) {
					InputPrimaryKey input = (InputPrimaryKey) entity;
					return saveOrUpdate(input.getId(), entity, tableInfo);
				}
			} else {
				throw new MybatisPlusException("Error:  Cannot execute. Could not find @TableId.");
			}
		}
		return false;
	}

	/**
	 * 类型转换执行saveOrUpdate
	 *
	 * @param id
	 * @param entity
	 * @return boolean
	 * @author Caratacus
	 * @date 2016/9/16 0027
	 * @version 1.0
	 */
	private boolean saveOrUpdate(Serializable id, T entity, TableInfo tableInfo) {
		if (null != id) {
			return updateById(entity);
		} else {
			/* 特殊处理 INPUT 主键策略逻辑 */
			if (IdType.INPUT == tableInfo.getIdType()) {
				T entityValue = selectById((Serializable) id);
				if (null != entityValue) {
					return updateById(entity);
				} else {
					return insert(entity);
				}
			}
			return insert(entity);
		}
	}

	/**
	 * <p>
	 * SQL 构建方法
	 * </p>
	 * 
	 * @param sql
	 *            SQL 语句
	 * @param args
	 *            执行参数
	 * @return
	 */
	protected String sqlBuilder(String sql, Object... args) {
		if (null == sql) {
			throw new IllegalArgumentException("Error: sql Can not be empty.");
		}
		return StringUtils.sqlArgsFill(sql, args);
	}

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity
	 *            实体对象
	 * @return boolean
	 */
	public boolean insertOrUpdate(T entity) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo) {
				Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
				if (null == idVal || "".equals(idVal)) {
					return insert(entity);
				} else {
					/* 特殊处理 INPUT 主键策略逻辑 */
					if (IdType.INPUT == tableInfo.getIdType()) {
						T entityValue = selectById((Serializable) idVal);
						if (null != entityValue) {
							return updateById(entity);
						} else {
							return insert(entity);
						}
					}
					return updateById(entity);
				}
			} else {
				throw new MybatisPlusException("Error:  Can not execute. Could not find @TableId.");
			}
		}
		return false;
	}

	public boolean insert(T entity) {
		return retBool(baseMapper.insert(entity));
	}

	public boolean insertBatch(List<T> entityList) {
		if (CollectionUtil.isEmpty(entityList)) {
			throw new IllegalArgumentException("Error: entityList must not be empty");
		}
		return retBool(baseMapper.insertBatch(entityList));
	}

	/**
	 * 批量插入
	 *
	 * @param entityList
	 * @param batchSize
	 * @return
	 */
	public boolean insertBatch(List<T> entityList, int batchSize) {
		if (CollectionUtil.isEmpty(entityList)) {
			throw new IllegalArgumentException("Error: entityList must not be empty");
		}
		TableInfo tableInfo = TableInfoHelper.getTableInfo(currentModleClass());
		if (null == tableInfo) {
			throw new MybatisPlusException("Error: Cannot execute insertBatch Method, ClassGenricType not found .");
		}
		SqlSession batchSqlSession = tableInfo.getSqlSessionFactory().openSession(ExecutorType.BATCH,
				false);
		try {
			int size = entityList.size();
			for (int i = 0; i < size; i++) {
				baseMapper.insert(entityList.get(i));
				if (i % batchSize == 0) {
					batchSqlSession.flushStatements();
				}
			}
			batchSqlSession.flushStatements();
		} catch (Exception e) {
			logger.warning("Error: Cannot execute insertBatch Method. Cause:" + e);
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	protected Class<T> currentModleClass() {
		return ReflectionKit.getSuperClassGenricType(getClass(), 1);
	}

	public boolean deleteById(Serializable id) {
		return retBool(baseMapper.deleteById(id));
	}

	public boolean deleteByMap(Map<String, Object> columnMap) {
		return retBool(baseMapper.deleteByMap(columnMap));
	}

	public boolean delete(Wrapper<T> wrapper) {
		return retBool(baseMapper.delete(wrapper));
	}

	public boolean deleteBatchIds(List<? extends Serializable> idList) {
		return retBool(baseMapper.deleteBatchIds(idList));
	}

	public boolean updateById(T entity) {
		return retBool(baseMapper.updateById(entity));
	}

	public boolean update(T entity, Wrapper<T> wrapper) {
		return retBool(baseMapper.update(entity, wrapper));
	}

	public boolean updateBatchById(List<T> entityList) {
		return retBool(baseMapper.updateBatchById(entityList));
	}

	public T selectById(Serializable id) {
		return baseMapper.selectById(id);
	}

	public List<T> selectBatchIds(List<? extends Serializable> idList) {
		return baseMapper.selectBatchIds(idList);
	}

	public List<T> selectByMap(Map<String, Object> columnMap) {
		return baseMapper.selectByMap(columnMap);
	}

	public T selectOne(Wrapper<T> wrapper) {
		List<T> list = baseMapper.selectList(wrapper);
		if (CollectionUtil.isNotEmpty(list)) {
			int size = list.size();
			if (size > 1) {
				logger.warning(String.format("Warn: selectOne Method There are  %s results.", size));
			}
			return list.get(0);
		}
		return null;
	}

	public int selectCount(Wrapper<T> wrapper) {
		return baseMapper.selectCount(wrapper);
	}

	public List<T> selectList(Wrapper<T> wrapper) {
		return baseMapper.selectList(wrapper);
	}

	public Page<T> selectPage(Page<T> page) {
		page.setRecords(baseMapper.selectPage(page, null));
		return page;
	}

	public Page<T> selectPage(Page<T> page, Wrapper<T> wrapper) {
		if (null != wrapper) {
			wrapper.orderBy(page.getOrderByField(), page.isAsc());
		}
		page.setRecords(baseMapper.selectPage(page, wrapper));
		return page;
	}

	// ------------------------------------------执行SQL部分-------------------------------------------//
	@Override
	public boolean insertSql(String sql, Object... args) {
		return retBool(baseMapper.insertSql(sqlBuilder(sql, args)));
	}

	@Override
	public boolean deleteSql(String sql, Object... args) {
		return retBool(baseMapper.deleteSql(sqlBuilder(sql, args)));
	}

	@Override
	public boolean updateSql(String sql, Object... args) {
		return retBool(baseMapper.updateSql(sqlBuilder(sql, args)));
	}

	@Override
	public List<Map<String, Object>> selectListSql(String sql, Object... args) {
		return baseMapper.selectListSql(sqlBuilder(sql, args));
	}

	@Override
	public <V> List<V> selectListSql(String sql, Class<V> clazz, Object... args) {
		List<Map<String, Object>> maps = baseMapper.selectListSql(sqlBuilder(sql, args));
		List<V> list = null;
		try {
			list = MapUtils.mapsToBeans(maps, clazz);
		} catch (Exception e) {
			logger.warning("Warn: Unexpected exception on selectListSql.  Cause:" + e);
		}
		return list;
	}

	@Override
	public Page selectPageSql(Page page, String sql, Object... args) {
		page.setRecords(baseMapper.selectPageSql(page, sqlBuilder(sql, args)));
		return page;
	}
}
