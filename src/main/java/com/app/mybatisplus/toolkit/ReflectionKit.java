package com.app.mybatisplus.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 反射工具类.
 */
public class ReflectionKit {

	private static Logger logger = LoggerFactory.getLogger(ReflectionKit.class);

	/**
	 * 调用对象的get方法检查对象所以属性是否为null
	 * 
	 * @param bean
	 * @return boolean true对象所有属性不为null,false对象所有属性为null
	 */
	public static boolean checkFieldValueNull(Object bean) {
		boolean result = false;
		if (bean == null) {
			return true;
		}
		Class<?> cls = bean.getClass();
		Method[] methods = cls.getDeclaredMethods();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);

		if (tableInfo == null) {
			logger.warn("Error: Could not find @TableId.");
			return false;
		}
		List<TableFieldInfo> fieldList = tableInfo.getFieldList();
		for (TableFieldInfo tableFieldInfo : fieldList) {
			String fieldGetName = StringUtils.concatCapitalize("get", tableFieldInfo.getProperty());
			if (!checkMethod(methods, fieldGetName)) {
				continue;
			}
			try {
				Method method = cls.getMethod(fieldGetName);
				Object obj = method.invoke(bean);
				if (null != obj) {
					result = true;
					break;
				}
			} catch (Exception e) {
				logger.warn("Unexpected exception on checkFieldValueNull.  Cause:" + e);
			}

		}
		return result;
	}

	/**
	 * 判断是否存在某属性的 get方法
	 *
	 * @param methods
	 *            对象所有方法
	 * @param method
	 *            当前检查的方法
	 * @return boolean true存在,false不存在
	 */
	public static boolean checkMethod(Method[] methods, String method) {
		for (Method met : methods) {
			if (method.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

}