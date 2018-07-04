/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.service;

import java.util.List;

import com.houjiahui.common.utils.DictUtils;
import com.houjiahui.core.entity.DictType;
import com.houjiahui.core.entity.DictValue;
import com.houjiahui.core.mapper.DictTypeMapper;
import com.houjiahui.core.mapper.DictValueMapper;
import com.houjiahui.core.persistence.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.houjiahui.common.utils.CacheUtils;
import com.houjiahui.common.utils.StringUtils;

/**
 * 数据字典Service
 * @author lgf
 * @version 2017-01-16
 */
@Service
@Transactional(readOnly = true)
public class DictTypeService extends CrudService<DictTypeMapper, DictType> {

	@Autowired
	private DictValueMapper dictValueMapper;
	
	public DictType get(String id) {
		DictType dictType = super.get(id);
		dictType.setDictValueList(dictValueMapper.findList(new DictValue(dictType)));
		return dictType;
	}
	
	public DictValue getDictValue(String id) {
		return dictValueMapper.get(id);
	}
	
	public List<DictType> findList(DictType dictType) {
		return super.findList(dictType);
	}
	
	public Page<DictType> findPage(Page<DictType> page, DictType dictType) {
		return super.findPage(page, dictType);
	}
	
	@Transactional(readOnly = false)
	public void save(DictType dictType) {
		super.save(dictType);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	@Transactional(readOnly = false)
	public void saveDictValue(DictValue dictValue) {
		if (StringUtils.isBlank(dictValue.getId())){
			dictValue.preInsert();
			dictValueMapper.insert(dictValue);
		}else{
			dictValue.preUpdate();
			dictValueMapper.update(dictValue);
		}
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	@Transactional(readOnly = false)
	public void deleteDictValue(DictValue dictValue) {
		dictValueMapper.delete(dictValue);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	@Transactional(readOnly = false)
	public void delete(DictType dictType) {
		super.delete(dictType);
		dictValueMapper.delete(new DictValue(dictType));
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
}