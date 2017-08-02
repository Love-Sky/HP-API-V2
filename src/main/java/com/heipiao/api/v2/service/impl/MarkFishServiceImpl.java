package com.heipiao.api.v2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.heipiao.api.v2.domain.FishSiteBase;
import com.heipiao.api.v2.domain.MarkCode;
import com.heipiao.api.v2.domain.MarkFish;
import com.heipiao.api.v2.domain.MarkHaveFish;
import com.heipiao.api.v2.exception.BadRequestException;
import com.heipiao.api.v2.mapper.FishSiteBaseMapper;
import com.heipiao.api.v2.mapper.MarkCodeMapper;
import com.heipiao.api.v2.mapper.MarkFishMapper;
import com.heipiao.api.v2.mapper.MarkHaveFishMapper;
import com.heipiao.api.v2.service.MarkFishService;
import com.heipiao.api.v2.util.ExDateUtils;




@Service
@Transactional(readOnly = true)
public class MarkFishServiceImpl implements MarkFishService{

	@Resource
	private MarkFishMapper markFishMapper; 
	@Resource
	private MarkHaveFishMapper markHaveFishMapper;
	@Resource
	private MarkCodeMapper markCodeMapper;
	@Resource
	private FishSiteBaseMapper fishSiteBaseMapper;
	
	@Override
	public List<MarkFish> getMarkFishList(Integer siteUid,Integer status) {
		List<MarkFish> list = markFishMapper.getMarkFishByUid(siteUid, status);
		return list;
	}

	@Override
	public List<MarkHaveFish> getMarkHaveFishList(Integer uid, Integer start) {
		List<MarkHaveFish> list = markHaveFishMapper.getMarkHaveFishByUid(uid, start);
		return list;
	}

	@Override
	public List<MarkCode> getMarkCode(Integer status) {
		List<MarkCode> list = markCodeMapper.getAbleMark(status);
		return list;
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void addSiteMarkFish(MarkFish markFish) {
		Integer status = 1;
		markCodeMapper.updateMarkCode(status,markFish.getMarkNum());
		markFishMapper.addMarkFish(markFish);
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void updateSiteMarkFish(MarkFish markFish) {
		markFishMapper.updateMarkFish(markFish);
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void deleteMarkFishList(Integer id) {
		markFishMapper.deleteDeliveryAddress(id);
	}

	@Override
	public List<MarkHaveFish> getMarkHaveFishOfmarkNumList(String marknum,Integer start) {
		List<MarkHaveFish> list = markHaveFishMapper.getMarkHaveFishByMarkNum(marknum, start);
		return list;
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void updateMarkFish(Integer uid,String marknum,Integer status) {
		markFishMapper.updateMarkFishByUid(uid,marknum,status);	
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void updatemarkHaveFish(MarkHaveFish markHaveFish) {
		markHaveFish.setAuditTime(ExDateUtils.getSqlDate());
		markHaveFishMapper.updateMarkHaveFish(markHaveFish);
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void updateFishSiteBase(Integer uid) {
		Integer applyMark = 1;
		FishSiteBase site = fishSiteBaseMapper.getFishSiteBaseByUid(uid);
		if(site.getApplyMark() == 1){
			throw new BadRequestException("已申请"); 
		}
		fishSiteBaseMapper.updateFishSiteBasemark(uid, applyMark);	
	}
	
	
}
