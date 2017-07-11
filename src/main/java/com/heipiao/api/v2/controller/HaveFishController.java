package com.heipiao.api.v2.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.heipiao.api.v2.constant.RespMsg;
import com.heipiao.api.v2.constant.Status;
import com.heipiao.api.v2.domain.FishSiteBase;
import com.heipiao.api.v2.domain.HaveFish;
import com.heipiao.api.v2.domain.HaveFishComment;
import com.heipiao.api.v2.domain.HaveFishLike;
import com.heipiao.api.v2.domain.PageInfo;
import com.heipiao.api.v2.exception.BadRequestException;
import com.heipiao.api.v2.exception.NotFoundException;
import com.heipiao.api.v2.service.HaveFishService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags = "有鱼模块")
@RestController
@RequestMapping(value = "havefish", produces = MediaType.APPLICATION_JSON_VALUE)
public class HaveFishController {
	
	@Resource
	private HaveFishService haveFishService;
	
	private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);
	
	
	@ApiOperation(value = "有鱼详情列表", response = List.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",required = true),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1", required = true),
		@ApiImplicitParam(paramType = "query", name = "longitude", value = "经度", dataType = "double", defaultValue = "114.032428", required = true),
		@ApiImplicitParam(paramType = "query", name = "latitude", value = "纬度", dataType = "double", defaultValue = "22.538205", required = true)})
	@RequestMapping(value = "detail/{uid}", method = RequestMethod.GET)
	public RespMsg<List<HaveFish>> getHaveFishDetail(
			@PathVariable(value = "uid", required = true) Integer uid,
			@RequestParam(value = "start", required = true) Integer start,
			@RequestParam(value = "longitude", required = true) Double longitude,
			@RequestParam(value = "latitude", required = true) Double latitude) {
		logger.debug("uid:{},start:{},longitude:{},latitude:{}",uid,start,longitude,latitude);
		if(uid == null || start==null ||longitude==null ||  latitude==null){
			throw new NotFoundException("参数不能为空");
		}
		start = start - 1 <= 0 ? 0 : (start - 1); 
		List<HaveFish> list = haveFishService.getHaveFishList(uid,start,longitude,latitude);
		return new RespMsg<List<HaveFish>>(list);
	}
	
	@ApiOperation(value = "有鱼默认列表",response = List.class)
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",required = true),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1", required = true),
		@ApiImplicitParam(paramType = "query", name = "size", value = "页大小", dataType = "int", required = true, example = "10"),
		@ApiImplicitParam(paramType = "query", name = "longitude", value = "经度", dataType = "double", defaultValue = "114.032428", required = true),
		@ApiImplicitParam(paramType = "query", name = "latitude", value = "纬度", dataType = "double", defaultValue = "22.538205", required = true)})
	@RequestMapping(value = "list/{uid}", method = RequestMethod.GET)
	public RespMsg<List<HaveFish>> getHaveFishList(
			@PathVariable(value = "uid", required = true) Integer uid,
			@RequestParam(value = "start", required = true) Integer start,
			@RequestParam(value = "size", required = true) Integer size,
			@RequestParam(value = "longitude", required = true) Double longitude,
			@RequestParam(value = "latitude", required = true) Double latitude){
		logger.debug("uid:{},start:{},size:{},longitude:{},latitude:{}",uid,start,size,longitude,latitude);
		if(uid == null || start==null || size == null ||longitude==null ||  latitude==null){
			throw new NotFoundException("参数不能为空");
		}
		start = start - 1 <= 0 ? 0 : (start - 1) * size;
		List<HaveFish> list = haveFishService.getHaveFishAllList(uid,start,size,longitude,latitude);
		return new RespMsg<List<HaveFish>>(list);
	}
	
	@ApiOperation(value = "发布有鱼", notes = "参数说明：<br />"
			+ "uid：用户id<br/>"
			+ "phone: 联系方式<br/>"
			+ "title：标题<br/>"
			+ "type: 有鱼类型（0-视频，1-图片）<br/>"
			+ "fishSiteName: 钓场名称<br/>"
			+ "url: 有鱼内容地址<br/>"
			+ "lon：经度<br/>"
			+ "lat：纬度<br/>"
			+ "isDefault：是否有默认设置<br/>"
			)
	@RequestMapping(method = RequestMethod.POST)
	public String applyHaveFish(@RequestBody HaveFish haveFish) {
		logger.debug("haveFish:{}", haveFish);
		
		if (haveFish.getUid() == null || StringUtils.isBlank(haveFish.getTitle())
				||  haveFish.getLon() == null || haveFish.getLat() == null
				|| haveFish.getIsDefault()==null) {
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addHaveFish(haveFish);
		return JSONObject.toJSONString(Status.success);
	}
	
	@ApiOperation(value = "获取有鱼默认设置", response = List.class) 	
	@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",required = true)
	@RequestMapping(value = "baseset/{uid}", method = RequestMethod.GET)
	public RespMsg<FishSiteBase> getFishBaseByUid(
			@PathVariable(value = "uid", required = true) Integer uid) {
		logger.debug("uid:{}",uid);
		if(uid == null){
			throw new NotFoundException("参数不能为空");
		}
		FishSiteBase fishSiteBase = haveFishService.getDefaultSet(uid);
		return new RespMsg<FishSiteBase>(fishSiteBase);
	}
	
	@ApiOperation(value = "添加钓场默认设置", notes = "参数说明：<br />"
			+ "fishSiteUid：用户id<br/>"
			+ "userName：钓场主姓名<br/>"
			+ "fishSiteName: 钓场名称<br/>"
			+ "phone: 联系方式<br/>"
			+ "lon：经度<br/>"
			+ "lat：纬度<br/>"
			)
	@RequestMapping(value = "baseset",method = RequestMethod.POST)
	public String applyFishSiteBase(@RequestBody FishSiteBase fishSiteBase) {
		
		logger.debug("fishSiteBase:{}", fishSiteBase);
		
		if (fishSiteBase.getFishSiteUid() == null 
				|| StringUtils.isBlank(fishSiteBase.getFishSiteName())
				|| fishSiteBase.getLon() == null 
				|| fishSiteBase.getLat() == null
				|| StringUtils.isBlank(fishSiteBase.getUserName())){
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addFishSiteBase(fishSiteBase);
		return JSONObject.toJSONString(Status.success);
	}	
	
	
	@ApiOperation(value = "用户点赞", notes = "参数说明：<br />"
			+ "haveFishId：有鱼id<br/>"
			+ "uid：用户id<br/>"
			)
	@RequestMapping(value = "like",method = RequestMethod.POST)
	public String haveFishLike(@RequestBody HaveFishLike haveFishLike) {
		
		logger.debug("haveFishLike:{}", haveFishLike);
		
		if (haveFishLike.getHaveFishId()== null 
				|| haveFishLike.getUid() == null 
				|| haveFishLike.getUid() == null){
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addLikeUser(haveFishLike);
		return JSONObject.toJSONString(Status.success);
	}	
	
	
	@ApiOperation(value = "用户评论", notes = "参数说明：<br />"
			+ "haveFishId：有鱼id<br/>"
			+ "uid：用户id<br/>"
			+ "comment：评论内容<br/>"
			)
	@RequestMapping(value = "comment",method = RequestMethod.POST)
	public String haveFishLike(@RequestBody HaveFishComment haveFishComment) {
		
		logger.debug("haveFishComment:{}", haveFishComment);
		
		if (	haveFishComment.getHaveFishId()== null 
				|| haveFishComment.getUid() == null 
				|| haveFishComment.getComment() == null){
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addCommentUser(haveFishComment);
		return JSONObject.toJSONString(Status.success);
	}	
	
	@ApiOperation(value = "OCC获取钓场基本信息", response = List.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "provinceId", value = "省份id",required = false),
		@ApiImplicitParam(paramType = "query", name = "cityId", value = "城市id", required = false),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1", required = true),
		@ApiImplicitParam(paramType = "query", name = "size", value = "页大小", dataType = "int", required = true, example = "10"),
		@ApiImplicitParam(paramType = "query", name = "regBegin", value = "注册起始日期（yyyy-MM-dd）", dataType = "date", required = false),
		@ApiImplicitParam(paramType = "query", name = "regEnd", value = "结束日期（yyyy-MM-dd）", dataType = "date", required = false)})
	@RequestMapping(value = "allbaseset", method = RequestMethod.GET)
	public PageInfo<List<FishSiteBase>> getAllFishBase(
			@RequestParam(value = "start", required = true) Integer start,
			@RequestParam(value = "size", required = true) Integer size,
			@RequestParam(value = "provinceId", required = false) Integer provinceId,
			@RequestParam(value = "cityId", required = false) Integer cityId,
			@RequestParam(value = "regBegin", required = false) Date regBegin,
			@RequestParam(value = "regEnd", required = false) Date regEnd) {
		logger.debug("start:{},size:{},provinceId:{},cityId:{},regBegin:{},regEnd:{}",start,size,provinceId,cityId,regBegin,regEnd);
		
		start = start - 1 <= 0 ? 0 : (start - 1) * size;
		
		PageInfo<List<FishSiteBase>> pageInfo = haveFishService.getAllFishSiteSet(start,size,provinceId,cityId,regBegin,regEnd);
		return pageInfo;
	}
	
	@ApiOperation(value = "OCC钓场基本信息审核")
	@ApiImplicitParams({ @ApiImplicitParam(paramType = "path", name = "uid", value = "钓场主uid", required = true),
		@ApiImplicitParam(paramType = "query", name = "status", value = "状态（0-待审核，1-审核通过，2-审核不通过）", dataType = "integer", defaultValue = "1", required = true) })
	@RequestMapping(value = "baseset/{uid}",method = RequestMethod.PUT)
	public String applyFishSiteBase(@PathVariable(value = "uid", required = true) Integer uid,
			@RequestParam(value = "status", required = true) Integer status) {
		
		logger.debug("uid:{},status:{}",uid,status);
		
		if (uid ==null || status == null){
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.updateFishSiteBase(uid,status);
		return JSONObject.toJSONString(Status.success);
	}	
	
	@ApiOperation(value = "OCC获取已发布有鱼列表", response = List.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "provinceId", value = "省份id",required = false),
		@ApiImplicitParam(paramType = "query", name = "cityId", value = "城市id", required = false),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1", required = true),
		@ApiImplicitParam(paramType = "query", name = "size", value = "页大小", dataType = "int", required = true, example = "10"),
		@ApiImplicitParam(paramType = "query", name = "regBegin", value = "注册起始日期（yyyy-MM-dd）", dataType = "date", required = false),
		@ApiImplicitParam(paramType = "query", name = "regEnd", value = "结束日期（yyyy-MM-dd）", dataType = "date", required = false),
		@ApiImplicitParam(paramType = "query", name = "type", value = " 有鱼类型（0-视频，1-图片）", dataType = "Integer", required = false),
		@ApiImplicitParam(paramType = "query", name = "nickname", value = "用户昵称", dataType = "String", required = false)})
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public PageInfo<List<HaveFish>> getAllHaveFish(
			@RequestParam(value = "start", required = true) Integer start,
			@RequestParam(value = "size", required = true) Integer size,
			@RequestParam(value = "provinceId", required = false) Integer provinceId,
			@RequestParam(value = "cityId", required = false) Integer cityId,
			@RequestParam(value = "regBegin", required = false) Date regBegin,
			@RequestParam(value = "regEnd", required = false) Date regEnd,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "nickname", required = false) String nickname) {
		logger.debug("start:{},size:{},provinceId:{},cityId:{},regBegin:{},regEnd:{},type:{},nickname:{}",start,size,provinceId,cityId,regBegin,regEnd,type,nickname);
		
		start = start - 1 <= 0 ? 0 : (start - 1) * size;
		
		PageInfo<List<HaveFish>> pageInfo = haveFishService.getAllHaveFishByPage(start,size,provinceId,cityId,regBegin,regEnd,type,nickname);
		return pageInfo;
	}
}