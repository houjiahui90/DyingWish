/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.service;

import java.util.List;

import com.houjiahui.core.entity.DataRule;
import com.houjiahui.core.mapper.DataRuleMapper;
import com.houjiahui.core.persistence.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 数据权限Service
 * @author lgf
 * @version 2017-04-02
 */
@Service
@Transactional(readOnly = true)
public class DataRuleService extends CrudService<DataRuleMapper, DataRule> {
	@Autowired
	private DataRuleMapper dataRuleMapper;
	
	public DataRule get(String id) {
		return super.get(id);
	}
	
	public List<DataRule> findList(DataRule dataRule) {
		return super.findList(dataRule);
	}
	
	public Page<DataRule> findPage(Page<DataRule> page, DataRule dataRule) {
		return super.findPage(page, dataRule);
	}
	
	@Transactional(readOnly = false)
	public void save(DataRule dataRule) {
		super.save(dataRule);
	}
	
	@Transactional(readOnly = false)
	public void delete(DataRule dataRule) {
		//解除数据权限角色关联
		dataRuleMapper.deleteRoleDataRule(dataRule);
		super.delete(dataRule);
	
	}
	
	
	
	
}