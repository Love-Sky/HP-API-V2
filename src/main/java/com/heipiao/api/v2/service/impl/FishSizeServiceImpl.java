package com.heipiao.api.v2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.heipiao.api.v2.component.map.AMapService;
import com.heipiao.api.v2.domain.FishSiteBaseInfo;
import com.heipiao.api.v2.domain.FishSiteEmployee;
import com.heipiao.api.v2.domain.HaveFish;
import com.heipiao.api.v2.domain.Location;
import com.heipiao.api.v2.domain.Region;
import com.heipiao.api.v2.exception.BadRequestException;
import com.heipiao.api.v2.mapper.FishSiteBaseInfoMapper;
import com.heipiao.api.v2.mapper.FishSiteBaseMapper;
import com.heipiao.api.v2.mapper.FishSiteEmployeeMapper;
import com.heipiao.api.v2.mapper.HaveFishMapper;
import com.heipiao.api.v2.repository.RegionRepository;
import com.heipiao.api.v2.service.FishSizeService;




@Service
@Transactional(readOnly = true)
public class FishSizeServiceImpl implements FishSizeService{

	@Resource
	private FishSiteBaseInfoMapper fishSiteBaseInfoMapper;
	@Resource
	private HaveFishMapper haveFishMapper;
	@Resource
	private FishSiteBaseMapper fishSiteBaseMapper;
	@Resource
	private FishSiteEmployeeMapper fishSiteEmployeeMapper;
	@Resource
	private AMapService amapService;
	@Resource
	private RegionRepository regionRepository;
	
	
	@Override
	public List<FishSiteBaseInfo> getFishsiteList(Integer start, Integer size, Double longitude, Double latitude) {
		List<FishSiteBaseInfo> list = fishSiteBaseInfoMapper.getFishSite(start, size, longitude, latitude);
		return list;
	}

	@Override
	public FishSiteBaseInfo getfishSiteDetial(Integer uid, Double longitude, Double latitude) {
		
		return fishSiteBaseInfoMapper.getFishSiteByuid(uid, longitude, latitude);
		
	}

	@Override
	public List<HaveFish> getHaveFishAllList(Integer uid,Integer start,Double lon,Double lat) {
		 List<HaveFish> list = haveFishMapper.getSiteHaveFishList(uid, start, lon, lat);
		return list;
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void addFishSiteBaseInfo(FishSiteBaseInfo fishSiteBaseInfo) {
		Location location = amapService.geocode_regeo(fishSiteBaseInfo.getLon(), fishSiteBaseInfo.getLat());
		Region region;
		region = regionRepository.getRegionByRegionName(location.getProvince());
		if (region == null) {
			throw new BadRequestException("找不到指定省份信息:" + location.getProvince());
		}
		
		int provinceId = region.getRegionNum();
		region = regionRepository.getRegionByRegionName(location.getCity());
		if (region == null) {
			throw new BadRequestException("找不到指定城市信息:" + location.getCity());
		}
		
		int cityId = region.getRegionNum();
		fishSiteBaseInfo.setCityId(cityId);
		fishSiteBaseInfo.setCityName(location.getCity());
		fishSiteBaseInfo.setProvinceId(provinceId);
		fishSiteBaseInfo.setProvinceName(location.getProvince());
		FishSiteBaseInfo fishSite = fishSiteBaseInfoMapper.getFishSiteByuid(fishSiteBaseInfo.getFishSiteUid(), null, null);
		if(fishSite!= null){
			fishSiteBaseInfoMapper.updateFishSiteInfo(fishSiteBaseInfo);
		}else{	
			fishSiteBaseInfoMapper.addFishSiteBaseInfo(fishSiteBaseInfo);
		}		
		fishSiteBaseMapper.updateFishSite(fishSiteBaseInfo);
	}

	@Override
	public List<FishSiteEmployee> getEmployee(Integer uid) {
		List<FishSiteEmployee> list = fishSiteEmployeeMapper.getEmployeeList(uid);
		return list;
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = {Exception.class})
	public void deleteFishSiteEmployee(Integer id) {
		fishSiteEmployeeMapper.deleteEmployee(id);
	}

	@Override
	public void addFishSiteEmployee(Integer uid, Integer employeeUid) {
		FishSiteEmployee fishSiteEmployee = new FishSiteEmployee();
		fishSiteEmployee.setEmployeeUid(employeeUid);
		fishSiteEmployee.setUid(uid);
		fishSiteEmployeeMapper.addEmployee(fishSiteEmployee);
	}

	@Override
	public FishSiteEmployee getUsableEmployee(String phone) {
		
		return fishSiteEmployeeMapper.getEmployeeByPhone(phone);
	}
	
	
}