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
import com.app.framework.entity.AutoPrimaryKey;
import com.app.framework.entity.IdWorkPrimaryKey;
import com.app.framework.entity.InputPrimaryKey;
import com.app.framework.entity.UuidPrimaryKey;
import com.app.framework.service.IService;
import com.app.mybatisplus.annotations.IdType;
import com.app.mybatisplus.exceptions.MybatisPlusException;
import com.app.mybatisplus.mapper.BaseMapper;
import com.app.mybatisplus.mapper.EntityWrapper;
import com.app.mybatisplus.plugins.Page;
import com.app.mybatisplus.toolkit.ReflectionKit;
import com.app.mybatisplus.toolkit.TableInfo;
import com.app.mybatisplus.toolkit.TableInfoHelper;
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
	 * @param isSelective
	 * @return boolean
	 * @throws MybatisPlusException
	 *             entity need @Id
	 * @author Caratacus
	 * @date 2016/8/27 0027
	 * @version 1.0
	 */
	private boolean saveOrUpdate(T entity, boolean isSelective) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo) {
				if (entity instanceof AutoPrimaryKey) {
					AutoPrimaryKey auto = (AutoPrimaryKey) entity;
					return saveOrUpdate(auto.getId(), entity, isSelective, tableInfo);
				} else if (entity instanceof UuidPrimaryKey) {
					UuidPrimaryKey uuid = (UuidPrimaryKey) entity;
					return saveOrUpdate(uuid.getId(), entity, isSelective, tableInfo);
				} else if (entity instanceof IdWorkPrimaryKey) {
					IdWorkPrimaryKey idwork = (IdWorkPrimaryKey) entity;
					return saveOrUpdate(idwork.getId(), entity, isSelective, tableInfo);
				} else if (entity instanceof InputPrimaryKey) {
					InputPrimaryKey input = (InputPrimaryKey) entity;
					return saveOrUpdate(input.getId(), entity, isSelective, tableInfo);
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
	 * @param isSelective
	 * @return boolean
	 * @author Caratacus
	 * @date 2016/9/16 0027
	 * @version 1.0
	 */
	private boolean saveOrUpdate(Serializable id, T entity, boolean isSelective, TableInfo tableInfo) {
		if (null != id) {
			return isSelective ? updateSelectiveById(entity) : updateById(entity);
		} else {
			/* 特殊处理 INPUT 主键策略逻辑 */
			if (IdType.INPUT == tableInfo.getIdType()) {
				T entityValue = selectById((Serializable) id);
				if (null != entityValue) {
					return isSelective ? updateSelectiveById(entity) : updateById(entity);
				} else {
					return isSelective ? insertSelective(entity) : insert(entity);
				}
			}
			return isSelective ? insertSelective(entity) : insert(entity);
		}
	}

	@Override
	public boolean saveOrUpdate(T entity) {
		return saveOrUpdate(entity, false);
	}

	@Override
	public boolean saveOrUpdateSelective(T entity) {
		return saveOrUpdate(entity, true);
	}

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity
	 *            实体对象
	 * @param isSelective
	 *            true 选择字段 false 不选择字段
	 * @return boolean
	 */
	public boolean insertOrUpdate(T entity, boolean isSelective) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo) {
				Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
				if (null == idVal || "".equals(idVal)) {
					return isSelective ? insertSelective(entity) : insert(entity);
				} else {
					/* 特殊处理 INPUT 主键策略逻辑 */
					if (IdType.INPUT == tableInfo.getIdType()) {
						T entityValue = selectById((Serializable) idVal);
						if (null != entityValue) {
							return isSelective ? updateSelectiveById(entity) : updateById(entity);
						} else {
							return isSelective ? insertSelective(entity) : insert(entity);
						}
					}
					return isSelective ? updateSelectiveById(entity) : updateById(entity);
				}
			} else {
				throw new MybatisPlusException("Error:  Cannot execute. Could not find @TableId.");
			}
		}
		return false;
	}

	public boolean insertOrUpdate(T entity) {
		return insertOrUpdate(entity, false);
	}

	public boolean insertOrUpdateSelective(T entity) {
		return insertOrUpdate(entity, true);
	}

	public boolean insert(T entity) {
		return retBool(baseMapper.insert(entity));
	}

	public boolean insertSelective(T entity) {
		return retBool(baseMapper.insertSelective(entity));
	}

	public boolean insertBatch(List<T> entityList) {
		return retBool(baseMapper.insertBatch(entityList));
	}

	public boolean deleteById(Serializable id) {
		return retBool(baseMapper.deleteById(id));
	}

	public boolean deleteByMap(Map<String, Object> columnMap) {
		return retBool(baseMapper.deleteByMap(columnMap));
	}

	public boolean deleteSelective(T entity) {
		return retBool(baseMapper.deleteSelective(entity));
	}

	public boolean deleteBatchIds(List<? extends Serializable> idList) {
		return retBool(baseMapper.deleteBatchIds(idList));
	}

	public boolean updateById(T entity) {
		return retBool(baseMapper.updateById(entity));
	}

	public boolean updateSelectiveById(T entity) {
		return retBool(baseMapper.updateSelectiveById(entity));
	}

	public boolean update(T entity, T whereEntity) {
		return retBool(baseMapper.update(entity, whereEntity));
	}

	public boolean updateSelective(T entity, T whereEntity) {
		return retBool(baseMapper.updateSelective(entity, whereEntity));
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

	public T selectOne(T entity) {
		return baseMapper.selectOne(entity);
	}

	public T selectOne(EntityWrapper<T> entityWrapper) {
		List<T> list = baseMapper.selectList(entityWrapper);
		if (CollectionUtil.isNotEmpty(list)) {
			int size = list.size();
			if (size > 1) {
				logger.warning("Warn: selectOne Method There are " + size + " results.");
			}
			return list.get(0);
		}
		return null;
	}

	public int selectCount(T entity) {
		return baseMapper.selectCount(entity);
	}

	public int selectCount(EntityWrapper<T> entityWrapper) {
		return baseMapper.selectCountByEw(entityWrapper);
	}

	public List<T> selectList(EntityWrapper<T> entityWrapper) {
		return baseMapper.selectList(entityWrapper);
	}

	public Page<T> selectPage(Page<T> page, EntityWrapper<T> entityWrapper) {
		if (null != entityWrapper) {
			entityWrapper.orderBy(page.getOrderByField(), page.isAsc());
		}
		page.setRecords(baseMapper.selectPage(page, entityWrapper));
		return page;
	}

}
