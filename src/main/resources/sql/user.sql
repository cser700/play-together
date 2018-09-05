### 获得user_info下一个自增主键
#sql("nextPrimaryKey")
	SELECT auto_increment id FROM information_schema.`TABLES` 
	WHERE TABLE_SCHEMA='play' AND TABLE_NAME='user_info';
#end

### 根据手机号码查询用户
#sql("findByMobile")
	select * from user_info
	where mobile = #para(0)
#end

### 根据openid查询数据
#sql("findByOpenId")
	select 
		user_id user_id,
		user_id userId,
		ifnull(mobile,'') mobile,
		ifnull(nick_name,'') nickName,
		ifnull(real_name,'') realName,
		ifnull(position,'') position,
		company_id,
		company_id  companyId,
		ifnull(gender,'') gender,
		ifnull(avatar_url,'') avatarUrl,
		ifnull(email,'') email,
		ifnull(country,'') country,
		ifnull(province,'') province,
		ifnull(city,'') city,
		ifnull(admin,'') admin,
		ifnull(openid,'') openid,
		ifnull(unionid,'') unionid,
		ifnull(language,'') language,
		ifnull(status,'') status
	from user_info
	where openid = #para(0)
#end


### 搜索用户
#sql("searchUser")
	 select user_id , nick_name , avatar_url from user_info 
	 where company_id = #para(0) 
	 and nick_name like concat('%', #para(1), '%')
	 limit 10
#end

### 用户列表
#sql("userPage")
	select * from 
	(
		select * from user_info 
		where company_id = #para(companyId) 
		#if(userId)
			and user_id != #para(userId)
		#end
		order by convert(nick_name USING gbk)COLLATE gbk_chinese_ci ASC
	) as temp
#end

### 根据 unionid 查询用户
#sql("findUserByUnionId")
	select * from user_info
	where unionid = #para(0)
#end

