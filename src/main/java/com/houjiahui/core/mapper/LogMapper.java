/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.mapper;

import com.houjiahui.core.entity.Log;
import com.houjiahui.core.persistence.BaseMapper;
import com.houjiahui.core.persistence.annotation.MyBatisMapper;

/**
 * 日志MAPPER接口
 * @author nxzhxt
 * @version 2017-05-16
 */
@MyBatisMapper
public interface LogMapper extends BaseMapper<Log> {

	public void empty();
}
