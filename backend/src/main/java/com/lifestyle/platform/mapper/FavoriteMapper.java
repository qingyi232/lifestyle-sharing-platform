package com.lifestyle.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifestyle.platform.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
