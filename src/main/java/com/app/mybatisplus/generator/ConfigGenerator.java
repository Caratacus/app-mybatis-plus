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
package com.app.mybatisplus.generator;

import com.app.mybatisplus.generator.ConfigDataSource;
import com.app.mybatisplus.generator.ConfigIdType;
import com.app.mybatisplus.annotations.IdType;

/**
 * <p>
 * 生成器配置类
 * ********************************* 使用前必读 *********************
 * saveDir 文件生成目录
 * entity_package entity 包路径
 * mapper_package mapper 包路径
 * xmlPackage xx_mapper.xml 包路径，默认为mapper/xml
 * servicePackage service 包路径
 * serviceImplPackage serviceImpl包路径，默认为service/impl
 * superServiceImpl service 父类包路径名称
 * tableNames   要生成的表名称，如为空就直接指定所有表.格式为逗号分割
 * fileOverride 是否覆盖当前已有文件
 * -------------------------------------
 * 以下数据库相关配置：
 * -------------------------------------
 * db_include_prefix 表是否包含前缀，例如: tb_xxx 其中 tb_ 为前缀
 * db_driverName 驱动
 * db_user 用户名
 * db_password 密码
 * db_url 连接地址
 **************************************************************
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public class ConfigGenerator {

	private String saveDir;

	private String entityPackage;

	private String mapperPackage;

	private String xmlPackage;

	private String servicePackage;

	private String serviceImplPackage;

	private String superServiceImpl;

	/*
	 * 自定义 mapperName serviceName serviceImplName
	 */
	private String mapperName = "%sMapper";

	private String serviceName = "I%sService";

	private String serviceImplName = "%sServiceImpl";

	/*
	 * 指定生成表名
	 */
	private String[] tableNames = null;

	/*
	 * 是否覆盖当前路径下已有文件（默认 true）
	 */
	private boolean fileOverride = true;

	/* db_config */
	private boolean dbPrefix = false;

	/*
	 * 数据库字段使用下划线命名（默认 false）
	 */
	private boolean dbColumnUnderline = false;

	private String dbDriverName;

	private String dbUser;

	private String dbPassword;

	private String dbUrl;

	private IdType idType = null;

	private ConfigDataSource configDataSource = ConfigDataSource.MYSQL;

	private ConfigIdType configIdType = ConfigIdType.LONG;

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}

	public String getSuperServiceImpl() {
		if (superServiceImpl == null || "".equals(superServiceImpl)) {
			if (this.getConfigIdType() == ConfigIdType.STRING) {
				return "com.app.framework.service.impl.CommonServiceImpl";
			} else {
				return "com.app.framework.service.impl.SuperServiceImpl";
			}
		}
		return superServiceImpl;
	}

	public void setSuperServiceImpl(String superServiceImpl) {
		this.superServiceImpl = superServiceImpl;
	}

	public boolean verifyDefinedName(String definedName) {
		return (null != definedName && definedName.contains("%s"));
	}

	public String getMapperName() {
		return mapperName;
	}

	public void setMapperName(String mapperName) {
		if (verifyDefinedName(mapperName)) {
			this.mapperName = mapperName;
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		if (verifyDefinedName(serviceName)) {
			this.serviceName = serviceName;
		}
	}

	public String getServiceImplName() {
		return serviceImplName;
	}

	public void setServiceImplName(String serviceImplName) {
		if (verifyDefinedName(serviceImplName)) {
			this.serviceImplName = serviceImplName;
		}
	}

	public boolean isDbPrefix() {
		return dbPrefix;
	}

	public void setDbPrefix(boolean dbPrefix) {
		this.dbPrefix = dbPrefix;
	}

	public boolean isDbColumnUnderline() {
		return dbColumnUnderline;
	}

	public void setDbColumnUnderline(boolean dbColumnUnderline) {
		this.dbColumnUnderline = dbColumnUnderline;
	}

	public String getDbDriverName() {
		return dbDriverName;
	}

	public void setDbDriverName(String dbDriverName) {
		this.dbDriverName = dbDriverName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public IdType getIdType() {
		return idType;
	}

	public void setIdType(IdType idType) {
		this.idType = idType;
	}

	public ConfigDataSource getConfigDataSource() {
		return configDataSource;
	}

	public void setConfigDataSource(ConfigDataSource configDataSource) {
		this.configDataSource = configDataSource;
	}

	public ConfigIdType getConfigIdType() {
		return configIdType;
	}

	public void setConfigIdType(ConfigIdType configIdType) {
		this.configIdType = configIdType;
	}

	public String getXmlPackage() {
		if (null == xmlPackage || "".equals(xmlPackage)) {
			xmlPackage = mapperPackage + ".xml";
		}
		return xmlPackage;
	}

	public void setXmlPackage(String xmlPackage) {
		this.xmlPackage = xmlPackage;
	}

	public String[] getTableNames() {
		return tableNames;
	}

	public void setTableNames(String[] tableNames) {
		this.tableNames = tableNames;
	}

	public boolean isFileOverride() {
		return fileOverride;
	}

	public void setFileOverride(boolean fileOverride) {
		this.fileOverride = fileOverride;
	}

	public String getServiceImplPackage() {
		if (null == serviceImplPackage || "".equals(serviceImplPackage)) {
			serviceImplPackage = servicePackage + ".impl";
		}
		return serviceImplPackage;
	}

	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}
}
