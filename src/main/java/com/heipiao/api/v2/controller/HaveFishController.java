package com.heipiao.api.v2.controller;

import java.sql.Date;
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
import com.heipiao.api.v2.domain.FishSiteBaseSign;
import com.heipiao.api.v2.domain.HaveFish;
import com.heipiao.api.v2.domain.HaveFishComment;
import com.heipiao.api.v2.domain.HaveFishDefault;
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
	
	private static final Logger logger = LoggerFactory.getLogger(HaveFishController.class);
	
	
	@ApiOperation(value = "有鱼详情列表", response = HaveFish.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",dataType = "int",required = true),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1",dataType = "int", required = true),
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
	
	@ApiOperation(value = "有鱼OCC详情列表", response = HaveFish.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",dataType = "int",required = true),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1",dataType = "int", required = true)})
	@RequestMapping(value = "Occdetail/{uid}", method = RequestMethod.GET)
	public PageInfo<List<HaveFish>> getHaveFishOccDetail(
			@PathVariable(value = "uid", required = true) Integer uid,
			@RequestParam(value = "start", required = true) Integer start) {
		logger.debug("uid:{},start:{}",uid,start);
		if(uid == null || start==null ){
			throw new NotFoundException("参数不能为空");
		}
		start = start - 1 <= 0 ? 0 : (start - 1); 
		PageInfo<List<HaveFish>> list = haveFishService.getHaveFishOCCList(uid,start);
		return list;
	}
	
	@ApiOperation(value = "有鱼默认列表",response = HaveFish.class)
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
			+ "如果是管理员或钓场主发布标鱼信息，需要同时添加一下参数"
			+ "fishSize: 有鱼大小<br/>"
			+ "rewardUid：中标用户id<br/>"
			+ "markCode：中标鱼标编码<br/>"
			)
	@RequestMapping(method = RequestMethod.POST)
	public String applyHaveFish(@RequestBody HaveFish haveFish){
		logger.debug("haveFish:{}", haveFish);
		
		if (haveFish.getUid() == null || StringUtils.isBlank(haveFish.getTitle())
				||  haveFish.getLon() == null || haveFish.getLat() == null) {
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addHaveFish(haveFish);
		return JSONObject.toJSONString(Status.success);
	}
	
	@ApiOperation(value = "获取有鱼默认设置", response = HaveFishDefault.class) 	
	@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",dataType = "int",required = true)
	@RequestMapping(value = "baseset/{uid}", method = RequestMethod.GET)
	public RespMsg<HaveFishDefault> getFishBaseByUid(
			@PathVariable(value = "uid", required = true) Integer uid) {
		logger.debug("uid:{}",uid);
		if(uid == null){
			throw new NotFoundException("参数不能为空");
		}
		HaveFishDefault haveFishDefault = haveFishService.getDefaultSet(uid);
		return new RespMsg<HaveFishDefault>(haveFishDefault);
	}
	
	@ApiOperation(value = "判断用户是否为钓场主或管理员(并且是否申请了标鱼)",notes = "参数说明：(1-为普通用户 ，2 - 为钓场主或管理员)<br />") 	
	@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",dataType = "int",required = true)
	@RequestMapping(value = "user/{uid}", method = RequestMethod.GET)
	public String isUserOfSite(
			@PathVariable(value = "uid", required = true) Integer uid) {
		logger.debug("uid:{}",uid);
		if(uid == null){
			throw new NotFoundException("参数不能为空");
		}
		Integer rs = haveFishService.isUserOfSite(uid);
		return JSONObject.toJSONString(rs);
	}
	
	@ApiOperation(value = "查询钓场签到用户查询") 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "uid", value = "用户id",dataType = "int",required = true),
		@ApiImplicitParam(paramType = "query", name = "nickname", value = "用户昵称",dataType = "string",required = true)})
	@RequestMapping(value = "signuser/{uid}", method = RequestMethod.GET)
	public RespMsg<List<FishSiteBaseSign>> getUserOfSiteSign(
			@PathVariable(value = "uid", required = true) Integer uid,
			@RequestParam(value = "nickname",required = true)String nickname) {
		logger.debug("uid:{},nickname:{}",uid,nickname);
		if(uid == null || nickname == null){
			throw new NotFoundException("参数不能为空");
		}
		List<FishSiteBaseSign> list = haveFishService.getSignUserOfSite(uid,nickname);
		return new RespMsg<List<FishSiteBaseSign>>(list);
	}
	
	@ApiOperation(value = "添加钓场默认设置", notes = "参数说明：<br />"
			+ "uid：用户id<br/>"
			+ "userName：钓场主姓名<br/>"
			+ "fishSiteName: 钓场名称<br/>"
			+ "phone: 联系方式<br/>"
			+ "lon：经度<br/>"
			+ "lat：纬度<br/>"
			+ "address：详细地址<br/>"
			)
	@RequestMapping(value = "baseset",method = RequestMethod.POST)
	public String applyFishSiteBase(@RequestBody HaveFishDefault haveFishDefault) {
		
		logger.debug("haveFishDefault:{}", haveFishDefault);
		
		if (haveFishDefault.getUid() == null 
				|| StringUtils.isBlank(haveFishDefault.getFishSiteName())
				|| haveFishDefault.getLon() == null 
				|| haveFishDefault.getLat() == null
				|| StringUtils.isBlank(haveFishDefault.getUserName())){
			throw new BadRequestException("必要参数不能为空");
		}
		haveFishService.addHaveFishDefaultBase(haveFishDefault);
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
		boolean result = haveFishService.addLikeUser(haveFishLike);
		if(result){
			return JSONObject.toJSONString(Status.ALREADY_LIKE);
		}else{
			return JSONObject.toJSONString(Status.success);
		}
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
	
	@ApiOperation(value = "OCC获取已发布有鱼列表", response = HaveFish.class) 	
	@ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "provinceId", value = "省份id",dataType = "int",required = false),
		@ApiImplicitParam(paramType = "query", name = "cityId", value = "城市id",dataType = "int", required = false),
		@ApiImplicitParam(paramType = "query", name = "start", value = "查询页码，首页传1", required = true),
		@ApiImplicitParam(paramType = "query", name = "size", value = "页大小", dataType = "int", required = true, example = "10"),
		@ApiImplicitParam(paramType = "query", name = "regBegin", value = "注册起始日期（yyyy-MM-dd）", dataType = "date", required = false),
		@ApiImplicitParam(paramType = "query", name = "regEnd", value = "结束日期（yyyy-MM-dd）", dataType = "date", required = false),
		@ApiImplicitParam(paramType = "query", name = "type", value = " 有鱼类型（0-视频，1-图片）",dataType = "int", required = false),
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
	
	@ApiOperation(value = "OCC审核已发布有鱼")
	@ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "id", value = "有鱼id",dataType = "int", required = true),
		@ApiImplicitParam(paramType = "query", name = "isDisplay", value = "审核状态（0-显示，1-不显示）",dataType = "int", required = true)})
	@RequestMapping(value = "auditing/{id}", method = RequestMethod.PUT)
	public String auditHaveFish(
			@PathVariable(value = "id", required = true) Integer id,
			@RequestParam(value = "isDisplay", required = true) Integer isDisplay) {
		logger.debug("id:{},isDisplay:{}",id,isDisplay);
		haveFishService.updateHaveFish(id,isDisplay);
		return JSONObject.toJSONString(Status.success);
	}
	
	@ApiOperation(value = "OCC根据id获取有鱼内容", response = HaveFish.class)
	@ApiImplicitParam(paramType = "path", name = "id", value = "有鱼id",dataType = "int", required = true)
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public HaveFish getOneHaveFish(
			@PathVariable(value = "id", required = true) Integer id) {
		logger.debug("id:{}",id);
		HaveFish haveFish = haveFishService.getOneHaveFish(id);
		return haveFish;
	}
}
