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

import com.app.common.MapUtils;
import com.app.framework.entity.AutoPrimaryKey;
import com.app.framework.entity.IdWorkPrimaryKey;
import com.app.framework.entity.InputPrimaryKey;
import com.app.framework.entity.UuidPrimaryKey;
import com.app.framework.service.IService;
import com.app.mybatisplus.exceptions.MybatisPlusException;
import com.app.mybatisplus.mapper.BaseMapper;
import com.app.mybatisplus.mapper.EntityWrapper;
import com.app.mybatisplus.plugins.Page;
import com.app.mybatisplus.toolkit.TableInfo;
import com.app.mybatisplus.toolkit.TableInfoHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 * </p>
 *
 * @author hubin
 * @Date 2016-04-20
 */
public class ServiceImpl<M extends BaseMapper<T, PK>, T, PK extends Serializable> implements IService<T, PK> {

    @Autowired
    protected M baseMapper;

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    protected boolean retBool(int result) {
        return result >= 1;
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

    public boolean deleteById(PK id) {
        return retBool(baseMapper.deleteById(id));
    }

    public boolean deleteByMap(Map<String, Object> columnMap) {
        return retBool(baseMapper.deleteByMap(columnMap));
    }

    public boolean deleteSelective(T entity) {
        return retBool(baseMapper.deleteSelective(entity));
    }

    public boolean deleteBatchIds(List<PK> idList) {
        return retBool(baseMapper.deleteBatchIds(idList));
    }

    public boolean updateById(T entity) {
        return retBool(baseMapper.updateById(entity));
    }

    public boolean updateSelectiveById(T entity) {
        return retBool(baseMapper.updateSelectiveById(entity));
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
     * 根据对象主键属性正确的调用保存或修改方法
     *
     * @param entity
     * @param isSelective
     * @return boolean
     * @throws MybatisPlusException entity need @Id
     * @author Caratacus
     * @date 2016/8/27 0027
     * @version 1.0
     */
    private boolean saveOrUpdate(T entity, boolean isSelective) {
        if (entity instanceof AutoPrimaryKey) {
            AutoPrimaryKey auto = (AutoPrimaryKey) entity;
            Long id = auto.getId();
            return saveOrUpdate(id, entity, isSelective);
        } else if (entity instanceof UuidPrimaryKey) {
            UuidPrimaryKey uuid = (UuidPrimaryKey) entity;
            String id = uuid.getId();
            return saveOrUpdate(id, entity, isSelective);
        }else if(entity instanceof IdWorkPrimaryKey){
            IdWorkPrimaryKey idwork = (IdWorkPrimaryKey) entity;
            Long id = idwork.getId();
            return saveOrUpdate(id, entity, isSelective);
        }else if(entity instanceof InputPrimaryKey){
            InputPrimaryKey input = (InputPrimaryKey) entity;
            Long id = input.getId();
            return saveOrUpdate(id, entity, isSelective);
        } else {
            throw new MybatisPlusException("Not found @Id annotation in " + entity.getClass() + ",saveOrUpdate is Fail!");
        }

    }

    /**
     * <p>
     * TableId 注解存在更新记录，否插入一条记录
     * </p>
     *
     * @param entity
     *            实体对象
     * @param selective
     *            true 选择字段 false 不选择字段
     * @return boolean
     */
    public boolean insertOrUpdate(T entity, boolean selective) {
        if (null != entity) {
            Class<?> cls = entity.getClass();
            TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
            if (null != tableInfo) {
                try {
                    Method m = cls.getMethod("get" + StringUtils.capitalize(tableInfo.getKeyProperty()));
                    Serializable idVal = (Serializable) m.invoke(entity);
                    return saveOrUpdate(idVal, entity, selective);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new MybatisPlusException("Error:  Cannot execute. Could not find @TableId.");
            }
        }
        return false;
    }

    @Override
    public boolean insertOrUpdate(T entity) {
        return insertOrUpdate(entity, false);
    }

    @Override
    public boolean insertOrUpdateSelective(T entity) {
        return insertOrUpdate(entity, true);
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
    private boolean saveOrUpdate(Serializable id, T entity, boolean isSelective) {
        if (null != id) {
            return isSelective ? updateSelectiveById(entity) : updateById(entity);
        } else {
            return isSelective ? insertSelective(entity) : insert(entity);
        }
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

    public T selectById(PK id) {
        return baseMapper.selectById(id);
    }

    public List<T> selectBatchIds(List<PK> idList) {
        return baseMapper.selectBatchIds(idList);
    }

    public List<T> selectByMap(Map<String, Object> columnMap) {
        return baseMapper.selectByMap(columnMap);
    }

    public T selectOne(T entity) {
        return baseMapper.selectOne(entity);
    }

    public int selectCount(T entity) {
        return baseMapper.selectCount(entity);
    }

    public List<T> selectList(EntityWrapper<T> entityWrapper) {
        return baseMapper.selectList(entityWrapper);
    }

    public Page<T> selectPage(Page<T> page, EntityWrapper<T> entityWrapper) {
        page.setRecords(baseMapper.selectPage(page, entityWrapper));
        return page;
    }

    @Override
    public List query(EntityWrapper<T> entityWrapper) {
        List<T> ts = selectList(entityWrapper);
        return MapUtils.beanToListMapNotNull(ts);
    }
}
