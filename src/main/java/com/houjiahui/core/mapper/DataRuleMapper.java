/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.mapper;

import java.util.List;

import com.houjiahui.core.entity.DataRule;
import com.houjiahui.DyingWish.entity.User;
import com.houjiahui.core.persistence.BaseMapper;
import com.houjiahui.core.persistence.annotation.MyBatisMapper;

/**
 * 数据权限MAPPER接口
 * @author lgf
 * @version 2017-04-02
 */
@MyBatisMapper
public interface DataRuleMapper extends BaseMapper<DataRule> {

	public void deleteRoleDataRule(DataRule dataRule);
	
	public List<DataRule> findByUserId(User user);
}