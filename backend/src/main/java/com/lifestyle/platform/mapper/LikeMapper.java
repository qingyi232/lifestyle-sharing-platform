package com.lifestyle.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifestyle.platform.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends BaseMapper<UserLike> {
}
