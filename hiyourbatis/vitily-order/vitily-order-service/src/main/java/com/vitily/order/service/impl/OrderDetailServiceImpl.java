package com.vitily.order.service.impl;

import com.vitily.common.service.impl.BasicServiceImpl;
import com.vitily.order.module.entity.TbOrderDetail;
import com.vitily.order.module.request.TqOrderDetail;
import com.vitily.order.module.view.TvOrderDetail;
import com.vitily.order.mapper.OrderDetailMapper;
import com.vitily.order.service.OrderDetailService;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailServiceImpl extends BasicServiceImpl<TbOrderDetail, TqOrderDetail, TvOrderDetail, OrderDetailMapper> implements OrderDetailService {
}