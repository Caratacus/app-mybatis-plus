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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.app.common.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.app.framework.service.IService;
import com.app.mybatisplus.mapper.BaseMapper;
import com.app.mybatisplus.mapper.EntityWrapper;
import com.app.mybatisplus.plugins.Page;

/**
 * <p>
 * IService 实现类（ 泛型：M 是 mapper 对象， T 是实体 ， I 是主键泛型 ）
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
        if (null != entityWrapper) {
            entityWrapper.orderBy(page.getOrderByField(), page.isAsc());
        }
        page.setRecords(baseMapper.selectPage(page, entityWrapper));
        return page;
    }

    @Override
    public List query(EntityWrapper<T> entityWrapper) {
        List<T> ts = selectList(entityWrapper);
        return MapUtils.beanToListMapNotNull(ts);
    }
}
