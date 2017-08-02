package com.heipiao.api.v2.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.heipiao.api.v2.domain.MarkCode;

/**
 * 有鱼默认设置
 * 
 * @author Duh
 * @version 3.0
 * @date 2017.07.10
 *
 */
@Mapper
public interface MarkCodeMapper {
	
	List<MarkCode> getAbleMark(@Param("status")Integer status);
	
	void addMarkCode(MarkCode markCode);
	
	void updateMarkCode(@Param("status") Integer status,@Param("markNum")String markNum);
	
	void deleteMarkCode(@Param("id")Integer id);
}
