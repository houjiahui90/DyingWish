/**
 * Copyright &copy; 2015-2020 <a href="http://www.nxzhxt.com/">nxzhxt</a> All rights reserved.
 */
package com.houjiahui.core.entity;


import com.houjiahui.core.persistence.DataEntity;
import com.houjiahui.common.utils.excel.annotation.ExcelField;

/**
 * 数据字典Entity
 * @author lgf
 * @version 2017-01-16
 */
public class DictValue extends DataEntity<DictValue> {
	
	private static final long serialVersionUID = 1L;
	private String label;		// 标签名
	private String value;		// 键值
	private Integer sort;		// 排序
	private DictType dictType;		// 外键 父类
	
	public DictValue() {
		super();
	}

	public DictValue(String id){
		super(id);
	}

	public DictValue(DictType dictType){
		this.dictType = dictType;
	}

	@ExcelField(title="标签名", align=2, sort=1)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@ExcelField(title="键值", align=2, sort=2)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@ExcelField(title="排序", align=2, sort=3)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public DictType getDictType() {
		return dictType;
	}

	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}
	
}